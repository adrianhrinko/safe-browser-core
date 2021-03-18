package org.chromium.chrome.browser.compliance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.chromium.chrome.R;

import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.base.Callback;
import org.chromium.chrome.browser.util.SafetyNetCheck;
import org.chromium.chrome.browser.settings.BraveSyncScreensPreference;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ComplianceCheckFragment extends Fragment implements View.OnClickListener { 

    private BraveActivity mainActivity;
    private Fragment thisFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_compliance, container, false);

        mainActivity = BraveActivity.getBraveActivity();
        thisFragment = this;

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startSafetyNetCheck();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private Toast mToast;
    protected void showToast(String value) {

        if (mToast == null) {
            mToast = Toast.makeText(this.getContext(), "", Toast.LENGTH_SHORT);
        } else {
            mToast.cancel();
            mToast = Toast.makeText(this.getContext(), "", Toast.LENGTH_SHORT);
        }

        mToast.setText(value);
        mToast.show();
    }

    public void startSafetyNetCheck() {
        if (null == mainActivity) {
            showToast("No connection with main activity");
            return;
        }

        showToast("SafetyNet check started!");
        SafetyNetCheck.updateSafetynetStatus(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) {
                    showToast("SafetyNet check succeeded!");
                    performAppScan();
                } else {
                    showToast("SafetyNet check failed.");
                }
            }
        });
    }

    public void performAppScan() {
        SafetyNetCheck.performSafetyNetAppScan(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) {
                    showToast("App scan is clear!");
                    mainActivity.switchFragment(thisFragment, BraveSyncScreensPreference.newInstance(true), mainActivity.INIT_FRAGMENT_TAG);
                } else {
                    if (result == null) {
                        showToast("App scan failed.");
                    } else {
                        showToast("App scan have found some suspicious apps on your device.");
                    }
                }
            }
        });
    }


}