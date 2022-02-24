import java.util.HashMap;
import java.lang.StringBuilder;

/**
 * Game board class
 * Handles the game board matrix
 *  and accepts input from player 
 *  interface implementations
 */
public class Gameboard {
    //region Constants
    private static String DIVIDING_ROW = "+---+---+---+---+---+";
    private static final String DIVIDER = "|";
    private static final Character EMPTY = ' ';
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
     * Return the last move by player
     * Returns -1 for missing key
     */
    public int getLastMoveForPlayer(IPlayer player) {
        if (_recentMoves.containsKey(player.getPlayerCharacter()))
            return _recentMoves.get(player.getPlayerCharacter());
        return -1;
    }
    public int getGoal() { return _goal; }
    public int getN() { return _n; }
    //endregion

    //region Constructor
    public Gameboard(int n, int m) {
        _board = new char[n*n];
        _n = n;

        ClearBoard();

        StringBuilder bldr = new StringBuilder();
        for (int i = 0; i < n; i++) {
            bldr.append("+---");
        }
        bldr.append("+");
        DIVIDING_ROW = bldr.toString();
        _goal = m;
    }
    //endregion

    //region Methods
    public void ClearBoard() {
        for (int i = 0; i < _n*_n; i++) {
            _board[i] = EMPTY;
        }
    }

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
            System.out.print(" " + _board[i] +  " ");
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
        if (column < 0)
            return false;
            
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

    public boolean IsComplete() {
        // Count whitespace to check for full
        //  board
        int whitespace = 0;

        // Counters for calculating longest 
        //  string of characters
        int[] vertCounts = new int[_n];
        int[] horizCounts = new int[_n];
        int[] diagClockwiseCounts = new int[(_n*2) - 1];
        int[] diagCClockwiseCounts = new int[(_n*2) - 1];

        // Current Character to compare too
        Character[] curVertChars = new Character[_n];
        Character[] curHorizChars  = new Character[_n];
        Character[] diagCWChars = new Character[(_n*2) - 1];
        Character[] diagCCWChars = new Character[(_n*2) - 1];

        // Initialize the arrays to initial
        //  values
        for (int i = 0; i < _n; i++) {
            vertCounts[i] = 0;
            horizCounts[i] = 0;
            diagClockwiseCounts[i] = 0;
            diagCClockwiseCounts[i] = 0;
        }

        // Check all of the columns and rows
        for (int nOne = 0; nOne < _n; nOne++) {
            for (int nTwo = 0; nTwo < _n; nTwo++) {
                // Retrieve the current characters
                //  in the vertical and horizontal direction
                Character atIndex = GetAtIndex(nOne, nTwo);

                // Increment the whitespace count
                if (atIndex.equals(EMPTY)) {
                    whitespace++;
                }

                if (nOne == 0) {
                    curHorizChars[nTwo] = atIndex;

                    horizCounts[nTwo] = 0;

                } else if (!atIndex.equals(EMPTY)) {
                    if (atIndex.equals(curHorizChars[nTwo]))
                        horizCounts[nTwo]++;
                    else{
                        horizCounts[nTwo] = 0;
                        curHorizChars[nTwo] = atIndex;
                    }

                    if (horizCounts[nTwo] >= _goal - 1){
                        System.out.println("Horiz: " + nTwo + " Winner! Player \'" + atIndex + "\'");
                        return true;
                    }
                } else {
                    horizCounts[nTwo] = 0;
                    curHorizChars[nTwo] = atIndex;
                }

                if (nTwo == 0) {
                    // Reset the column and row counts
                    curVertChars[nOne] = GetAtIndex(nOne, nTwo);

                    vertCounts[nOne] = 0;

                } else if (!atIndex.equals(EMPTY)) {
                    if (atIndex.equals(curVertChars[nOne])){
                        vertCounts[nOne]++;
                    } else {
                        vertCounts[nOne] = 0;
                        curVertChars[nOne] = atIndex;
                    }

                    if (vertCounts[nOne] >= _goal - 1){
                        System.out.println("Vert: " + nOne + " Winner! Player \'" + atIndex + "\'");
                        return true;
                    }
                } else {
                    vertCounts[nOne] = 0;
                    curVertChars[nOne] = atIndex;
                }

                // Handle the diagonals
                int clockwiseDiagonalIndex = (_n-1) + (nOne - nTwo);
                int countercwDiagonalIndex = (nOne + nTwo);

                int clockwiseCharArrayIndex = (clockwiseDiagonalIndex < _n) ? nOne : nTwo;
                int countercwCharArrayIndex = (countercwDiagonalIndex < _n) ? nOne : (_n-1) - nTwo;

                if (clockwiseCharArrayIndex == 0) {
                    diagCWChars[clockwiseDiagonalIndex] = atIndex;
                    diagClockwiseCounts[clockwiseDiagonalIndex] = 0;
                } else if (!atIndex.equals(EMPTY)) {
                    if (atIndex.equals(diagCWChars[clockwiseDiagonalIndex])) {
                        diagClockwiseCounts[clockwiseDiagonalIndex]++;
                    } else {
                        diagClockwiseCounts[clockwiseDiagonalIndex] = 0;
                        diagCWChars[clockwiseDiagonalIndex] = atIndex;
                    }

                    if (diagClockwiseCounts[clockwiseDiagonalIndex] >= _goal - 1) {
                        System.out.println("DC: " + clockwiseDiagonalIndex + " Winner! Player \'" + atIndex + "\'");
                        return true;
                    }
                } else {
                    diagClockwiseCounts[countercwDiagonalIndex] = 0;
                    diagCWChars[countercwDiagonalIndex] = atIndex;
                }

                if (countercwCharArrayIndex == 0) {
                    diagCCWChars[countercwDiagonalIndex] = atIndex;
                    diagCClockwiseCounts[countercwDiagonalIndex] = 0;
                } else if (!atIndex.equals(EMPTY)) {
                    if (atIndex.equals(diagCCWChars[countercwDiagonalIndex])) {
                        diagCClockwiseCounts[countercwDiagonalIndex]++;
                    } else {
                        diagCClockwiseCounts[countercwDiagonalIndex] = 0;
                        diagCCWChars[countercwDiagonalIndex] = atIndex;
                    }

                    if (diagCClockwiseCounts[countercwDiagonalIndex] >= _goal - 1) {
                        System.out.println("DCC: " + countercwDiagonalIndex + " Winner! Player \'" + atIndex + "\'");
                        return true;
                    }
                } else {
                    diagCClockwiseCounts[countercwDiagonalIndex] = 0;
                    diagCCWChars[countercwDiagonalIndex] = atIndex;
                }
            }
        }

        // The last test case is to see
        //  if the entire board has been filled
        //  If the whitespace is zero, the board
        //   is filled
        return whitespace == 0;
    }

    /**
     * Get the character at coordinate (x, y)
     * @param x coordinate (starting at 0)
     * @param y coordinate (starting at 0)
     * @return character at index (x, y)
     */
    public Character GetAtIndex(int x, int y) {
        return _board[y*_n + x];
    }
    //endregion
}