import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.lang.Math;

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

        Node(Node parent, Gameboard board, Action delta) {
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

        }

        int EvaluateBestMove(IPlayer player) {
            return _rand.nextInt(_initialState.getN());
        }
    }
}