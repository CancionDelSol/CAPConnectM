import java.util.Random;
import java.util.Scanner;

public class HumanPlayer implements IPlayer {
    //region Fields
    private static Random _rand = new Random();
    private char _playerCharacter = 'X';
    //endregion

    //region Constructor
    public HumanPlayer(char playerCharacter) {
        _playerCharacter = playerCharacter;
    }
    //endregion

    //region IPlayer
    public int RequestMove(Gameboard board) {
        
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Which column would you like to select as your move?");
        
        int input = -1;

        try {
            input = sc.nextInt();
        } catch (Exception exc) {

        }

        return input;
    }

    /**
     * Return the players character piece e.g 'X'
     */
    public char getPlayerCharacter() {
        return _playerCharacter;
    }
    //endregion
}