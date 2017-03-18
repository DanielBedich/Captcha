package com.example.danielbedich.captcha;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mNewPasswordText;
    private EditText mConfirmNewPasswordText;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mEmailText = (EditText) findViewById(R.id.emailText);
        mEmailText.setText(PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getString("EMAIL", "Error: no email"));

        mPasswordText = (EditText) findViewById(R.id.oldPasswordText);
        final String password = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).getString("PASSWORD", "Error: no password");

        mNewPasswordText = (EditText) findViewById(R.id.newPasswordText);

        mConfirmNewPasswordText = (EditText) findViewById(R.id.newPasswordConfirmText);

        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Update Email
                SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                SharedPreferences.Editor mEditor = mPrefs.edit();


                //Check for new password
                if(mNewPasswordText.getText().length()>0){
                    String enteredPassword = mPasswordText.getText().toString();

                    //Check if old password is correct
                    if(checkPassword(enteredPassword, password)){
                        //Toast.makeText(SettingsActivity.this, "Old Password Correct", Toast.LENGTH_SHORT).show();

                        //Check if new password is correct
                        if(mNewPasswordText.getText().toString().equals(mConfirmNewPasswordText.getText().toString())){
                            mEditor = mPrefs.edit();
                            mEditor.putString("PASSWORD", mNewPasswordText.getText().toString());
                            mEditor.commit();
                            Toast.makeText(SettingsActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SettingsActivity.this, CaptchaActivity.class));
                        } else {
                            Toast.makeText(SettingsActivity.this, "Password Denied", Toast.LENGTH_SHORT).show();
                            mPasswordText.setText("");
                            mNewPasswordText.setText("");
                            mConfirmNewPasswordText.setText("");
                        }
                    } else {
                        Toast.makeText(SettingsActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                        mPasswordText.setText("");
                        mNewPasswordText.setText("");
                        mConfirmNewPasswordText.setText("");
                    }
                } else {
                    mEditor.putString("EMAIL", mEmailText.getText().toString());
                    mEditor.commit();
                    Toast.makeText(SettingsActivity.this, "Email Saved", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SettingsActivity.this, CaptchaActivity.class));
                }
            }
        });

    }

    public boolean checkPassword(String enteredPassword, String actualPassword){
        boolean b;
        if(enteredPassword.equals(actualPassword)){
            b=true;
        }
        else{
            b=false;
        }
        return b;
    }
}
