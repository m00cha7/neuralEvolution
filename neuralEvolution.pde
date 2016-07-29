import com.hamoid.*; //Lib esportazione video.
VideoExport videoExport;

World myworld; 

boolean videoOn = false; //Flag per registrare video

void setup() {
	
	size(1000, 600);

	videoExport = new VideoExport(this, "neural.mp4");

	//Initialize my world:
	myworld = new World(15, 50);
}

void draw() {
	background(33, 33, 33);

	//Run my world
	myworld.run();

	//Export video
	if(videoOn) videoExport.saveFrame();
}

//Mouse clicked
void mouseClicked(){
	myworld.addBloops();
}

//Key pressed
void keyPressed(){
	if (key == 'r'){
		videoOn = true;
		println("start recording.... ");
	}
}




//The World class manage the entire world
//it create Bloops and Foods and control them
class World {

	ArrayList<Bloop> bloops;
	ArrayList<Food> foods;

	int bloopsCount = 0;

	World (int bloops_num, int foods_num) {
		bloops = new ArrayList<Bloop>();
		foods = new ArrayList<Food>();

		for (int i = 0; i < bloops_num; ++i) {
			bloops.add(new Bloop( bloopsCount++ ));
		}
		for (int i = 0; i < foods_num; ++i) {
			foods.add(new Food());
		}
	}

	void run() {

		//Update bloops:
		for (int i = 0; i < bloops.size(); ++i) {

			bloops.get(i).update(foods);   //Update Bloop position
			bloops.get(i).eat(foods);  //Bloop eating
			bloops.get(i).display();  //Bloop display


			if( bloops.get(i).health > 800 ){  //Bloop reproduction
				if( random(1) < 0.0013 ){
					bloops.get(i).health -= 100;
					bloops.add( bloops.get(i).reproduce(bloopsCount++) );  
				}
			}
			
			if(bloops.get(i).isDead()){  //Bloop death
				println("Bloops n. " + bloops.get(i).id + " is dead.");
				bloops.remove( bloops.get(i) );
			}
		}

		//Display Foods:
		for (int i = 0; i < foods.size(); ++i) {
			foods.get(i).display();
		}

		//Add some foods:
		if(random(1) < 0.05){
			foods.add( new Food() );
		}
	}

	//Used for clicking add:
	void addBloops(){
		float[] goodGenes = {0.65979946, 0.9237745, -0.95996153, 0.984262, -0.10405171, 0.8009598, 0.43076366, 0.8778855, 0.042821348, 0.993147, 0.57589555, 0.29872072, 0.25054073, 0.050901234, 0.8436686, 0.44286066, -0.23298085, -0.15841329, 0.014826775, -0.6465541, 0.9070223, 0.9349151, -0.13380969, 0.5622362, -0.51015556, 0.7004848, -0.9447372, 0.48166156, 0.9900281, 0.6025151, 0.4556185, -0.2769916, 52.33656, 13.038952, 13.491612, 40.85528, 33.24558, 45.536972, 11.461625, 14.195549};
		DNA dna = new DNA( goodGenes );
		bloops.add(new Bloop( dna, new PVector(mouseX, mouseY), bloopsCount++ ));


		float[] goodGenes2 = {0.20275891, 0.026649952, 0.43252873, -0.40793216, -0.54590714, -0.25586605, 0.3218981, -0.35193098, 0.58812934, 0.83577764, -0.016791105, 0.64107144, 0.97045064, 0.16259098, -0.14545023, 0.7636057, -0.7766409, 0.1283921, 0.16343546, -0.3811437, 0.7840737, -0.7701818, 0.24260998, 0.43112117, 0.90271205, 0.6994773, 0.15249717, 0.75517744, 0.4281698, -0.4254781, 0.6863489, -0.5756632, 25.924147, 6.260845, 31.409222, 32.69906, 13.4582405, 30.142233, 47.648323, 2.0961523}; 
		DNA dna2 = new DNA( goodGenes2 );
		bloops.add(new Bloop( dna2, new PVector(mouseX+300, mouseY), bloopsCount++ ));
	}
}

//The Bloop class
class Bloop {

	//TODO: Aggiungere parametri: dimensioni, forma, area_sensibile, pesi rete neurale, numero nodi della rete
	int id;  //Identification number of Bloops
	float mass;  //Mass of the Bloops, it affect movement and size
	int health;  //Health of the Bloops
	PVector location;
	PVector velocity;
	PVector acceleration;

	DNA dna;
	Brain brain;

	//Food targetFood;  //the Food to eat
	
	//Force that move the Bloops
	PVector leftForce = new PVector (0.05, 0);
	PVector rightForce = new PVector (-0.05, 0);
	PVector upForce = new PVector (0, 0.05);
	PVector downForce = new PVector (0, -0.05);

