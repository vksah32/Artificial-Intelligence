package chai;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

import java.util.*;


public class FasterAlphaBetaAI implements ChessAI {
    int MAX_DEPTH = 4;
    int startingPlayer;
    int MAX_HASHMAPSIZE = 5000000;
    int visitedNodes;
    int maxDepthReached;
    short bestMoves[] = new short[MAX_DEPTH+1];
    LinkedHashMap<Integer,Integer> transpositionTable;
    public short getMove(Position position) {
        startingPlayer = position.getToPlay();
        visitedNodes=0;
        maxDepthReached=0;
        transpositionTable = new LinkedHashMap<Integer, Integer>(MAX_HASHMAPSIZE, 0.75f, true){
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > MAX_HASHMAPSIZE;
            }
        };
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
        int bestMoveValue = Integer.MAX_VALUE;
        short bestMove = Move.NO_MOVE;
        Set<Short> triedMoves = new HashSet<>();
        //first try the best move at previous depth
        Position newPosition = new Position(position);

        //first do the best move
        if (maxDepth > 0 ){
            short currentMove = bestMoves[maxDepth-1];
            try {
                newPosition.doMove(bestMoves[maxDepth-1]); //try to do move to get new position

            } catch (IllegalMoveException e) {
                e.printStackTrace();
            }
            triedMoves.add(bestMoves[maxDepth-1]);
            int val =minValue(newPosition,alpha, beta,0, maxDepth);
            if(bestMove==(Move.NO_MOVE)){
                bestMove =currentMove;
                bestMoveValue = val;
            }
            if(val > bestMoveValue){
                bestMoveValue = val;
                bestMove =  bestMoves[maxDepth-1];
            }
            newPosition.undoMove();

        }

        //then do the capturing moves
        for (short move : position.getAllCapturingMoves()){
            if(triedMoves.contains(move)){
                continue;
            }
            newPosition = new Position(position);
            try {
                newPosition.doMove(move); //try to do move to get new position

            } catch (IllegalMoveException e) {
                e.printStackTrace();
                continue;
            }
//            triedMoves.add(move);
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

        //do the other moves
        for (short move : position.getAllNonCapturingMoves()){
            if(triedMoves.contains(move)){
                continue;
            }
            newPosition = new Position(position);
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

    private int getUtility(Position position) {
        return eval(position);
    }



    private int eval(Position position) {
        //calculate hashcode
        int currentHashCValue = position.hashCode()%MAX_HASHMAPSIZE;
        if(transpositionTable.containsKey(currentHashCValue)){ //check if its in table
            return transpositionTable.get(currentHashCValue);
        }
        int val = position.getMaterial() + (int)position.getDomination(); //calculate score
        if (startingPlayer != position.getToPlay()) //check if its the starting player turn when eval was called
            val = -1*val;
        transpositionTable.put(currentHashCValue,val); //put it in the table
        return val;
    }



}