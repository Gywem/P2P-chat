package project2OOPT.model;

import java.net.ServerSocket;

public class Server extends NodeP2P {

	private ServerSocket socket;
	
	public Server(ServerSocket s) {
		super(s.getInetAddress(), s.getLocalPort());
		
		this.socket = s;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

}
