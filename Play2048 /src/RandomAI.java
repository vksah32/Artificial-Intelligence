/**
 * File: RandomAI.java
 * Author: Vivek Sah
 * Date: March 8, 2016
 * Description: Provides random moves to the game
 */
public class RandomAI extends MoveMakerAI{

    public GamePlay.Move getNextMove(Board2048 p){
        System.out.println("returning here");
        return moves[random.nextInt(4)];
    }



}
