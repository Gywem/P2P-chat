package project2OOPT.view;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import project2OOPT.model.ChatMessage;
import project2OOPT.model.LoginMessage;
import project2OOPT.model.Message;
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
 * Displayer
 * Abstraction layer charged with the display of the information in the console.
 * */

public class Displayer {
	public static Displayer Singleton = Displayer.getInstance();
	
	private Displayer() {
		
	}

	public static Displayer getInstance() {
		if(Singleton == null) {
			return new Displayer();
		} else {
			return Singleton;
		}
	}
	
	public void display(List<User> users, List<Message> messages) {
		this.clearConsole();
		
		this.displayMessagePanel(messages);
		this.displayUserPanel(users);
	
	}
	
	private void displayUserPanel(List<User> users) {
		Iterator<User> it = users.iterator();
		String userPanel = "Conected users : ";
		
		while(it.hasNext()) {
			User next = it.next();
			userPanel += next.getNick();
			
			if(it.hasNext()) userPanel += ", ";
		}
		
		System.out.println(userPanel);
	}

	private void displayMessagePanel(List<Message> messages){
		Iterator<Message> it = messages.iterator();
		
		while(it.hasNext()) {
			Message next = it.next();
			
			switch(next.getType()){
				case MSG_CHAT:
					this.displayChatMessage((ChatMessage)next);
					break;
				case MSG_LOGIN:
					this.displayLoginMessage((LoginMessage)next);
					break;
				default:
			}
		}
	}
	
	private void displayChatMessage(ChatMessage chatmsg) {
		System.out.println("["+chatmsg.getUser().getNick()+"] : "+chatmsg.getBody());
	}
	
	private void displayLoginMessage(LoginMessage chatmsg) {		
		System.out.println(chatmsg.getUser().getNick()+" has logged");
	}
	
	private void clearConsole(){
		try {
			Runtime.getRuntime().exec("reset");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
