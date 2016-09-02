import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Author:  Vivek Sah
 * date:  2/29/16.
 * file: Robot.java
 */
public class Robot {
    private Maze maze;
    public int pos[]= new int[2];
    private Sensor sensor;
    public LinkedList<int[]> prevMovesAndColors;

    public Robot(Maze m, int x, int y){
        this.maze = m;
        pos[0] = x; pos[1] = y;
        this.sensor = new Sensor(this.maze);
        prevMovesAndColors = new LinkedList<>();
    }

    /**
     * moves the robot
     * @param action
     */
    public void move(int[] action){
        int newx = pos[0]+action[0];
        int newy = pos[1]+action[1];
        if(maze.isLegal(newx,newy)){
            pos[0] = newx; pos[1] = newy;
        }
    }

    /**
     * gets sensor input
     * @return
     */
    public int getSensorInput(){
        return this.sensor.getColor(pos[0],pos[1]);
    }

    /**
     * adds to the history
     * @param moveAndColor
     */
    public void addToHistory(int[] moveAndColor){
        prevMovesAndColors.addFirst(moveAndColor);
    }

    /**
     * returns the last two states
     * @return
     */
    public  ArrayList<int[]> getLastTwoStates(){
        ArrayList<int[]> prevs = new ArrayList<int[]>();
        if(prevMovesAndColors.size() == 1){
            prevs.add(prevMovesAndColors.get(0));
        } else if(prevMovesAndColors.size() > 1){
            prevs.add(prevMovesAndColors.get(0));
            prevs.add(prevMovesAndColors.get(1));
        }
        return  prevs;
    }

}
