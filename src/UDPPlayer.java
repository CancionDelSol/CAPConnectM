import java.util.Random;

public class UDPPlayer implements IPlayer {
    //region Fields
    private static Random _rand = new Random();
    private char _playerCharacter = 'X';
    private String _address = "";
    //endregion

    //region Constructor
    public UDPPlayer(char playerCharacter, String address) {
        _playerCharacter = playerCharacter;
        _address = address;
    }
    //endregion

    //region IPlayer
    public int RequestMove(Gameboard board) {
        // TODO : Places random piece for now
        return _rand.nextInt() % board.getN();
    }

    /**
     * Return the players character piece e.g 'X'
     */
    public char getPlayerCharacter() {
        return _playerCharacter;
    }
    //endregion
}