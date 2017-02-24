#FTP
This is a simple FTP program.
coded using two programming languages (C++ for the client and Java for the server).
What makes this FTP so uniqe, is that the server using two design patterns two handle the clients(the user can choose which one two use):
1-	Thread-per-client(The simple one): 
 * Advantages: 1)	 scalable:Which mean that every new client will be handled easly.
			   2)	 Low accept latency: every client will be accepted without being blocked for any reason.
			   3)	 Reply latency: because of the "Multi-threaded technic" no client depend on other, so every client has his own thread that take care of him.
			   
 * Disadvatage: Low efficiency: the only problem with this design pattern is the efficiency which can holds "sub-disadvantages" inside it.
								if you are know what "multi-threaded" expression means, you already recognize what is the "specific" problem here.
								this kind of servers can't handle more than a few hundreds of clients (RAM issue), and every time the number of the clients
								increasing, the reply latency will increase too (because of the context switch the CPU is making between threads).

2- The Reactor Design Pattern (The complex one): although this version of the rector is a "simple" one, it stills complex.
												 this design pattern came to solve the low efficiency of the Thread-per-client design pattern.
												 

*NOTE: there are some "extra" classes in the server project folder, you are free two take a look.