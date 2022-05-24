package com.nandivaleamol.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoginActivity1 extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;
    TextView goToSignUp;
    EditText email, password;
    Button login_btn;
    ProgressDialog progressDialog;

    // for firebase
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    String currentUserId;

    // additional work
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;

    String time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);

        FirebaseApp.initializeApp(LoginActivity1.this);
        init();

//        auth = FirebaseAuth.getInstance();
//        firebaseUser = auth.getCurrentUser();

        // getting current user id after login
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
//                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    currentUserId = firebaseUser.getUid();
                }else{
                    Toast.makeText(LoginActivity1.this, "Current user id no found", Toast.LENGTH_SHORT).show();
                }
            }
        };


        // additional work
        //databaseReference = database.getReference("UserLogin details").child(currentUserId);

       //databaseReference= FirebaseDatabase.getInstance().getReference().child("User Login Details");


        Calendar cDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-mm-yyyy");
        final String saveDate = currentDate.format(cDate.getTime());

        Calendar cTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        final  String saveTime = currentTime.format(cTime.getTime());

        time = saveDate +":" +saveTime;
        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        //////////////////////

        //creating collection in realtime database for saving usr login time
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Login Details");


        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity1.this, SignUpActivity.class));
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String e = email.getText().toString();
                String p = password.getText().toString();

                if (e.isEmpty()) {
                    email.setError("Please enter valid email..");
                } else if (p.isEmpty()) {
                    password.setError("Please enter password..");
                } else {
                    signIn(e,p);
                }
            }
        });


    }
    private void signIn(String e, String p) {
        progressDialog = new ProgressDialog(LoginActivity1.this);
        progressDialog.setTitle("Logging");
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        // email and pass authentication
        auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity1.this,"Login success..",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity1.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                    // to save user last login time
                    //saveLoginData(currentUserId,e,time);

                   /*

                     //email is verified or not checking
                    if(firebaseUser.isEmailVerified()){
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity1.this, "Login success...", Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(LoginActivity1.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                        saveLoginData(e,time);
                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity1.this, "Please verify email", Toast.LENGTH_SHORT).show();

                        // sending email for current user
                        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity1.this, "Email sended on your registered email", Toast.LENGTH_SHORT).show();
//                                    if (firebaseUser.isEmailVerified()){
//                                        Toast.makeText(LoginActivity1.this,"Email verified successfully",Toast.LENGTH_SHORT).show();
//                                    }
//                                    else{
//                                        progressDialog.dismiss();
//                                        //Toast.makeText(LoginActivity1.this,"Please verify email",Toast.LENGTH_SHORT).show();
//                                    }
                                }
                                else{
                                    Toast.makeText(LoginActivity1.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    */


                }else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity1.this, "Failed to login " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


//     additional work
//     to save user last login date time
/*
    private void saveLoginData(String currentUserId,String userEmail, String t) {

        HashMap<String,Object> map=new HashMap<>();

        map.put("uid",currentUserId);
        map.put("email",userEmail);
        map.put("time",t);


        databaseReference.child(firebaseUser.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    //startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    Toast.makeText(LoginActivity1.this, "Your Login details are saved", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity1.this, "Something went wrong "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

 */
    /////////////////////

    private void init()
    {
        //sign=findViewById(R.id.GsignIn);
        goToSignUp=findViewById(R.id.goToSignUp);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login_btn=findViewById(R.id.login_btn);
    }
}