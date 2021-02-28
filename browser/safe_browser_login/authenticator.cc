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


namespace {

Profile* GetOriginalProfile() {
  return ProfileManager::GetActiveUserProfile()->GetOriginalProfile();
}

}  // namespace

namespace safe_browser_login {

  bool Authenticate(std::string password) {

        const size_t kCostParameter = 8192;  // 2^13.
        const size_t kBlockSize = 8;
        const size_t kParallelizationParameter = 11;
        const size_t kMaxMemoryBytes = 32 * 1024 * 1024;  // 32 MiB.
        const size_t kHashSize = 32;

        LOG(INFO) << "Authentication started.";

        std::string passHash= GetOriginalProfile()->GetPrefs()->GetString(kPasswordHash);
        std::string hashDecoded;
        if(!base::Base64Decode(passHash, &hashDecoded)) {
            LOG(ERROR) << "Password hash b64 decoding failed.";
            return false;
        }


        std::string salt = hashDecoded.substr(kHashSize);
        std::string xpectedHash = hashDecoded.substr(0, kHashSize);

        std::unique_ptr<SymmetricKey> hash = SymmetricKey::DeriveKeyFromPasswordUsingScrypt(
        SymmetricKey::AES, password, salt, kCostParameter, kBlockSize,
        kParallelizationParameter, kMaxMemoryBytes,
        kHashSize);

        return xpectedHash == hash->key();
  }

}

