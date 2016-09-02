/**
 * This algorithm is based on the following paper:
 * Lydia E. Kavraki, Petr Svestka, Jean-Claude Latombe, and Mark H. Overmars,
 * Probabilistic roadmaps for path planning in high-dimensional configuration spaces, 
 * IEEE Transactions on Robotics and Automation 12 (4): 566–580.
 * http://dx.doi.org/10.1109/70.508439
 */
package assignment_motion_planning;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.DoubleAccumulator;

public class PRMPlanner extends MotionPlanner {
    private int numberOfAttempts = 10;
    private HashMap<Vector, HashMap<Vector, Double>> roadMapGraph;
    
    /**
     * Constructor
     * @param environment  the workspace
     * @param robot        a robot with a steering method
     */
    public PRMPlanner(Environment environment, Robot robot)
    {
        super(environment, robot);
        roadMapGraph = new HashMap<Vector, HashMap<Vector, Double>>(); //initialize a new hashmap

    }
    
    @Override
    public int getSize() {
        int nodes = roadMapGraph.size();
        int edges = 0;
        for (Vector v : roadMapGraph.keySet()){
            edges += roadMapGraph.get(v).keySet().size();//add egdes to the counter
        }
        return nodes+edges;
    }

    @Override
    protected void setup() {
        addVertex(getStart());
        addVertex(getGoal());

        // YOU WILL WRITE THIS METHOD
    }

    @Override
    protected void growMap(int K) {
        for (int i = 0; i < K ; i++) {
            Vector v = generateFreeConfiguration();
            if (v != null){
                addVertex(v);

            }
        }
        System.out.println(roadMapGraph.keySet().size());
    }
    
    /**
     * Add a free configuration to the roadmap
     * @param free  a free configuration
     */
    @SuppressWarnings("boxing")


    private void addVertex(Vector free) {
        if (! (roadMapGraph.keySet().contains(free))) { //check to see if the graph contains vertex
            List<Vector> neighbors = nearestKNeighbors(roadMapGraph.keySet(), free, kValue()); //get nearest neighbors
            roadMapGraph.put(free, new HashMap<Vector, Double>()); //make a new entry in roadmap braph
            for (Vector neighbor : neighbors) {
                if (getEnvironment().isSteerable(getRobot(), free,neighbor, this.RESOLUTION)) {
                    roadMapGraph.get(free).put(neighbor, getRobot().getMetric(free, neighbor));//put an edge between both vertices
                    roadMapGraph.get(neighbor).put(free, getRobot().getMetric(neighbor, free));
                }
            }
        }
    }

    /**
     * variation on traditional addVertex(considers distance)
     * @param free
     */
    private void addVertex2(Vector free) {
        float distance = 5;
        if (! (roadMapGraph.keySet().contains(free))) { //check to see if the graph contains vertex
//            List<Vector> neighbors = nearestKNeighbors(roadMapGraph.keySet(), free, kValue()); //get nearest neighbors
            roadMapGraph.put(free, new HashMap<Vector, Double>()); //make a new entry in roadmap braph
//            int connectedNeighbor=0;
            for (Vector neighbor :roadMapGraph.keySet() ) {
                float dist = (float) Math.sqrt(Math.pow(free.get(0) - neighbor.get(0), 2) + Math.pow(free.get(1) - neighbor.get(1), 2) );
                if (dist < distance) {
//                if(connectedNeighbor == kValue())
//                    break;
                    if (getEnvironment().isSteerable(getRobot(), free, neighbor, this.RESOLUTION)) {
                        roadMapGraph.get(free).put(neighbor, getRobot().getMetric(free, neighbor));
                        roadMapGraph.get(neighbor).put(free, getRobot().getMetric(neighbor, free));
                        //                    connectedNeighbor++;
                    }
                }


            }
        }
    }

    // second variation , gets 2K neighbors instead of K
    private void addVertex3(Vector free) {
        if (! (roadMapGraph.keySet().contains(free))) { //check to see if the graph contains vertex
            List<Vector> neighbors = nearestKNeighbors(roadMapGraph.keySet(), free, 2* kValue()); //get nearest neighbors
            roadMapGraph.put(free, new HashMap<Vector, Double>()); //make a new entry in roadmap braph
            int connectedNeighbor=0;
            for (Vector neighbor : neighbors) {
                if(connectedNeighbor == kValue())
                    break;
                if (getEnvironment().isSteerable(getRobot(), free,neighbor, this.RESOLUTION)) {
                    roadMapGraph.get(free).put(neighbor, getRobot().getMetric(free, neighbor));
                    roadMapGraph.get(neighbor).put(free, getRobot().getMetric(neighbor, free));
                    connectedNeighbor++;
                }


            }
        }
    }


    @Override
    protected void reset() {
        roadMapGraph.clear();
    }
    
    /**
     * 
     */
    @Override
    protected Trajectory query() {
        addVertex(getStart());
        addVertex(getGoal());
        return findPath();
    }
    
    /**
     * Generate a free configuration
     * @return a free configuration if possible, and null otherwise
     */
    private Vector generateFreeConfiguration() {
//        Vector v = null;
        for (int i = 0; i < numberOfAttempts; i++) {
            Vector randomConfiguration = getRobot().getRandomConfiguration(getEnvironment(),this.random);//get random config
            if (this.getEnvironment().isValidConfiguration(getRobot(), randomConfiguration)){ //check if valid
                return randomConfiguration;
            }
        }
        return null;
    }
    
