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
	float feedforward (float[] inputs){
		float sum = 0;
		for (int i = 0; i < weights.length; ++i) {
			sum += inputs[i]*weights[i];
		}
		return activate (sum);  //It returns -1 or 1
	}

	float activate (float sum){
		if(sum > 0) return 1;
		else return -1;
	}
}