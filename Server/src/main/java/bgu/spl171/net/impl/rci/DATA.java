package bgu.spl171.net.impl.rci;

public class DATA extends Message {
	short packetSize;
	short blockNumber;
	byte[] data;
	
	public DATA(){}
	public DATA(short packetSize,short blockNum,byte[]data){
		this.packetSize=packetSize;
		this.blockNumber=blockNum;
		this.data=data;
		this.opcode=3;
	}
	
	public short getPacketSize() {
		return packetSize;
	}
	public void setPacketSize(short packetSize) {
		this.packetSize = packetSize;
	}
	public short getBlockNumber() {
		return blockNumber;
	}
	public void setBlockNumber(short blockNumber) {
		this.blockNumber = blockNumber;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	

}
