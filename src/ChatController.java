import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import project2OOPT.interfaces.IConectionListener;
import project2OOPT.manager.ChatManager;
import project2OOPT.manager.ConectionManager;
import project2OOPT.model.ChatMessage;
import project2OOPT.model.Client;
import project2OOPT.model.LoginMessage;
import project2OOPT.model.Message;
import project2OOPT.model.Server;
import project2OOPT.view.Displayer;
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
 * Chat Controller 
 * Class in charge for listen the connections, manage the chat data and update the display
 * */

public class ChatController implements IConectionListener {	
	private static ChatController Singleton = ChatController.getInstance();
	public static ConectionManager ConectionM = ConectionManager.getInstance();
	public static ChatManager ChatM = ChatManager.getInstance();
	public static Displayer displayer = Displayer.getInstance();
	
	public static ChatController getInstance(){
		if(Singleton == null) return new ChatController();
		else return Singleton;
	}
	
	public static void main(String[] args) {
		// Initiation of services (server and client conection)
		if(ChatController.Singleton.initServices()) {	
			Server serverInfo = ConectionM.getServerInfo();
			
			@SuppressWarnings("resource")
			Scanner userInputScanner = new Scanner(System.in);
			
			String nick;
			
			// Choosing nick from the user
			System.out.print("Please choose the nick you want to use for chatting :");
			nick = userInputScanner.nextLine();
			
			LoginMessage loginmsg = ChatM.newLoginMessage(serverInfo, nick);
			ChatController.Singleton.PerformLoginMessage(loginmsg);
			
			String msg;
			while(true) {
				// It is asked to the user to give us the messages to share to others
				msg = userInputScanner.nextLine();
				
				ChatMessage chatmsg = ChatM.newChatMessage(serverInfo, msg);
				ChatController.Singleton.PerformChatMessage(chatmsg);
			}
		} else {
			System.out.print("Fallo");
		}
	}

	
	public boolean initServices() {
		ConectionM.addListener(this);
		if(ConectionM.tryServerConection() != null){
			ConectionM.runServer();
			ConectionM.tryNewConection();
			ConectionM.tryNewConection();
			return true;
		} else {
			return false;
		}
		
	}
	
	public void updateDisplay(){
		displayer.display(ChatM.getUsers(), ChatM.getMessages());
	}

	@Override
	public synchronized void onNewPacket(Client client, String m) {

		Message msg;
			
		msg = Message.deserialize(m);
		
		if(msg == null) return;		
		
		switch(msg.getType()) {
			case MSG_LOGIN:
				LoginMessage loginmsg = (LoginMessage)msg;
				
				ChatM.addLoginMessage(client, loginmsg);
				break;
			case MSG_CHAT:
				ChatMessage chatmsg = (ChatMessage)msg;
				ChatM.addChatMessage(client, chatmsg);
				break;
				default:
		}
		
		ConectionM.sharePacket(client, m);
		
		updateDisplay();
	}
	
	public void PerformLoginMessage(LoginMessage loginmsg){
		ConectionM.broadcastPacket(loginmsg.serialize());
		
		updateDisplay();
	}
	
	public void PerformChatMessage(ChatMessage chatmsg) {
		ConectionM.broadcastPacket(chatmsg.serialize());
		
		updateDisplay();
	}

	@Override
	public synchronized void onNewConection(Client client) {
		List<Message> messages = ChatM.getMessages();
		
		Iterator<Message> it = messages.iterator();
		
		while(it.hasNext()) {
			Message msg = it.next();
			if(ChatM.getUsers().contains(msg.getUser())) {
				if(msg instanceof LoginMessage) ConectionM.sharePacketTo(msg.serialize(), client);
			}
		}
	}

	@Override
	public void onDisconection(Client client) {
		ChatM.removeUser(client);
		updateDisplay();
	}

}
