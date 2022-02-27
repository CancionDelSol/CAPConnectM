import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class ComputerPlayer implements IPlayer {
    //region Fields
    private static Random _rand = new Random();
    private char _playerCharacter = 'X';
    //endregion

    //region Constructor
    public ComputerPlayer(char playerCharacter) {
        _playerCharacter = playerCharacter;
    }
    //endregion

    //region IPlayer
    public int RequestMove(Gameboard board) {
        // Create a graph using the current state
        Graph tree = new Graph(board);

        try {
            return tree.EvaluateBestMove(this);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return -1;
    }

    /**
     * Return the players character piece e.g 'X'
     */
    public char getPlayerCharacter() {
        return _playerCharacter;
    }
    //endregion

    //region Alpha-Beta
    class Action {
        public int COLUMN = -1;
        Action(int column) {
            COLUMN = column;
        }
    }

    class Node {
        private Action _delta = null;
        private Node _parent = null;
        private Collection<Node> _children = new ArrayList<>();

        Node(Node parent, Action delta) {
            _parent = parent;
            _delta = delta;
        }

        void AppendNode(Node child) {
            _children.add(child);
        }

        boolean IsNill() { return _delta == null; }

        int GetDepth() {
            int depth = 0;
            Node curNode = this;
            do {
                depth++;
                curNode = curNode._parent;
            } while (curNode != null);
            return depth;
        }
    }

    class Graph {
        private Gameboard _initialState = null;
        private Node _headNode = null;

        Graph(Gameboard currentState) {
            _initialState = currentState;
            _headNode = new Node(null, null);
        }

        /**
         * Evaluate the best move for a player
         *  using a min-max tree and alpha-beta
         *  pruning
         */
        int EvaluateBestMove(IPlayer player) {
            double alpha = Double.MIN_VALUE;
            double beta = Double.MAX_VALUE;

            

            return _rand.nextInt(_initialState.getN());
        }

        /**
         * Use the _initialState and a node to gather
         *  action deltas along the tree and evaluate
         *  the new state's utility. 
         */
        double EvaluateNodeUtility(Node node, IPlayer player) {
            // Return value
            double rVal = Math.abs(_rand.nextDouble());

            // Climb the tree and gather deltas
            int nodeDepth = node.GetDepth();

            // Action list from all parent nodes
            //  Do not include head node (Action is nill)
            Action[] actions = new Action[nodeDepth - 1];

            // Gather actions
            Node curNode = node;
            int index = actions.length - 1;
            do {
                if (curNode.IsNill())
                    break;

                actions[index] = curNode._delta;
                curNode = curNode._parent;
            } while (curNode != null);

            // Clone Gameboard and place moves
            Gameboard sandbox = (Gameboard)_initialState.clone();
            for (Action action : actions) {
                sandbox.PlacePlayerPiece(action.COLUMN, player);
            }

            // Run evaluations
            sandbox.EvaluateStatistics();

            // Apply heuristic to current board state
            // TODO

            // Return utility
            return rVal;
        }
    }
}