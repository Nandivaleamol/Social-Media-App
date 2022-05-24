package com.nandivaleamol.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        //currentUid = user.getUid();

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
//                firebaseUser = firebaseAuth.getCurrentUser();
                if (user != null) {
                    currentUid = user.getUid();
                }
            }
        };

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (user!=null){
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity1.class));
                    finish();
                }
            }
        },1500);
    }
}