package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;
import java.util.Random;



public class MiniMaxAI implements ChessAI {

    int startingPlayer; //store the current player
    int MAX_DEPTH =2;
    int visitedNodes;
    int maxDepthReached;
    Random random = new Random();

    //get move
    public short getMove(Position position) {
        visitedNodes=0;
        maxDepthReached=0;
        startingPlayer = position.getToPlay();
        long startTime = System.currentTimeMillis();//track time
        short bestMove = minimax(position,MAX_DEPTH);
//        short bestMOve =  iterativeMinimax(position);
        System.out.println("time taken:" + (System.currentTimeMillis()- startTime) + "ms  visitedNodes: "
                + visitedNodes + "  maxDepthReached:" + maxDepthReached);
        return bestMove;
    }

    //do iterative minimax
    public short iterativeMinimax(Position position){
        short bestMove = Move.NO_MOVE;
        int maxVal = Integer.MIN_VALUE;
        System.out.println("started iteratuon");
        for (int i = 0; i <= MAX_DEPTH; i++) {
            System.out.println("iterating");
            bestMove = minimax(position, i);

        }
        return bestMove;
    }



    /**
     * uses minimax algorithm to find the best move
     * @param position: currentPosition
     * @return move
     */
    public short minimax(Position position, int maxDepth)  {
        int bestMoveValue = Integer.MIN_VALUE;
        short bestMove = Move.NO_MOVE;
        for (short move : position.getAllMoves()){ //loop through the moves and get the best move
            Position newPosition= new Position(position); //make a copy of position
            try {
                newPosition.doMove(move); //try to do move to get new position
            } catch (IllegalMoveException e) {
                e.printStackTrace();
            }
            int val =minValue(newPosition,0,maxDepth);
            if(bestMove==(Move.NO_MOVE)){
                bestMove =move;
                bestMoveValue = val;
            }
            if(val > bestMoveValue){
                bestMoveValue = val;
                bestMove =  move;
            }
        }
        return bestMove;
    }

    /**
     * returns the minValue
     * @param position currentposition
     * @param Alpha alpha value
     * @param Beta beta value
     * @param currentDepth
     * @param maxDepth
     * @return
     */
    public int minValue(Position position, int currentDepth, int maxDepth)  {
        visitedNodes++;
        //cut-off tests
        if(position.isStaleMate()){
            maxDepthReached=currentDepth;
            return 0;
        }
        if(position.isTerminal()){
            maxDepthReached=currentDepth;
            return Integer.MAX_VALUE;
        }
        if(currentDepth >= maxDepth){
            maxDepthReached=currentDepth;
            return getUtility(position);
        }

        //loop through the moves and get the best move by storing the values of position
        int val = Integer.MAX_VALUE;
        for (short move: position.getAllMoves()){
            Position tempPosition = new Position(position);
            try {
                tempPosition.doMove(move);

            } catch (IllegalMoveException e) {
                e.printStackTrace();
                continue;
            }
            val = Math.min(val,maxValue(tempPosition, currentDepth+1, maxDepth));
        }
        return val;

    }


    /**
     * returns the maxValue
     * @param position currentposition
     * @param Alpha alpha value
     * @param Beta beta value
     * @param currentDepth
     * @param maxDepth
     * @return
     */
    public int maxValue(Position position, int currentDepth, int maxDepth)  {
//        System.out.println("running max at depth " + node.getDepth());
        visitedNodes++;
        if(position.isStaleMate()){
            maxDepthReached=currentDepth;
            return 0;
        }
        if(position.isTerminal()){
            maxDepthReached=currentDepth;
            return Integer.MIN_VALUE;
        }
        if(currentDepth >= maxDepth){
            maxDepthReached=currentDepth;
            return getUtility(position);
        }

        int val = Integer.MIN_VALUE;
        for (short move: position.getAllMoves()){
            Position tempPosition = new Position(position);
            try {
                tempPosition.doMove(move);
            } catch (IllegalMoveException e) {
                e.printStackTrace();
                continue;
            }
            val = Math.max(val,minValue(tempPosition,currentDepth+1, maxDepth));
        }
        return val;

    }

    private int getUtility(Position position) {
        return eval(position);
//        return random_eval(position);
    }


    /**
     * return evaluation of a position
     * @param position
     * @return evaluation based getMaterial and getDomination
     */
    private int eval(Position position) {
        int val = position.getMaterial() + (int)position.getDomination();
        if (startingPlayer == position.getToPlay()) //check if its the starting player turn when eval was called
            return val;
        else //otherwise return negative
            return -1*val;
    }

    private int random_eval(Position position){
        return random.nextInt();
    }


}