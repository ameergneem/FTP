LDFLAGS:=-lboost_system -lboost_locale -lboost_thread
all: TFTPclient 

TFTPclient : bin/EncoderDecoder.o bin/Menu.o bin/Client.o bin/ConnectionHandler.o
		g++ -o bin/TFTPclient  bin/Menu.o bin/Client.o bin/ConnectionHandler.o bin/EncoderDecoder.o $(LDFLAGS) 


bin/EncoderDecoder.o:src/EncoderDecoder.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/EncoderDecoder.o src/EncoderDecoder.cpp

bin/Menu.o:src/Menu.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Menu.o src/Menu.cpp

bin/Client.o:src/Client.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Client.o src/Client.cpp

bin/ConnectionHandler.o:src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

clean:
	rm -f bin/*
