package org.chromium.chrome.browser.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.chromium.chrome.R;

import org.chromium.chrome.browser.app.BraveActivity;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;

public class VPNFragment extends Fragment implements View.OnClickListener { 

    private static final int VPN_REQ_CODE = 4566;

    private BraveActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vpn, container, false);

        mainActivity = BraveActivity.getBraveActivity();

        return rootView;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        prepareVpn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
    
    /**
     * Prepare for vpn connect with required permission
     */
    public void prepareVpn() {
        if (null == mainActivity) {
            showToast("No connection with main activity");
            return;
        }

        mainActivity.checkVPNRunning();

        if (!mainActivity.doesVPNStarted()) {
            if (mainActivity.getInternetStatus()) {

                // Checking permission for network monitor
                Intent intent = mainActivity.getVPNPermissionIntent();

                if (intent != null) {
                    startActivityForResult(intent, VPN_REQ_CODE);
                } else mainActivity.startVpn();//have already permission

                // Update confection status

            } else {

                // No internet connection available
                showToast("you have no internet connection !!");
            }
        } else {
            showToast("VPN is already running !!");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VPN_REQ_CODE) {
            if (null != mainActivity) {
                if (resultCode == RESULT_OK) {
                    mainActivity.startVpn();
                } else {
                    showToast("Permission Deny !! ");
                }
            } else {
                showToast("No connection with main activity");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}