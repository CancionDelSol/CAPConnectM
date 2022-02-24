import java.util.Random;

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
        // TODO : Places random piece for now
        return Math.abs(_rand.nextInt()) % board.getN();
    }

    /**
     * Return the players character piece e.g 'X'
     */
    public char getPlayerCharacter() {
        return _playerCharacter;
    }
    //endregion
}