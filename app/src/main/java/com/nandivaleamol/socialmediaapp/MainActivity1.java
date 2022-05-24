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

import java.util.HashMap;
import java.util.Random;

public class MainActivity1 extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        init();

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("Users");

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity1.this,LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n=username.getText().toString();
                String m=memer.getText().toString();
                String e=email.getText().toString();
                String p=password.getText().toString();

                if (n.isEmpty())
                {
                    username.setError("Please enter name");
                }else if (m.isEmpty())
                {
                    memer.setError("Please enter memer name");
                }else if (e.isEmpty())
                {
                    email.setError("Please enter email..");
                }else if (p.isEmpty())
                {
                    password.setError("Password cannot be empty..!");
                }else
                {
                    progressDialog=new ProgressDialog(MainActivity1.this);
                    progressDialog.setTitle("Registering....");
                    progressDialog.setMessage("Please wait..We're creating account for you a short while");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    auth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {


                                auth=FirebaseAuth.getInstance();
                                user=auth.getCurrentUser();

                                HashMap<String,Object> map=new HashMap<>();
                                map.put("username",n);
                                map.put("user_id",user.getUid());
                                map.put("memer","@"+m);
                                map.put("email",e);
                                map.put("profileUrl",profile);
                                map.put("background",url);

                                reference.child(user.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {
                                            startActivity(new Intent(MainActivity1.this,LoginActivity.class));
                                            Toast.makeText(MainActivity1.this, "Account created!!!", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }else
                                        {
                                            progressDialog.dismiss();
                                            Toast.makeText(MainActivity1.this, "Something went wrong "+task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }else
                            {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity1.this, "Unable to register.."+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
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