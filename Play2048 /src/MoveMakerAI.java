/**
 * File: MoveMakerAI.java
 * Author: Vivek Sah
 * Date: March 8, 2016
 * Description: abstract class for AIs.
 */

import java.util.Random;
public abstract class MoveMakerAI {
    public GamePlay.Move[] moves = {GamePlay.Move.LEFT, GamePlay.Move.RIGHT, GamePlay.Move.UP,GamePlay.Move.DOWN};
    private Board2048 play;
    Random random = new Random();
    public abstract GamePlay.Move getNextMove(Board2048 b);
}
