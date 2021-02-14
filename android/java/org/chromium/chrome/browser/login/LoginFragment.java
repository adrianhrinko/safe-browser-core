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
import org.chromium.chrome.browser.preferences.BravePrefServiceBridge;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private TextView detail;
    private TextView btnConfirm;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        password = (EditText) rootView.findViewById(R.id.passwordEntry);
        detail = (TextView) rootView.findViewById(R.id.loginFragmentDetail);
        detail.setText(BravePrefServiceBridge.getInstance().getPasswordHash());
        btnConfirm = (TextView) rootView.findViewById(R.id.loginButton);
        btnConfirm.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (R.id.loginButton == v.getId()) {
            BravePrefServiceBridge.getInstance().setPasswordHash("\"" + password.getText().toString() + "\"");
            ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove((Fragment) this).commit();
            return;
        }
    }

}