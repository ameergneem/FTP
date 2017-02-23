#include <iostream>
#include "EncoderDecoder.cpp"
#include <stdio.h>
#include "../include/Message.h"



 int main(int argc, char const *argv[]) {
  DATA* data = new DATA(45,7,"Hithere");
  ERROR* err = new ERROR(22,"Hey There");
  DIRQ* dirq = new DIRQ();
  LOGRQ* logrq = new LOGRQ("Ameer");
  DELRQ* del = new DELRQ("file.txt");
  BCAST* bcast = new BCAST("file.txt",'1');
  DISC* disc = new DISC();
  EncoderDecoder *encdec = new EncoderDecoder();
  char* array = encdec->Encode(disc);

  DISC* disc2=NULL;




for(int i=0;i<3;i++){
  disc2 = dynamic_cast<DISC*>(encdec->decodeNextByte(array[i]));
}

 cout<<disc2->getOpcode()<<endl;






  return 0;
}
