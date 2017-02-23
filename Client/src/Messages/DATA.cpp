#include "Message.cpp"
 class DATA :public Message {
	short packetSize;
	short blockNumber;
  char* data;
  string messageType = "DATA";
public:
   short getPacketSize() {
		return packetSize;
	}
	 void setPacketSize(short packetSize) {
		this->packetSize = packetSize;
	}
	 short getBlockNumber() {
		return blockNumber;
	}
	 void setBlockNumber(short blockNumber) {
		this->blockNumber = blockNumber;
	}
	 char* getData() {
		return data;
	}
	 void setData(char* data) {
		this->data = data;
	}


};
