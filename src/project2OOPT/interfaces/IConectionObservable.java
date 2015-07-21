package project2OOPT.interfaces;

import project2OOPT.model.Client;

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
 * IConectionObservable
 * Interface needed to implement the Observer pattern design.
 * */

public interface IConectionObservable {
	public void addListener(IConectionListener observer);
	public void newMessageUpdate(Client client, String m);
	public void newConectionUpdate(Client client);
	public void newDisconectionUpdate(Client client);
}
