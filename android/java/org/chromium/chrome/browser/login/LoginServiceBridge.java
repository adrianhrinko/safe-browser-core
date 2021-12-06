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
    private String key;

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
     * (should not run on main thread)
     * @param password from the user
     * @return returns true if password is valid, false otherwise
     */
    public boolean authenticate(String password) {
        this.key = LoginServiceBridgeJni.get().authenticate(passHash, password);

        return !this.key.isEmpty();
    }

    /**
     * Decrypts VPN config
     * (should not run on main thread)
     * @param message which is encrypted
     * @return returns decrypted message
     */
    public String decrypt(String message) {
        return LoginServiceBridgeJni.get().decrypt(message, this.key);
    }

    @NativeMethods
    interface Natives {
        String authenticate(String passHash, String password);
        String decrypt(String message, String key);
    }

    
}
