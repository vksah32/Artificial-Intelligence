import java.util.ArrayList;
import java.util.Random;


public class SensorProblem {
    private  ProbabilisticModel model;
    private double[][] probabilityMap;
    private Robot robot;
    int[][] moves = {Maze.NORTH,Maze.EAST,Maze.SOUTH, Maze.WEST};
    public static String[] COLORS = {"blue","red","green","yellow"};
    Random random = new Random();
    private Maze maze;

    public SensorProblem(int startX, int startY, String mapFile){
        maze = Maze.readFromFile(mapFile);
        robot = new Robot(maze,startX, startY);
        probabilityMap = new double[maze.height][maze.width];
        //populate the probabilitymap
        for (int i = 0; i < maze.height; i++) {
            for (int j = 0; j < maze.width; j++) {
                if(maze.isLegal(j,i)) {
                    probabilityMap[i][j] = 1.0 / maze.getTotalEmptyCells();//);//(maze.height * maze.width);
                }else {
                    probabilityMap[i][j] = 0;
                }
            }
        }
        model = new ProbabilisticModel(maze, probabilityMap);
        printMap();//print initial distribution
    }


    /**
     * starts the algorithm which keeps updating the pMap on each iteration
     * @param maxIterations
     */
    public void start(int maxIterations){
        int count = 0;
        doMove(false);
        int correctPositionCounter = 0;
        while(count < maxIterations){
            doMove(true);
            count++;
            if(isCorrectPos(robot.pos[0],robot.pos[1])){
                System.out.println("correct position detected");
                correctPositionCounter++;
            }
        }
        System.out.println("the accuracy is " + ((correctPositionCounter*1.0/maxIterations)*100) + "%");

    }

    /**
     * determines if the current location has the highest probaility
     * @param x
     * @param y
     * @return
     */
    public boolean isCorrectPos(int x,int y){
        int mostProbableCoordinate[] = {0,0};
        double highestProb = 0.0;
        for (int i = 0; i < maze.height; i++) {
            for (int j = 0; j < maze.width; j++) {
                if(probabilityMap[i][j] > highestProb){
                    highestProb = probabilityMap[i][j];
                    mostProbableCoordinate[0] = j;
                    mostProbableCoordinate[1] = i;

                //if equal, change if the previous max location is not the right one
                }else if (probabilityMap[i][j] == highestProb && (mostProbableCoordinate[0] != x || mostProbableCoordinate[1] != y )){
                    highestProb = probabilityMap[i][j];
                    mostProbableCoordinate[0] = j;
                    mostProbableCoordinate[1] = i;
                }
            }
        }
        return (mostProbableCoordinate[0] == x && mostProbableCoordinate[1] == y);

    }

    /**
     * main function which generates an action and moves the robot. It then updates the pMap
     * @param move
     */
    public void doMove(boolean move){
        int[] action = {0,0};
        if (move) {
            action = getNextMove();
        }
        robot.move(action);
        System.out.println("Robot is at x:" + robot.pos[0] + "Robot is at y:" + robot.pos[1]);
        int color = robot.getSensorInput();
        int[] currentState = {action[0],action[1],color};
        robot.addToHistory(currentState);
        System.out.println("color sensed is:" + COLORS[color]);
        String Actualcolor =  COLORS[Character.getNumericValue(maze.getChar(robot.pos[0],robot.pos[1]))];
        System.out.println("actual color  is:" + Actualcolor + " ");
        doFilter(robot.getLastTwoStates());
        model.setpMap(probabilityMap);
        printMap();
    }

    /**
     * returns next move
     * @return
     */
    public int[] getNextMove(){
        return moves[random.nextInt(4)];
    }

    /**
     * filtering algorithm on pmap
     * @param prevStates
     */
    public void doFilter(ArrayList<int[]> prevStates){
        for (int j = 0; j < maze.height ; j++) {
            for (int i = 0; i <maze.width ; i++) {
                probabilityMap[j][i] = model.doFilterPosition(i,j,prevStates,2);
            }
        }
        normalizepMap();
    }

    /**
     * print the map
     */
    public void printMap(){
        for (int j = maze.height-1; j >= 0 ; j--) {
            System.out.println("\n+------------------------------+");
            for (int i = 0; i < maze.width; i++) {
                System.out.print("|");
                if(!maze.isLegal(i,j)){
                    System.out.print("######");
                }else{
                    System.out.print(String.format( "%.4f", probabilityMap[j][i] ));
                }
                System.out.print("|");



            }


        }
        System.out.println("\n+------------------------------+\n");

    }

    /**
     * normalize the map
     */
    public void normalizepMap(){
        double totalProb = 0;
        for (int j = 0; j < maze.height; j++) {
            for (int i = 0; i < maze.width; i++) {
                totalProb+= probabilityMap[j][i];
            }
        }

        for (int j = 0; j < maze.height; j++) {
            for (int i = 0; i < maze.width; i++) {
                probabilityMap[j][i] *= (1/totalProb);
            }
        }
    }

    /**
     * main function
     * @param args
     */
    public static void main(String[] args) {
        SensorProblem sp = new SensorProblem(0,0,"simple2.maz");
        sp.start(1000);

    }
}
