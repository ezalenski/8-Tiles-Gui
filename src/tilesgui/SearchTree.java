package tilesgui;import java.util.*;

/**
 * This implements the A* state-space search algorithm which uses a
 * heuristic as an approximation of the goodness of each board configuration.
 */
public class SearchTree {
    private SearchTreeNode root;
    private HashSet<String> stateReached = new HashSet<>();
    private boolean solvable;
    private Stack<SearchTreeNode> moves;

    /**
     * Constructs search tree rooted with the initialState
     * @param initialState
     */
    public SearchTree(SearchTreeNode initialState) {
        root = initialState;
        root.setParent(null);
    }

    /**
     * Expands SearchTree in order of lowest heuristic until either a solved board is found or all
     * possible boards are searched. Stores path to best move in tree for to be retrieved via
     * nextMove() method
     */
    public void solve() {
        solvable = false;
        PriorityQueue<SearchTreeNode> nodePriorityQueue = new PriorityQueue<>();

        nodePriorityQueue.add(root);
        SearchTreeNode bestState = null;
        while(!nodePriorityQueue.isEmpty()) {
            SearchTreeNode n = nodePriorityQueue.poll();
            bestState = (n.compareTo(bestState) == -1) ? n : bestState;
            if(n.getValue() == 0) {
                solvable = true;
                break;
            }
            if (!stateReached.contains(n.getState())) {
                stateReached.add(n.getState());
                Board currConfiguration = new Board(n.getState());
                ArrayList<Integer> possibleMoves = currConfiguration.getPossibleMoves();
                for (Integer move : possibleMoves) {
                    currConfiguration.move(move);

                    SearchTreeNode child = new SearchTreeNode(currConfiguration.toString(), currConfiguration.getValue());

                    child.setTileMoved(move);
                    child.setParent(n);
                    nodePriorityQueue.add(child);
                    n.getChildren().add(child);

                    currConfiguration.move(move);
                }
            }
        }
        setMoves(bestState);
    }

    /**
     * Stores all the moves from initialState to finalState to be retrieved later via nextMove
     * @param finalState
     */
    private void setMoves(SearchTreeNode finalState) {
        this.moves = new Stack<>();
        while(finalState.getParent() != null) {
            moves.push(finalState);
            finalState = finalState.getParent();
        }
    }

    /**
     * Returns the next best move in search tree, if there's no next move, returns -1.
     * @return
     */
    public int nextMove() {
        if(!hasNextMove()) return -1;
        int bestMove = moves.pop().getTileMoved();
        return bestMove;
    }

    /**
     * Returns whether or not the search tree has a next move.
     * @return
     */
    public boolean hasNextMove() {
        return !moves.isEmpty();
    }

    /**
     * Returns the number of moves tried before solution was found or all possible moves were tried.
     * @return
     */
    public int movesTried() {
        return stateReached.size();
    }

    /**
     * Returns whether or not the initialState is solvable.
     * @return
     */
    public boolean isSolvable() {
        return solvable;
    }

    /**
     * Returns the best board found by the search tree.
     * @return
     */
    public Board getBestMoveBoard() {
        return new Board(moves.get(0).getState());
    }
}
