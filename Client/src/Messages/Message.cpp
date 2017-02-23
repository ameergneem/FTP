#include <string>
#include "Message.h"
  short Message::getOpcode(){
    return opcode;
  }
  void Message::setOpcode(short opcode){
    this->opcode = opcode;
  }
   string Message::getMessageType(){
  return messageType;
  }
   void Message::setMessageType(string messageType ){
  this->messageType = messageType;
  }
