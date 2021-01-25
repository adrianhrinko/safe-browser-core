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
import android.widget.Toast;
import android.app.Activity;

public class LoginFragment extends Fragment implements View.OnClickListener{

    private TextView btnConfirm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override 
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        btnConfirm = (TextView) rootView.findViewById(R.id.loginButton);
        btnConfirm.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (R.id.loginButton == v.getId()) {
            ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().remove((Fragment) this).commit();
            return;
        }
    }

}