package org.chromium.chrome.browser.compliance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.view.ViewGroup;
import org.chromium.chrome.R;

import org.chromium.chrome.browser.app.BraveActivity;
import org.chromium.base.Callback;
import org.chromium.chrome.browser.util.SafetyNetCheck;
import org.chromium.chrome.browser.settings.BraveSyncScreensPreference;

import com.google.android.gms.safetynet.HarmfulAppsData;

import android.widget.Toast;

import java.util.*; 

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ComplianceCheckFragment extends Fragment implements View.OnClickListener { 

    private BraveActivity mainActivity;
    private Fragment thisFragment;

    private View loaderLayout;
    private View failureLayout;

    private TextView detail;
    private TextView closeButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_compliance, container, false);

        loaderLayout = (View) rootView.findViewById(R.id.complianceLoader);
        failureLayout = (View) rootView.findViewById(R.id.complianceCheckLayout);

        detail = (TextView) rootView.findViewById(R.id.complianceCheckDetail);
        closeButton = (TextView) rootView.findViewById(R.id.closeAppButton);


        mainActivity = BraveActivity.getBraveActivity();
        thisFragment = this;

        closeButton.setOnClickListener(this);


        return rootView;
    }

    public void updateDetail(String text) {
        detail.setText(text);
    }

    public void showLoader() {
        failureLayout.setVisibility(View.GONE);
        loaderLayout.setVisibility(View.VISIBLE);
    }

    public void hideLoader() {
        loaderLayout.setVisibility(View.GONE);
        failureLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        startSafetyNetCheck();
    }

    @Override
    public void onClick(View v) {
        if (R.id.closeAppButton == v.getId()) {
            showToast("Closing the app...");
            mainActivity.finishAndRemoveTask();
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

        showToast("Performing device attestation...");
        SafetyNetCheck.updateSafetynetStatus(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) {
                    showToast("SafetyNet check succeeded!");
                    performAppScan();
                } else {
                    showToast("SafetyNet check failed.");
                    updateDetail("REASON:\nThe device did not pass the SafetyNet attestation.");
                    hideLoader();
                }
            }
        });
    }

    private void updateDetailWithHarmfulAppList(List<HarmfulAppsData> harmfulApps) {
        StringBuilder txt = new StringBuilder();
        txt.append("REASON:\nSome of installed apps are considered to be harmful:\n\n"); 

        for (HarmfulAppsData harmfulApp : harmfulApps) {
            txt.append(" • ");
            txt.append(harmfulApp.apkPackageName);
            txt.append("\n");
        }
        txt.append("\nPlease, uninstall apps listed above to be able to continue.\n");

        updateDetail(txt.toString());
    }

    public void performAppScan() {
        showToast("Performing app scan ...");
        SafetyNetCheck check = new SafetyNetCheck();
        check.performSafetyNetAppScan(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                if (result) {
                    showToast("App scan is clear!");
                    mainActivity.switchFragment(thisFragment, BraveSyncScreensPreference.newInstance(true), mainActivity.INIT_FRAGMENT_TAG);
                } else {
                    if (result == null) {
                        showToast("App scan failed.");
                        updateDetail("REASON:\nA problem occured during the app scan.");
                    } else {
                        showToast("App scan have found some suspicious apps on your device.");
                        updateDetailWithHarmfulAppList(check.getDetectedHarmfulApps());
                    }
                    hideLoader();
                }
            }
        });
    }


}