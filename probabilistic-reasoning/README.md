#Probabilistic Reasoning

## Background

The problem is based on a problem from Rob Schapire, a professor at Princeton. (This general type of problem, however, is quite standard in robotics, and the approach of using a Markov model to integrate sensor information is very standard. )

A robot is in a 4x4 maze of the type we used in the mazeworld assignment. The physics of the maze are the same as for the blind robot problem: if the robot tries to move west, but there is a wall there, then the robot fails to move.

However, in this case we will not try to come up with a clever sequence of movements to figure out where the robot is. Instead, we will rely on sensor information to figure out a probability distribution over location that the robot might be in.

For simplicity, let's first consider the case where there are no walls. There are 16 possible locations for the robot, and we don't know which location the robot is in. The state of the robot is a single random variable, X, with sixteen possible values (labeled 0 through 15, perhaps). At time zero, X has the distribution (.0625, .0625, .0625, ..., .0625), since .0625 is 1/16. This represents the belief of the robot that it might be at any of the 16 locations with equal probability.

The robot has an initial location and will take some moves. The robot does not know either its initial location or how it moved, although it does know that it only makes one move per time step. You'll pick those for the robot, perhaps by choosing a random sequence of actions. For example, if the initial location was (0, 0), the bottom left of the maze, and you choose the sequence "e w w e" for the robot, the robot would travel to the locations (1, 0), (0, 0), (0, 0), (1, 0) in that order. But the robot doesn't know the starting location, doesn't know the sequence "e w w e", and doesn't know where it ends up.

So how can the robot figure out something about where it is? Each empty square of the maze has a floor that is painted with one of four colors: red, green, yellow, or blue. The robot has exactly one sensor, pointing downwards. That sensor mostly works. If the robot is in a blue square, the probability of recieving the evidence "b" is .88, but the sensor might also give the evidence "r" (with probability .04), "g" (probability .04), or "y" (probability .04). The situation for squares with other colors is symmetric.

So the robot receives a sequence of colors as sensor input; one color after each attempted move. (Recall that a move might not actually change the robot location, in the case that there is a wall in the way.) For example, assume that (0, 0) is red and (1, 0) is blue. Then perhaps the above sequence of actions e w w e would give the sensor results b r r b, based on the locations visited as described above. (Or perhaps it would give b g r b, with the second sensor reading being an error.)

## Task
 Given a sequence of sensor readings, knowledge of the maze (where the walls are), and the colors of each location of the maze,we have to compute a sequence of probability distributions describing the possible locations of the robot at each time step.

Refer to "probabilistic-reasoning-Vivek.pdf" for implementation details
(adapted from Prof. Balkcom's notes)
