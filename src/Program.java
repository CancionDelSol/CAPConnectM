public class Program {
    //region Fields
    private Gameboard _gameBoard = null;
    private IPlayer _playerOne = null;
    private IPlayer _playerTow = null;
    private int _curPlayer = 0;
    //endregion

    //region Main Entry
    public static void main(String[] args) { 
        // Run tests, then exit
        if (args.length == 0) {
            RunTests();
            return;
        }

        // Set up Gameboard based on command
        //  line arguments
        IPlayer playerOne = null;
        IPlayer playerTwo = null;
        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);
        int h = Integer.parseInt(args[2]);
        String address = "";
        if (args.length == 4) {
            address = args[3];
            System.out.println("Recognized IP address: " + address);
        }
        else if (args.length == 5) {
            address = args[3] + ":" + args[4];
            System.out.println("Recognized IP address: " + address);
        }

        // Here are the appropriate setups concerning 
        //  players:
        //   [Human, Computer] : if no ip address is given
        //   [Computer, UDP] : if ip is present
        //
        //   In the first case, the cli argument "H" will
        //    determine whether the Human goes first
        //
        //   In the latter case, the cli argument "H" will
        //    determine whether the program is waiting for 
        //    a UDP response first or sending a move first

        // There will always be a computer player
        playerTwo = new ComputerPlayer('X');
        
        // Case UDP
        if (!address.isEmpty()) {
            playerOne = playerTwo;
            playerTwo = new UDPPlayer('O', address);
        } else {
            playerOne = new HumanPlayer('O');
        }

        // TODO : Start game loop
        boolean gameComplete = false;
        do {

        } while (!gameComplete)
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

        if (newBoard.getLastMoveForPlayer(newTestHumanPlayer) != col)
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