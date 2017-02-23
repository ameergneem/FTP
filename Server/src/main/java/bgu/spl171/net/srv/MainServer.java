package bgu.spl171.net.srv;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.Protocol;
import bgu.spl171.net.api.bidi.BidiMessagingProtocol;
import bgu.spl171.net.api.bidi.ConnectionsImpl;
import bgu.spl171.net.impl.rci.EncoderDecoder;
import bgu.spl171.net.impl.rci.LOGRQ;
import bgu.spl171.net.impl.rci.Message;

public class MainServer<T> extends BaseServer<T> {
	int port;
    private Supplier<BidiMessagingProtocol<T>> protocolFactory ;
    private Supplier<MessageEncoderDecoder<T>> encdecFactory;
	public MainServer(int port, Supplier<BidiMessagingProtocol<T>> protocolFactory,
			Supplier<MessageEncoderDecoder<T>> encdecFactory) {
		super(port, protocolFactory, encdecFactory);
		
		this.protocolFactory=(Supplier<BidiMessagingProtocol<T>>)protocolFactory;
		this.encdecFactory=(Supplier<MessageEncoderDecoder<T>>)encdecFactory;
		
	}

	public static void main(String[] args) {
//		
//		ServerSocket serverSocket=null;
//		try {
//			serverSocket = new ServerSocket(5254);
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		EncoderDecoder encdec = new EncoderDecoder();
//		Protocol<Message> protocol = new Protocol<Message>();
//		
//		Socket socket=null;
//		try {
//			socket = serverSocket.accept();
//			System.out.println("Accepted!");
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		BlockingConnectionHandler<Message> connectionHandler = new BlockingConnectionHandler<Message>(socket, encdec, protocol);
//		 ConnectionsImpl con = new  ConnectionsImpl();
//		 int conId=con.addConnection(connectionHandler);
//		 protocol.start(conId, con);
//		 
//		
//		
//		connectionHandler.run();

		
		int port=5254;

		

		

	}

	@Override
	protected void execute(BlockingConnectionHandler<T> handler) {
		 ConnectionsImpl con = new  ConnectionsImpl();
		 int conId=con.addConnection(handler);
		 protocolFactory.get().start(conId, con);
		 handler.run();
		
	}

}
