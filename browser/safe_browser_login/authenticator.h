#include<string>

namespace safe_browser_login {

  bool Authenticate(std::string passHash, std::string password);
  bool Decrypt(const std::string& encrypted, std::string* value);
  bool DecryptVPNConfig();

}
