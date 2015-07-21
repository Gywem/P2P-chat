package project2OOPT.model;

import java.net.Socket;

public class Client extends NodeP2P {

	private Socket socket;
	
	public Client(Socket s) {
		super(s.getInetAddress(), s.getPort());
		
		this.socket = s;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
