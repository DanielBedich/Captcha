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

//Creates new user on first run of app
public class NewUserActivity extends AppCompatActivity {

    //Items in views
    private EditText mEmailText;
    private EditText mPasswordText;
    private EditText mPasswordConfirmText;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        //set view items
        mEmailText = (EditText) findViewById(R.id.emailText);
        mPasswordText = (EditText) findViewById(R.id.passwordText);
        mPasswordConfirmText = (EditText) findViewById(R.id.passwordConfirmText);

        //on submit button click, save entered info if info is valid
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordText.getText().toString().equals(mPasswordConfirmText.getText().toString())) {
                    SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(NewUserActivity.this);
                    SharedPreferences.Editor mEditor = mPrefs.edit();
                    mEditor.putString("PASSWORD", mPasswordText.getText().toString());
                    mEditor.putString("EMAIL", mEmailText.getText().toString());
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                            .putBoolean("passwordFirstRun", false).commit();
                    mEditor.commit();
                    Toast.makeText(NewUserActivity.this, "Password Accepted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(NewUserActivity.this, CaptchaActivity.class));
                } else {
                    Toast.makeText(NewUserActivity.this, "Password Denied", Toast.LENGTH_SHORT).show();
                    mPasswordText.setText("");
                    mPasswordConfirmText.setText("");
                }
            }
        });
    }
}
