package tilesgui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Used to store the board pieces, initialize a board, compute the board heuristic value, find possible next moves from any board position.
 * If you do use a data structure that needs a hash value, you should define that as well.
 * Unless the board is initialized by the user, the tilesgui.Board class should create a board where the destination pieces are chosen at random.
 */
public class Board {
    private ArrayList<Integer> grid = new ArrayList<>();
    private final int dim;

    /**
     * Test main function that tests constructors
     * @param args
     */
    public static void main(String[] args) {
        Board test = new Board("123456780");
        Board test2 = new Board(3);
        System.out.println(test.toString(3));
        System.out.println(test2.toString(3));
    }

    /**
     * Constructor that creates random board of size n x n
     * @param n
     */
    Board(int n) {
        dim = n;
        int size = n*n;
        ArrayList<Integer> bag = new ArrayList<>();
        Random randomGenerator = new Random(System.currentTimeMillis());

        for(int i = 0; i < size; i++) {
            bag.add(i);
        }
        while(!bag.isEmpty()) {
            int item = randomGenerator.nextInt(bag.size());
            grid.add(bag.get(item));
            bag.remove(item);
        }
    }

    /**
     * Constructor that creates board from configuration string
     * @param configuration
     */
    Board(String configuration) {
        String[] tiles = configuration.split("");
        for(String tile : tiles) {
            grid.add(Integer.parseInt(tile));
        }
        dim = (int)Math.sqrt((double)grid.size());
    }

    /**
     * Serialization method
     * @return
     */
    public String toString() {
        String ret = "";
        for(int i = 0; i < grid.size(); i++) {
            ret += grid.get(i);
        }
        return ret;
    }

    /**
     * Returns formatted board with indent number of spaces before each row.
     * @param indent
     * @return
     */
    public String toString(int indent) {
        String ret = "";
        for(int i = 0; i < grid.size(); i++) {
            if(i % dim == 0) {
                ret += '\n';
                for(int j = 0; j < indent; j++) {
                    ret += " ";
                }
            }
            ret += (grid.get(i) == 0) ? " " : grid.get(i);
            ret += " ";
        }
        return ret;
    }

    /**
     * Returns if the board is solved.
     * @return
     */
    public boolean isSolved() {
        return getValue() == 0;
    }

    /**
     * Calculates and returns heuristic value
     * @return
     */
    public int getValue() {
        int numInversions = 0;
        for(int i = 0; i < grid.size(); i++) {
            for(int j = i+1; j < grid.size(); j++){
                int left = (grid.get(j) == 0) ? grid.size() : grid.get(j);
                int right = (grid.get(i) == 0) ? grid.size() : grid.get(i);

                if(left<right) {
                    numInversions++;
                }
            }
        }
        return numInversions;
    }

    /**
     * Attempts to move number, returns true if move is successful, false if not.
     * @param move
     * @return
     */
    public boolean move(int move) {
        ArrayList<Integer> possiblesMoves = getPossibleMoves();
        if(possiblesMoves.contains(move)) {
            Collections.swap(grid, grid.indexOf(0), grid.indexOf(move));
            return true;
        }
        return false;
    }

    /**
     * Returns an ArrayList of valid moves.
     * @return
     */
    public ArrayList<Integer> getPossibleMoves() {
        ArrayList<Integer> ret = new ArrayList<>();
        int blankIndex = grid.indexOf(0);
        if(blankIndex - dim >= 0) {
            ret.add(grid.get(blankIndex - dim));
        }
        if (blankIndex + dim < grid.size()) {
            ret.add(grid.get(blankIndex + dim));
        }
        if (blankIndex - 1 >= 0 && blankIndex % dim != 0) {
            ret.add(grid.get(blankIndex - 1));
        }
        if (blankIndex + 1 < grid.size() && (blankIndex + 1) % dim != 0) {
            ret.add(grid.get(blankIndex + 1));
        }
        return ret;
    }
}
