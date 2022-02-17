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
    private InetAddress _localAddress;
    private InetAddress _outgoingAddress;
    private byte[] _buffer;
    private int _port;
    //endregion

    //TODO : Change address from localhost to IP
    //add try catch for error reporting? 
    
    //region Constructor
    public UDPPlayer(char playerCharacter, int port)  throws SocketException, UnknownHostException {
        this(playerCharacter, port, "");
    }

    public UDPPlayer(char playerCharacter, int port, String outgoingAddress) throws SocketException, UnknownHostException {
        _playerCharacter = playerCharacter;
        _socket = new DatagramSocket(port);
        _localAddress = InetAddress.getByName("localhost");

        if (outgoingAddress.isEmpty())
            _outgoingAddress = InetAddress.getByName("localhost");
        else
            _outgoingAddress = InetAddress.getByAddress(outgoingAddress, getRawAddress(outgoingAddress));
            
        _port = port; 
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
    
    /**
     * send UDPPlayer move(int)
     * receive oponents move and return(int)
     * @throws IOException 
     */
    public int sendEcho(int column) throws IOException {
    	
    	String move = String.valueOf(column);
    	
    	_buffer = move.getBytes();
    	
    	DatagramPacket packet 
    		= new DatagramPacket(_buffer, _buffer.length, _outgoingAddress, _port);
    	
    	_socket.send(packet);
    	
    	packet = new DatagramPacket(_buffer, _buffer.length, _localAddress, _port);
    	
    	_socket.receive(packet);
    	
    	String received = new String(packet.getData(), 0, packet.getLength());
    	
    	int opponentMove = Integer.parseInt(received);
    	
    	return opponentMove;
    }

    private byte[] getRawAddress(String address) {
        String[] addAndPort = address.split(":");
        String[] intArray = addAndPort[0].split(".");

        byte[] res = new byte[4];
        for (int i = 0; i < 4; i++) {
            res[i] = (byte)(Integer.parseInt(intArray[i]) & 0xFF);
        }
        return res;
    }
}