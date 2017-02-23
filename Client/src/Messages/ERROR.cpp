#include "Message.cpp"
#include <string>
using namespace std;
 class ERROR :public Message {

	private:
     short errorCode;
	 string errMsg;
	 char lastByte=0;
   string messageType="ERROR";
	public:
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
	 char getLastByte() {
		return lastByte;
	}
	 void setLastByte(char lastByte) {
		this->lastByte = lastByte;
	}



};
