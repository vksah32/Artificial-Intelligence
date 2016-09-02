
/**
 * File: Board2048.java
 * Author: Vivek Sah
 * Date: March 8, 2016
 * Description: Innitializes the board and handles all the game moves.
 */


import java.util.ArrayList;
import java.util.Random;

public class Board2048 {
    private Cell[] board; //the puzzle grid
    Random random = new Random();
    private boolean playerWins = false;
    private boolean playerLoses = false;
    private int score; //keep scores
//    private int highestScoreIndex = 0;

    /**
     * constructor for the board
     */
    public Board2048(){
        board = new Cell[16];
        score = 0;
        resetGrid();
        addToGrid();
        addToGrid();
    }

    /**
     * constructor given another Board, clones the given and initializes a different Board2048 class
     * @param newBoard
     */
    public Board2048(Board2048 newBoard){
        board = new Cell[16];
        //copy all the cell values
        for (int i = 0; i < 16 ; i++) {
            Cell cell = new Cell(i);
            cell.setValue(newBoard.getCell(i).getValue());
            board[i] = cell;
        }
        this.score = newBoard.score;
    }

    /**
     * sets all the cells to 0
     */
    public void resetGrid(){
        for (int i = 0; i < 16 ; i++) {
            Cell cell = new Cell(i);
            board[i] = cell;
        }
    }

    public boolean playerWon(){
        return playerWins;
    }

    public int getScore(){
        return score;
    }

    public boolean playerLost(){
        return playerLoses;
    }

    /**
     * assign a random unassigned cell a vale either 2 or 4
     */
    public void addToGrid(){
        ArrayList<Cell> emptycells = getEmptyCells();
        if(emptycells.size() == 0){
            playerLoses = true;
            return;
        }
        int i = random.nextInt(emptycells.size());
        int k = (random.nextFloat() < 0.9) ? 2 : 4;
        emptycells.get(i).setValue(k);
    }

    //add a given value to a given index in the cell
    public void addToGrid(int index, int value){
        this.board[index].setValue(value);
    }

    /**
     * gives a list of all unassigned cells
     * @return
     */
    public ArrayList<Cell> getEmptyCells(){
        ArrayList<Cell> emptycells = new ArrayList<>();
        for (int i = 0; i < 16 ; i++) {
            if(board[i].getValue() == 0){
                emptycells.add(board[i]);
            }
        }
        return emptycells;
    }

    /**
     * ascii representation of board
     */
    public void printBoard(){
//        System.out.print("|");
        String s = "+---+----+---+-----+\n";
        String tempString = "";
        for (int i = 0; i < 16 ; i++) {
            tempString+=board[i];
            if((i+1)%4==0 && i> 0){
                System.out.println(s+tempString);
                tempString="";
            }
        }
        System.out.println(s);
    }

    public Cell[] getBoard(){
        return this.board;
    }

    public Cell getCell(int index){
        return this.board[index];
    }
    public void setCellValue(int index,int value){
        this.board[index].setValue(value);
    }

    /**
     * whther the game is finished
     * @return
     */
    public boolean isFinished(){
        //check to see if any moves can be made
        Board2048 g1 = new Board2048(this);
        Board2048 g2 = new Board2048(this);
        Board2048 g3 = new Board2048(this);
        Board2048 g4 = new Board2048(this);
        int n1 = g1.apply(GamePlay.Move.LEFT);
        int n2 = g2.apply(GamePlay.Move.RIGHT);
        int n3 = g3.apply(GamePlay.Move.UP);
        int n4 = g4.apply(GamePlay.Move.DOWN);
        //if no moves are available, player loses
        if(n1== 0 && n2 == 0 && n3 == 0 && n4 == 0){
            playerLoses = true;
        }

        //game finishes if player wins or loses
        return (playerWins ) || (playerLoses);
    }

    /**
     * apply a move to the board
     * @param m move
     */
    public int apply(GamePlay.Move m){
        if(m == GamePlay.Move.NO_MOVE){
            return 0;
        }
        if(m == GamePlay.Move.LEFT){
            return goLeft();
        } else if(m == GamePlay.Move.RIGHT){
            return goRight();
        } else if (m == GamePlay.Move.UP){
            return goUp();
        } else {
            return goDown();
        }

    }

