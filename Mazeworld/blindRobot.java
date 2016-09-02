package assignment_mazeworld;

import java.util.*;

/**
 * Created by vivek on 1/20/16.
 */
public class blindRobot extends InformedSearchProblem{
    private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST};
    private HashSet<int[]> actionsSet = new HashSet<int[]>();
    private LinkedHashSet<blindNode> possibles;
    private int[] goalState;
    private Maze maze;
    private ArrayList<int[]> solution;
    private int[] prevMove = {0,0}; //prev move from the blind robo
    private int[] restrictedMove= {0,0};
    public ArrayList<LinkedHashSet<blindNode>> successors = new ArrayList<LinkedHashSet<blindNode>>();
//    public  startNode;

    public blindRobot(Maze m){ //initialization step for blindrobot
        possibles= new LinkedHashSet<blindNode>(); //all possible places for the robot
        this.maze = m;
        //build possibleStartStates
        for (int i = 0; i < m.width ; i++) {
            for (int j = 0; j < m.height; j++) {
                if (maze.isLegal(i,j)){
                    blindNode n = new blindNode(i,j, 0);
                    possibles.add(n);
                }
            }
        }

        this.solution   = new ArrayList<int[]>(); //set of direction tp go the certain place
        int times= 0;
        successors.add(possibles);
        while(possibles.size() != 1 && times < 300 ){ //try to solve the maze  fpr a limited nubner of trials

            solveBlindRobot();
            successors.add(possibles);
            times++;

        }
//        System.out.println("after solving"+ possibles.size());

        if(possibles.size() == 1){
            System.out.println("FOUND SOLUTION");
            for (int[] action: solution ){
                System.out.print("Action: "+ action[0] + ":" + action[1] + "|||");
            }
            System.out.println("\n");
            startNode = (blindNode) possibles.toArray()[0];
            System.out.println("StartNode is " + startNode);


        }



    }


    public void solveBlindRobot( ){ //figures out a starting point for the blind robot
//        System.out.println("calling solve blind");

        if (possibles.size() == 1){ //base case
            return;
        }
        LinkedHashSet<blindNode> newPossiblePlaces= new LinkedHashSet<blindNode>(); //store new state
        int[] minStuckAction = null;
        int minStuckNumber = 999999999;
        int movNum = 0; //count for movement number for an action=> how many places move
        for (int[] action: actions) {
            //if the action is not the reverse of previous and not the one which resulted in no change last time
            if (!(Arrays.equals(action, getReverse(prevMove))) && !(Arrays.equals(action, restrictedMove) )) {
                int newStuckNumber = getStuckNumber(action); //for each action determine how many p;aces are going to be stuck
//                System.out.println("for action" + action[0] + ":" + action[1] + " getStuck number is " + newStuckNumber);

               //prioritize the actions which result in minum stuck positions and then the ations which result in maximum movement
                if (minStuckNumber > newStuckNumber) {
                    minStuckNumber = newStuckNumber;
                    minStuckAction = action; //action resulting in minimum stuck positions

                } else if (minStuckNumber == newStuckNumber) {
                    int tempMov = moveNumber(action);
                    if (movNum < tempMov) {
                        minStuckAction = action;
                        minStuckNumber = newStuckNumber;
                        movNum = tempMov;
                    }
                }
            }
        }

//        System.out.println(" new action is" + minStuckAction[0] + ":" + minStuckAction[1]);
        prevMove = minStuckAction;
        solution.add(minStuckAction); //add  this action to solution

        for (blindNode node: possibles) {
            int xNew = node.state[0] + minStuckAction[0];
            int yNew = node.state[1] + minStuckAction[1];

            if (maze.isLegal(xNew, yNew)) {

                blindNode newNode = new blindNode(xNew, yNew, 0);
                newPossiblePlaces.add(newNode);
            }else{
                newPossiblePlaces.add(node);
            }
        }
        if(newPossiblePlaces.size() == possibles.size()) {
            restrictedMove = minStuckAction;
        }

        possibles = newPossiblePlaces; //set new state to the real state of the system

    }

    //function to generate the number which denotes how many poostions will be stuck between walls due to an action
    private int getStuckNumber( int[] action) {
//        System.out.println("calling getStuckNumber for " + action[0] + action[1]);
        int stuckNumber = 0;
//        System.out.println(possibles.size());
        for (blindNode node: possibles){
//            System.out.println();
            int xNew = node.state[0] + action[0];
            int yNew = node.state[1] + action[1];
            if (maze.isLegal(xNew, yNew)){
                if(isStuck(xNew, yNew, action)) {
                    stuckNumber++;
//                    System.out.println("increasing stuck number for " + node.state[0] + node.state[1] + "while moving");

                }

//                stuckNumber = ( isStuck(xNew, yNew, action) ? stuckNumber+1 : stuckNumber);
            } else {
                    if(isStuck(node.state[0], node.state[1], action)) {
                        stuckNumber++;
//                        System.out.println("increasing stuck number for " + node.state[0] + node.state[1] + "while not moving");
                    }

            }



        }
        return stuckNumber;
    }


    //determines how many movements the action will create
    public int moveNumber(int[] action){
        int moveNum=0;

        for (blindNode node: possibles){
//            System.out.println();
            int xNew = node.state[0] + action[0];
            int yNew = node.state[1] + action[1];
            if (maze.isLegal(xNew, yNew)){
                    moveNum++;
            }

        }
        return moveNum;
    }

    //get reverse of an action
    public int[] getReverse(int[] action){
        int[] temp = new int[2];
        temp[0] = (action[0] != 0 ? (0-action[0]) : 0);
        temp[1] = (action[1] != 0 ? (0-action[1]) : 0);
//        System.out.println("returning reverse step for" + action[0] + action[1] + "is" + temp[0] + temp[1]);
        return temp;
    }


    //boolean isStuck tells if the node is stuck with the given prevAction
    public boolean isStuck(int x , int y, int[] prevAction){
        //we want to see that applying all other action except the action in the argument will result in invalid location
        boolean stuck = true;
        for (int[] action: actions){
            if (! Arrays.equals(action, getReverse(prevAction))){
//                System.out.println("valid action"+ action[0] + action[1]);
                int xNew = x + action[0];
                int yNew = y + action[1];
                if (maze.isLegal(xNew, yNew)){
//                    System.out.println("legal", x);
                    stuck = false;
                    break;
                }

            }
        }
        return stuck;
    }


    public class blindNode implements SearchNode {

        // location of the agent in the maze
        public int[] state;

        // how far the current node is from the start.  Not strictly required
        //  for uninformed search, but useful information for debugging,
        //  and for comparing paths
        private double cost;

        public blindNode(int x, int y, double c) {
            state = new int[2];
            this.state[0] = x;
            this.state[1] = y;

            cost = c;

        }

        public int getX() {
            return state[0];
        }

        public int getY() {
            return state[1];
        }

        public ArrayList<SearchNode> getSuccessors() {

            ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

            for (int[] action: actions) {
                int xNew = state[0] + action[0];
                int yNew = state[1] + action[1];

                //System.out.println("testing successor " + xNew + " " + yNew);

                if(maze.isLegal(xNew, yNew)) {
                    //System.out.println("legal successor found " + " " + xNew + " " + yNew);
                    SearchNode succ = new blindNode(xNew, yNew, getCost() + 1.0);
                    successors.add(succ);
                }

            }
            return successors;

        }

        @Override
        public boolean goalTest() {

            //return state[0] == xGoal && state[1] == yGoal;
            return true;
        }


        // an equality test is required so that visited sets in searches
        // can check for containment of states
        @Override
        public boolean equals(Object other) {
            return Arrays.equals(state, ((blindNode) other).state);
        }

        @Override
        public int hashCode() {
            return state[0] * 100 + state[1];
        }

        @Override
        public String toString() {
            return new String("Maze state " + state[0] + ", " + state[1] + " "
                    + " depth " + getCost());
        }

        @Override
        public double getCost() {
            return cost;
        }


        @Override
        public double heuristic() {
            // manhattan distance metric for simple maze with one agent:
//            double dx = xGoal - state[0];
//            double dy = yGoal - state[1];
//            return Math.abs(dx) + Math.abs(dy);
            return 0;
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
        blindRobot mazeProblem = new blindRobot(maze);

    }
}
