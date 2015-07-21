package project2OOPT.model;

import java.net.InetAddress;

/*
 * Information about a node (computer) for connecting purposes
 * */
public abstract class NodeP2P {
	private InetAddress ip;
	private int port;
	
	NodeP2P(InetAddress inetAddress, int port){
		this.ip = inetAddress;
		this.port = port;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
