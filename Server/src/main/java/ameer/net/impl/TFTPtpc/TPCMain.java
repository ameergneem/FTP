package ameer.net.impl.TFTPtpc;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Supplier;

import ameer.net.api.MessageEncoderDecoder;
import ameer.net.api.MessagingProtocol;
import ameer.net.api.Protocol;
import ameer.net.impl.rci.EncoderDecoder;
import ameer.net.impl.rci.Message;
import ameer.net.srv.BaseServer;
import ameer.net.srv.BlockingConnectionHandler;
import ameer.net.srv.Server;

public class TPCMain {

 public static void main(String[] args) {
		int port=Integer.parseInt(args[0]);;
	
		
	Server.threadPerClient(port, ()-> new Protocol<Message>(),EncoderDecoder::new).serve();
	

	
	}



}
