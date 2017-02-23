#include <string>
#include <cstring>
#include <vector>
#include <iostream>
#include <boost/asio.hpp>

#include "../include/Message.h"

using namespace std;
class EncoderDecoder{

  private:
    vector<char> *decObjectBytes = new vector<char>();
     char* opcodeBytes = new char[2];
 int opcodeBytesIdx = 0;
   short dataPacketSize=0;
   short dataPacketBlockNum=0;
 short decOpcode;

public:
char* Encode(Message* message){
  char* finalArray=nullptr;
  opcodeBytes = shortToBytes(message->getOpcode());
  if( ACK* ack = dynamic_cast<ACK*>(message)){
         char* blockNum = new char[2];
         blockNum = shortToBytes(ack->getBlockNumber());
         finalArray = mergeArrays(opcodeBytes, blockNum,2,2);
  }

  if( BCAST* bcast = dynamic_cast<BCAST*>(message)){

    char* delOrAdd = new char(bcast->getDelOrAdd());
    finalArray = mergeArrays(opcodeBytes, delOrAdd,2,1);
    char* fileNameBytes = stringToByte(bcast->getFileName());
    finalArray = mergeArrays(finalArray, fileNameBytes,3,(bcast->getFileName()).length());
    char* lastByte = new char(bcast->getLastByte());
    finalArray = mergeArrays(finalArray, lastByte,3+(bcast->getFileName()).length(),1);

  }
  if( DATA* data = dynamic_cast<DATA*>(message)){
    char* packetSize = shortToBytes(data->getPacketSize());
    char* blockNum = shortToBytes(data->getBlockNumber());
    char* Data = data->getData();
    finalArray = mergeArrays(opcodeBytes, packetSize,2,2);
    finalArray = mergeArrays(finalArray, blockNum,4,2);
    finalArray = mergeArrays(finalArray, Data,6,data->getPacketSize());

  }
  if( DELRQ* delrq = dynamic_cast<DELRQ*>(message)){
    char* fileNameBytes = stringToByte(delrq->getFileName());
    char* lastByte =new char(delrq->getLastByte());
   finalArray = mergeArrays(opcodeBytes, fileNameBytes,2,(delrq->getFileName()).length());
   finalArray = mergeArrays(finalArray, lastByte,2+(delrq->getFileName()).length(),1);

  }
  if( RWRQ* rwrq = dynamic_cast<RWRQ*>(message)){
    char* fileNameBytes = stringToByte(rwrq->getFileName());
    char* lastByte =new char(rwrq->getLastByte());
    finalArray = mergeArrays(opcodeBytes, fileNameBytes,2,(rwrq->getFileName()).length());
    finalArray = mergeArrays(finalArray, lastByte,2+(rwrq->getFileName()).length(),1);
   }
  if(DIRQ* dirq = dynamic_cast<DIRQ*>(message)){
    finalArray =opcodeBytes;
  }

  if(DISC* disc = dynamic_cast<DISC*>(message)){
  finalArray =opcodeBytes;
  }
  if(ERROR* err = dynamic_cast<ERROR*>(message)){
     char* errorCode = shortToBytes(err->getErrorCode());
     char* errMsg = stringToByte(err->getErrMsg());
     char* lastByte = new char(err->getLastByte());
     finalArray = mergeArrays(opcodeBytes, errorCode,2,2);
     finalArray = mergeArrays(finalArray, errMsg,4,(err->getErrMsg()).length());
    finalArray = mergeArrays(finalArray, lastByte,4+(err->getErrMsg()).length(),1);
  }
  if(LOGRQ* log = dynamic_cast<LOGRQ*>(message)){
     char* userNameBytes = stringToByte(log->getUserName());
      char* lastByte =new char(log->getLastByte());
      finalArray = mergeArrays(opcodeBytes, userNameBytes,2,(log->getUserName()).length());
      finalArray = mergeArrays(finalArray, lastByte,2+(log->getUserName()).length(),1);
  }


  return finalArray;
}

Message* decodeNextByte(char nextByte){
  if(opcodeBytesIdx<2){
  opcodeBytes[opcodeBytesIdx]=nextByte;
  opcodeBytesIdx++;
  if(opcodeBytesIdx==2){
    decOpcode = bytesToShort(opcodeBytes);
    if(decOpcode==6){
      DIRQ* dir = new DIRQ();
      opcodeBytesIdx = 0;
      return dir;
    }
  }
  }else{
   return decodeToMessage(decOpcode, nextByte);

  }

return nullptr;
}
private:
Message* decodeToMessage(short opcode,char nextByte){
  if(opcode==1||opcode==2){

			if(nextByte!='0'){
			decObjectBytes->push_back(nextByte);
			return nullptr;
			}
			else{
				RWRQ* readWriteMessage = new RWRQ(opcode,vectorToArray(*decObjectBytes,0));
				decObjectBytes->clear();
        opcodeBytesIdx = 0;
				return readWriteMessage;
			}
		}

    ///////////////////////DATA
  if(opcode==3){
    decObjectBytes->push_back(nextByte);
       if(decObjectBytes->size()==2){
         char* packetSizeBytes = new char[2];
         packetSizeBytes[0]=decObjectBytes->at(0);
         packetSizeBytes[1]=decObjectBytes->at(1);
         dataPacketSize = bytesToShort(packetSizeBytes);
       }else
       if(decObjectBytes->size()==4){
         char* packetBlockNumBytes = new char[2];
         packetBlockNumBytes[0]=decObjectBytes->at(2);
         packetBlockNumBytes[1]=decObjectBytes->at(3);
         dataPacketBlockNum = bytesToShort(packetBlockNumBytes);
         if(dataPacketSize==0){
           DATA* dataMsg = new DATA(dataPacketSize,dataPacketBlockNum,vectorToArray(*decObjectBytes,4));
           decObjectBytes->clear();
           opcodeBytesIdx = 0;
           return dataMsg;
         }
       }else
         if(decObjectBytes->size()==dataPacketSize+4){//take care about the  packetSizeBytes and packetBlockNumBytes
           DATA* dataMsg = new DATA(dataPacketSize,dataPacketBlockNum,vectorToArray(*decObjectBytes,4));
           decObjectBytes->clear();
           opcodeBytesIdx = 0;
           return dataMsg;
         }
  }

  ///////////////////////////////Acknowledgment
if(opcode==4){
  decObjectBytes->push_back(nextByte);
  if(decObjectBytes->size()==2){
    char* blockNumBytes = new char[2];
    blockNumBytes[0]=decObjectBytes->at(0);
    blockNumBytes[1]=decObjectBytes->at(1);
    //decObjectBytes->push_back(nextByte);
    ACK* ack = new ACK(bytesToShort(blockNumBytes));
    decObjectBytes->clear();
    opcodeBytesIdx = 0;
    return ack;
  }
}

///////////////////////////////////////Error
  if(opcode==5){

  if(nextByte=='0'&&decObjectBytes->size()>=2 ){
    char* errCode = new char[2];
    errCode[0]=decObjectBytes->at(0);
    errCode[1]=decObjectBytes->at(1);
    ERROR* err = new ERROR(bytesToShort(errCode),vectorToArray(*decObjectBytes,2));
    decObjectBytes->clear();
    opcodeBytesIdx = 0;
    return err;
  }else{decObjectBytes->push_back(nextByte);return nullptr;}
  }

  ///////////////////////////////////////////Directory listing request
		if(opcode==6){
			DIRQ* dir = new DIRQ();
      opcodeBytesIdx = 0;
			return dir;
		}

    ////////////////////////////////////////Log in request

  if(opcode==7){

    if(nextByte!='0'){
      decObjectBytes->push_back(nextByte);
      return nullptr;
      }
      else{
        LOGRQ* log = new LOGRQ(vectorToArray(*decObjectBytes,0));
        decObjectBytes->clear();
        return log;
      }
  }



  if(opcode==8){
    if(nextByte!='0'){
      decObjectBytes->push_back(nextByte);
      return nullptr;
      }
      else{
        DELRQ* del = new DELRQ(vectorToArray(*decObjectBytes,0));
        decObjectBytes->clear();
        opcodeBytesIdx = 0;
        return del;
      }
  }

  ///////////////////////////////////Broadcast file added/deleted
  if(opcode==9){

    if(nextByte=='0'){
      if(decObjectBytes->size()==0){decObjectBytes->push_back(nextByte);return nullptr;}
      else{
        string name ="";
        for(int i=1;i<decObjectBytes->size();i++){
          name+=decObjectBytes->at(i);
        }
        BCAST* bcast = new BCAST(name,decObjectBytes->at(0));
        decObjectBytes->clear();
        opcodeBytesIdx = 0;
        return bcast;
      }

    }else{
      decObjectBytes->push_back(nextByte);
      return nullptr;
    }

  }

  if(opcode==10){
    DISC* disc = new DISC();
    opcodeBytesIdx = 0;
    return disc;
  }
return nullptr;


}
   char* stringToByte(string str){
       char* ret = (char*)str.c_str();
      return ret;
   }
 string popString(char* bytes){
  string s(bytes);
  return s;
 }
short bytesToShort(char* bytesArr)
    {
       short result = (short)((bytesArr[0] & 0xff) << 8);
      result += (short)(bytesArr[1] & 0xff);
       return result;
     }
    char* shortToBytes(short num)
     {
       char* bytesArr = new char[2];
       bytesArr[0] = ((num >> 8) & 0xFF);
       bytesArr[1] = (num & 0xFF);
      return bytesArr;
     }
char* vectorToArray(vector<char> bytesVector,int fromPos){
  char* arr = new char[bytesVector.size()-fromPos];
  int i;
  for( i=fromPos;i<bytesVector.size();i++){
    arr[i-fromPos] = bytesVector.at(i);
  }
  return arr;
}

char* BCASTvectorToArray(vector<char> bytesVector,int fromPos){
  char* arr = new char[bytesVector.size()-fromPos];
  int i;
  for( i=fromPos;i<bytesVector.size()-1;i++){
    arr[i-fromPos] = bytesVector.at(i);
  }
  return arr;
}

char* mergeArrays(char* arr1, char* arr2,int size1,int size2){
  char* mergedArray = new char[size1+size2];
      int counter=0;
  for(int i=0;i<size1;i++){
    mergedArray[i]=arr1[i];
    counter++;
  }
  for(int i=0;i<size2;i++){
    mergedArray[counter+i] = arr2[i];
  }


  return mergedArray;

}


};
