package project2OOPT.manager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import project2OOPT.interfaces.IConectionListener;
import project2OOPT.interfaces.IConectionObservable;
import project2OOPT.model.Client;
import project2OOPT.model.Server;

/*
 * Project 2 - OOPT
 * Ignacio José Codoñer Gil - st number : 0416040
 * This Chat has been implemented as a group chat (Advanced requirements (grade 1-5)). However, I had some problems
 * related to manage user disconnection.
 * The Chat is implemented in such way:
 * 	You have to use the chat firstly in the same local server. It would not be difficult to adapt to a broader
 * 		approach. Nevertheless, because of testing issues, I decided to make it simple.
 * 	Users try to instantiate a server socket in a range of ports.
 * 	Users try to only connect to a one user within such range
 * 	The messages are shared between the peers
 * */

/*
 * ConectionManager
 * Abstraction layer to manage connection matters. It is a observable which provides to the chat controller
 * 	the information about new connections, new messages arrived and so on.
 * */

public class ConectionManager implements IConectionObservable {
	private static ConectionManager Singleton = ConectionManager.getInstance();
	
	private String host = "localhost";
	
	private List<Client> clients;
	private List<IConectionListener> listeners;
	
	@SuppressWarnings("serial")
	public List<Integer> rangePortsAvailable = new ArrayList<Integer>() {{
		   add(5000);
		   add(5010);
		}};
	
	public int nextPort = 0;
	
	private Server serverInfo;
	
	
	public Server getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(Server serverInfo) {
		this.serverInfo = serverInfo;
	}

	private ConectionManager(){
		this.listeners = new ArrayList<IConectionListener>();
		this.clients = new ArrayList<Client>();
		
	}
	
	public Client tryNewConection(){
		Client client = null;
		try {
			int port = rangePortsAvailable.get(0) + nextPort;
			if(serverInfo != null && serverInfo.getPort() == port) {
				nextPort++;
				
				return tryNewConection();
			}
			if(port >= rangePortsAvailable.get(0) && port <= rangePortsAvailable.get(1)) {
				InetAddress address = InetAddress.getByName(this.host);
				client = this.newConection(new Socket(address, port));
				client.setPort(port);
				nextPort = 0;
			} else {
				return null;
			}
		} catch (IOException e) {
			nextPort++;
			
			return tryNewConection();
		}
		return client;		
	}
	
	public Client newConection(Socket socket) {
		Client client = null;
		client = new Client(socket);
		this.clients.add(client);
		
		new listenClientMessages(client).start();
		
		return client;
	}
	
	public Server tryServerConection(){
		if(serverInfo != null) return null;
		Server server = null;
		try {
			int port = rangePortsAvailable.get(0) + nextPort;
			if(port >= rangePortsAvailable.get(0) && port <= rangePortsAvailable.get(1)) {
				server = new Server(new ServerSocket(port));
				this.serverInfo = server;
				nextPort = 0;
			} else {
				return null;
			}
		} catch (IOException e) {
			nextPort++;
			
			return tryServerConection();
		}
		return server;
	}
	
	public static ConectionManager getInstance(){
		if(Singleton == null) {
			return new ConectionManager();
		} else {
			return Singleton;
		}
	}

	@Override
	public void addListener(IConectionListener observer) {
		listeners.add(observer);
	}

	public void runServer() {
		new acceptConections(this.serverInfo).start();
	}
	
	public void broadcastPacket(String raw){
		synchronized(this.clients) {
			Iterator<Client> it = this.clients.iterator();
			
			while(it.hasNext()) {
				Client c = it.next();
				
				this.sendPacket(c, raw);
			}
		}
	}
	
	public void sharePacket(Client from, String raw){
		Iterator<Client> it = this.clients.iterator();
		
		while(it.hasNext()) {
			Client c = it.next();
			
			if(!from.equals(c)) this.sendPacket(c, raw);
		}
	}
	
	public synchronized void sendPacket(Client client, String raw){
		if(client.getSocket() != null) {
			try {
				BufferedOutputStream os = new BufferedOutputStream(client.getSocket().getOutputStream());
			    OutputStreamWriter osw = new OutputStreamWriter(os, "US-ASCII");
				osw.write(raw+(char) 13);
				osw.flush();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				this.newDisconectionUpdate(client);
				try {
					client.getSocket().close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	class acceptConections extends Thread {	
		Server serverInfo;
		acceptConections(Server serverInfo) {
			this.serverInfo = serverInfo;
        }

        public void run() {
        	Client client;
    		try {
    			while(true) {
    				client = newConection(serverInfo.getSocket().accept());
    				
    				newConectionUpdate(client);
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	
        }
    }
	
	class listenClientMessages extends Thread {
		Client client;
		
		listenClientMessages(Client c) {
			this.client = c;
        }

        public void run() {
			BufferedInputStream is;
			try {
				is = new BufferedInputStream(client.getSocket().getInputStream());

				
				InputStreamReader isr = new InputStreamReader(is);
				String message = "";
				
				int character;
				
				while(true) {
					while((character = isr.read()) != 13) {
						message += (char)character;
					}
					new newMessageUpdate(client, message.toString()).start();

					message = "";
				}				
			} catch (IOException e1) {
				try {
					this.client.getSocket().close();
				} catch (IOException ev) {
					ev.printStackTrace();
				}
				e1.printStackTrace();
			}
        	
        }
    }
	
	class newMessageUpdate extends Thread {
		String msg;
		Client client;
		
		newMessageUpdate(Client client, String message) {
			this.client = client;
			this.msg = message;
        }

        public void run() {        	
        	newMessageUpdate(client, msg);
        }
    }
	
	@Override
	public synchronized void newMessageUpdate(Client client, String m) {
		Iterator<IConectionListener> it = listeners.iterator();
		
		while(it.hasNext()){
			IConectionListener next = it.next();
			next.onNewPacket(client, m);
		}
		
	}

	@Override
	public synchronized void newConectionUpdate(Client client) {
		Iterator<IConectionListener> it = listeners.iterator();
		
		while(it.hasNext()){
			IConectionListener next = it.next();
			next.onNewConection(client);
		}
	}
	
	@Override
	public void newDisconectionUpdate(Client client) {
		Iterator<IConectionListener> it = listeners.iterator();
		
		while(it.hasNext()){
			IConectionListener next = it.next();
			next.onDisconection(client);
		}
	}

	public void sharePacketTo(String serialize, Client client) {
		this.sendPacket(client, serialize);
	}

		
	
}
