public class Program {
    //region Fields
    private Gameboard _gameBoard = null;
    private IPlayer _playerOne = null;
    private IPlayer _playerTow = null;
    //endregion

    //region Main Entry
    public static void main(String[] args) { 
        // Run tests, then exit
        if (args.length == 0) {
            RunTests();
            return;
        }

        // TODO : Set up Gameboard based on command
        //         line arguments
        int n = Integer.parseInteger(args[0]);
        int m = Integer.parseInteger(args[1]);

        // TODO : Create message publisher
        //         in order to send moves

        // TODO : Create player objects (Human vs. Computer)
        //        The player objects will send actions to the 
        //        board. The board will wait for commands and 
        //        print the layout and request input from user
        //        if required
    }
    //endregion

    // Test Scripts
    /**
     * Runs all available tests
     */
    private static void RunTests() {
        boolean resOne = GameBoardConstructorTest();
        boolean resTwo = GameBoardPlacePieceTest();

        System.out.println(resOne + " | GameBoardConstructorTest");
        System.out.println(resTwo + " | GameBoardPlacePieceTest");

    }

    /**
     * Construct a gameboard successfully
     */
    private static boolean GameBoardConstructorTest() {
        int n = 5;
        int m = 3;

        Gameboard newBoard = new Gameboard(n, m);
        if (newBoard.getBoard().length != n*n)
            return false;
        return true;
    }

    /**
     * Place a piece in a gameboard
     * Checks for correct placement and value
     * Checks recent move for player matches input
     */
    private static boolean GameBoardPlacePieceTest() {
        int n = 5;
        int m = 3;
        int col = 3; // indexed starting at 0
        char playerPiece = 'X';

        Gameboard newBoard = new Gameboard(n, m);
        IPlayer newTestHumanPlayer = new HumanPlayer('X');
        newBoard.PlacePlayerPiece(col, newTestHumanPlayer);

        if (newBoard.getLastMoveForPlayer(newTestHumanPlayer) != newTestHumanPlayer.getPlayerCharacter())
            return false;

        // Piece should be at index 23
        try {
            if (newBoard.getBoard()[23] == playerPiece) { 
                return true;
            } else {
                System.out.println("Fail GameBoardPlacePieceTest");
                newBoard.DisplayGameBoard();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        
        return false;
    }
}