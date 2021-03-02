#include "brave/build/android/jni_headers/LoginServiceBridge_jni.h"

#include "build/build_config.h"
#include "base/android/jni_string.h"
#include "brave/browser/safe_browser_login/authenticator.h"
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

using base::android::ConvertUTF8ToJavaString;
using base::android::ConvertUTF8ToJavaString;
using base::android::JavaParamRef;
using base::android::ScopedJavaLocalRef;

namespace chrome {
namespace android {


  jboolean JNI_LoginServiceBridge_Authenticate(
      JNIEnv* env,
      const base::android::JavaParamRef<jstring>& passHash,
      const base::android::JavaParamRef<jstring>& password) {
      LOG(INFO) << "Checking password.";
  
      return safe_browser_login::Authenticate(ConvertJavaStringToUTF8(env, passHash), ConvertJavaStringToUTF8(env, password));
    }
}  // namespace android
}  // namespace chrome
