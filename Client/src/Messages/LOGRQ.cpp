#include "FileNameMessage.cpp"
#include <string>
using namespace std;
 class LOGRQ :public FileNameMessage {

	string userName;
  string messageType = "LOGRQ";

	public:
     string getUserName() {
		return userName;
	}

	 void setUserName(string userName) {
		this->userName = userName;
	}


};
