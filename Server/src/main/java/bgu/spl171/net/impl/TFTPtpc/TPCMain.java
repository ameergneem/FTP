package bgu.spl171.net.impl.TFTPtpc;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Supplier;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.MessagingProtocol;
import bgu.spl171.net.api.Protocol;
import bgu.spl171.net.impl.rci.EncoderDecoder;
import bgu.spl171.net.impl.rci.Message;
import bgu.spl171.net.srv.BaseServer;
import bgu.spl171.net.srv.BlockingConnectionHandler;
import bgu.spl171.net.srv.Server;

public class TPCMain {

 public static void main(String[] args) {
		int port=Integer.parseInt(args[0]);;
	
		
	Server.threadPerClient(port, ()-> new Protocol<Message>(),EncoderDecoder::new).serve();
	

	
	}



}