    /**
     * apply left move
     */
    public int goLeft(){
        ArrayList<Integer> mergedcells = new ArrayList<>(); //keep track of merged cells indices, so that we can set them to unmerge
        //for the next move
        int numMoved = 0; //keep track of how many moved
        int tempInd1[] = { 1,5,9,13};//second leftmost cells(second column from the left)
        for (int i = 0; i < 4 ; i++) { //loop through each cell in that column
            for (int k = 0; k <3 ; k++) { //loop through each cell to the right of this cell

                    if(board[tempInd1[i] + k].getValue() == 0){ //don;t bother about the empty cells
                        continue;
                    }
                    int p = 1; //counter to keep track of how many cells we are searching to the left of current cell

                    //increase p until we hit the wall or find a non-empty cell
                    while((tempInd1[i] + k - p) > (tempInd1[i]-1)  && board[tempInd1[i] + k - p].getValue() == 0  ){
                        p++;
                    }

                    //if we find something, merge if allowed
                    //only merge if values are same and that cell didnt go through merge already in this move
                    if (board[tempInd1[i] + k - p].getValue() == board[tempInd1[i] + k].getValue() && !(board[tempInd1[i] + k - p].merged)) {
                        int newValue = board[tempInd1[i] + k - p].getValue() + board[tempInd1[i] + k].getValue();
                        if(board[tempInd1[i] + k - p].getValue() == board[tempInd1[i] + k].getValue()){
                            score+= newValue;
                        }
                        board[tempInd1[i] + k - p].setValue(newValue); //update the value
                        board[tempInd1[i] + k].setValue(0);
                        numMoved++;
                        board[tempInd1[i] + k - p].merged = true; //set that cell to mergedStateus so that no other cell can merge
                        //to this cell in the same move
                        mergedcells.add((tempInd1[i] + k - p));

                        if (newValue == 2048){
                            playerWins = true;
                        }
                    //if we hit the wall, drag the current cell all the way to the left
                    } else if(board[tempInd1[i] + k - p].getValue() == 0) {
                        board[tempInd1[i] + k - p].setValue(board[tempInd1[i] + k].getValue());
                        board[tempInd1[i] + k].setValue(0);
                        numMoved++;

                        //if we hit a cell, thats non-zero but different that current cell, drag the current cell
                        //to the immediate right of that cell
                    } else{
                        //checking to see if we the non-zero cell that we found is actually at least one -block apart
                        //if it is already neighbor, then dont bother moving around
                        if(p!= 1){
                            board[tempInd1[i] + k - p+1].setValue(board[tempInd1[i] + k].getValue());

                            board[tempInd1[i] + k].setValue(0);
                            numMoved++;
                        }

                    }
                }
            }
        //set all the mergedCells to unmerged status so that they are free to merge in the next move
        for(int i: mergedcells){
            board[i].merged = false;
        }
        return numMoved;
    }

    /**
     * apply right move
     */
    public int goRight(){
//        System.out.println("to the right");
        ArrayList<Integer> mergedcells = new ArrayList<>();
        int numMoved = 0;
        int tempInd1[] = { 2,6,10,14};
        for (int i = 0; i < 4 ; i++) {
            for (int k = 0; k <3 ; k++) {
                if(board[tempInd1[i] - k].getValue() == 0){
                    continue;
                }
                int p = 1;
                while((tempInd1[i] - k + p) < (tempInd1[i]+1) && board[tempInd1[i] - k + p].getValue() == 0){
                    p++;
                }
                if (board[tempInd1[i] - k + p].getValue() == board[tempInd1[i] - k].getValue() && !board[tempInd1[i] - k + p].merged) {
                    int newValue = board[tempInd1[i] - k + p].getValue() + board[tempInd1[i] - k].getValue();
                    if(board[tempInd1[i] - k + p].getValue() == board[tempInd1[i] - k].getValue()){
                        score+= newValue;
                    }
                    board[tempInd1[i] - k + p].setValue(newValue);
                    board[tempInd1[i] - k].setValue(0);
                    numMoved++;
                    board[tempInd1[i] - k + p].merged = true;
                    mergedcells.add(tempInd1[i] - k + p);

                    if (newValue == 2048){
                        playerWins = true;
                    }
                } else if (board[tempInd1[i] - k + p].getValue() == 0) {
                    board[tempInd1[i] - k + p].setValue(board[tempInd1[i] - k].getValue());

                    board[tempInd1[i] - k].setValue(0);
                    numMoved++;
                } else{
                    if(p!= 1){
                        board[tempInd1[i] - k + p-1].setValue(board[tempInd1[i] - k].getValue());

                        board[tempInd1[i] - k].setValue(0);
                        numMoved++;
                    }
                }
            }
        }
        for(int i: mergedcells){
            board[i].merged = false;
        }
        return numMoved;
    }

