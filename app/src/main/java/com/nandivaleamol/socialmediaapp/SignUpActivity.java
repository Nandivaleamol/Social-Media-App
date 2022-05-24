package com.nandivaleamol.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class SignUpActivity extends AppCompatActivity {

    TextView goToLogin;
    EditText username,memer,email,password;
    Button signUp;
    ProgressDialog progressDialog;

    Random random;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;

    String url="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/download.jpg?alt=media&token=143d5e9d-c652-465d-b513-6ee75537ad11";

    String profile="https://firebasestorage.googleapis.com/v0/b/signin-function.appspot.com/o/user.svg?alt=media&token=561ed6be-db1a-4068-8023-15904ed28796";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("SignUp Details");


        ///////////////////////
        user=auth.getCurrentUser();
        Calendar cDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveDate = currentDate.format(cDate.getTime());

        Calendar cTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        final  String saveTime = currentTime.format(cTime.getTime());

        String time = saveDate +":" +saveTime;
        ////////////////////

        // sign Up button onClick listener
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String u = username.getText().toString();
                String m = memer.getText().toString();
                String e = email.getText().toString();
                String p = password.getText().toString();

                if (u.isEmpty() | u.contains("-") | u.endsWith(" ")) {
//                    username.setError("Please enter name");
                    username.setError("Please enter username");
                }
                else if (u.contains(" ") | u.endsWith(" ")){
                    username.setError("spaces are not allowed in username");
                }
                else if (m.isEmpty()) {
                    memer.setError("Please enter memer name");
                } else if (e.isEmpty()) {
                    email.setError("Please enter email..");
                } else if (p.isEmpty()) {
                    password.setError("Password cannot be empty..!");
                } else if (p.length() < 6) {
                    password.setError("Password must be greater than 5 character");
                } else {
                    creatAccount(u, m, e, p, time);
                }
            }
        });

        // goToLogin textView OnClick Listener
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this,LoginActivity1.class));
                finish();
            }
        });
    }

    private void creatAccount(String u, String m, String e, String p, String time) {
        progressDialog=new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Registering....");
        progressDialog.setMessage("Please wait..We're creating account for you a short while");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                saveData(u,m,e,p,time);
                                progressDialog.dismiss();

                                //////////////////////////
                                Toast.makeText(SignUpActivity.this, "Please verify email", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity1.class);
                                startActivity(intent);
                                if (user.isEmailVerified()){
                                    Toast.makeText(SignUpActivity.this,"Your data saved",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this,"Please verify email",Toast.LENGTH_SHORT).show();
                                }
                                if(user.isEmailVerified()){
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Email verified", Toast.LENGTH_SHORT).show();
                                    Intent intent1 = new Intent(SignUpActivity.this, LoginActivity1.class);
                                    startActivity(intent1);
                                    finish();

                                }

                                else{
                                    progressDialog.dismiss();
                                   // Toast.makeText(SignUpActivity.this, "Please verify email", Toast.LENGTH_SHORT).show();//////////////////////////////////

                                    // sending email for current user
                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                progressDialog.dismiss();
                                                Toast.makeText(SignUpActivity.this, "Email sended on your registered email", Toast.LENGTH_SHORT).show();
                                                if (user.isEmailVerified()){
                                                    Toast.makeText(SignUpActivity.this,"Email verified successfully",Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    progressDialog.dismiss();
                                                    //Toast.makeText(LoginActivity1.this,"Please verify email",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            else{
                                                Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Unable to register.."+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void saveData(String u, String m, String e, String p, String t) {

        // fore firebase authentication
        auth=FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        HashMap<String,Object> map=new HashMap<>();

        map.put("username",u);
        map.put("user_id",user.getUid());
        map.put("memer","@"+m);
        map.put("email",e);
        map.put("password",p);
        map.put("profileUrl",profile);
        map.put("background",url);
        map.put("time",t);/////////////////////////////////////
        reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    //startActivity(new Intent(SignUpActivity.this,LoginActivity.class));
                    Toast.makeText(SignUpActivity.this, "Account created!!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else
                {
                    progressDialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Something went wrong "+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("WrongViewCast")
    private void init()
    {
        goToLogin=findViewById(R.id.goToLogin);
        username=findViewById(R.id.username);
        memer=findViewById(R.id.memer);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        signUp=findViewById(R.id.signUp);
        random=new Random();
    }

}