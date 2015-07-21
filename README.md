P2P-chat
=========

This P2P-Chat has been implemented as a group chat. The Chat is implemented in such way:
 * 	You have to use the chat firstly in the same local server. It would not be difficult to adapt to a broader approach. Nevertheless, because of testing issues, I decided to make it simple.
 * 	Users try to instantiate a server socket in a range of ports.
 * 	Users try to only connect to a one user within such range
 * 	The messages are shared between the peers

This project was meant for studying pattern designs. Concretly, this project uses the Singleton, Observer and MVC patterns.

Technologies
Java

Bibliography
Head First Design Patterns - O'Reilly Media
