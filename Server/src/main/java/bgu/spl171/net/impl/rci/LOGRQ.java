package bgu.spl171.net.impl.rci;

public class LOGRQ extends FileNameMessage {

	String userName;
	
	public LOGRQ(){
		opcode = 7;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	
}
