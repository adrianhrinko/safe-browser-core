#include<string>

namespace safe_browser_login {

  std::string Authenticate(const std::string passHash, const std::string password);
  bool Decrypt(const std::string key, const std::string& encrypted, std::string* value);
}
