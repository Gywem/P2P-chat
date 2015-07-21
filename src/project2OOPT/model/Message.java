package project2OOPT.model;

public abstract class Message {
	public enum TypeMessage {
		MSG_LOGIN (0),
		MSG_CHAT (1);
		private int integer;
		TypeMessage(int integer){
			this.integer = integer;
		}
		public int getValue(){
			return this.integer;
		}
	};
	
	private User user;
	private int port;
	private TypeMessage type;
	
	Message(TypeMessage type) {
		this.type = type;
		this.port = -1;
	}
	
	public TypeMessage getType(){
		return this.type;
	}
	
	public static Message deserialize(String raw){
		if(raw.charAt(0) != '(') return null;
		
		String port = "";
		boolean end = false;
		int i;
		for(i = 1; i < raw.length() && !end; ++i){
			if(raw.charAt(i) == ')') end = true;
			else port += raw.charAt(i);
		}
		
		if(raw.charAt(i) != '(') return null;
		
		String type = "";
		end = false;
		int j;
		for(j = i+1; j < raw.length() && !end; ++j){
			if(raw.charAt(j) == ')') end = true;
			else type += raw.charAt(j);
		}
		
		if(end) {
			String rest = raw.substring(j, raw.length());
			
			switch(Integer.parseInt(type)) {
				case 0:
					LoginMessage lm = new LoginMessage(rest);
					
					lm.setPort(Integer.parseInt(port));
					return lm;
				case 1:
					ChatMessage cm = new ChatMessage(rest);
					
					cm.setPort(Integer.parseInt(port));
					return cm;
				default:
			}
		}	
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String serialize() {
		return "("+this.getPort()+")"+"("+type.getValue()+")";
	}
}
