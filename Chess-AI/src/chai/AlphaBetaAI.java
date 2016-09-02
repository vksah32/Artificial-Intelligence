package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;


public class AlphaBetaAI implements ChessAI {
    int MAX_DEPTH = 4;
    int startingPlayer;
    int visitedNodes;
    int maxDepthReached;
    short bestMoves[] = new short[MAX_DEPTH+1];
    public short getMove(Position position) {
        startingPlayer = position.getToPlay();
        visitedNodes=0;
        maxDepthReached=0;
        long startTime = System.currentTimeMillis();
        short move = iterativeAlphaBeta(position);
//        short move = alphaBeta(position, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH); //get the most best move from the alphaBeta algorithm
        System.out.println("time taken:" + (System.currentTimeMillis()- startTime) + "ms  visitedNodes: "
                + visitedNodes + "  maxDepthReached:" + maxDepthReached);
        return move;
    }

    /**
     * does the iterative alpha beta
     * @param position start position
     * @return the best move
     */
    public short iterativeAlphaBeta(Position position){
        short bestMove = Move.NO_MOVE; //initialize bestmove to no move
        int maxVal = Integer.MIN_VALUE;

        //loop through and check bestmove for each depth
        for (int i = 0; i <= MAX_DEPTH; i++) {
            bestMove = alphaBeta(position,Integer.MIN_VALUE, Integer.MAX_VALUE, i); //best move for this depth
            bestMoves[i] = bestMove;
        }
        return bestMove;
    }

    /**
     * uses alphaBeta algorithm to find the best move
     * @param position: currentPosition
     * @return move
     */
    public short alphaBeta(Position position, int alpha, int beta, int maxDepth) {
//        ArrayList<alphaBetaNode> possibleMoveNodes = new ArrayList<>(); //create an arrayList of possible nodes.
        //Node is a private class defined later.
        int bestMoveValue = Integer.MAX_VALUE;
        short bestMove = Move.NO_MOVE;
        for (short move : position.getAllMoves()){

            Position newPosition = new Position(position);
            try {
                newPosition.doMove(move); //try to do move to get new position
            } catch (IllegalMoveException e) {
                e.printStackTrace();
                continue;
            }
            int val =minValue(newPosition,alpha, beta,0, maxDepth);
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
    public int minValue(Position position, int Alpha, int Beta, int currentDepth, int maxDepth)  {
        visitedNodes++;

        //check for cutoff
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
        int alpha =Alpha;
        int beta = Beta;
        int val = Integer.MAX_VALUE;
        for (short move: position.getAllMoves()){
            Position newPosition = new Position(position);
            try {
                newPosition.doMove(move);
            } catch (IllegalMoveException e) {
                e.printStackTrace();
                continue;
            }
            val = Math.min(val,maxValue(newPosition, alpha, beta, currentDepth+1, maxDepth));
            if (val <= alpha ){
               break;
            }
            beta= Math.min(beta,val);
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
    public int maxValue(Position position, int Alpha, int Beta, int currentDepth, int maxDepth)  {
//        System.out.println("running max at depth " + node.getDepth());
        visitedNodes++;
        int alpha =Alpha;
        int beta = Beta;

        //check for cutoff
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
            val = Math.max(val,minValue(tempPosition,alpha, beta,currentDepth+1, maxDepth ));
            if (val >= beta ){
                break;
            }
            alpha= Math.max(alpha,val);
        }
        return val;

    }

    /**
     * return utility
     * @param position
     * @return
     */
    private int getUtility(Position position) {
        return eval(position);
    }

    /**
     * return evaluation of a position
     * @param position
     * @return evaluation based getMaterial and getDomination
     */
    private int eval(Position position) {
        int val = position.getMaterial() + (int)position.getDomination();;
        if (startingPlayer == position.getToPlay()) //check if its the starting player turn when eval was called
            return val;
        else //otherwise return negative
            return -1*val;
    }

}