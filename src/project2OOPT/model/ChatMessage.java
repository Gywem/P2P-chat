package project2OOPT.model;

/*
 * Message entity
 * */
public class ChatMessage extends Message {
	private String body;
	
	public ChatMessage(String msg){
		super(TypeMessage.MSG_CHAT);
		this.body = msg;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String serialize(){
		return super.serialize() + body;
	}
}
