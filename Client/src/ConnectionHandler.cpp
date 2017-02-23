#include <string>
#include <iostream>
#include <boost/asio.hpp>
using boost::asio::ip::tcp;
using namespace std;
class ConnectionHandler{
private:
    const std::string host_;
    const short port_;
    boost::asio::io_service io_service_;   // Provides core I/O functionality
    tcp::socket socket_;
public:
ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_){


}

bool connect(){
  // std::cout << "Starting connect to "
  //   << host_ << ":" << port_ << std::endl;
try {
    tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
    boost::system::error_code error;
    socket_.connect(endpoint, error);
    if (error)
        throw boost::system::system_error(error);
}
catch (std::exception& e) {
    std::cerr << "Connection failed (Error: " << e.what() << ')' << std::endl;
    return false;
}
return true;
}

bool getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    size_t bytesReadedSize=0;
    boost::system::error_code error;
    try {////////////////////////CASE IF READ LESS THAN WHAT WE NEED!!
        while (!error && bytesToRead > tmp ) {
            tmp+=socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);

        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        //std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}














bool sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        std::cerr << "recv failed (Error: " << e.what() << ')' << std::endl;
        return false;
    }
    return true;
}



// Close down the connection properly.
void close() {
    try{
        socket_.close();
    } catch (...) {
        std::cout << "closing failed: connection already closed" << std::endl;
    }
}

};
