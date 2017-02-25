package ameer.net.impl.rci;
public class FileNameMessage extends Message {

	protected String fileName;
	protected byte lastByte='0';
	

  public void setFileName(String filename){
	   this.fileName=filename;
  }
 public String getFileName(){
	  return fileName;
 }
 
 public void setLastByte(byte lastByte){
	  this.lastByte = lastByte;
 }
public byte getLastByte(){
	  return lastByte;
}


}
