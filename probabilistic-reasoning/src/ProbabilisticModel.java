import java.util.ArrayList;

/**
 * Created by vivek on 2/29/16.
 */
public class ProbabilisticModel {
    Maze maze;
    double[][] pMap;
    public ProbabilisticModel(Maze m, double[][] pMap){
        this.maze = m;
        this.pMap = pMap;

    }

    public double getTransitionProb( int x, int y){
        double probTransition = 0; //probability transition into that position
        double probNoTrasition = 0; //probability it stayed where it was

        //check if it could have transitioned from the four sides
        int[][] possibleTransitions = {{x,y+1},{x,y-1},{x+1,y},{x-1,y}};
        for(int[] possibleTransition: possibleTransitions){
            if(maze.isLegal(possibleTransition[0],possibleTransition[1])){
                probTransition+=0.25*pMap[possibleTransition[1]][possibleTransition[0]];
            } else {
                probNoTrasition+=0.25 * pMap[y][x];
            }
        }

        return probTransition+probNoTrasition;
    }

    public void setpMap(double[][] newPMap){
        for (int j = 0; j < newPMap.length; j++) {
            for (int i = 0; i <newPMap[0].length ; i++) {
                pMap[j][i] = newPMap[j][i];
            }
        }

    }

    /**
     * get the probability that the sensor read the right color
     * @param x
     * @param y
     * @return
     */
    public double getSensorProb(int color, int x, int y){
        int actualColor = Character.getNumericValue(maze.getChar(x,y));
        if(actualColor == color){
            return 0.88;
        } else {
            return 0.04;
        }
    }

    /**
     * returns the probability for a position after running filtering algorithm(can use Markov's 1st order and 2nd order
     * @param x
     * @param y
     * @param prevStates
     * @param order
     * @return
     */
    public double doFilterPosition(int x, int y, ArrayList<int[]> prevStates, int order){
        double prob = 0.0;
        if (order ==1){
            return getTransitionProb(x,y) * getSensorProb((prevStates.get(0)[2]),x,y);
        }else {
            if(prevStates.size() == 1){ //if first order
                return getTransitionProb(x,y) * getSensorProb((prevStates.get(0)[2]),x,y);
            } else { //if second order
                int x1; int y1;
                int col;
                x1= x-prevStates.get(0)[0];
                y1= y-prevStates.get(0)[1];
                col = prevStates.get(1)[2];
                if(maze.isLegal(x1,y1)){ //if legal, then use the second order or just use the first order
                    return getTransitionProb(x1,y1) * getSensorProb(col,x1,y1) * getTransitionProb(x,y) * getSensorProb((prevStates.get(0)[2]),x,y) ;
                }
                return getTransitionProb(x,y) * getSensorProb((prevStates.get(0)[2]),x,y);
            }
        }

    }
}