    /**
     * The number of vertices for a new vertex attempts to connect.
     * The following paper describes a way to determine the value in order
     * to ensure asymptotic optimality.
     * Sertac Karaman and Emilio Frazzoli, 
     * Sampling-based Algorithms for Optimal Motion Planning, 
     * International Journal of Robotics Research, vol. 30, no.7, pp. 846-894, 2011.
     * http://dx.doi.org/10.1177/0278364911406761
     * @return number of neighbors for a new vertex attempts to connect.
     */
    private int kValue() {
        return 15; // Magic number suggested in Steven M. LaValle's "Planning Algorithms"
    }
    
    /**
     * Determine whether this edge connecting two configurations can be ignored.
     * The following paper describes a way to ignore some edges while maintaining
     * asymptotic optimality.
     * Weifu Wang, Devin J. Balkcom, and Amit Chakrabarti
     * A fast online spanner for roadmap construction
     * International Journal of Robotics Research, vol. 34, no.11, pp. 1418-1432, 2015.
     * http://dx.doi.org/10.1177/0278364915576491
     * @param u first configuration
     * @param v second configuration
     * @return true if this edge can be ignored, and false otherwise. 
     */
    private boolean safeToIgnore(Vector u, Vector v) {
        return false; // Extra credit!!
    }
    
    @Override
    protected Trajectory findPath() {
        List<Vector> path = aStar(getStart(), getGoal());
        return path != null ? convertToTrajectory(path) : null; 
    }
    
    /**
     * Convert a list of configurations to a corresponding trajectory based on the steering method
     * @param path a list of configurations
     * @return a trajectory
     */
    private Trajectory convertToTrajectory(List<Vector> path) {
        Trajectory result = new Trajectory();
        Vector previous = path.get(0);
        for (int i = 1; i < path.size(); ++i) {
            Vector next = path.get(i);
            result.append(getRobot().steer(previous, next));
            previous = next;
        }
        return result;
    }
    
    /**
     * Astar search
     * @return a path
     */
    @SuppressWarnings("boxing")
    private List<Vector> aStar(Vector start, Vector goal) {
        NavigableSet<Node> pq = new TreeSet<>();
        Map<Vector, Node> map = new HashMap<>();
        Node root = new Node(start, null, 0, getRobot().getMetric(start, goal));
        pq.add(root);
        map.put(start, root);
        while (!pq.isEmpty()) {
//            System.out.println("popping from queue");
            Node node = pq.pollFirst();
            Vector configuration = node.getConfiguration();
            double cost = node.getCost();
            if (goal.equals(configuration))
                return backChain(node);
            
            for (Vector config : getSuccessors(configuration)){
//                System.out.println("astar"+config );
                double heuristic = getRobot().getMetric(config, goal);
                double newCost = cost+roadMapGraph.get(configuration).get(config); // YOU NEED TO MODIFY THIS LINE
//                double newCost = cost+getRobot().steer(getStart(),config).totalTime();
                Node test = map.get(config);
                
                if (test != null) {
                    if (test.getPriority() > newCost + heuristic)
                        pq.remove(test);
                    else
                        continue;
                }
                Node newNode = new Node(config, node, newCost, heuristic);
                map.put(config, newNode);
                pq.add(newNode);
            }
        }
        return null;
    }
    
    /**
     * Get successors for a configuration
     * @param configuration
     * @return a collection of successors
     */
    private Collection<Vector> getSuccessors(Vector configuration) {
        // YOU WILL WRITE THIS METHOD

        return roadMapGraph.get(configuration).keySet();

    }
    
    /**
     * Backchain to construct a path
     * @param node the end node
     * @return a path
     */
    private static List<Vector> backChain(Node node) {
        LinkedList<Vector> result = new LinkedList<>();
        for (Node current = node; current != null; current = current.getParent()) {
            result.addFirst(current.getConfiguration());
        }
        return result;
    }
    
    final class Node implements Comparable<Node> {
        private Vector configuration;
        private Node parent;
        private double heuristic;
        private double cost;
        
        public Node(Vector config, Node p, double cost, double heuristic) {
            this.configuration = config;
            this.parent = p;
            this.heuristic = heuristic;
            this.cost = cost;
        }
        
        @Override
        public int compareTo(Node o) {
            int comparison = Double.compare(getPriority(), o.getPriority());
            return comparison == 0 ? configuration.compareTo(o.getConfiguration()) : comparison;
        }
        
        /**
         * Get the parent
         * @return the parent
         */
        public Node getParent(){
            return parent;
        }
        
        /**
         * Get the configuration
         * @return the configuration
         */
        public Vector getConfiguration() {
            return configuration;
        }
        
        /**
         * Get the cost
         * @return the cost
         */
        public double getCost() {
            return cost;
        }
        
        /**
         * Get the heuristic
         * @return the heuristic
         */
        public double getHeuristic() {
            return heuristic;
        }
        
        /**
         * Get the priority 
         * @return the priority
         */
        public double getPriority(){
            return getCost() + getHeuristic();
        }
    }

//    public static void main(String[] args) {
//        PRMPlanner p = new P
//    }
}
