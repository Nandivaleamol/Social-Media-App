package com.nandivaleamol.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnEdit, btnDelete;
    TextView textView;

    ProgressDialog progressDialog;

    FirebaseAuth auth;
    FirebaseUser user;
    DocumentReference reference;
    String url;
    String currentUid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        btnDelete = findViewById(R.id.btn_delete_iv);
        btnEdit = findViewById(R.id.btn_edit_iv);
        imageView = findViewById(R.id.iv_name_expand);
        textView = findViewById(R.id.tv_name_image);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentUid = user.getUid();

        reference = db.collection("users").document(currentUid);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImageActivity.this, UpdateProfilePhotoActivity.class);
                startActivity(intent);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);

                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ImageActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                })
                 .addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         Toast.makeText(ImageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                     }
                 });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String name = task.getResult().getString("name");
                    url = task.getResult().getString("url");

                    // loading image
                    Picasso.get().load(url).into(imageView);
                    textView.setText(name);
                }
                else{
                    Toast.makeText(ImageActivity.this, "No Profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}