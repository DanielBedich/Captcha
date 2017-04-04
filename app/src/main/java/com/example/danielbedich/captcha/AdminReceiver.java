package com.example.danielbedich.captcha;

/**
 * Created by DanielBedich on 3/20/17.
 */

import android.app.PendingIntent;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {

    public final static String DEBUG_TAG = "AdminReceiver";
    private Camera camera;
    private int cameraId = 0;


    @Override
    public void onEnabled(Context ctxt, Intent intent) {


    }

    @Override
    public void onPasswordFailed(Context ctxt, Intent intent) {
        Log.d("LockScreen", "onPasswordFailed");
        DevicePolicyManager mgr = (DevicePolicyManager) ctxt.getSystemService(Context.DEVICE_POLICY_SERVICE);
        int no = mgr.getCurrentFailedPasswordAttempts();
        if (no >= 3) {
            Log.d("LockScreen", "Failed 3 times");
            //Toast does not show
            //Toast.makeText(ctxt, R.string.password_failed, Toast.LENGTH_LONG).show();
            //Intent alertIntent = new Intent(ctxt, SettingsActivity.class);
            // do we have a camera?
            if (!ctxt.getPackageManager()
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                Toast.makeText(ctxt, "No camera on this device", Toast.LENGTH_LONG)
                        .show();
            } else {
                cameraId = findFrontFacingCamera();
                if (cameraId < 0) {
                    Toast.makeText(ctxt, "No front facing camera found.",
                            Toast.LENGTH_LONG).show();
                } else {
                    camera = Camera.open(cameraId);
                }
            }

            camera.startPreview();
            camera.takePicture(null, null,
                    new PhotoHandler(ctxt.getApplicationContext()));
            Intent alarmIntent = new Intent("android.intent.action.MAIN");
            alarmIntent.setClass(ctxt, ToastActivity.class);
            alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alarmIntent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED +
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD +
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON +
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            ctxt.startActivity(alarmIntent);
        }
    }

    @Override
    public void onPasswordSucceeded(Context ctxt, Intent intent) {
        Toast.makeText(ctxt, R.string.password_success, Toast.LENGTH_LONG)
                .show();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
}
