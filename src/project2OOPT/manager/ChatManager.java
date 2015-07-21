package project2OOPT.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import project2OOPT.model.ChatMessage;
import project2OOPT.model.Client;
import project2OOPT.model.LoginMessage;
import project2OOPT.model.Message;
import project2OOPT.model.NodeP2P;
import project2OOPT.model.User;

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
 * ChatManager
 * Class manager responsible of manage the chat data.
 * */

public class ChatManager {
	private static ChatManager Singleton = ChatManager.getInstance();
	
	private User mainUser;
	
	// What is the mapping between the user network definition (port in this case) and the user from the chat.
	private Map<Integer,User> port2user;
	// What is the mapping between the client socket port (port in this case) and its server socket port.
	private Map<Integer,Integer> client2ServerId; 
	// Messages and users in the chat room
	private List<Message> messages;
	private List<User> users;
	
	private ChatManager(){
		this.client2ServerId = new HashMap<Integer,Integer> ();
		this.port2user = new HashMap<Integer,User> ();
		this.messages = new ArrayList<Message>();
		this.users = new ArrayList<User>();
	}
	
	public static ChatManager getInstance(){
		if(Singleton == null) {
			return new ChatManager();
		} else {
			return Singleton;
		}
	}
	
	public User createMainUser(String nick){
		this.mainUser = this.createUser(nick);
		
		return this.mainUser;
	}
	
	public User getMainUser(){
		return this.mainUser;
	}
	
	
	public User createUser(String nick){
		User u;
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			u = it.next();
			
			if(u.getNick() == nick) return u;
		}
		
		u = new User();
		u.setNick(nick);
		
		this.users.add(u);
		
		return u;
	}
	
	public void linkNode2User(int port, User user) {
		this.port2user.put(port, user);
	}
	
	public LoginMessage createLoginMessage(String nick){
		LoginMessage loginmsg = new LoginMessage(nick);		
		return loginmsg;
	}
	
	public LoginMessage newLoginMessage(NodeP2P node, String nick){
		User user = this.createUser(nick);
		this.linkNode2User(node.getPort(), user);
		
		LoginMessage loginmsg = new LoginMessage(nick);
		loginmsg.setPort(node.getPort());
		
		this.addLoginMessage(node, loginmsg);
		
		return loginmsg;
	}
	
	public void addLoginMessage(NodeP2P node, LoginMessage loginmsg) {
		User user = this.createUser(loginmsg.getNick());
		this.linkNode2User(loginmsg.getPort(), user);
		this.client2ServerId.put(node.getPort(), loginmsg.getPort());
		
		loginmsg.setUser(user);
		
		messages.add(loginmsg);
	}
	
	public ChatMessage newChatMessage(NodeP2P node, String msg) {		
		ChatMessage chatMessage = new ChatMessage(msg);
		chatMessage.setPort(node.getPort());
		
		this.addChatMessage(node, chatMessage);
		
		return chatMessage;
	}
	
	public void addChatMessage(NodeP2P node, ChatMessage chatMessage) {		
		User user = this.port2user.get(chatMessage.getPort());
		chatMessage.setUser(user);

		this.client2ServerId.put(node.getPort(), chatMessage.getPort());

		messages.add(chatMessage);
	}
	
	public void removeUser(Client node){
		if(this.client2ServerId.containsKey(node.getPort())) {
			int port = this.client2ServerId.get(node.getPort());
			String nick = port2user.get(port).getNick();
			port2user.remove(node.getPort());
			
			User u;
			Iterator<User> it = users.iterator();
			while(it.hasNext()) {
				u = it.next();
				
				if(u.getNick() == nick) it.remove();
			}
			this.client2ServerId.remove(port);
		}
	}

	public List<Message> getMessages() {
		return this.messages;
	}
	
	public List<User> getUsers() {
		return this.users;
	}

	public void newMessage(Message msg) {
		messages.add(msg);
	}
}
