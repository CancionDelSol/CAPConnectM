import java.util.HashMap;

/**
 * Game board class
 * Handles the game board matrix
 *  and accepts input from player 
 *  interface implementations
 */
public class Gameboard {
    //region Constants
    private static final String DIVIDING_ROW = "+---+---+---+---+---+";
    private static final String DIVIDER = "|";
    private static final String EMPTY = " ";
    //endregion

    //region Fields
    private char[] _board = null;
    private HashMap<Character, Integer> _recentMoves = new HashMap<>();
    private int _n = 0;
    private int _goal = 0;
    //endregion

    //region Properties
    public char[] getBoard() { return _board; }

    /**
     * Return the last move by player with 
     */
    public int getLastMoveForPlayer(IPlayer player) {
        if (_recentMoves.containsKey(player.getPlayerCharacter()))
            return _recentMoves.get(player.getPlayerCharacter());
    }
    public int getGoal() { return _goal; }
    public int getN() { return _n; }
    //endregion

    //region Constructor
    public Gameboard(int n, int m) {
        _board = new char[n*n];
        _n = n;

        for (int i = 0; i < n*n; i++) {
            _board[i] = ' ';
        }
        _goal = m;
    }
    //endregion

    //region Methods
    /**
     * Display the game board using ASCII characters
     * +---+---+---+---+---+ 
     * |   |   |   |   |   | 
     * +---+---+---+---+---+ 
     * |   |   | X | O |   | 
     * +---+---+---+---+---+ 
     * | X | X | O | X | X | 
     * +---+---+---+---+---+ 
     * | X | O | O | O | X | 
     * +---+---+---+---+---+ 
     * | O | O | O | X | X | 
     * +---+---+---+---+---+ 
    */
    public void DisplayGameBoard() {
        for (int i = 0; i < _n * _n; i++) {
            if (i % _n == 0) {
                if (i > 0)
                    System.out.println(DIVIDER);

                System.out.println(DIVIDING_ROW);
            }
            
            System.out.print(DIVIDER);
            System.out.print(" " + _board[i] + " ");
        }
        System.out.println(DIVIDER);
        System.out.println(DIVIDING_ROW);
    }

    /**
     * Place a player character piece in the column
     *  specified. Will place in the last index available
     * Origin starting in the top left corner
     */
    public boolean PlacePlayerPiece(int column, IPlayer player) {
        // Get the largest available row
        for (int yIndex = _n - 1; yIndex > -1; yIndex--) {
            int index = (_n * yIndex) + column;
            if (_board[index] == ' ') {
                _board[index] = player.getPlayerCharacter();
                _recentMoves.put(player.getPlayerCharacter(), column);
                return true;
            }
        }
        System.out.println("Unable to place piece in column: " + column);
        return false;
    }
    //endregion
}