    /**
     * apply up move
     */
    public int goUp(){
        ArrayList<Integer> mergedcells = new ArrayList<>();
        int numMoved = 0;
        int tempInd1[] = { 4,5,6,7};
        for (int i = 0; i < 4 ; i++) {
            for (int k = 0; k <3 ; k++) {
                if(board[tempInd1[i] + 4*k].getValue() == 0){
                    continue;
                }
                int p = 1;
                while((tempInd1[i] + 4*k - 4*p)> (tempInd1[i] -4) && board[tempInd1[i] + 4*k - 4*p].getValue() == 0){
                    p++;
                }
                if (board[tempInd1[i] + 4*k -4* p].getValue() == board[tempInd1[i] + 4*k].getValue() && !(board[tempInd1[i] + 4*k -4* p].merged)) {
                    int newValue = board[tempInd1[i] + 4*k -4* p].getValue() + board[tempInd1[i] + 4*k].getValue();
                    if(board[tempInd1[i] + 4*k -4* p].getValue() == board[tempInd1[i] + 4*k].getValue()){
                        score+= newValue;
                    }
                    board[tempInd1[i] + 4*k -4* p].setValue(newValue);
                    board[tempInd1[i] + 4*k].setValue(0);
                    numMoved++;
                    board[tempInd1[i] + 4*k -4* p].merged = true;
                    mergedcells.add((tempInd1[i] + 4*k -4* p));

                    if (newValue == 2048){
                        playerWins = true;
                    }
                } else if (board[tempInd1[i] + 4*k -4* p].getValue() == 0) {
                    board[tempInd1[i] + 4*k -4*p].setValue(board[tempInd1[i] + 4*k].getValue());

                    board[tempInd1[i] + 4*k].setValue(0);
                    numMoved++;
                }else  {
                    if(p!= 1){
                        board[tempInd1[i] + 4*k -4*p+4].setValue(board[tempInd1[i] + 4*k].getValue());

                        board[tempInd1[i] + 4*k].setValue(0);
                        numMoved++;
                    }
                }
            }
        }
        for(int i: mergedcells){
            board[i].merged = false;
        }
        return numMoved;
    }
    /**
     * apply down move
     */
    public int goDown(){
        ArrayList<Integer> mergedcells = new ArrayList<>();
        int numMoved = 0;
        int tempInd1[] = { 8,9,10,11};
        for (int i = 0; i < 4 ; i++) {
            for (int k = 0; k <3 ; k++) {

                if(board[tempInd1[i] - 4*k].getValue() == 0){
                    continue;
                }
                int p = 1;
                while((tempInd1[i] - 4*k + 4*p < tempInd1[i]+4) && board[tempInd1[i] - 4*k + 4*p].getValue() == 0){
                    p++;
                }
                if (board[tempInd1[i] - 4*k +4*p].getValue() == board[tempInd1[i] - 4*k].getValue() && !(board[tempInd1[i] - 4*k + 4*p].merged)) {
                    int newValue = board[tempInd1[i] - 4*k +4*p].getValue() + board[tempInd1[i] - 4*k].getValue();
                    if(board[tempInd1[i] - 4*k + 4*p].getValue() == board[tempInd1[i] - 4*k].getValue()){
                        score+= newValue;
                    }
                    board[tempInd1[i] - 4*k +4*p].setValue(newValue);
                    board[tempInd1[i] - 4*k].setValue(0);
                    numMoved++;
                    board[tempInd1[i] - 4*k +4*p].merged = true;
                    mergedcells.add((tempInd1[i] - 4*k + 4*p));

                    if (newValue == 2048){
                        playerWins = true;
                    }
                } else if (board[tempInd1[i] - 4*k + 4*p].getValue() == 0) {
                    board[tempInd1[i] - 4*k +4*p].setValue(board[tempInd1[i] - 4*k].getValue());
                    board[tempInd1[i] - 4*k].setValue(0);
                    numMoved++;
                }  else{
                    if(p!= 1){
                        board[tempInd1[i] - 4*k +4*p-4].setValue(board[tempInd1[i] - 4*k].getValue());
                        board[tempInd1[i] - 4*k].setValue(0);
                        numMoved++;
                    }

                }
            }
        }
        for(int i: mergedcells){
            board[i].merged = false;
        }
        return numMoved;
    }
    /**
     * representation of cell
     */
    public class Cell {
        private int x;
        private int y;
        private int val;
        private boolean merged = false;
        private int BoardIndex;
        public Cell(int index){
            BoardIndex = index;
            x = index%4;
            y = index/4;
            val = 0;
        }

        public void setValue(int val){
            this.val = val;
        }

        public int getValue(){
            return val;
        }
        public int getBoardIndex(){
            return BoardIndex;
        }

        public String toString(){
            String s = "";
            s+= (val == 0) ? "    " : (String.format( "%4d",val));
            s+= "|";
//            if(x!= 0 && x%3 == 0){
//                s+="\n|";
//            }
            return  s;
        }

    }

    //test code
    public static void main(String[] args) {
        Board2048 game = new Board2048();
        game.addToGrid(0,4);
        game.addToGrid(4,2);
        game.addToGrid(8,2);
        game.addToGrid(12,2);
        game.printBoard();
        game.apply(GamePlay.Move.DOWN);

        System.out.println("After move");
        game.printBoard();
    }
}
