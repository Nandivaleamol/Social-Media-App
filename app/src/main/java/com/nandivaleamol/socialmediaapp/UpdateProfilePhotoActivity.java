package com.nandivaleamol.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateProfilePhotoActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageReference;

    ImageView imageView;
    Button button;
    UploadTask uploadTask;
    ProgressBar progressBar;

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    Uri uri, imageUri;

    String currentUid;
    private final static int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_photo);

        imageView = findViewById(R.id.iv_updatePhoto);
        button = findViewById(R.id.btn_updatephoto);
        progressBar = findViewById(R.id.pv_updatephoto);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        storageReference = FirebaseStorage.getInstance().getReference("Profile_images");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        });
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                    data !=null || data.getData()!=null) {

                imageUri = data.getData();

                // load image
                Picasso.get().load(imageUri).into(imageView);
            }

        }catch (Exception e){
            Toast.makeText(this, "Error : "+e, Toast.LENGTH_SHORT).show();
        }


    }

    private void updateImage() {

        final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageUri));
        uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();

//                    Map<String, Object> profile = new HashMap<>();
//                    profile.put("url",downloadUri);

                    final DocumentReference documentReference = db.collection("users").document(currentUid);

                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            //DocumentSnapshot snapshot = transaction.get(sDoc);

                            // Note: this could be done without a transaction
                            //       by updating the population using FieldValue.increment()
                            transaction.update(documentReference, "url", downloadUri);

                            // Success
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateProfilePhotoActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference db1, db2;

                            Map<String, Object> profile = new HashMap<>();
                            profile.put("url",downloadUri);

                            db1 = database.getReference("All posts");

                            Query query = db1.orderByChild("uid").equalTo(currentUid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        dataSnapshot.getRef().updateChildren(profile)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(UpdateProfilePhotoActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                            db2 = database.getReference("All Questions");

                            Query query1 = db2.orderByChild("uid").equalTo(currentUid);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        dataSnapshot.getRef().updateChildren(profile)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(UpdateProfilePhotoActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateProfilePhotoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(UpdateProfilePhotoActivity.this, "Error, Image not updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void chooseImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);

    }

}