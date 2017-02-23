#ifndef MESSAGE_H_
#define MESSAGE_H_
#include <string>
using namespace std;
class Message{
   private:
      short opcode;
     string messageType;
   public:
     Message(short opcode,string messageType):opcode(opcode),messageType(messageType){}
    short getOpcode(){
       return opcode;
     }
     void setOpcode(short opcode){
       this->opcode = opcode;
     }
    virtual string getMessageType(){
      return messageType;
     }
     virtual void setMessageType(string messageType ){
       this->messageType = messageType;
     }
     virtual short getMessageLength(){
       return 2;
     }
};
class ACK: public Message{
   private:
     short blockNumber;

   public:
     ACK(short blockNumber):Message(4,"ACK"),blockNumber(blockNumber){}
     short getBlockNumber(){
      return blockNumber;
     }
    void setBlockNumber(short blockNumber){
       this->blockNumber = blockNumber;
    }
    short getMessageLength(){
      return 4;
    }
};
class FileNameMessage :public Message {
  private:
    string fileName;
    char lastByte='0';

  public:
    FileNameMessage(short opcode,string messageType,string fileName):Message(opcode,messageType),fileName(fileName){}
  void setFileName(string filename){
     this->fileName=filename;
    }
   string getFileName(){
     return fileName;
    }
   virtual void setLastByte(char lastByte){
     this->lastByte = lastByte;
     }
   virtual char getLastByte(){
     return lastByte;
     }
};
class BCAST:public FileNameMessage {
  private:
  char delOrAdd;

  public:
    BCAST(string fileName,char delOrAdd):FileNameMessage(9,"BCAST",fileName),delOrAdd(delOrAdd){}
   char getDelOrAdd(){
	return delOrAdd;
  }
  void setDelOrAdd(char delOrAdd){
	  this->delOrAdd = delOrAdd;
    }
    short getMessageLength(){
      return 4+getFileName().length();
    }
};
class DATA :public Message {
  private:
     short packetSize;
     short blockNumber;
     char* data;
   public:
     DATA(short packetSize,short blockNumber,char* data):Message(3,"DATA"),packetSize(packetSize),blockNumber(blockNumber),data(data){}
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
    short getMessageLength(){
      return 6+packetSize;
    }

};
class DELRQ :public FileNameMessage {
 public:
  DELRQ(string fileName):FileNameMessage(8,"DELRQ",fileName){}
  short getMessageLength(){
    return 3+getFileName().length();
  }
  };
class DIRQ :public Message {
 public:
  DIRQ():Message(6,"DIRQ"){}
    };
class DISC :public Message {
 public:
  DISC():Message(10,"DISC"){}
   };
class ERROR :public FileNameMessage {
    private:
      short errorCode;
      string errMsg;
    public:
      ERROR(short errorCode,string errMsg):FileNameMessage(5,"ERROR",""),errorCode(errorCode),errMsg(errMsg){}
       short getErrorCode() {
      return errorCode;
    }
     void setErrorCode(short errorCode) {
      this->errorCode = errorCode;
    }
     string getErrMsg() {
      return errMsg;
    }
     void setErrMsg(string errMsg) {
      this->errMsg = errMsg;
    }
    short getMessageLength(){
      return 5+getErrMsg().length();
    }
    string toString(){
      return "ERROR<" + to_string(errorCode)+">";
    }

   };
class LOGRQ :public FileNameMessage {
  private:
  	string userName;
  	public:
      LOGRQ(string userName):FileNameMessage(7,"LOGRQ",""),userName(userName){}
       string getUserName() {
  		return userName;
  	}

  	 void setUserName(string userName) {
  		this->userName = userName;
  	}
    short getMessageLength(){
      return 3+userName.length();
    }


  };
class RWRQ :public FileNameMessage {
 public:
   RWRQ(short opcode,string fileName):FileNameMessage(opcode,"RWRQ",fileName){}
   short getMessageLength(){
     return 3+getFileName().length();
   }
 };
#endif
