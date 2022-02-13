public interface IPlayer {
    /**
     * The program will request a move
     *  from the player. The player class
     *  will perform necessary actions
     *  and return a column number to play
    */
    public int RequestMove(Gameboard board);

    /**
     * Return the players character piece e.g 'X'
     */
    public char getPlayerCharacter();
}