import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private int[] _availableColumns;
    private boolean _isComplete = false;
    private HashMap<Character, HashMap<Directionality, int[]>> _stats = null;
    private List<IPlayer> _registeredPlayers = new ArrayList<>();

    public String BoardMessage = "";
    //endregion

    //region Properties
    public int[] getAvailableMoves() { return _availableColumns; }
    
    public char[] getBoard() { return _board; }

    public boolean getIsComplete() { return _isComplete; }

    public HashMap<Character, HashMap<Directionality, int[]>> getStats() { return _stats; }

    /**
     * Return the last move by player
     * Returns -1 for missing key
     */
    public HashMap<Character, Integer> getLastMoves() {
        return _recentMoves;
    }
    public int getGoal() { return _goal; }
    public int getN() { return _n; }
    //endregion

    //region Constructor
    public Gameboard(int n, int m) {
        _board = new char[n*n];
        _availableColumns = new int[n];
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
    public List<IPlayer> getPlayers() {
        return _registeredPlayers;
    }
    
    public void RegisterPlayer(IPlayer player) {
        if (!_registeredPlayers.contains(player))
            _registeredPlayers.add(player);
    }

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

    /**
     * Evaluate all stats for the current state
     *  of the gameboard. This only iterates through
     *  the entire board once. Although it does use
     *  a decent chunk of memory in order to do so
     */
    public void EvaluateStatistics() {
        // Clear stats reference
        _stats = null;

        // Reset available column counts
        Arrays.fill(_availableColumns, 0);

        // Build hash map of output statistics
        HashMap<Character, HashMap<Directionality, int[]>> rVal = new HashMap<>();
        for (IPlayer player : _registeredPlayers) {
            HashMap<Directionality, int[]> subMap = new HashMap<>();

            subMap.put(Directionality.Vertical, new int[_n]);
            subMap.put(Directionality.Horizontal, new int[_n]);
            subMap.put(Directionality.DiagonalDown, new int[(_n * 2) - 1]);
            subMap.put(Directionality.DiagonalUp, new int[(_n * 2) - 1]);

            rVal.put(player.getPlayerCharacter(), subMap);
        }

        // Count whitespace to check for full
        //  board
        int whitespace = 0;
        _isComplete = false;

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

        // Check all of the columns and rows
        for (int nOne = 0; nOne < _n; nOne++) {
            for (int nTwo = 0; nTwo < _n; nTwo++) {
                // Retrieve the current characters
                //  in the vertical and horizontal direction
                Character atIndex = GetAtIndex(nOne, nTwo);

                // Increment the whitespace count
                if (atIndex.equals(EMPTY)) {
                    whitespace++;
                    _availableColumns[nOne]++;
                }

                // Diagonal indicies
                int clockwiseDiagonalIndex = (_n-1) + (nOne - nTwo);
                int countercwDiagonalIndex = (nOne + nTwo);

                int clockwiseCharArrayIndex = (clockwiseDiagonalIndex < _n) ? nOne : nTwo;
                int countercwCharArrayIndex = (countercwDiagonalIndex < _n) ? nOne : (_n-1) - nTwo;

                // Handle zero indicies
                if (nOne == 0) {
                    curHorizChars[nTwo] = atIndex;
                    horizCounts[nTwo] = 0;
                }
                if (nTwo == 0) {
                    // Reset the column count to 1
                    curVertChars[nOne] = atIndex;
                    vertCounts[nOne] = 0;
                }
                if (clockwiseCharArrayIndex == 0) {
                    diagCWChars[clockwiseDiagonalIndex] = atIndex;
                    diagClockwiseCounts[clockwiseDiagonalIndex] = 0;
                }
                if (countercwCharArrayIndex == 0) {
                    diagCCWChars[countercwDiagonalIndex] = atIndex;
                    diagCClockwiseCounts[countercwDiagonalIndex] = 0;
                }

                // Rows
                if (atIndex.equals(curHorizChars[nTwo])) {
                    horizCounts[nTwo]++;
                    if (rVal.containsKey(atIndex)) {
                        if (horizCounts[nTwo] > rVal.get(atIndex).get(Directionality.Horizontal)[nTwo]) {
                            rVal.get(atIndex).get(Directionality.Horizontal)[nTwo] = horizCounts[nTwo];
                        }
                    }
                }
                else{
                    horizCounts[nTwo] = 1;
                    curHorizChars[nTwo] = atIndex;
                }

                if (horizCounts[nTwo] >= _goal && !curHorizChars[nTwo].equals(EMPTY)){
                    BoardMessage = "Horiz: " + nTwo + " Winner! Player \'" + atIndex + "\'";
                    _isComplete = true;
                }

                // Columns
                if (atIndex.equals(curVertChars[nOne])){
                    vertCounts[nOne]++;
                    if (rVal.containsKey(atIndex)) {
                        if (vertCounts[nOne] > rVal.get(atIndex).get(Directionality.Vertical)[nOne]) {
                            rVal.get(atIndex).get(Directionality.Vertical)[nOne] = vertCounts[nOne];
                        }
                    }
                } else {
                    vertCounts[nOne] = 1;
                    curVertChars[nOne] = atIndex;
                }

                if (vertCounts[nOne] >= _goal && !curVertChars[nOne].equals(EMPTY)){
                    BoardMessage = "Vert: " + nOne + " Winner! Player \'" + atIndex + "\'";
                    _isComplete = true;
                }
                
                // Diagonal down
                if (atIndex.equals(diagCWChars[clockwiseDiagonalIndex])) {
                    diagClockwiseCounts[clockwiseDiagonalIndex]++;
                    if (rVal.containsKey(atIndex)) {
                        if (diagClockwiseCounts[clockwiseDiagonalIndex] > rVal.get(atIndex).get(Directionality.DiagonalDown)[clockwiseDiagonalIndex]) {
                            rVal.get(atIndex).get(Directionality.DiagonalDown)[clockwiseDiagonalIndex] = diagClockwiseCounts[clockwiseDiagonalIndex];
                        }
                    }
                } else {
                    diagClockwiseCounts[clockwiseDiagonalIndex] = 1;
                    diagCWChars[clockwiseDiagonalIndex] = atIndex;
                }

                if (diagClockwiseCounts[clockwiseDiagonalIndex] >= _goal && !diagCWChars[clockwiseDiagonalIndex].equals(EMPTY)) {
                    BoardMessage = "DC: " + clockwiseDiagonalIndex + " Winner! Player \'" + atIndex + "\'";
                    _isComplete = true;
                }
                
                // Diagonal up
                if (atIndex.equals(diagCCWChars[countercwDiagonalIndex])) {
                    diagCClockwiseCounts[countercwDiagonalIndex]++;
                    if (rVal.containsKey(atIndex)) {
                        if (diagCClockwiseCounts[countercwDiagonalIndex] > rVal.get(atIndex).get(Directionality.DiagonalUp)[countercwDiagonalIndex]) {
                            rVal.get(atIndex).get(Directionality.DiagonalUp)[countercwDiagonalIndex] = diagCClockwiseCounts[countercwDiagonalIndex];
                        }
                    }
                } else {
                    diagCClockwiseCounts[countercwDiagonalIndex] = 1;
                    diagCCWChars[countercwDiagonalIndex] = atIndex;
                }

                if (diagCClockwiseCounts[countercwDiagonalIndex] >= _goal && !diagCCWChars[countercwDiagonalIndex].equals(EMPTY)) {
                    BoardMessage = "DCC: " + countercwDiagonalIndex + " Winner! Player \'" + atIndex + "\'";
                    _isComplete = true;
                }
            }
        }

        // The last test case is to see
        //  if the entire board has been filled
        //  If the whitespace is zero, the board
        //   is filled
        _isComplete = _isComplete || whitespace == 0;

        // Return the stats
        _stats = rVal;
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

    /**
     * Make an exact copy of this gameboard
     */
    @Override
    public Object clone() {
        Gameboard newBoard = new Gameboard(_n, _goal);
        for (int i = 0; i < _board.length; i++) 
            newBoard._board[i] = _board[i];
            
        for (int i = 0; i < _n; i++)
            newBoard._availableColumns[i] = _availableColumns[i];
        
        for (Character key : _recentMoves.keySet()) 
            newBoard._recentMoves.put(key, _recentMoves.get(key));

        for (IPlayer player : _registeredPlayers) {
            newBoard.RegisterPlayer(player);
        }
        
        return newBoard;
    }
    //endregion

    static enum Directionality {
        Vertical,
        Horizontal,
        DiagonalUp,
        DiagonalDown
    }
}