package project2OOPT.model;

public class LoginMessage extends Message {
	
	private String nick;
	
	public LoginMessage(String nick) {
		super(TypeMessage.MSG_LOGIN);
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	public String serialize(){
		return super.serialize() + nick;
	}
}
