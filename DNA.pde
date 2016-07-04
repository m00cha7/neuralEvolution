
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
	DNA copy() {
    	float[] newgenes = new float[genes.length];
    	arraycopy(genes,newgenes);
    	return new DNA(newgenes);
  	}

	//Mutate the genes
	void mutate( float mutationRate ) {

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