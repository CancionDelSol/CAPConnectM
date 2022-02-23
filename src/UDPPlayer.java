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
    //add try catch added for local error reporting
    
    //region Constructor
    public UDPPlayer(char playerCharacter, int port) {
        _playerCharacter = playerCharacter;
       
        try {
        
        	_socket = new DatagramSocket();
        
        }catch(SocketException e){
        
        	System.err.println("unable to create and bind socket");
    
        }
        
        try {
        
        	_address = InetAddress.getByName("localhost");
        
        }catch(UnknownHostException e){
        	
        	System.err.println("Unknown Host");
        	
        }
        
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
    
    
    //region send request 
    public void sendRequest(int column) {
    	
    	String move = String.valueOf(column);
    	
    	_buffer = move.getBytes();
    	
    	DatagramPacket packet 
		= new DatagramPacket(_buffer, _buffer.length, _address, _port);
    	
    	try {
    		
    		_socket.send(packet);
    	
    	}catch(IOException e) {
    
    		System.err.println("unable to send message");
    	
    	}
    }	//endregion
    
    //region receive request
    public int receiveRequest() {
    		
    	 byte[] buffer = new byte[_buffer.length];
    	
    	DatagramPacket packet = new DatagramPacket(buffer, _buffer.length);
    	
    	try {
    		
    		_socket.receive(packet);
    		
    	}catch(IOException ex) {
    		System.err.println("unable to receive message");
    		return -1;
    	}
    	
    	String received = new String(packet.getData(), 0, packet.getLength());
    	
    	int opponentMove = Integer.parseInt(received);
    	
    	return opponentMove;
    }	//endregion
    
    
    public void closeSocket() {
    
    	_socket.close();
    	
    }
    
}