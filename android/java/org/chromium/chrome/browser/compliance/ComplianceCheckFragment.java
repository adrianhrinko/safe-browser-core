package org.chromium.chrome.browser.compliance;

import org.chromium.base.BuildInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import org.chromium.chrome.browser.preferences.BravePrefServiceBridge;

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
        startCheck();
    }

    @Override
    public void onClick(View v) {
        if (R.id.closeAppButton == v.getId()) {
            showToast("Closing the app...");
            mainActivity.finishAndRemoveTask();
        }

    }

    private void startCheck() {
        showLoader();
        if (isbrowserAndOSUpToDate()) {
            performSafetyNetCheck();
        } else {
            showToast("Versions check failed.");
            updateDetail("REASON:\nBrowser or Android version is lower than the minimum set in the policy.\nUpdate browser or Android to the latest version please.");
            hideLoader();
        }
    }

    private boolean isbrowserAndOSUpToDate() {
        int browserMinVersion = BravePrefServiceBridge.getInstance().getMinBrowserVersion();
        int osMinVersion = BravePrefServiceBridge.getInstance().getMinAndroidVersion();

        return browserMinVersion <= BuildInfo.getInstance().versionCode && 
            osMinVersion <= android.os.Build.VERSION.SDK_INT;
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

    public void performSafetyNetCheck() {
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

    private List<String> getPackageNames(List<HarmfulAppsData> harmfulApps) {
        List<String> packages = new ArrayList<String>();

        for (HarmfulAppsData harmfulApp : harmfulApps) {
            packages.add(harmfulApp.apkPackageName);
        }
        
        return packages;
    }

    private void updateDetailWithHarmfulAppList(List<String> harmfulApps) {
        StringBuilder txt = new StringBuilder();
        txt.append("REASON:\nSome of installed apps are considered to be harmful:\n\n"); 

        for (String harmfulApp : harmfulApps) {
            txt.append(" • ");
            txt.append(harmfulApp);
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
                    performInstallationScourcesScan();
                } else {
                    if (result == null) {
                        showToast("App scan failed.");
                        updateDetail("REASON:\nA problem occured during the app scan.");
                    } else {
                        showToast("App scan have found some suspicious apps on your device.");
                        updateDetailWithHarmfulAppList(getPackageNames(check.getDetectedHarmfulApps()));
                    }
                    hideLoader();
                }
            }
        });
    }

    public void performInstallationScourcesScan() {
        showToast("CHecking installation sources ...");
        String browsersPackageName = getActivity().getApplication().getPackageName();
        PackageManager packageManager = getActivity().getApplication().getPackageManager();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> installedApps = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS );

                List<String> appsFromUnknownSources = new ArrayList<String>();
                
                for(PackageInfo info : installedApps) {
                    // retrieving the installer package name
                    String packageName = info.packageName;
        
                    // TODO: only for dev purposes, delete in production
                    // the browser itself should be always installed from trusted source
                    if (packageName.contains(browsersPackageName)) continue;
        
                    String installerPackageName = packageManager.getInstallerPackageName(packageName);
                    // if the installer package name is not null , and is different from the Google play store package name
                    if (installerPackageName != null && !installerPackageName.contains("com.android.vending")) {
                        // the application is installed from the unknown sources
                        appsFromUnknownSources.add(packageName);
                    }
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!appsFromUnknownSources.isEmpty()) {
                            showToast("App scan have found some apps installed from unknown sources.");
                            updateDetailWithHarmfulAppList(appsFromUnknownSources);
                            hideLoader();
                            return;
                        }
                
                        showToast("Installation sources are trusted.");  
                        mainActivity.switchFragment(thisFragment, BraveSyncScreensPreference.newInstance(true), mainActivity.INIT_FRAGMENT_TAG);    
                    }
                });
            }
         }).start();




    }

}