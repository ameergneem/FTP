package ameer.net.impl.rci;

public class BCAST extends FileNameMessage {

short delOrAdd;

public BCAST() {
	super();
}
public BCAST(byte addOrDelete,String fileName){
	this.delOrAdd=addOrDelete;
	this.fileName=fileName;
	this.opcode=9;
}
public short getDelOrAdd() {
	return delOrAdd;
}

public void setDelOrAdd(short delOrAdd) {
	this.delOrAdd = delOrAdd;
}

	
}
