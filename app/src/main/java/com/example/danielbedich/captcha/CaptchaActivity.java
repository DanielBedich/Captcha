package com.example.danielbedich.captcha;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

//Main Activity that starts Captcha!
public class CaptchaActivity extends AppCompatActivity {

    //Tag for debugging
    private String tag = "CaptchaActivity Log";

    //Items in view
    private TextView mStatusTextView;
    private Button mStatusButton;
    private Button mSettingsButton;


    //Creates DeviceAdminReceiver
    private void startLock(){
        ComponentName cn=new ComponentName(this, AdminReceiver.class);
        /*DevicePolicyManager mgr=
                (DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);*/

        Intent intent=new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_explanation));
        startActivity(intent);

    }

    //Changes running status on button press
    private void changeRunningStatus(View v, Button b){
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(CaptchaActivity.this);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        Toast.makeText(this, R.string.statusToast, Toast.LENGTH_SHORT).show();
        if(mStatusButton.getText().equals(getString(R.string.statusStartButton))){
            mStatusButton.setText(R.string.statusStopButton);
            mStatusTextView.setText(R.string.statusOn);
            mEditor.putBoolean("RUNNING_STATUS", true);
            startLock();
        } else {
            mEditor.putBoolean("RUNNING_STATUS", false);
            mStatusButton.setText(R.string.statusStartButton);
            mStatusTextView.setText(R.string.statusOff);
        }
        mEditor.putString("STATUS_TEXT", mStatusTextView.getText().toString());
        mEditor.putString("STATUS_BUTTON", mStatusButton.getText().toString());
        mEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);


        //Set view items
        mStatusTextView = (TextView) findViewById(R.id.statusText);
        mStatusButton = (Button) findViewById(R.id.changeStatusButton);

        //get status first run of app
        Boolean settingsFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("statusFirstRun", true);


        //if status first run go to set default settings, else load last settings
        if (settingsFirstRun) {
            //Toast.makeText(this, "FIRST RUN", Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(R.string.statusOff);
            mStatusButton.setText(R.string.statusStartButton);
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(CaptchaActivity.this);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("STATUS_TEXT", mStatusTextView.getText().toString());
            mEditor.putString("STATUS_BUTTON", mStatusButton.getText().toString());
            mEditor.putBoolean("RUNNING_STATUS", false);
            mEditor.commit();
        } else {
            //Toast.makeText(this, "NOT FIRST RUN", Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("STATUS_TEXT", "fail"));
            mStatusButton.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("STATUS_BUTTON", "fail"));
        }

        //Change status first run
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("statusFirstRun", false).commit();


        //On status button press, change running status
        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRunningStatus(v, mStatusButton);
            }
        });

        //On settings button press, go to SettingsActivity
        mSettingsButton = (Button) findViewById(R.id.settingsButton);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptchaActivity.this, SettingsActivity.class));
            }
        });

    }

    //Android Activity Lifecycle Methods
    public void onStart()
    {
        super.onStart();
        Log.d(tag, "In the onStart() event");
    }
    public void onRestart()
    {
        super.onRestart();
        Log.d(tag, "In the onRestart() event");
    }
    public void onResume()
    {
        super.onResume();
        Log.d(tag, "In the onResume() event");
    }
    public void onPause()
    {
        super.onPause();
        Log.d(tag, "In the onPause() event");
    }
    public void onStop()
    {
        super.onStop();
        Log.d(tag, "In the onStop() event");
    }
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(tag, "In the onDestroy() event");
    }
}
