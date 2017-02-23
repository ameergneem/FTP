#FTP
This is a simple FTP program.
coded using two programming languages (C++ for the client and Java for the server).
What makes this FTP so uniqe, is that the server using two design patterns two handle the clients(the user can choose which one two use):
1-Thread-per-client(The simple one): 
 * Advantages: 1)	 scalable:Which mean that every new client will be handled easly.
			   2)	 Low accept latency: every client will be accepted without being blocked for any reason.
			   3)	 Reply latency: because of the "Multi-threaded technic" no client depend on other, so every client has his own thread that take care of him.
			   