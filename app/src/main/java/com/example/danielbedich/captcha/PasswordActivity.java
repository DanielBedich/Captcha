package com.example.danielbedich.captcha;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasswordActivity extends AppCompatActivity {

    private EditText mPasswordText;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        Boolean passwordFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("passwordFirstRun", true);

        if(passwordFirstRun){
            startActivity(new Intent(PasswordActivity.this, NewUserActivity.class));
            Toast.makeText(PasswordActivity.this, "New user", Toast.LENGTH_LONG)
                    .show();
        }

        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("passwordFirstRun", false).commit();

        final String password = PreferenceManager.getDefaultSharedPreferences(PasswordActivity.this).getString("PASSWORD", "Error: no password");

        mPasswordText = (EditText) findViewById(R.id.passwordText);

        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String enteredPassword = mPasswordText.getText().toString();
                    if(checkPassword(enteredPassword, password)){
                        Toast.makeText(PasswordActivity.this, "Password Correct", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PasswordActivity.this, CaptchaActivity.class));
                    } else {
                        Toast.makeText(PasswordActivity.this, "Password Incorrect", Toast.LENGTH_SHORT).show();
                        mPasswordText.setText("");
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
