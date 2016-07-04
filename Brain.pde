
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
	float[] computeOutput ( float[] inputs ){

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
	void printWeights (){
		println("Weights: ");
		for (int i = 0; i < layers_num; ++i) {
			for (int j = 0; j < perceptron_num; ++j) {
				println (layers[i][j].weights);
			}
		}
		println("........");
	}
}