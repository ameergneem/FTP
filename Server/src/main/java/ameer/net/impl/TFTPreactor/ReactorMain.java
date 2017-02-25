package ameer.net.impl.TFTPreactor;
import ameer.net.api.Protocol;
import ameer.net.impl.rci.EncoderDecoder;
import ameer.net.impl.rci.Message;
import ameer.net.srv.BaseServer;
import ameer.net.srv.Server;

public class ReactorMain {

	public static void main(String[] args) {
		int port=Integer.parseInt(args[0]);;
		

	
	Server.reactor(3, port, ()-> new Protocol<Message>(), EncoderDecoder::new).serve();
   

	}

}
