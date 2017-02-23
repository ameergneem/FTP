package bgu.spl171.net.impl.rci;

public class ACK extends Message {
	short blockNumber;
	
	public ACK() {
		super();
	}
	public ACK(short blockNumber){
		this.blockNumber=blockNumber;
		this.opcode=4;
	}
	public short getBlockNumber() {
		return blockNumber;
	}

	public void setBlockNumber(short blockNumber) {
		this.blockNumber = blockNumber;
	}
	
	
	
}
