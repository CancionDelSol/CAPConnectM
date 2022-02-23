public class Program {
    //region Fields
    private static Gameboard _gameBoard = null;
    private static IPlayer _playerOne = null;
    private static IPlayer _playerTwo = null;
    private static int _curPlayer = 0;
    private static int _totalRounds = 0;
    //endregion

    //region Properties
    public static Gameboard getGameboard() { return _gameBoard; }
    //endregion

    //region Main Entry
    public static void main(String[] args) { 
        // Run tests, then exit
        // Special case with no cli args
        if (args.length == 0) {
            RunTests();
            return;
        }

        // Set up Gameboard based on command
        //  line arguments
        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);
        _gameBoard = new Gameboard(n, m);

        // First mover
        _curPlayer = Integer.parseInt(args[2]);

        String address = "";
        int port = 0;
        if (args.length == 4) {
            address = args[3];
            System.out.println("Recognized IP address: " + address);
        }
        else if (args.length == 5) {
            address = args[3] + ":" + args[4];
            port = Integer.parseInt(args[4]);
            System.out.println("Recognized IP address: " + address);
            System.out.println("Recognized Port      : " + port);
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
        _playerTwo = new ComputerPlayer('X');
        
        // Case UDP
        if (!address.isEmpty()) {
            _playerOne = _playerTwo;
            try {
                _playerTwo = new UDPPlayer('O', port);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            _playerOne = new HumanPlayer('O');
        }

        GameLoop();

    }
    //endregion

    // Test Scripts
    /**
     * Runs all available tests
     */
    private static void RunTests() {
        boolean resOne = GameBoardConstructorTest();
        boolean resTwo = GameBoardPlacePieceTest();
        boolean resThree = GameBoardFillTest();
        boolean resFour = ComputerVsComputerTest();

        System.out.println(resOne + " | GameBoardConstructorTest");
        System.out.println(resTwo + " | GameBoardPlacePieceTest");
        System.out.println(resThree + " | GameBoardFillTest");
        System.out.println(resThree + " | ComputerVsComputerTest");
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
        newBoard.DisplayGameBoard();
        try {
            if (newBoard.getBoard()[23] == playerPiece) { 
                return true;
            } else {
                System.out.println("Fail GameBoardPlacePieceTest");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        GameLoop();

        System.out.println("Have a nice Day!");
        
        return false;
    }

    private static boolean GameBoardFillTest() {
        int n = 5;
        int m = 3;
        int col = 3; // indexed starting at 0
        char playerPiece = 'X';

        Gameboard newBoard = new Gameboard(n, m);
        IPlayer playerOne = new HumanPlayer('X');

        // We can have a total of 5 * 5 = 25 total piece placements
        for (int i = 0; i < n*n; i++) {
            if(!newBoard.PlacePlayerPiece(i%n, playerOne))
                return false;
        }
        newBoard.DisplayGameBoard();
        return true;
    }

    private static void GameLoop() {
        // Start game loop
        boolean gameComplete = false;
        int curMoveNumber = 0;
        do {

            _totalRounds += 1; 

            // Request move from player one
            IPlayer player = _curPlayer == 0 ? _playerOne : _playerTwo;
            _curPlayer = (_curPlayer + 1)%2;
            
            int res = -1;
            try {
                do {
                    res = player.RequestMove(_gameBoard);
                    System.out.println("Recieved move: " + res + " from player: " + (player.getPlayerCharacter()));
                } while (!_gameBoard.PlacePlayerPiece(res, player));
                
                
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            
            // TODO : Check for game completion
            //         just play 5 rounds for now
            if (_totalRounds >= _gameBoard.getN()*(_gameBoard.getN()-1))
                gameComplete = true;

            _gameBoard.DisplayGameBoard();

        } while (!gameComplete);
    }

    private static boolean ComputerVsComputerTest() {
        _gameBoard = new Gameboard(10, 4);
        _playerOne = new ComputerPlayer('O');
        _playerTwo = new ComputerPlayer('X');

        try {
            GameLoop();
        } catch (Exception exc) {
            exc.printStackTrace();
            return false;
        }

        return true;
    }
}