package tilesgui;

import java.util.ArrayList;

/**
 * Nodes are what are stored in the tilesgui.SearchTree.
 * Each node has a tilesgui.Board (represented as a string) as well as elements needed to connect Nodes to each other, as needed.
 */
public class SearchTreeNode implements Comparable<SearchTreeNode> {
    private String state;
    private int value;
    private int tileMoved = -1;
    private SearchTreeNode parent = null;
    private ArrayList<SearchTreeNode> children = new ArrayList<>();

    /**
     * Initializes a node with serialized board and heuristic value of the board.
     * @param state
     * @param val
     */
    public SearchTreeNode(String state, int val) {
        this.state = state;
        this.value = val;
    }

    /**
     * Initializes a node with serialized board, heuristic value of board, and the move used to reach board.
     * @param state
     * @param val
     * @param move
     */
    public SearchTreeNode(String state, int val, int move){
        this.state = state;
        this.value = val;
        this.tileMoved = move;
    }

    /**
     * Returns the serialized board in the node.
     * @return
     */
    public String getState() {
        return state;
    }

    /**
     * Returns heuristic value of the board.
     * @return
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns children of node.
     * @return
     */
    public ArrayList<SearchTreeNode> getChildren() {
        return children;
    }

    /**
     * Sets parent of node.
     * @param parent
     */
    public void setParent(SearchTreeNode parent) {
        this.parent = parent;
    }

    /**
     * Returns parent of node.
     * @return
     */
    public SearchTreeNode getParent() {
        return parent;
    }

    /**
     * Returns number of tile moved to reach node.
     * @return
     */
    public int getTileMoved() {
        return tileMoved;
    }

    /**
     * Sets number of tile moved to reach node.
     * @param tileMoved
     */
    public void setTileMoved(int tileMoved) {
        this.tileMoved = tileMoved;
    }

    /**
     * compareTo function implemented for Comparable interface to allow usage in PriorityQueue.
     * @param o
     * @return
     */
    @Override
    public int compareTo(SearchTreeNode o) {
        if(o == null)
            return -1;
        return Integer.compare(this.value, o.value);
    }
}
