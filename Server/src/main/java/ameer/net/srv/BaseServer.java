package ameer.net.srv;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

import ameer.net.api.MessageEncoderDecoder;
import ameer.net.api.MessagingProtocol;
import ameer.net.api.bidi.BidiMessagingProtocol;
import ameer.net.api.bidi.ConnectionsImpl;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    ConnectionsImpl connections=new ConnectionsImpl();
    
    

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {

        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {

                Socket clientSock = serverSock.accept();
                MessageEncoderDecoder enDe=encdecFactory.get();
                BidiMessagingProtocol protocol = protocolFactory.get();
                BlockingConnectionHandler<T> handler=new BlockingConnectionHandler<T>(clientSock,enDe, protocol);
               
               protocol.start(connections.addConnection(handler), connections);

                execute(handler);
            }
        } catch (IOException ex) {
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}