
(Refer to report.pdf for more details about this project)

## How to run

The maze should be in top level folder in src file outside of assignment_mazeworld


1.A-star search (in informedSearcgProblem)

Excecute SimpleMazeDriver to compare BFS, DFS and a-star.



2. MultiRobotProblem ( MultiRobotProblem.java )

The testing code for multiRobotProblem is in MultiRobotProblem.java  main function.
The user can change the start and the goal state. The default start anf goal state are:


 int[] startState = {0,1,1,1,0,6,1};

 int[] goalState = {6,0,6,6,6,1,2};


The length of start and goal state should be odd with the starting turn at the end of the array. In the above start state,
the first three pairs denote the location of first three robots, last number indicates the turn 1=> first robot and all


3. blind robot

run the blindrobot.java the code is in main function.
The code has been tested to work on this maze:
.......
.##....
..##...
....#..
..##...
..#....
....##.

Simply replace the maze in simple.maz in the outer folder to see the effect.


