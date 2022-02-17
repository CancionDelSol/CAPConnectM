import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class UDPPlayer implements IPlayer {
    //region Fields
    private static Random _rand = new Random();
    private char _playerCharacter = 'X';
    private DatagramSocket _socket;
    private InetAddress _address;
    private byte[] _buffer;
    private int _port;
    //endregion

    //TODO : Change address from localhost to IP
    //add try catch for error reporting? 
    
    //region Constructor
    public UDPPlayer(char playerCharacter, int port) throws SocketException, UnknownHostException {
        _playerCharacter = playerCharacter;
        _socket = new DatagramSocket();
        _address = InetAddress.getByName("localhost");
        _port = port; 
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
    
    /**
     * send UDPPlayer move(int)
     * receive oponents move and return(int)
     * @throws IOException 
     */
    
    public int sendEcho(int column) throws IOException {
    	
    	String move = String.valueOf(column);
    	
    	_buffer = move.getBytes();
    	
    	DatagramPacket packet 
    		= new DatagramPacket(_buffer, _buffer.length, _address, _port);
    	
    	_socket.send(packet);
    	
    	packet = new DatagramPacket(_buffer, _buffer.length);
    	
    	_socket.receive(packet);
    	
    	String received = new String(packet.getData(), 0, packet.getLength());
    	
    	int opponentMove = Integer.parseInt(received);
    	
    	return opponentMove;
    }
}