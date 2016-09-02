package csp_solver;
/**
 * CircuitProblem.java
 * Author: Vivek Sah
 * Date: 20th feb 2016
 */

import java.lang.reflect.Array;
import java.util.*;

import javafx.util.Pair;

public class CircuitBoard {
    private ConstraintSatisfactionProblem solver = new ConstraintSatisfactionProblem();
    private ArrayList<Chip> chips;
    private static int numBoardRows;//num of rows of board
    private static int numBoardColumns; //num of columns of board

    @SuppressWarnings({ "restriction", "boxing" })
    /**
     * Initialize circuitboard problem
     * @chips  arraylist of components
     * @board  dimensions of board
     *
     */
    public CircuitBoard(ArrayList<Chip> chips, int[] board) {
        this.chips = chips;
        this.numBoardColumns = board[0];
        this.numBoardRows = board[1];

        //create domain for each chip and add variable
        for (int k = 1; k <= chips.size(); k++ ) {
            Set<Integer> domain = new HashSet<>();
            for (int i = 1; i <= numBoardColumns; i++) {
                for (int j = 1; j <= numBoardRows; j++) {
                    if (i <= (numBoardColumns+1) - chips.get(k - 1).width && j <= (numBoardRows+1) - chips.get(k - 1).height) {
                        domain.add( i+numBoardColumns*(j-1)); //formula to convert coordinates to integer
                    }
                }
            }
            solver.addVariable(k, domain);
        }

        //now add constraint
        System.out.println("before constrainning\n" + chips);
        for (int i = 1; i <= chips.size() - 1; ++i) {
            for (int j = i + 1; j <= chips.size(); ++j) {
                Set<Pair<Integer, Integer>> constraint = new HashSet<>();
                for (int dom1: solver.getVariables().get(i)){
                    for(int dom2: solver.getVariables().get(j)){
                        int x1 = dom1%(numBoardColumns) == 0 ? numBoardColumns :dom1%(numBoardColumns)  ;
                        int y1 = (dom1/numBoardColumns)+1;
                        int x2 = dom2%(numBoardColumns) == 0 ? numBoardColumns :dom2%(numBoardColumns);
                        int y2 = (dom2/numBoardColumns)+1;

                        //constraints check consitions
                        if( ((x2>(x1+chips.get(i-1).width-1))  ||  (x2<(x1-chips.get(j-1).width+1)))){
                            constraint.add(new Pair<>(x1+numBoardColumns*(y1-1),x2+numBoardColumns*(y2-1) ));

                        } else if ((y2>(y1+chips.get(i-1).height-1))  ||  (y2<(y1-chips.get(j-1).height+1))){
                            constraint.add(new Pair<>(x1+numBoardColumns*(y1-1),x2+numBoardColumns*(y2-1) ));
                        }

                    }
                }
                solver.addConstraint(i,j,constraint);
            }

        }
    }


    @SuppressWarnings("boxing")
    public int[] solve() {
        Map<Integer, Integer> solution = solver.solve();
        if (solution == null)
            return null;
        int[] result = new int[chips.size()];
        for (int i = 1; i <= chips.size(); ++i)
            result[i - 1] = solution.get(i);
        return result;
    }
    public static void printSolution(int[] solution, ArrayList<Chip> chips, int[] dimension){
        int[][] result = new int[dimension[1]][dimension[0]];

        for (int i = 0; i < solution.length; i++) {
            int x1 = solution[i]%(numBoardColumns) == 0 ? numBoardColumns : solution[i]%(numBoardColumns) ;
            int y1 = (solution[i]/numBoardColumns)+1;
            for (int j = 0; j < chips.get(i).width ; j++) {
                for(int k = 0; k < chips.get(i).height; k++ )
                    result[y1-1+k][x1-1+j] = i+1;
            }
        }
        for (int i = result.length-1; i >= 0 ; i--) {
            System.out.println(Arrays.toString(result[i]));
        }
    }

    public static final void main(String[] args) {

        ArrayList<Chip> chipsToSolve = new ArrayList<>();
        //test case 1
//        String[] chipsString = {"bbbbb","cc", "aaa","bbbbb","cc","cc" ,"eeeeeee","aaa"};
//        int[] heightArray = {1,1,1,1,1,1,1,1};

        //test case 2
        String[] chipsString = {"ccc","bb", "aa","ddd"};
        int[] heightArray = {2,2,1,2};

        for (int i = 0; i < chipsString.length ; i++) {
            chipsToSolve.add(new Chip(chipsString[i].length(),heightArray[i])); //create and add chips

        }
//        int[] boardDimension = {10,3};
        int[] boardDimension = {5,4}; //board dimensions
        int[] solution = new CircuitBoard(chipsToSolve, boardDimension).solve();
        if(solution != null)
            System.out.println("Solution is:");
            printSolution(solution, chipsToSolve,boardDimension);

    }


    /*
        private class chip, stores width and height of component
     */
    public static class Chip{
        int width;
        int height;
        public Chip(int width, int height){
            this.width = width;
            this.height = height;
        }
        public String toString(){
            return "Width is " + this.width + " this.height " + this.height;
        }
    }





}
