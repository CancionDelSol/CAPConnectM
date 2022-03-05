public class Program {
    //region Fields
    private static Gameboard _gameBoard = null;
    private static IPlayer _playerOne = null;
    private static IPlayer _playerTwo = null;
    private static int _curPlayer = 0;
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
                _playerTwo = new UDPPlayer('O', port, address);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        } else {
            _playerOne = new HumanPlayer('O');
        }

        // Register players with gameboard
        //  and evaluate stats
        _gameBoard.RegisterPlayer(_playerOne);
        _gameBoard.RegisterPlayer(_playerTwo);
        _gameBoard.EvaluateStatistics();

        GameLoop();

    }
    //endregion

    // Test Scripts
    /**
     * Runs all available tests
     */
    private static void RunTests() {
        //boolean resOne = GameBoardConstructorTest();
        //boolean resTwo = GameBoardPlacePieceTest();
        //boolean resThree = GameBoardFillTest();
        boolean resFour = ComputerVsComputerTest();
        boolean resFive = GameboardCompletionTests();

        //System.out.println(resOne + " | GameBoardConstructorTest");
        //System.out.println(resTwo + " | GameBoardPlacePieceTest");
        //System.out.println(resThree + " | GameBoardFillTest");
        System.out.println(resFour + " | ComputerVsComputerTest");
        System.out.println(resFive + " | GameboardCompletionTests");
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

        if (newBoard.getLastMoves().get(newTestHumanPlayer.getPlayerCharacter()) != col)
            return false;

        // Piece should be at index 23
        try {
            if (newBoard.getBoard()[23] == playerPiece) { 
                return true;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return false;
    }

    private static boolean GameBoardFillTest() {
        int n = 5;
        int m = 3;

        Gameboard newBoard = new Gameboard(n, m);
        IPlayer playerOne = new HumanPlayer('X');

        // We can have a total of 5 * 5 = 25 total piece placements
        for (int i = 0; i < n*n; i++) {
            if(!newBoard.PlacePlayerPiece(i%n, playerOne))
                return false;
        }
        return true;
    }

    private static void GameLoop() {
        // Register players with gameboard
        //  and evaluate stats
        _gameBoard.RegisterPlayer(_playerOne);
        _gameBoard.RegisterPlayer(_playerTwo);
        _gameBoard.EvaluateStatistics();

        // Start game loop
        do {
            // Request move from player one
            IPlayer player = _curPlayer == 0 ? _playerOne : _playerTwo;
            _curPlayer = (_curPlayer + 1)%2;
            
            int invalidCounter = 0;
            
            int res = -1;
            try {
                do {
                    // For the UDP Player, it must request the
                    //  last move to make sure it was a successful move
                    //  otherwise, handle a request for a replacement move
                    
                	if(invalidCounter > 0) {
                		
                		System.out.println("That is an invalid move!");
                	}
                	
                	res = player.RequestMove(_gameBoard);
                    
                    invalidCounter++;
                    
                } while (!_gameBoard.PlacePlayerPiece(res, player));
                
                invalidCounter = 0;
                
            } catch (Exception exc) {
                exc.printStackTrace();
            }

            _gameBoard.DisplayGameBoard();

            _gameBoard.EvaluateStatistics();

        } while (!_gameBoard.getIsComplete());
        System.out.println(_gameBoard.BoardMessage);
    }

    private static boolean ComputerVsComputerTest() {
        _gameBoard = new Gameboard(10, 3);
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

    private static boolean GameboardCompletionTests() {
        IPlayer playerOne = new ComputerPlayer('X');
        IPlayer playerTwo = new ComputerPlayer('O');

        Gameboard testBoard = new Gameboard(3, 3);
        testBoard.RegisterPlayer(playerOne);
        testBoard.RegisterPlayer(playerTwo);

        // horizontal, row n = 2
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // horizontal, row n = 1
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerTwo);
        testBoard.PlacePlayerPiece(2, playerOne);
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // horizontal, row n = 0
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerTwo);
        testBoard.PlacePlayerPiece(2, playerOne);
        testBoard.PlacePlayerPiece(0, playerTwo);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerTwo);
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // diagonal, y = x
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerTwo);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerTwo);
        testBoard.PlacePlayerPiece(2, playerTwo);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();
        
        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // diagonal, y = -x
        testBoard.PlacePlayerPiece(0, playerTwo);
        testBoard.PlacePlayerPiece(0, playerTwo);
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(1, playerTwo);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // vertical n = 0
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(0, playerOne);
        testBoard.PlacePlayerPiece(0, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // vertical n = 1
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(1, playerOne);
        testBoard.PlacePlayerPiece(1, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        // vertical n = 2
        testBoard.PlacePlayerPiece(2, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);
        testBoard.PlacePlayerPiece(2, playerOne);

        testBoard.EvaluateStatistics();

        if (!testBoard.getIsComplete())
            return false;

        testBoard.ClearBoard();

        return true;
    }
}