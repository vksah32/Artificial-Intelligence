/**
 * This algorithm is from the following paper
 * Steven M. LaValle and James  Kuffner Jr.,
 * Randomized Kinodynamic Planning, 
 * The International Journal of Robotics Research 20 (5), pp. 378–400.
 * http://dx.doi.org/10.1177/02783640122067453
 */

package assignment_motion_planning;

import java.util.*;

import com.sun.javafx.geom.Edge;
import javafx.geometry.Point2D;
import javafx.util.Pair;

public class RRTPlanner extends MotionPlanner {
    private static final double DEFAULT_DELTA = 0.2;  // Duration for the control
    Map<Vector, MyEdge> parents;
    int rateofMapGrow = 1;
    /**
     * Constructor 
     * @param environment the workspace
     * @param robot       the robot
     */
    public RRTPlanner(Environment environment, Robot robot) {

        super(environment, robot);
        parents = new HashMap<>();

    }
    
    @Override
    public List<Pair<Point2D, Point2D>> getEdges() {
        // YOU WILL WRITE THIS METHOD
        List<Pair<Point2D,Point2D>> edges = new ArrayList<>();
        for(MyEdge currentEdge: parents.values() ){
            if (!currentEdge.getChildConfig().equals(getStart())) {
                Point2D child = new Point2D(currentEdge.getChildConfig().get(0), currentEdge.getChildConfig().get(1));
                Point2D parent = new Point2D(currentEdge.getParent().get(0), currentEdge.getParent().get(1));
                edges.add(new Pair<>(child, parent));
            }
        }

        return edges;
    }
    
    @Override
    public int getSize() {
        // YOU WILL WRITE THIS METHOD
        return parents.keySet().size();
    }

    @Override
    protected void setup() {
        parents.put(getStart(), new MyEdge(getStart(),null,null, 0) );
//        parents.put(getGoal());
        // YOU WILL WRITE THIS METHOD
    }
    
    @Override
    protected void growMap(int K) {
        while (getSize() < K){
            Vector randomConfig = getRobot().getRandomConfiguration(getEnvironment(),this.random);
            if (randomConfig != null) {
                List<Vector> qnears = nearestKNeighbors(parents.keySet(), randomConfig,rateofMapGrow);
                for (Vector qnear: qnears){
                     newConf(qnear, DEFAULT_DELTA);

                }
            }
        }
        System.out.println("Size after growing map :" + parents.keySet().size());
    }

    /**
     * Generate a new configuration from a configuration and insert it
     * @param qnear    the beginning configuration of the random motion
     * @param duration the duration of the random motion
     * @return true if one new configuration is inserted, and false otherwise
     */
    @SuppressWarnings("boxing")
    private boolean newConf(Vector qnear, double duration) {
        Vector randomControl = this.getRobot().getRandomControl(this.random);
        Vector newConfig = this.getRobot().move(qnear, randomControl,duration);
        Trajectory t = new Trajectory(randomControl, duration);
        if(this.getEnvironment().isValidMotion(this.getRobot(),qnear,t,this.RESOLUTION)){ //check if valiud
           if(!parents.containsKey(newConfig)){ //if already present, dont add
//               System.out.println(newConfig);
               parents.put(newConfig,new MyEdge(newConfig,qnear, randomControl, duration));
               return true;
           }
        }

        return false;


    }
    
    @SuppressWarnings("boxing")
    @Override
    protected Trajectory findPath() {
        Vector qnear = nearestNeighbor(parents.keySet(),getGoal());
        Trajectory path = new Trajectory();
        ArrayList<MyEdge> pathNodes = new ArrayList<>(); //make a new arraylist to hold the nodes
        for (Vector current = qnear; current != getStart(); current = parents.get(current).getParent()) { //do backtracking
            pathNodes.add(parents.get(current));
        }
        //add the constrols.durations to the trajectory in reverse order so that the trajectory is from start to goal
        for (int i = pathNodes.size()-1; i >= 0 ; i--) {
            path.addControl(pathNodes.get(i).getControl(), pathNodes.get(i).getDuration());

        }
        return path;
    }

    @Override
    protected void reset() {
        parents.clear();
        // YOU WILL WRITE THIS METHOD
    }

}