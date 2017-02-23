#include "Message.h"



   void FileNameMessage::setFileName(string filename){
	   this->fileName=filename;
  }
  string FileNameMessage::getFileName(){
	  return fileName;
 }

  void FileNameMessage::setLastByte(char lastByte){
	  this->lastByte = lastByte;
 }
 char FileNameMessage::getLastByte(){
	  return lastByte;
}
