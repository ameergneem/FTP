package ameer.net.api;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import ameer.net.api.bidi.BidiMessagingProtocol;
import ameer.net.api.bidi.Connections;
import ameer.net.api.bidi.ConnectionsImpl;
import ameer.net.impl.rci.ACK;
import ameer.net.impl.rci.BCAST;
import ameer.net.impl.rci.DATA;
import ameer.net.impl.rci.DELRQ;
import ameer.net.impl.rci.DIRQ;
import ameer.net.impl.rci.ERROR;
import ameer.net.impl.rci.FileNameMessage;
import ameer.net.impl.rci.LOGRQ;
import ameer.net.impl.rci.Message;
import ameer.net.impl.rci.RWRQ;
import ameer.net.srv.BlockingConnectionHandler;
public class Protocol<T> implements BidiMessagingProtocol<T> {
	ConnectionsImpl<T> connections;
	int connectId;
	String fileName;
	String name;
	short block=0;
	ArrayList<byte[]> dataList=new ArrayList<byte[]>();
	byte[] fileBytes;
	boolean login = false;
	boolean sendingData=false;

	
	@Override
	public boolean shouldTerminate() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start(int connectionId, Connections<T> connections) {
		connectId=connectionId;
		this.connections=(ConnectionsImpl<T>) connections;
		
		
	}

	@Override
	public void process(T message) {
		if(login){
			if(((Message)message).getOpcode()==1)readReq(message);
			else if(((Message)message).getOpcode()==2) WriteReq(message);
			else if(((Message)message).getOpcode()==3) Data(message);
			else if(((Message)message).getOpcode()==4) ack(message);
			else if(((Message)message).getOpcode()==5) error(message);
			else if(((Message)message).getOpcode()==6) DIRQ(message);
			else if(((Message)message).getOpcode()==8) deleteReq(message);
			else if(((Message)message).getOpcode()==10)disconnect();
		}
		else{
			if(((Message)message).getOpcode()==7) logInReq(message);
			else if(((Message)message).getOpcode()==10)disconnect();
			else connections.send(connectId,(T)new ERROR((short)6,"User need to login the first"));
		}
			
	}


	private void deleteReq(T message) {
		String filePath= "Files"+File.separator+((DELRQ)message).getFileName();
		File file = new File(filePath);
		if(file.delete()){
                      connections.send(connectId,(T) new ACK((short)0));
			connections.broadcast((T)new BCAST((byte)0,((DELRQ)message).getFileName()));
		}
		else connections.send(connectId,(T)new ERROR((short)1,"File name: "+((DELRQ)message).getFileName()+" not found"));
		
		
	}

	private void disconnect() {
		ACK msg= new ACK((short) 0);
		connections.send(connectId, ((T) msg));
		connections.removeNameAndDisconnect(connectId,name);
	}

	
	private void logInReq(T msg) {
		 name=((LOGRQ)msg).getUserName();
		connections.send(connectId,(T) new ACK((short)0));
		if ( connections.addName(name,connectId) && name.length()>0){
			//((BlockingConnectionHandler<T>)connections.getHandler(connectId)).setName(name);
			login=true;
		}
		
		else {connections.send(connectId,(T)new ERROR((short)7,"Login "+name+" already connected"));}
		
	}

	private void DIRQ(T msg) {
		block=0;
		File file = new File("Files");
		String [] dirq=file.list();
		fileBytes=toByteArray(dirq);
		sendingData=true;
		sendDataPack();
		

		
	}

	

	private void error(T msg) {
		fileBytes=null;
		sendingData=false;
		connections.send(connectId,(T)new ERROR((short)7,"Login "+"Ameer"+" already connected"));

	}

	private void ack(T msg) {
		if(((ACK) msg).getBlockNumber()==block){
			if(sendingData)sendDataPack();
		}
		else connections.send(connectId,(T)new ERROR((short)0,"invalid block"));
			
		
	}

	private void Data(T msg) {
		if(((DATA) msg).getBlockNumber()==block){
			dataList.add(((DATA)msg).getData());
			if(((DATA)msg).getPacketSize()<512){
				String filePath="Files"+File.separator+fileName;
				Path path = Paths.get(filePath);
				try {
					Files.write(path,getFinalData());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connections.send(connectId,(T)new ACK(block++));
				connections.broadcast((T)new BCAST((byte)1,fileName));
				block=0;
				
			}else
			connections.send(connectId,(T)new ACK(block++));
			
		}
		else connections.send(connectId,(T)new ERROR((short)0,"invalid block"));
	}

	

	private void WriteReq(T msg) {
		block=0;
		fileName=((FileNameMessage)msg).getFileName();
		String filePath="Files"+File.separator+fileName;
			File file= new File(filePath);
			
				if(!file.exists()){
					connections.send(connectId,(T) new ACK((short)0));
					block++;
					}
					else connections.send(connectId,(T)new ERROR((short)5,"File name: "+fileName+" allready exists"));
	
			
	}

	private void readReq(T msg) {
		Path path=Paths.get("Files"+File.separator+((RWRQ)msg).getFileName());
		try {
			fileBytes=Files.readAllBytes(path);
			sendingData=true;
			block=0;
			sendDataPack();
		} catch (IOException e) {
			connections.send(connectId, (T) new ERROR((short)1,"File name: "+((RWRQ)msg).getFileName()+" not found"));
		}
		}
	private void sendDataPack() {

		byte[]data=new byte[Math.min(512,fileBytes.length-512*block)];
		for(int i=0;i<data.length;i++)
			data[i]=fileBytes[i+512*block];
		
		connections.send(connectId,(T)new DATA((short)data.length,++block,data));
		if(data.length<512){
			sendingData=false;
			fileBytes=null;
			}
		
	}

	private byte[] getFinalData() {
		int size=0;
		
		for(int i=0;i<dataList.size();i++)size+=dataList.get(i).length;
		byte[] finalData=new byte[size];
		int j=0;
		while(!dataList.isEmpty()){
			byte[] temp=dataList.remove(0);
			for(int i=0;i<temp.length;i++)
				finalData[j++]=temp[i];
			
		}
		return finalData;
	}
	private byte[] toByteArray(String[] dirq) {
		String intoOneString="";
		for(int i=0;i<dirq.length;i++){
			intoOneString+=dirq[i]+"\0";
		}
		
		return intoOneString.getBytes();
	}

}
