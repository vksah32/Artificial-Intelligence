import java.util.Random;

/**
 * Author:  Vivek Sah
 * date:  2/29/16.
 * file: Sensor.java
 */
public class Sensor {
    private Maze maze;
    public static int BLUE = 0;
    public static int RED = 1;
    public static int GREEN = 2;
    public static int YELLOW = 3;
//    public static String[] COLORS = {"blue","red","green","yellow"};
    public static int[] COLORS ={BLUE,RED,GREEN,YELLOW};
    public Random random;

    public Sensor(Maze m){
        this.maze = m;
        random = new Random();
    }

    public int getColor(int x, int y){
        int realColorIndex = Character.getNumericValue(maze.getChar(x,y));
        if(random.nextFloat()< 0.88){
            return  COLORS[realColorIndex];
        }else {
            int randomIndex = random.nextInt(4);
            return randomIndex == realColorIndex? COLORS[((randomIndex+1 )% 4)] : COLORS[randomIndex];
        }
    }
}
