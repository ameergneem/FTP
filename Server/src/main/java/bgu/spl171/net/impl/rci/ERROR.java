package bgu.spl171.net.impl.rci;

public class ERROR extends Message {

	short errorCode;
	String errMsg;
	byte lastByte = '0';
	
	public ERROR() {
		super();
		opcode=5;
	}
	public ERROR(short errorCode,String errMsg){
		this.errMsg=errMsg;
		this.errorCode=errorCode;
		this.opcode=5;
	}
	public short getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(short errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	public byte getLastByte() {
		return lastByte;
	}
	public void setLastByte(byte lastByte) {
		this.lastByte = lastByte;
	}
	
	
	
}
