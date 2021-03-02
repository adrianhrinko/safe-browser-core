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


public class LoginFragment extends Fragment implements View.OnClickListener{

    private View loaderLayout;
    private View passwordLayout;

    private TextView detail;
    private TextView btnConfirm;
    private EditText password;

    private LoginServiceBridge loginService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        loginService = LoginServiceBridge.getInstance();

        loaderLayout = (View) rootView.findViewById(R.id.password_loader);
        passwordLayout = (View) rootView.findViewById(R.id.password_form);

        password = (EditText) rootView.findViewById(R.id.passwordEntry);
        detail = (TextView) rootView.findViewById(R.id.loginFragmentDetail);
        detail.setText("Enter your password, please.");
        btnConfirm = (TextView) rootView.findViewById(R.id.loginButton);
        btnConfirm.setOnClickListener(this);

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
        showLoader();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isPassValid = loginService.authenticate(pass);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isPassValid) {
                            closeFragment();
                        } else {
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
        ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove((Fragment) this).commit();
    }

    public void updateOnInvalidPassword() {
        hideLoader();
        detail.setText("Entered password is invalid. Try again, please.");
        detail.setTextColor(this.getResources().getColor(R.color.colorCritical));
    }


}