	float[] input;
	float xoff, yoff; //variable for Perlin Noise

	//Constructor with a random DNA, random Location
	Bloop( int _id ){

		id = _id;
		dna = new DNA();  //create random DNA
		brain = new Brain(dna);  //create the brain

		mass = dna.genes[32];
		health = 1000;

		location = new PVector( random(width), random(height));
		velocity = new PVector( 0, 0);
		acceleration = new PVector ( 0.0, 0.0);

		xoff = int(random(100));
		yoff = int(random(100));

		input = new float[4];

		//targetFood = new Food();
		
		printInformation();
	}

	//Constructor with specific DNA and Location
	Bloop( DNA _dna, PVector loc, int _id ){

		id = _id;
		dna = _dna;
		brain = new Brain(dna);

		mass = dna.genes[32];
		health = 1000;

		location = new PVector (loc.x + 10, loc.y + 10);
		velocity = new PVector( 0, 0);
		acceleration = new PVector ( 0.0, 0.0);

		xoff = int(random(100));
		yoff = int(random(100));

		input = new float[4];

		//targetFood = new Food();
		printInformation();	
	}

	//Update Bloops location, with Neural Network
	void update( ArrayList<Food> foods ){


		//If there is some foods:
		if(foods.size() > 0){

			//Find the closest foods:
			Food closestFood = foods.get(0);
			float distMin = PVector.dist (closestFood.location, location);
			for (int i = 1; i < foods.size(); ++i) {
				float dist = PVector.dist( foods.get(i).location, location );
				if (  dist < distMin ){
					closestFood = foods.get(i);
					distMin = dist;
				}
			}

			stroke(255, 255, 255, 100);
			line(closestFood.location.x, closestFood.location.y, location.x, location.y);

			float distX = location.x - closestFood.location.x;
			float distY = location.y - closestFood.location.y;

			//Create the input for the brain:
			input[0] = distX/100;
			input[1] = distY/100;
			input[2] = 0;
			input[3] = 0;

			//Compute the Neural Network Output
			float[] move = brain.computeOutput ( input );
				
			//Apply force:
			if ( move[0] == 1 ) {
				this.applyForce( leftForce ); 
			}
			if ( move[1] == 1 ) {
				this.applyForce( rightForce ); 
			}
			if ( move[2] == 1 ) {
				this.applyForce( upForce ); 
			}
			if ( move[3] == 1 ) {
				this.applyForce( downForce ); 
			}
		}
		
		//Move the bloops:
		velocity.add(acceleration);
		location.add(velocity);
		acceleration.mult(0);
		velocity.mult(0.95); //Add some friction

		//If reach the border:
		if( location.x < 0 ) location.x = width;
		if( location.x > width ) location.x = 0;
		if( location.y < 0 ) location.y = height;
		if( location.y > height ) location.y = 0;
		
		//Decrement health:
		health -= 1;
	}

	//Apply a force:
	void applyForce(PVector force){
		PVector f = PVector.div(force,mass/35);
		acceleration.add(f);
	}

	//Eat the Foods:
	void eat( ArrayList<Food> foods ){

		//Search if there is food:
		for (int i = 0; i < foods.size(); ++i) {
			
			float d = PVector.dist( location, foods.get(i).location );

			if( d < mass/2 ){
				health += 300;
				foods.remove ( foods.get(i) );  //Remove foods from array				
			}

		}
	}

	//Draw the Bloops:
	void display (){

		noStroke();

		if( health > 800) stroke(0, 200, 0);

		fill (map(health, 0, 1000, 33, 255), 200);
		
		ellipse(location.x - 5, location.y, mass, mass);

		fill(255);
		text(id, location.x, location.y);		
	}

	//Reproduction:
	Bloop reproduce (int _id){
		println("Reproduction! ");
		DNA childDNA = dna.copy(); 
     	childDNA.mutate(0.02); //2% mutation rate	
      	return new Bloop(childDNA, location, _id);
	}	

	//Print Bloops information:
	void printInformation(){
		println("Bloops number: " + id);
		println("Bloops mass: " + mass);
		print("DNA: ");
		for (int i = 0; i < dna.genes.length; ++i) {
			print(dna.genes[i]);
			print(" ");
		}
		println("    Dna-End");
		println("Location: " + location.x + "  " + location.y);
		println("Health: " + health);
		println("\n");
	}

	//Check if a Bloops is Dead:
	boolean isDead(){
		if(health < 0.0){
			return true;
		}
		else {
			return false;
		}
	}
}

//The Food class
class Food {

	PVector location;

	Food (){
		location = new PVector( random(width), random(height) );
	}

	void display (){
		noStroke();
		fill(200, 50, 0);
		rect(location.x, location.y, 7, 7);
	}
}

