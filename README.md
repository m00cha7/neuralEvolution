# neuralEvolution
Experiment with Neural Networks and Genetic Algorithms using Processing

# Video Url:
https://vimeo.com/176736676

# Project Description:

Neural Evolution is the result of personal several research regarding artificial intelligence. Using the software Processing I create a model of an ipotetical world in wich differents species fight to survive. 

The world is composed by two basic elements: the Bloop and the Food . The Bloop (the white balls) walk and eat the food (red dots). The Bloop have an initial health of 1000 points. Every time they eat, the health is increased by 100. If it goes to zero the Bloop dies. The Bloop are moved by a neural network shown in the image below. The inputs of the neural network are the distances along the X axis and the distance on the Y axis from the nearest Food. While the outputs are the movement commands up, down, right, left. All the weights of the network form the DNA of the individual Bloop, which is his way of interacting with the world. Also in the DNA it is a further variable that indicates the magnitude of the Bloop and therefore its speed of movement. 

At first all Bloop are instantiated at random and many do not know how to move. Reproduction is asexual: in every moment every Bloop has a small chance to reproduce, however, whether the Bloop is not healthy enough it cannot reproduce itself. Therefore more a Bloop eats and survives more likely has to reproduce. Reproducing create a Bloop with the same DNA but with some mutation of any gene or weight of the neural network. What happens is that after several generations in the world there are only Bloop able to eat, and they are increasingly good at it. The video shows only a few minutes of evolution, but if sent on for a long time the Bloops ability improves. At some point, however, they do not improve more because the world is very limited and there isn't nothing to learn. 

This is an example of a Neural Network trained through natural selection (genetic algorithm).
