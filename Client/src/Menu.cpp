#include <iostream>
class Menu{

public:
   void displayMenu(){
  std::cout << "opcode operation" << '\n';
  std::cout << "1 Read request (RRQ)" << '\n';
  std::cout << "2 Write request (WRQ)" << '\n';
  std::cout << "3 Data (DATA)" << '\n';
  std::cout << "4 Acknowledgment (ACK)" << '\n';
  std::cout << "5 Error (ERROR)" << '\n';
  std::cout << "6 Directory listing request (DIRQ)" << '\n';
  std::cout << "7 Login request (LOGRQ)" << '\n';
  std::cout << "8 Delete request (DELRQ)" << '\n';
  std::cout << "9 Broadcast file added/deleted (BCAST)" << '\n';
  std::cout << "10 Disconnect (DISC)" << '\n';
}


};
