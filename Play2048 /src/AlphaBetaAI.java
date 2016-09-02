/**
 * File: AlphaBetaAI.java
 * Author: Vivek Sah
 * Date: March 8, 2016
 * Description: Implements the alpha-beta search AI in the context of 2048 game
 * Note: Includes snippets from open-source code from Vasilis Vryniotis which is under GPL v3 license
 */

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AlphaBetaAI extends MoveMakerAI {
    int MAX_DEPTH = 7;
    int visitedNodes;
    int maxDepthReached;

    /**
     * gets the next best move
     * @param board current board
     * @return next move
     */
    public GamePlay.Move getNextMove(Board2048 board) {
        visitedNodes = 0;
        maxDepthReached = 0;
        long startTime = System.currentTimeMillis();
        //invoke alphabeta
        HashMap<String, Object> result = alphaBeta(board, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_DEPTH); //get the most best move from the alphaBeta algorithm
        GamePlay.Move move = (GamePlay.Move)result.get("move");
        System.out.println("time taken:" + (System.currentTimeMillis() - startTime) + "ms  visitedNodes: "
                + visitedNodes + "  maxDepthReached:" + maxDepthReached);
        return move;
    }

    //start with the player's move
    public HashMap<String, Object> alphaBeta(Board2048 board, int alpha, int beta, int maxDepth) {
        return minValue(board,  alpha,  beta, 0, maxDepth);
    }


    /**
     * represents computers move
     * @param board current board
     * @param alpha
     * @param beta
     * @param currentDepth
     * @param maxDepth
     * @return hashmap containing value and the associated move(since computer doesnt have actual move, it just returns NO Move)
     */
    public HashMap<String, Object>  maxValue(Board2048 board, int alpha, int beta, int currentDepth, int maxDepth) {
        GamePlay.Move bestMove = null;
        int bestScore;
        HashMap<String,Object> result = new HashMap<>();

        //check for terminal states
        if (board.isFinished()) {
            if (board.playerWon()) {
                bestScore = Integer.MAX_VALUE; //highest possible score
            } else {
                bestScore = Math.min(board.getScore(), 1); //lowest possible score
            }
        } else if (currentDepth >= maxDepth) {
            maxDepthReached = currentDepth;
            bestScore = getUtility(board);
        } else {
            List<Board2048.Cell> emptycells = board.getEmptyCells();//get empty cells
            int[] possibleValues = {2, 4}; //possible values to pupulate the cells with
            mainloop:
            for (Board2048.Cell emptyCell : emptycells) {
                for (int v : possibleValues) {
                    Board2048 newboard = new Board2048(board); //make new board
                    newboard.getCell(emptyCell.getBoardIndex()).setValue(v);//set one of each possible values to each cell
                    int currentValue = (Integer) minValue(newboard, alpha, beta, currentDepth + 1, maxDepth).get("value");
                    if (currentValue < beta) { //minimize best score
                        beta = currentValue;
                    }
                    if (beta <= alpha) {
                        break mainloop; //alpha cutoff
                    }
                }
            }
            bestScore = beta;
            if (emptycells.isEmpty()) {
                bestScore = 0;
            }
        }
        result.put("value", bestScore);
        result.put("move", GamePlay.Move.NO_MOVE);
        return result;
    }


    /**
     * Player's moves
     * @param board
     * @param alpha
     * @param beta
     * @param currentDepth
     * @param maxDepth
     * @return
     */
    public HashMap<String, Object>  minValue(Board2048 board, int alpha, int beta, int currentDepth, int maxDepth) {
        GamePlay.Move bestMove = null;
        int bestScore;
        HashMap<String,Object> result = new HashMap<>();
        //check for terminal cases
        if (board.isFinished()) {
            if (board.playerWon()) {
                bestScore = Integer.MAX_VALUE; //highest possible score
            } else {
                bestScore = Math.min(board.getScore(), 1); //lowest possible score
            }
        } else if (currentDepth >= maxDepth) {
            maxDepthReached = currentDepth;
            bestScore = getUtility(board);
        }else {
            //try all the moves
            for (GamePlay.Move move : moves) {
                Board2048 tempBoard = new Board2048(board);
                int moved = tempBoard.apply(move);
                if (moved == 0) {
                    continue;
                }
                int currentValue = (Integer) maxValue(tempBoard, alpha, beta, currentDepth + 1, maxDepth).get("value");

                if (currentValue > alpha) { //maximize score
                    alpha = currentValue;
                    bestMove = move;
                }

                if (beta <= alpha) {
                    break; //beta cutoff
                }
            }
            bestScore = alpha;
        }

        result.put("value", bestScore);
        result.put("move", bestMove);
        return  result;

    }


    /**
     * utility function, takes into account current score, number of empty calles, and how clustered are the larger values
     * @param board
     * @return
     */
    private int getUtility(Board2048 board) {
        int score = board.getScore();
        int numberOfEmptyCells = board.getEmptyCells().size();
        int clusteringValue = calculateClustering(board);
        int heuristicScore = (int) (score+Math.log(score)*numberOfEmptyCells -clusteringValue);
        return Math.max(heuristicScore, Math.min(score, 1));
    }

    /**
     * calculates the clustering score for the game by taking sum of average scores between a cell and its neighbors
     * @param board
     * @return
     */
    private int calculateClustering(Board2048 board){
        int possibleNeighborIndices[] = {-1, 1,-4, 4};
        int clusteringValue = 0;
        for (int i = 0; i < 16; i++) {
            int numberOfNeighbors = 0;
            int sumScoreOfNeighbors = 0;
            for(int n: possibleNeighborIndices){
                if(i+n < 0 || i+n> 15){
                    continue;
                }
                if(board.getCell(i).getValue() == 0){
                    continue;
                }
                if(board.getCell(i).getValue()>0) {
                    numberOfNeighbors++;
                    sumScoreOfNeighbors+=Math.abs(board.getCell(i).getValue()-board.getCell(i+n).getValue());
                }
            }
            if(numberOfNeighbors >= 1)
                clusteringValue+=sumScoreOfNeighbors/numberOfNeighbors;
        }
        return clusteringValue;
    }




}
