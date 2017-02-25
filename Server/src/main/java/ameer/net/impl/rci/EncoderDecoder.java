package ameer.net.impl.rci;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.management.DescriptorKey;

import ameer.net.api.MessageEncoderDecoder;




public class EncoderDecoder<T> implements MessageEncoderDecoder<Message> {	
	private List<Byte> decObjectBytes = new ArrayList<Byte>();
	private byte[] opcodeBytes = new byte[2];
	private int opcodeBytesIdx = 0;
    private short dataPacketSize;
    private short dataPacketBlockNum;
	private short decOpcode;
	private byte lastByte = 0;
	
	@Override
	public Message decodeNextByte(byte nextByte) {
      if(opcodeBytesIdx<2){
    	  opcodeBytes[opcodeBytesIdx]=nextByte;
    	  opcodeBytesIdx++;
    	  if(opcodeBytesIdx==2){
    		 decOpcode = bytesToShort(opcodeBytes);
    		 if(decOpcode==6){
 			DIRQ dir = new DIRQ();
 			dir.setOpcode(decOpcode);
 			opcodeBytesIdx=0;
 			return dir;
    		 }
    		 if(decOpcode==10){
    	 			DISC disc = new DISC();
    	 			disc.setOpcode(decOpcode);
    	 			opcodeBytesIdx=0;
    	 			return disc;
    		 }
    	  }
      }else{
    	 
    	
    	     return  decodeToMessage(decOpcode, nextByte);
    	  
    	  
    	  
      }
		
		return null;
	}

	@Override
	public byte[] encode(Message message) {
	
		 byte[] finalArray=null;
		
		opcodeBytes =shortToBytes(message.getOpcode());
		if(message instanceof ACK){
			ACK ack = (ACK)message;
          byte[] blockNum = new byte[2];
          blockNum = shortToBytes(ack.getBlockNumber());
          finalArray = mergeArrays(opcodeBytes, blockNum);
		}
		if(message instanceof BCAST){
			BCAST bcast = (BCAST)message;
			byte[] delOrAdd = new byte[]{(byte) bcast.getDelOrAdd()};
			finalArray = mergeArrays(opcodeBytes, delOrAdd);
			byte[] fileNameBytes = stringToByte(bcast.getFileName());
			finalArray = mergeArrays(finalArray, fileNameBytes);
			byte[] lastByte = new byte[]{bcast.getLastByte()};
			finalArray = mergeArrays(finalArray, lastByte);			
		}if(message instanceof DATA){
			DATA data = (DATA)message;
			byte[] packetSize = shortToBytes(data.getPacketSize());
			byte[] blockNum = shortToBytes(data.getBlockNumber());
			byte[] Data = data.getData();
			finalArray = mergeArrays(opcodeBytes, packetSize);
			finalArray = mergeArrays(finalArray, blockNum);
			finalArray = mergeArrays(finalArray, Data);
		}if(message instanceof DELRQ){
			DELRQ delrq = (DELRQ)message;
			byte[] fileNameBytes = stringToByte(delrq.getFileName());
			byte[] lastByte =new byte[]{delrq.getLastByte()};
			finalArray = mergeArrays(opcodeBytes, fileNameBytes);
			finalArray = mergeArrays(finalArray, lastByte);
		}
		if(message instanceof RWRQ){
			RWRQ rwrq = (RWRQ)message;
			byte[] fileNameBytes = stringToByte(rwrq.getFileName());
			byte[] lastByte =new byte[]{rwrq.getLastByte()};
			finalArray = mergeArrays(opcodeBytes, fileNameBytes);
			finalArray = mergeArrays(finalArray, lastByte);
		}
		
		if(message instanceof DIRQ||message instanceof DISC){
			finalArray = opcodeBytes;
		}
		if(message instanceof ERROR){
			ERROR err = (ERROR)message;
			byte[] errorCode = shortToBytes(err.getErrorCode());
			byte[] errMsg = stringToByte(err.getErrMsg());
			byte[] lastByte = new byte[]{err.getLastByte()};
			finalArray = mergeArrays(opcodeBytes, errorCode);
			finalArray = mergeArrays(finalArray, errMsg);
			finalArray = mergeArrays(finalArray, lastByte);
			
		}
		
		if(message instanceof LOGRQ){
			LOGRQ log = (LOGRQ)message;
			byte[] userNameBytes = stringToByte(log.getUserName());
			byte[] lastByte =new byte[]{log.getLastByte()};
			finalArray = mergeArrays(opcodeBytes, userNameBytes);
			finalArray = mergeArrays(finalArray, lastByte);
		}
		
		return finalArray;
	}
	
