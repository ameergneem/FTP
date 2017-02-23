#include <iostream>
#include <fstream>
#include <cmath>
#include <iterator>
#include "EncoderDecoder.cpp"
#include <stdio.h>
#include <cstring>
#include <boost/thread.hpp>
#include "../include/Message.h"
#include "ConnectionHandler.cpp"
#include "Menu.cpp"
using namespace std;
using namespace boost;
class Client{
  string host,userNameOrFileName;
  short port;
  bool disconnect = false;
  EncoderDecoder* encdec = new EncoderDecoder();
  short opcodeFromKeyBoard;
  ConnectionHandler* connectionHandler ;
  mutex * _mutex;
public:
  Client(string host,short port,mutex* mutex):host(host),port(port),_mutex(mutex){
   connectionHandler = new ConnectionHandler(host,port);
  }
short readOperationFromUser(){
  string operation,op;
  string::size_type pos;
  short opcode=0;

  Menu* menu = new Menu();
  while(opcode==0){
  //  menu->displayMenu();
    getline(cin, op);
    pos=op.find(' ',0);
    if(pos != string::npos){
    userNameOrFileName=op.substr(pos+1);
    operation=op.substr(0,pos);
    }else{operation = op;}

      if(operation.compare("RRQ")==0)opcode= 1;
      if(operation.compare("WRQ")==0)opcode= 2;
      if(operation.compare("DATA")==0)opcode= 3;
      if(operation.compare("ACK")==0)opcode= 4;
      if(operation.compare("ERROR")==0)opcode= 5;
      if(operation.compare("DIRQ")==0)opcode= 6;
      if(operation.compare("LOGRQ")==0)opcode= 7;
      if(operation.compare("DELRQ")==0)opcode= 8;
      if(operation.compare("BCAST")==0)opcode= 9;
      if(operation.compare("DISC")==0)opcode= 10;

     }


     return opcode;


}
void connect(){
    if(!connectionHandler->connect()){
      //std::cerr << "Cannot connect to " << host << ":" << port << std::endl;

    }else{
    //  cout<<"connected!"<<endl;
    }
}
void send(char* bytes,short size){
  mutex::scoped_lock lock(*_mutex);
  connectionHandler->sendBytes(bytes,size);
}
Message* getBytes(){
  char* singleByte = new char();
  Message* msg=nullptr;
  int i=0;
  while(msg==nullptr){
  connectionHandler->getBytes(singleByte,1);
  msg = encdec->decodeNextByte(singleByte[0]);
  i++;
}

  return msg;
}
short bytesToShort(char* bytesArr)
    {
       short result = (short)((bytesArr[0] & 0xff) << 8);
      result += (short)(bytesArr[1] & 0xff);
       return result;
     }
string getUserNameOrFileName(){return userNameOrFileName; }
Message* getMessageByOpcode(short opcode){
    if(opcode==1 || opcode==2){
      return new RWRQ(opcode,userNameOrFileName);
    }
  if(opcode==5){
    short errNum = atoi(userNameOrFileName.c_str());
    return new ERROR(errNum," ");
  }
  if(opcode==6)return new DIRQ();
  if(opcode==7)return new LOGRQ(userNameOrFileName);
  if(opcode==8)return new DELRQ(userNameOrFileName);
  if(opcode==10)return new DISC();
    return nullptr;
}

void sendACK(short packNum){
  ACK* ack = new ACK(packNum);
  EncoderDecoder encdec = EncoderDecoder() ;
  char* bytes = encdec.Encode(ack);
  send(bytes,ack->getMessageLength());
}
vector<char> ReadAllBytes(char const* filename)
  {

    std::ifstream input( filename, std::ios::binary );
        // copies all data into buffer
        std::vector<char> buffer((std::istreambuf_iterator<char>(input)),(std::istreambuf_iterator<char>()));

     return buffer;

  }

void WriteAllBytes(vector<char> fileBytes,string fileName){
  ofstream a_file(fileName);
  for(int i=0;i<fileBytes.size();i++)
  a_file <<fileBytes.at(i);
  a_file.close();
}

char* vectorToArray(vector<char> bytesVector,int fromPos){
  char* arr = new char[bytesVector.size()-fromPos];
  for(int i=fromPos;i<bytesVector.size();i++){
    arr[i-fromPos] = bytesVector.at(i);
  }
  return arr;
}

void printACK(short blockNum){
   cout<<"ACK "<<blockNum<<endl;
}
void fromKeyBoard(){
    while(opcodeFromKeyBoard!=10){
    Message* msg = nullptr;
  opcodeFromKeyBoard=readOperationFromUser();
  msg = getMessageByOpcode(opcodeFromKeyBoard);
  char* bytes = encdec->Encode(msg);
  send(bytes,msg->getMessageLength());

  }
}
void fromServer(){
  vector<char> allPacketsData;
  while(1){
    short opcode;
    char* recievedBytes;
    recievedBytes = new char();
    Message* message= getBytes();
    opcode=message->getOpcode();



   if(opcode==3){
     DATA* data = dynamic_cast<DATA*>(message);
     if(data->getPacketSize()==512){
       for(int i=0;i<512;i++)
       allPacketsData.push_back(data->getData()[i]);
       sendACK(data->getBlockNumber());
     }else{
       for(int i=0;i<data->getPacketSize();i++)
       allPacketsData.push_back(data->getData()[i]);
       sendACK(data->getBlockNumber());
       if(opcodeFromKeyBoard==1){
       cout<<userNameOrFileName<< " complete"<<endl;
       WriteAllBytes(allPacketsData,userNameOrFileName);
       allPacketsData.clear();
       opcodeFromKeyBoard=0;
     }else{//DIRQ

       for(int i=0;i<allPacketsData.size();i++){
         if(allPacketsData.at(i) == '\0')cout<<endl;
         else{
           cout<<allPacketsData.at(i);
         }

       }
        allPacketsData.clear();
     opcodeFromKeyBoard=0;
     }
     }
   }
   if(opcode==4){
     ACK* ack = dynamic_cast<ACK*>(message);
     printACK(ack->getBlockNumber());
     if(opcodeFromKeyBoard==2){
    sendFile(userNameOrFileName.c_str());
    opcodeFromKeyBoard=0;
  }else{
    if(opcodeFromKeyBoard==10)break;
  }
   }
   if(opcode==5){
     ERROR* error = dynamic_cast<ERROR*>(message);
     cout<<"ERROR "<<error->getErrorCode()<<endl;
   }
   if(opcode==9){
     BCAST* bcast = dynamic_cast<BCAST*>(message);
     if(bcast->getDelOrAdd()==0){
       cout<<"BCAST del "<<bcast->getFileName()<<endl;

     }else{
       cout<<"BCAST add "<<bcast->getFileName()<<endl;
     }

   }
  }
}
void sendFile( char const* fileName){

  vector<char> fileBytes = ReadAllBytes(fileName);
  vector<char> bytesToSend;
  char* ACKbytes = new char();
  int blockNum = 1;
  int i=0;
  while(i<fileBytes.size()){
    bytesToSend.push_back(fileBytes.at(i));
    if((i+1)%512==0){
      DATA* data = new DATA(512,blockNum,vectorToArray(bytesToSend,0));
      char* dataArray = encdec->Encode(data);
      send(dataArray,data->getMessageLength());
      ACK* ack = dynamic_cast<ACK*>(getBytes());
      printACK(ack->getBlockNumber());
      blockNum++;
      bytesToSend.clear();
    }
    i++;
  }
  if(bytesToSend.size()!=0){
    DATA* data = new DATA(bytesToSend.size(),blockNum,vectorToArray(bytesToSend,0));
    char* dataArray = encdec->Encode(data);
    send(dataArray,data->getMessageLength());

  }else{
    if(i==0){//the file we want to send is empty

      DATA* data = new DATA(bytesToSend.size(),blockNum,vectorToArray(bytesToSend,0));
      char* dataArray = encdec->Encode(data);
      send(dataArray,data->getMessageLength());
    }
  }
}
Message* getMessageFromBytes(char* recievedBytes){
  Message* message=nullptr;
  int i=0;
  while(message==nullptr){
   message = encdec->decodeNextByte(recievedBytes[i]);
   i++;
 }
 return message;
}
};
 int main(int argc, char const *argv[]) {
  std::string host = argv[1];
  short port = atoi(argv[2]);
  mutex mutex;
  Client* client=new Client(host,port,&mutex);
  client->connect();
  thread* t1=new thread(bind(&Client::fromKeyBoard,client));
  thread* t2=new thread(bind(&Client::fromServer,client));
  t1->join();
  t2->join();










  return 0;
}
