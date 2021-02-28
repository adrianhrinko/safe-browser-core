package org.chromium.chrome.browser.login;

import androidx.annotation.NonNull;

import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.NativeMethods;
import org.chromium.chrome.browser.profiles.Profile;

@JNINamespace("chrome::android")
public class LoginServiceBridge {
    private LoginServiceBridge() {
    }

    private static LoginServiceBridge sInstance;

    public static LoginServiceBridge getInstance() {
        ThreadUtils.assertOnUiThread();
        if (sInstance == null) {
            sInstance = new LoginServiceBridge();
        }
        return sInstance;
    }

    /**
     * Authenticates user
     * @param password from the user
     * @return true when password is valid, false otherwise
     */
    public boolean authenticate(String password) {
        return LoginServiceBridgeJni.get().authenticate(password);
    }


    @NativeMethods
    interface Natives {
        boolean authenticate(String password);
    }
}
