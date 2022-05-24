package com.nandivaleamol.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText emailEt, passEt;
    Button register_btn, login_btn;
    CheckBox checkBox;
    ProgressBar progressBar;

    FirebaseAuth auth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // finding Resource id's
        emailEt = findViewById(R.id.login_email_et);
        passEt = findViewById(R.id.login_password_et);
        register_btn = findViewById(R.id.login_to_signup);
        login_btn = findViewById(R.id.button_login);
        checkBox = findViewById(R.id.login_checkbox);
        progressBar = findViewById(R.id.progressbar_login);

        // firebase authentication
        auth = FirebaseAuth.getInstance();


        // logical buttons click

        // checkbox click listener
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //confirm_pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    //passEt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    //confirm_pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // register button click listener
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // login button click listener
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                String pass = passEt.getText().toString();
                
                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)){
                    progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.INVISIBLE);/////////////////////////////////
                                sendToMain();
                            }
                            else{
                                progressBar.setVisibility(View.INVISIBLE); ///////////////////////////////
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    /////////////////////////////

                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendToMain() {
        Intent intent = new Intent(LoginActivity.this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity1.class);
            startActivity(intent);
            finish();
        }

        /////////////////////////////////////////////////////////
        else{
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        }
    }
}