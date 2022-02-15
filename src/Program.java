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
    private static void RunTests() {
        boolean resOne = GameBoardConstructorTest();
        boolean resTwo = GameBoardPlacePieceTest();

        System.out.println(resOne + " | GameBoardConstructorTest");
        System.out.println(resTwo + " | GameBoardPlacePieceTest");

    }
    private static boolean GameBoardConstructorTest() {
        int n = 5;
        int m = 3;

        Gameboard newBoard = new Gameboard(n, m);
        if (newBoard.getBoard().length != n*n)
            return false;
        return true;
    }

    private static boolean GameBoardPlacePieceTest() {
        int n = 5;
        int m = 3;
        int col = 3; // indexed starting at 0
        char playerPiece = 'X';

        Gameboard newBoard = new Gameboard(n, m);
        IPlayer newTestHumanPlayer = new HumanPlayer('X');
        newBoard.PlacePlayerPiece(col, newTestHumanPlayer);

        // Piece should be at index 23
        try {
            if (newBoard.getBoard()[22] == playerPiece) { //23
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