	private Message decodeToMessage(short opcode,byte nextByte){
		///////////////////////////////Read and Write
		if(opcode==1||opcode==2){
			if(nextByte!='0'){
			decObjectBytes.add(nextByte);
			return null;
			}
			else{
				RWRQ readWriteMessage = new RWRQ(opcode);
				readWriteMessage.setOpcode(opcode);
				readWriteMessage.setFileName(popString(listToArray(decObjectBytes,0)));
				decObjectBytes.clear();
				opcodeBytesIdx=0;
				return readWriteMessage;
			}
		}
		
		///////////////////////DATA
		if(opcode==3){
			decObjectBytes.add(nextByte);
         if(decObjectBytes.size()==2){
        	 byte[] packetSizeBytes = new byte[2];
        	 packetSizeBytes[0]=decObjectBytes.get(0);
        	 packetSizeBytes[1]=decObjectBytes.get(1);
        	 dataPacketSize = bytesToShort(packetSizeBytes);
         }else
         if(decObjectBytes.size()==4){
        	 byte[] packetBlockNumBytes = new byte[2];
        	 packetBlockNumBytes[0]=decObjectBytes.get(2);
        	 packetBlockNumBytes[1]=decObjectBytes.get(3);
        	 dataPacketBlockNum = bytesToShort(packetBlockNumBytes); 
             if(dataPacketSize==0){
                 DATA dataMsg = new DATA(dataPacketSize,dataPacketBlockNum,listToArray(decObjectBytes,4));
                 decObjectBytes.clear();
                 opcodeBytesIdx = 0;
                 return dataMsg;
               }
         }else
        	 if(decObjectBytes.size()==dataPacketSize+4){//take care about the  packetSizeBytes and packetBlockNumBytes
        		 DATA dataMsg = new DATA();
        		 dataMsg.setOpcode(opcode);
        		 dataMsg.setPacketSize(dataPacketSize);
        		 dataMsg.setBlockNumber(dataPacketBlockNum);
        		 dataMsg.setData(listToArray(decObjectBytes,4));
        		 decObjectBytes.clear();
        		 opcodeBytesIdx=0;
        		 return dataMsg;
        	 }
         
			
		}
		
		///////////////////////////////Acknowledgment
		if(opcode==4){
			if(decObjectBytes.size()==1){
				decObjectBytes.add(nextByte);
				ACK ack = new ACK();
				ack.setOpcode(opcode);
				ack.setBlockNumber(bytesToShort(listToArray(decObjectBytes,0)));
				decObjectBytes.clear();
				opcodeBytesIdx=0;
				return ack;
			}else{
				decObjectBytes.add(nextByte);
				return null;
			}
		}
		
		///////////////////////////////////////Error
		if(opcode==5){
		if(nextByte=='0'&&decObjectBytes.size()>=2 ){
			ERROR err = new ERROR();
			byte[] errCode = new byte[2];
			errCode[0]=decObjectBytes.remove(0);
			errCode[1]=decObjectBytes.remove(0);
			err.setOpcode(opcode);
			err.setErrorCode(bytesToShort(errCode));
			err.setErrMsg(popString(listToArray(decObjectBytes,4)));
			err.setLastByte(lastByte);
			decObjectBytes.clear();
			opcodeBytesIdx=0;
			return err;
		}else{decObjectBytes.add(nextByte);return null;}
		}
		

		
		////////////////////////////////////////Log in request
		
		if(opcode==7){
			
			if(nextByte!='0'){
				decObjectBytes.add(nextByte);
				return null;
				}
				else{
					LOGRQ log = new LOGRQ();
					log.setOpcode(opcode);
					log.setUserName(popString(listToArray(decObjectBytes,0)));
					decObjectBytes.clear();
					opcodeBytesIdx=0;
					return log;
				}
			
		}
		
		if(opcode==8){
			if(nextByte!='0'){
				decObjectBytes.add(nextByte);
				return null;
				}
				else{
					DELRQ del = new DELRQ();
					del.setOpcode(opcode);
					del.setFileName(popString(listToArray(decObjectBytes,0)));
					decObjectBytes.clear();
					opcodeBytesIdx=0;
					return del;
				}
		}
		
		
		///////////////////////////////////Broadcast file added/deleted 
		if(opcode==9){
	          
			if(nextByte=='0'){
				if(decObjectBytes.size()==0){decObjectBytes.add(nextByte);return null;}
				else{
					
					BCAST bcast = new BCAST();
					bcast.setOpcode(opcode);
					bcast.setDelOrAdd(decObjectBytes.remove(0));
					bcast.setFileName(popString(listToArray(decObjectBytes,3)));
					decObjectBytes.clear();
					opcodeBytesIdx=0;
					return bcast;
				}

			}else{
				decObjectBytes.add(nextByte);
				return null;
			}
			
		}
		
		
		
		
		
		return null;
	}
	

	
	private short bytesToShort(byte[] byteArr)
	{
	    short result = (short)((byteArr[0] & 0xff) << 8);
	    result += (short)(byteArr[1] & 0xff);
	    return result;
	}
	
	private byte[] shortToBytes(short num)
	{
	    byte[] bytesArr = new byte[2];
	    bytesArr[0] = (byte)((num >> 8) & 0xFF);
	    bytesArr[1] = (byte)(num & 0xFF);
	    return bytesArr;
	}
	
     private byte[] stringToByte(String str){
    	 return str.getBytes();
     }
	 private String popString(byte[] stringBytes) {

	        String result = new String(stringBytes, 0, stringBytes.length,StandardCharsets.UTF_8);
	        return result;
	    }
	 private byte[] mergeArrays(byte[] firstArray,byte[] secondArray){
		 byte[] mergedArray = new byte[firstArray.length+secondArray.length];
         int counter=0;
		 for(int i=0;i<firstArray.length;i++){
			 mergedArray[i]=firstArray[i];
			 counter++; 
		 }
		 for(int i=0;i<secondArray.length;i++){
			 mergedArray[counter+i] = secondArray[i];
		 }
		 return mergedArray;
		 
	 }
	 private byte[] listToArray(List<Byte> list,int fromPos){
		 byte[] array = new byte[list.size()-fromPos];
		 for(int i=fromPos;i<list.size();i++){
			 array[i-fromPos]=list.get(i);
		 }
		 return array;
	 }


	 
	 
	 
	 
}
