package bgu.spl171.net.impl.TFTPreactor;
import bgu.spl171.net.api.Protocol;
import bgu.spl171.net.impl.rci.EncoderDecoder;
import bgu.spl171.net.impl.rci.Message;
import bgu.spl171.net.srv.BaseServer;
import bgu.spl171.net.srv.Server;

public class ReactorMain {

	public static void main(String[] args) {
		int port=Integer.parseInt(args[0]);;
		

	
	Server.reactor(3, port, ()-> new Protocol<Message>(), EncoderDecoder::new).serve();
   

	}

}
