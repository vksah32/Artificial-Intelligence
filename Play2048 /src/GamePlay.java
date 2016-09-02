/**
 * File: Gameplay.java
 * Author: Vivek Sah
 * Date: March 8, 2016
 * Description: Innitializes the game and inputs moves from MovemakerAI to teh game to keep the game running.
 */


public class GamePlay {
    public static enum Move { LEFT, RIGHT, UP, DOWN, NO_MOVE}; //possible moves
    public static void main(String[] args) {
        Board2048 game = new Board2048(); //initialize the puzzle board
        MoveMakerAI movemaker = new AlphaBetaAI(); //initialze the movemaker AI
//        MoveMakerAI movemaker = new RandomAI();

       //run the game until its finished
        while(!game.isFinished()){
//            System.out.print(String.format("\033[2J"));
            GamePlay.Move m = movemaker.getNextMove(game);
            game.apply(m);
            System.out.println("Move applied : " + m);
            game.printBoard();
            System.out.println("the score is " + game.getScore());
            System.out.println("piece added");
            game.addToGrid();
            game.printBoard();

        }
        if(game.playerWon())
            System.out.println("Player Wins");
        else
            System.out.println("Player Lost");

    }
}
