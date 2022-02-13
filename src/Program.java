public class Program {
    private Gameboard _gameBoard = null;
    public static void main(String[] args) { 
        
        // TODO : Set up Gameboard based on command
        //         line arguments
        

        // TODO : Create message publisher
        //         in order to send moves

        // TODO : Create player objects (Human vs. Computer)
        //        The player objects will send actions to the 
        //        board. The board will wait for commands and 
        //        print the layout and request input from user
        //        if required

        // Print hello world
        GameBoardConstructorTest();
    }

    // Test Scripts
    private static boolean GameBoardConstructorTest() {
        int n = 5;
        int m = 3;

        Gameboard newBoard = new Gameboard(n, m);
        if (newBoard.getBoard().size() != n*n)
            return false;
        return true;
    }
}