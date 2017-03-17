package com.example.danielbedich.captcha;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CaptchaActivity extends AppCompatActivity {

    private TextView mStatusTextView;
    private Button mStatusButton;
    private Button mSettingsButton;
    private String tag;

    private void changeRunningStatus(View v, Button b){
        Toast.makeText(this, R.string.statusToast, Toast.LENGTH_SHORT).show();
        if(mStatusButton.getText().equals(getString(R.string.statusStartButton))){
            mStatusButton.setText(R.string.statusStopButton);
            mStatusTextView.setText(R.string.statusOn);
        } else {
            mStatusButton.setText(R.string.statusStartButton);
            mStatusTextView.setText(R.string.statusOff);
        }
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(CaptchaActivity.this);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putString("STATUS_TEXT", mStatusTextView.getText().toString());
        mEditor.putString("STATUS_BUTTON", mStatusButton.getText().toString());
        mEditor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        Boolean settingsFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("settingsFirstRun", true);

        mStatusTextView = (TextView) findViewById(R.id.statusText);
        mStatusButton = (Button) findViewById(R.id.changeStatusButton);

        if (settingsFirstRun) {
            Toast.makeText(this, "FIRST RUN", Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(R.string.statusOff);
            mStatusButton.setText(R.string.statusStartButton);
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(CaptchaActivity.this);
            SharedPreferences.Editor mEditor = mPrefs.edit();
            mEditor.putString("STATUS_TEXT", mStatusTextView.getText().toString());
            mEditor.putString("STATUS_BUTTON", mStatusButton.getText().toString());
            mEditor.commit();
        } else {
            Toast.makeText(this, "NOT FIRST RUN", Toast.LENGTH_SHORT).show();
            mStatusTextView.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("STATUS_TEXT", "fail"));
            mStatusButton.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("STATUS_BUTTON", "fail"));
        }



        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("settingsFirstRun", false).commit();


        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeRunningStatus(v, mStatusButton);
            }
        });

        mSettingsButton = (Button) findViewById(R.id.settingsButton);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

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
