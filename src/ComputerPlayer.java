import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ComputerPlayer implements IPlayer {
    //region Fields
    private static Random _rand = new Random();
    private char _playerCharacter = 'X';
    private static final int MAX_DEPTH = 1;
    //endregion

    //region Constructor
    public ComputerPlayer(char playerCharacter) {
        _playerCharacter = playerCharacter;
    }
    //endregion

    //region IPlayer
    public int RequestMove(Gameboard board) {
        // Create a graph using the current state
        Graph tree = new Graph(board, this);

        try {
            return tree.EvaluateBestMove();
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
        private IPlayer _player = null;

        Graph(Gameboard currentState, IPlayer player) {
            _initialState = currentState;
            _player = player;
            _headNode = new Node(null, null);
        }

        /**
         * Evaluate the best move for a player
         *  using a min-max tree and alpha-beta
         *  pruning
         */
        int EvaluateBestMove() {
            // Return column action
            int rVal = -1;
            List<Integer> bestMoves = new ArrayList<>();

            // Initialize alpha and beta
            double alpha = Util.VerySmall;
            double beta = Util.VeryLarge;

            // Get available moves
            int[] availableMoves = _initialState.getAvailableMoves();

            // Iterate over available moves
            for (int i = 0; i < availableMoves.length; i++) {
                // Don't evaluate if no open spaces
                if (availableMoves[i] == 0)
                    continue;

                // Create new node with this action
                Node newNode = new Node(_headNode, new Action(i));
                double aPrime = minValue(newNode, alpha, beta, MAX_DEPTH);

                if (Math.abs(aPrime - alpha) <= Util.Epsilon) {
                    alpha = aPrime;
                    bestMoves.add(i);
                } else if ( aPrime > alpha) {
                    alpha = aPrime;
                    bestMoves.clear();
                    bestMoves.add(i);
                }
            }

            if (bestMoves.size() == 0)
                return _rand.nextInt(_initialState.getN());

            return bestMoves.get(_rand.nextInt(bestMoves.size()));
        }

        /**
         * Recursive min call
         */
        double minValue(Node node, double alpha, double beta, int depth) {
            Util.RefSupport<int[]> availMoves = new Util.RefSupport<int[]>(null);
            Util.RefSupport<Boolean> isComplete = new Util.RefSupport<>(false);
            double nodeUtil = EvaluateNodeUtility(node, _player, availMoves, isComplete);

            // Handle game completion
            if (isComplete.getRef() || depth == 0)
                return nodeUtil;

            for (int i = 0; i < availMoves.getRef().length; i ++) {
                if (availMoves.getRef()[i] == 0)
                    continue;

                Node newNode = new Node(node, new Action(i));
                beta = Math.min(beta, maxValue (newNode, alpha, beta, depth - 1));
                if (alpha >= beta){
                    return Util.VerySmall;
                }
            }
            return beta;
        }

        /**
         * Recursive max call
         */
        double maxValue(Node node, double alpha, double beta, int depth) {
            Util.RefSupport<int[]> availMoves = new Util.RefSupport<int[]>(null);
            Util.RefSupport<Boolean> isComplete = new Util.RefSupport<>(false);
            double nodeUtil = EvaluateNodeUtility(node, _player, availMoves, isComplete);

            // Handle game completion
            if (isComplete.getRef() || depth == 0)
                return nodeUtil;

            for (int i = 0; i < availMoves.getRef().length; i ++) {
                if (availMoves.getRef()[i] == 0)
                    continue;
                    
                Node newNode = new Node(node, new Action(i));
                alpha = Math.max(alpha, minValue(newNode, alpha, beta, depth - 1));
                if (alpha >= beta){
                    return Util.VeryLarge;//Double.MAX_VALUE;
                }
            }
            return alpha;    
        }

        /**
         * Use the _initialState and a node to gather
         *  action deltas along the tree and evaluate
         *  the new state's utility. 
         */
        double EvaluateNodeUtility(Node node, IPlayer player, Util.RefSupport<int[]> availMoves, Util.RefSupport<Boolean> isComplete) {
            IPlayer opponent = null;
            for (IPlayer p : _initialState.getPlayers()) {
                if (p.getPlayerCharacter() != _player.getPlayerCharacter()) {
                    opponent = p;
                    break;
                }
            }

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

                actions[index--] = curNode._delta;
                curNode = curNode._parent;
            } while (curNode != null);

            // Clone Gameboard and place moves
            Gameboard sandbox = (Gameboard)_initialState.clone();

            int flip = 0;
            for (Action action : actions) {
                boolean placementRes = flip == 0 ? sandbox.PlacePlayerPiece(action.COLUMN, player, false) : sandbox.PlacePlayerPiece(action.COLUMN, opponent, false);
                if (!placementRes)
                    break;
            }

            // Run evaluations
            sandbox.EvaluateStatistics();

            // Set reference object
            availMoves.setRef(sandbox.getAvailableMoves());

            // Return NaN for game complete
            //  Special case
            if (sandbox.getIsComplete())
                isComplete.setRef(true);

            // Apply heuristic to current board state
            //  Simple heuristic for now:
            //   - Look through the counts for each 
            //      Directionality. Add the inverse
            //      of the opponents longest chain for
            //      each directionality. Return the average
            //      over each directionality as a single double
            double sum = 0.0;
            int count = 0;
            double goal = (double)_initialState.getGoal();
            HashMap<Character, HashMap<Gameboard.Directionality, int[]>> stats = sandbox.getStats();

            // Get the set counts
            int[] verticalPlayerSet = stats.get(_player.getPlayerCharacter()).get(Gameboard.Directionality.Vertical);
            int[] verticalOpponentSet = stats.get(opponent.getPlayerCharacter()).get(Gameboard.Directionality.Vertical);
            int[] horizPlayerSet = stats.get(_player.getPlayerCharacter()).get(Gameboard.Directionality.Horizontal);
            int[] horizOpponentSet = stats.get(opponent.getPlayerCharacter()).get(Gameboard.Directionality.Horizontal);
            int[] diagUpPlayerSet = stats.get(_player.getPlayerCharacter()).get(Gameboard.Directionality.DiagonalUp);
            int[] diagUpOpponentSet = stats.get(opponent.getPlayerCharacter()).get(Gameboard.Directionality.DiagonalUp);
            int[] diagDownPlayerSet = stats.get(_player.getPlayerCharacter()).get(Gameboard.Directionality.DiagonalDown);
            int[] diagDownOpponentSet = stats.get(opponent.getPlayerCharacter()).get(Gameboard.Directionality.DiagonalDown);

            // Max counts
            int maxPlayer = 0;
            int maxOpponent = 0;

            // Over Verticals and horizontals
            for (int i = 0; i < _initialState.getN(); i++) {
                count += 2;
                sum += verticalPlayerSet[i]/goal + (1.0 - (verticalOpponentSet[i]/goal));
                sum += horizPlayerSet[i]/goal + (1.0 - (horizOpponentSet[i]/goal));

                if (verticalPlayerSet[i] > maxPlayer) 
                    maxPlayer = verticalPlayerSet[i];
                if (horizPlayerSet[i] > maxPlayer)
                    maxPlayer = horizPlayerSet[i];

                if (verticalOpponentSet[i] > maxOpponent)
                    maxOpponent = verticalOpponentSet[i];
                if (horizOpponentSet[i] > maxOpponent)
                    maxOpponent = horizOpponentSet[i];
            }


            // Over Diagonals
            for (int i = 0; i < diagUpPlayerSet.length; i++) {
                count += 2;
                sum += diagUpPlayerSet[i]/goal + (1.0 - (diagUpOpponentSet[i]/goal));
                sum += diagDownPlayerSet[i]/goal + (1.0 - (diagDownOpponentSet[i]/goal));
                if (diagDownPlayerSet[i] > maxPlayer)
                    maxPlayer = diagDownPlayerSet[i];
                if (diagUpPlayerSet[i] > maxPlayer)
                    maxPlayer = diagUpPlayerSet[i];

                if (diagDownOpponentSet[i] > maxOpponent)
                    maxOpponent = diagDownOpponentSet[i];
                if (diagUpOpponentSet[i] > maxOpponent)
                    maxOpponent = diagUpOpponentSet[i];
                
            }

            if (maxPlayer >= _initialState.getGoal())
                return Util.VeryLarge;

            if (maxOpponent >= _initialState.getGoal())
                return Util.VerySmall;

            // Return utility
            return sig(sum/((double)count)) + (maxPlayer - (maxOpponent * 5));
        }

        /**
         * Return a value between -1 and 1
         * @param x
         * @return
         */
        private double sig(double x) {
            return (1.0/(1.0 + Math.exp(-x)) * 2.0) - 1.0;
        }
    }
}