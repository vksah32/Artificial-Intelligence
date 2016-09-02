package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by vivek on 1/18/16.
 */
public class MultiRobotProblem extends InformedSearchProblem {
    private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST, Maze.REST};


    private int[] startState;
    private int[] goalState;

    private Maze maze;

    public MultiRobotProblem(Maze m, int[] startState, int[] goalState) {
        startNode = new MultiRobotMazeNode(startState, 0);

        startState=startState;
        this.goalState=goalState;
        maze = m;

    }



    // node class used by searches.  Searches themselves are implemented
    //  in SearchProblem.
    public class MultiRobotMazeNode implements SearchNode {

        // location of the agents in the maze and turn
        protected int[] state; //[ x_1, y_1, x_2, y_2, turn ]

        // how far the current node is from the start.  Not strictly required
        //  for uninformed search, but useful information for debugging,
        //  and for comparing paths
        private double cost;
        private int numRobots;

        public MultiRobotMazeNode(int[] state, double c) {
            this.state = state;
//            this.state[0] = x;
//            this.state[1] = y;
            this.numRobots = state.length /2 ;

            cost = c;

        }


        public ArrayList<SearchNode> getSuccessors() {

            ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
            int turn = this.state[this.state.length-1] ; //which robot turn is it, the last index of the state array
            // index of x position of current robot is (turn-1)*2
			for (int[] action: actions) {
				int xNew = state[(turn-1)*2] + action[0]; //move the robot whose turn it is
				int yNew = state[(turn-1)*2+1] + action[1];

                //if its legal move in the maze check to see if other robots are not in the same location
                if(maze.isLegal(xNew, yNew)) {
                    boolean noCollison = true;
                    int[] newSucc = new int[this.state.length];
                    for (int i = 0; i < state.length-1 ; i++) {
                        newSucc[i] = state[i];
                        if (i % 2 == 0 && i != (turn - 1) * 2) { //go to every x coordinate of other robot

                            if (state[i] == xNew && state[i + 1] == yNew) {
                                noCollison = false;
                            }
                        }
                        if (noCollison) {
                            newSucc[(turn - 1) * 2] = xNew;
                            newSucc[(turn - 1) * 2 + 1] = yNew;
                            newSucc[this.state.length - 1] = (turn + 1 > this.numRobots ? 1 : turn + 1);
                            SearchNode succ = new MultiRobotMazeNode(newSucc, getCost() + 1.0);
                            successors.add(succ);
                        }
                    }
				}

			}
            //System.out.println("The successors for" + state "is");

            return successors;

		}

        public int getNumRobots(){
            return numRobots;
        }

        @Override
        public boolean goalTest() {

            return (new MultiRobotMazeNode(state,1)).equals(new MultiRobotMazeNode(goalState,1));
        }

        // an equality test is required so that visited sets in searches
        // can check for containment of states
        @Override
        public boolean equals(Object other) {
            boolean equals = true; //only compare the indices
//            return Arrays.equals(Arrays.copyOfRange(state, 0, state.length-2), Arrays.co((MultiRobotMazeNode) other).state);
            for (int i = 0; i < state.length-1 ; i++) {
                if (state[i] != ((MultiRobotMazeNode) other).state[i] ){
//                    System.out.println("doing equate  index not matching" + i + "items" + state[i] + ": " + ((MultiRobotMazeNode) other).state[i] );
//                    System.out.println();
                    equals = false;
                    break;

                }
            }
            return equals;

        }

        @Override
        public int hashCode() {
            int k = 0;
            for (int i = 0; i < state.length ; i++) {
                k+= state[i]*Math.pow(10, (state.length -1));
            }

            return k;
        }

        @Override
        public String toString() {

            String s="Maze state ";
            for (int i = 0; i < state.length ; i++) {
                s+= "," + state[i] ;
            }
            return s + "depth" + getCost();

        }

        @Override
        public double getCost() {
            return cost;
        }


        @Override
        public double heuristic() {
            // sum of manhattan distance for each one
            int dis=0;
            for (int i = 0; i <state.length-1 ; i++) {
                if (i%2 == 0){
                    double dx = goalState[i] - state[i];
                    double dy = goalState[i+1] - state[i+1];
                    dis+= (Math.abs(dx) + Math.abs(dy));

                }

            }
            return dis;


        }

        @Override
        public int compareTo(SearchNode o) {
            return (int) Math.signum(priority() - o.priority());
        }

        @Override
        public double priority() {
            return heuristic() + getCost();
        }

    }

    public static void main(String[] args) {
        Maze maze = Maze.readFromFile("simple.maz");
        int[] startState = {0,1,1,1,0,6,1};
        int[] goalState = {6,0,6,6,6,1 ,2};
//        System.out.println("In main  multi");

        MultiRobotProblem mazeProblem = new MultiRobotProblem(maze,startState, goalState );


        List<SearchNode> astarPath = mazeProblem.astarSearch();
		System.out.println(astarPath);

		mazeProblem.printStats();





    }

}
