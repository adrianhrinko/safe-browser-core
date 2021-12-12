package org.chromium.chrome.browser.login;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import org.chromium.chrome.R;
import android.view.*;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import org.chromium.chrome.browser.login.LoginServiceBridge;
import org.chromium.chrome.browser.sync.BraveSyncDevices;
import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.chrome.browser.vpn.VPNFragment;
import android.content.Intent;
import android.content.IntentFilter;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private View loaderLayout;
    private View passwordLayout;

    private TextView detail;
    private TextView btnConfirm;
    private EditText password;

    private BraveActivity mainActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);


        loaderLayout = (View) rootView.findViewById(R.id.password_loader);
        passwordLayout = (View) rootView.findViewById(R.id.password_form);

        password = (EditText) rootView.findViewById(R.id.passwordEntry);
        detail = (TextView) rootView.findViewById(R.id.loginFragmentDetail);
        detail.setText("Enter your password, please.");
        btnConfirm = (TextView) rootView.findViewById(R.id.loginButton);
        btnConfirm.setOnClickListener(this);
        
        mainActivity = BraveActivity.getBraveActivity();


        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (R.id.loginButton == v.getId()) {
            tryLogin();
            return;
        }
    }

    public void tryLogin() {
        String pass = password.getText().toString();
        LoginServiceBridge loginService = LoginServiceBridge.getInstance();
        BraveSyncDevices deviceInfoSync = BraveSyncDevices.get();

        showLoader();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isPassValid = loginService.authenticate(pass);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isPassValid) {
                            deviceInfoSync.LogAction("login","password_valid");
                            closeFragment();
                        } else {
                            deviceInfoSync.LogAction("login","password_invalid");
                            updateOnInvalidPassword();
                        }             
                    }
                });
            }
         }).start();
    }

    public void showLoader() {
        passwordLayout.setVisibility(View.GONE);
        loaderLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        loaderLayout.setVisibility(View.GONE);
        passwordLayout.setVisibility(View.VISIBLE);
    }

    public void closeFragment() {
        if (null != mainActivity) {
            mainActivity.switchFragment(this, new VPNFragment(), mainActivity.VPN_FRAGMENT_TAG);
        }    
    }

    public void updateOnInvalidPassword() {
        hideLoader();
        detail.setText("Entered password is invalid. Try again, please.");
        detail.setTextColor(this.getResources().getColor(R.color.colorCritical));
    }



}