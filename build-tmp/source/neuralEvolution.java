import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import com.hamoid.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class neuralEvolution extends PApplet {

 //Lib esportazione video.
VideoExport videoExport;

World myworld; 

boolean videoOn = false; //Flag per registrare video

public void setup() {
	
	

	videoExport = new VideoExport(this, "neural.mp4");

	//Initialize my world:
	myworld = new World(15, 50);
}

public void draw() {
	background(33, 33, 33);

	//Run my world
	myworld.run();

	//Export video
	if(videoOn) videoExport.saveFrame();
}

//Mouse clicked
public void mouseClicked(){
	myworld.addBloops();
}

//Key pressed
public void keyPressed(){
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

	public void run() {

		//Update bloops:
		for (int i = 0; i < bloops.size(); ++i) {

			bloops.get(i).update(foods);   //Update Bloop position
			bloops.get(i).eat(foods);  //Bloop eating
			bloops.get(i).display();  //Bloop display


			if( bloops.get(i).health > 800 ){  //Bloop reproduction
				if( random(1) < 0.0013f ){
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
		if(random(1) < 0.05f){
			foods.add( new Food() );
		}
	}

	//Used for clicking add:
	public void addBloops(){
		float[] goodGenes = {0.65979946f, 0.9237745f, -0.95996153f, 0.984262f, -0.10405171f, 0.8009598f, 0.43076366f, 0.8778855f, 0.042821348f, 0.993147f, 0.57589555f, 0.29872072f, 0.25054073f, 0.050901234f, 0.8436686f, 0.44286066f, -0.23298085f, -0.15841329f, 0.014826775f, -0.6465541f, 0.9070223f, 0.9349151f, -0.13380969f, 0.5622362f, -0.51015556f, 0.7004848f, -0.9447372f, 0.48166156f, 0.9900281f, 0.6025151f, 0.4556185f, -0.2769916f, 52.33656f, 13.038952f, 13.491612f, 40.85528f, 33.24558f, 45.536972f, 11.461625f, 14.195549f};
		DNA dna = new DNA( goodGenes );
		bloops.add(new Bloop( dna, new PVector(mouseX, mouseY), bloopsCount++ ));


		float[] goodGenes2 = {0.20275891f, 0.026649952f, 0.43252873f, -0.40793216f, -0.54590714f, -0.25586605f, 0.3218981f, -0.35193098f, 0.58812934f, 0.83577764f, -0.016791105f, 0.64107144f, 0.97045064f, 0.16259098f, -0.14545023f, 0.7636057f, -0.7766409f, 0.1283921f, 0.16343546f, -0.3811437f, 0.7840737f, -0.7701818f, 0.24260998f, 0.43112117f, 0.90271205f, 0.6994773f, 0.15249717f, 0.75517744f, 0.4281698f, -0.4254781f, 0.6863489f, -0.5756632f, 25.924147f, 6.260845f, 31.409222f, 32.69906f, 13.4582405f, 30.142233f, 47.648323f, 2.0961523f}; 
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
	PVector leftForce = new PVector (0.05f, 0);
	PVector rightForce = new PVector (-0.05f, 0);
	PVector upForce = new PVector (0, 0.05f);
	PVector downForce = new PVector (0, -0.05f);

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
		acceleration = new PVector ( 0.0f, 0.0f);

		xoff = PApplet.parseInt(random(100));
		yoff = PApplet.parseInt(random(100));

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
		acceleration = new PVector ( 0.0f, 0.0f);

		xoff = PApplet.parseInt(random(100));
		yoff = PApplet.parseInt(random(100));

		input = new float[4];

		//targetFood = new Food();
		printInformation();	
	}

	//Update Bloops location, with Neural Network
	public void update( ArrayList<Food> foods ){


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
		velocity.mult(0.95f); //Add some friction

		//If reach the border:
		if( location.x < 0 ) location.x = width;
		if( location.x > width ) location.x = 0;
		if( location.y < 0 ) location.y = height;
		if( location.y > height ) location.y = 0;
		
		//Decrement health:
		health -= 1;
	}

	//Apply a force:
	public void applyForce(PVector force){
		PVector f = PVector.div(force,mass/35);
		acceleration.add(f);
	}

	//Eat the Foods:
	public void eat( ArrayList<Food> foods ){

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
	public void display (){

		noStroke();

		if( health > 800) stroke(0, 200, 0);

		fill (map(health, 0, 1000, 33, 255), 200);
		
		ellipse(location.x - 5, location.y, mass, mass);

		fill(255);
		text(id, location.x, location.y);		
	}

	//Reproduction:
	public Bloop reproduce (int _id){
		println("Reproduction! ");
		DNA childDNA = dna.copy(); 
     	childDNA.mutate(0.02f); //2% mutation rate	
      	return new Bloop(childDNA, location, _id);
	}	

	//Print Bloops information:
	public void printInformation(){
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
	public boolean isDead(){
		if(health < 0.0f){
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

	public void display (){
		noStroke();
		fill(200, 50, 0);
		rect(location.x, location.y, 7, 7);
	}
}


class Brain {

	Perceptron[][] layers;  //Perceptron Layers

	int layers_num = 2;  //Numbers of Layers
	int perceptron_num = 4;  //Numbers of perceptron for each Layers

	Brain( DNA dna ){

		layers = new Perceptron[layers_num][perceptron_num];
		

		//Assign the Weights, using the DNA genes, and create the perceptron layers:
		for (int i = 0; i < layers_num; ++i) {
			for (int j = 0; j < perceptron_num; ++j) {

				float[] w = new float[4];
				w[0] = dna.genes[16*i + 4*j + 0];
				w[1] = dna.genes[16*i + 4*j + 1];
				w[2] = dna.genes[16*i + 4*j + 2];
				w[3] = dna.genes[16*i + 4*j + 3]; 
				
				layers[i][j] = new Perceptron(perceptron_num, w);
			}
		}
	}

	//Compute the Neural Network Outputs
	public float[] computeOutput ( float[] inputs ){

		float out[][] = new float[layers_num+1][perceptron_num];

		out[0] = inputs;

		for (int i = 0; i < layers_num; ++i) {
			for (int j = 0; j < perceptron_num; ++j) {

				out[i+1][j] = layers[i][j].feedforward( out[i] );
				out[i+1][j] = layers[i][j].feedforward( out[i] );
				out[i+1][j] = layers[i][j].feedforward( out[i] );
				out[i+1][j] = layers[i][j].feedforward( out[i] );
			
			}
		}

		return out[layers_num];
	}

	//Print the Weights:
	public void printWeights (){
		println("Weights: ");
		for (int i = 0; i < layers_num; ++i) {
			for (int j = 0; j < perceptron_num; ++j) {
				println (layers[i][j].weights);
			}
		}
		println("........");
	}
}

class DNA {

	//The genes include: Neural Network Weights, Mass
	float[] genes;

	//Construction, with random genes
	DNA(){
		genes = new float[40];
		for (int i = 0; i < 32; ++i) {  //Weights genes
			genes[i] = random(-1, 1);
		}
		for (int i = 32; i < 33; ++i) {  //Mass Gene
			genes[i] = random(10, 70);
		}
		for (int i = 33; i < 40; ++i) {  //Others Genes - not used
			genes[i] = random(0, 50);
		}
	}

	//Construction, with defined genes
	DNA( float[] f ){
		genes = f;
	}

	//Return the exact copy of the dna
	public DNA copy() {
    	float[] newgenes = new float[genes.length];
    	arraycopy(genes,newgenes);
    	return new DNA(newgenes);
  	}

	//Mutate the genes
	public void mutate( float mutationRate ) {

    	for (int i = 0; i < 32; i++) {
      		if (random(1) < mutationRate) {
      			println("Mutation!");
        		genes[i] = random(0, 1);
      		}
    	}
    	for (int i = 32; i < 33; i++) {
      		if (random(1) < mutationRate) {
      			println("Mutation!");
        		genes[i] = random(50, 150);
      		}
    	}
    	for (int i = 33; i < 40; i++) {
      		if (random(1) < mutationRate) {
      			println("Mutation!");
        		genes[i] = random(10, 50);
      		}
    	}

  	}
}
class Perceptron {

	float[] weights;
	
	//Constructor with random Weights
	//n = number of inputs
	Perceptron (int n){
		weights = new float[n];
		for (int i = 0; i < n; ++i) {
			weights[i] = random(-1, 1);
		}
	}

	//Constructior with defined Weights
	//n = number of inputs
	Perceptron (int n, float[] w){
		weights = new float[n];
		for (int i = 0; i < n; ++i) {
			weights[i] = w[i];
		}
	}

	//Compute the calculus
	public float feedforward (float[] inputs){
		float sum = 0;
		for (int i = 0; i < weights.length; ++i) {
			sum += inputs[i]*weights[i];
		}
		return activate (sum);  //It returns -1 or 1
	}

	public float activate (float sum){
		if(sum > 0) return 1;
		else return -1;
	}
}
  public void settings() { 	size(1000, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "neuralEvolution" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
