package com.example.danielbedich.captcha;

/**
 * Created by DanielBedich on 3/20/17.
 */

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        Log.d("LockScreen", "onPasswordFailed");
        DevicePolicyManager mgr = (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        int no = mgr.getCurrentFailedPasswordAttempts();
        if (no >= 5) {
            Log.d("LockScreen", "Failed 3 times");
            //Toast does not show
            Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG)
                .show();
    }
}
