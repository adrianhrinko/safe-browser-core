#include "brave/browser/safe_browser_login/authenticator.h"

#include "build/build_config.h"
#include "base/android/jni_string.h"
#include "brave/common/pref_names.h"
#include "brave/components/brave_perf_predictor/browser/buildflags.h"
#include "brave/components/brave_referrals/common/pref_names.h"
#include "brave/components/brave_rewards/common/pref_names.h"
#include "brave/components/brave_shields/browser/brave_shields_util.h"
#include "brave/components/brave_sync/brave_sync_prefs.h"
#include "brave/components/p3a/buildflags.h"
#include "chrome/browser/content_settings/host_content_settings_map_factory.h"
#include "chrome/browser/browser_process.h"
#include "chrome/browser/profiles/profile.h"
#include "chrome/browser/profiles/profile_android.h"
#include "chrome/browser/profiles/profile_manager.h"
#include "components/content_settings/core/browser/host_content_settings_map.h"
#include "components/prefs/pref_service.h"

#include "crypto/encryptor.h"
#include "crypto/hmac.h"
#include "crypto/random.h"
#include "crypto/symmetric_key.h"
#include "base/base64.h"
#include "base/check_op.h"

using base::Base64Decode;
using base::Base64Encode;
using crypto::HMAC;
using crypto::SymmetricKey;

namespace safe_browser_login {

  std::string Authenticate(const std::string passHash, const std::string password) {

        const size_t kCostParameter = 8192;  // 2^13.
        const size_t kBlockSize = 8;
        const size_t kParallelizationParameter = 11;
        const size_t kMaxMemoryBytes = 32 * 1024 * 1024;  // 32 MiB.
        const size_t kHashSizeInBites = 256;
        const size_t kHashSizeInBytes = kHashSizeInBites / 8;

        LOG(INFO) << "Authentication started.";

        std::string hashDecoded;
        
        if(!base::Base64Decode(passHash, &hashDecoded)) {
            LOG(ERROR) << "Password hash b64 decoding failed.";
            return "";
        }

        std::string salt = hashDecoded.substr(kHashSizeInBytes);
        
        std::string xpectedHash = hashDecoded.substr(0, kHashSizeInBytes);

        std::unique_ptr<SymmetricKey> hash = SymmetricKey::DeriveKeyFromPasswordUsingScrypt(
        SymmetricKey::AES, password, salt, kCostParameter, kBlockSize,
        kParallelizationParameter, kMaxMemoryBytes,
        kHashSizeInBites);

        if (xpectedHash != hash->key()) {
          return "";
        }

        std::unique_ptr<SymmetricKey> decrKey = SymmetricKey::DeriveKeyFromPasswordUsingScrypt(
        SymmetricKey::AES, password, hash->key(), kCostParameter, kBlockSize,
        kParallelizationParameter, kMaxMemoryBytes,
        kHashSizeInBites);

        std::string decrKeyEncoded;
        
        base::Base64Encode(decrKey->key(), &decrKeyEncoded);

        return decrKeyEncoded;
  }

  bool Decrypt(const std::string key, const std::string& encrypted, std::string* value) {

    const size_t kKeySize = 16;
    const size_t kIvSize = 16;
    const size_t kHashSize = 32;

    std::string input;
    std::string keyDecoded;
        
    if(!base::Base64Decode(key, &keyDecoded)) {
      LOG(ERROR) << "Key hash b64 decoding failed.";
      return false;
    }

    if (!Base64Decode(encrypted, &input)) {
      LOG(ERROR) << "B64 decoding failed.";
      return false;
    }

    if (input.size() < kIvSize * 2 + kHashSize) {
      LOG(ERROR) << "Input size is not valid. (" << input.size() << ")";
      return false;
    }

    if (keyDecoded.size() != kKeySize * 2) {
      LOG(ERROR) << "Decr key check failed. (" << key.size() << ")";
      return false;
    }

    std::string hmacKey = keyDecoded.substr(kKeySize);    
    std::string encrKeyRaw = keyDecoded.substr(0, kKeySize);

    std::unique_ptr<SymmetricKey> encrKey = SymmetricKey::Import(SymmetricKey::AES, encrKeyRaw);
      
    // The input is:
    // * iv (16 bytes)
    // * ciphertext (multiple of 16 bytes)
    // * hash (32 bytes)
    std::string iv(input.substr(0, kIvSize));
    std::string ciphertext(
      input.substr(kIvSize, input.size() - (kIvSize + kHashSize)));
    std::string hash(input.substr(input.size() - kHashSize, kHashSize));

    HMAC hmac(HMAC::SHA256);
    if (!hmac.Init(hmacKey)) {
      LOG(ERROR) << "HMAC initialization failed.";
      return false;
    }

    if (!hmac.Verify(ciphertext, hash)) {
      LOG(ERROR) << "HMAC verification failed.";
      return false;
    }

    crypto::Encryptor encryptor;
    if (!encryptor.Init(encrKey.get(), crypto::Encryptor::CBC, iv)) {
      LOG(ERROR) << "Encryptor initialization failed.";
      return false;
    }
    if (!encryptor.Decrypt(ciphertext, value)) {
      LOG(ERROR) << "Decryption process failed.";
      return false;
    }

    return true;
  }

}

