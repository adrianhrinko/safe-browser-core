package org.chromium.chrome.browser.login;

import androidx.annotation.NonNull;

import org.chromium.base.ThreadUtils;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.NativeMethods;
import org.chromium.chrome.browser.profiles.Profile;
import org.chromium.chrome.browser.preferences.BravePrefServiceBridge;


@JNINamespace("chrome::android")
public class LoginServiceBridge {

    private String passHash;

    private LoginServiceBridge() {
    }

    private LoginServiceBridge(String passHash) {
        this.passHash = passHash;
    }

    private static LoginServiceBridge sInstance;

    public static LoginServiceBridge getInstance() {
        ThreadUtils.assertOnUiThread();
        if (sInstance == null) {
            String passHash = BravePrefServiceBridge.getInstance().getPasswordHash();
            sInstance = new LoginServiceBridge(passHash);
        }
        return sInstance;
    }

    /**
     * Authenticates user
     * @param password from the user
     * @return true when password is valid, false otherwise
     */
    public boolean authenticate(String password) {
        return LoginServiceBridgeJni.get().authenticate(passHash, password);
    }


    @NativeMethods
    interface Natives {
        boolean authenticate(String passHash, String password);
    }
}
