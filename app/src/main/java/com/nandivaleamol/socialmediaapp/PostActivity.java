package com.nandivaleamol.socialmediaapp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nandivaleamol.socialmediaapp.Model.PostMember;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    ImageView imageView;
    ProgressBar progressBar;
    VideoView videoView;
    Button btn_chooseFile, btn_uploadFile;
    EditText et_desc;

    private Uri selectedUri;
    private static final int PICK_FILE =1;
    UploadTask uploadTask;

    String url, name;
    String currentUid;

    // firebase database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    StorageReference storageReference;

    DatabaseReference db1; // All Images
    DatabaseReference db2; // All videos
    DatabaseReference db3; // All posts

    MediaController mediaController;
    String type;

    PostMember postMember;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mediaController = new MediaController(this);

        progressBar = findViewById(R.id.pb_post);
        imageView = findViewById(R.id.iv_post);
        videoView = findViewById(R.id.vv_post);
        btn_chooseFile = findViewById(R.id.btn_choosefile_post);
        btn_uploadFile = findViewById(R.id.btn_uploadfile_post);
        et_desc = findViewById(R.id.et_desc_post);

        // PosMember class object initializing
        postMember = new PostMember();

        // creating new collection in firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference("User posts");

        // firebase user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        // firebase database
        db1 = database.getReference("All images");
        db2 = database.getReference("All videos");
        db3 = database.getReference("All posts");
        

        btn_uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               doPost();
            }
        });
        
        btn_chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        
    }

    private void doPost() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        String desc = et_desc.getText().toString();

        // for current date
        Calendar cdate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final  String saveDate = currentDate.format(cdate.getTime());

        // for current time
        Calendar cTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm-ss");
        final  String saveTime = currentTime.format(cTime.getTime());

        String time = saveDate +":"+saveTime;

        if (selectedUri !=null) {

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(selectedUri));
            uploadTask = reference.putFile(selectedUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        // checking file type
                        if (type.equals("iv")){
                            postMember.setDesc(desc);
                            postMember.setName(name);
                            postMember.setPostUri(downloadUri.toString());
                            postMember.setTime(time);
                            postMember.setUid(currentUid);
                            postMember.setUrl(url);
                            postMember.setType("iv");

                            // for image
                            String id = db1.push().getKey();
                            db1.child(id).setValue(postMember);

                            // for both
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postMember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Image Post uploaded", Toast.LENGTH_SHORT).show();

                        }else if (type.equals("vv")){
                            postMember.setDesc(desc);
                            postMember.setName(name);
                            postMember.setPostUri(downloadUri.toString());
                            postMember.setTime(time);
                            postMember.setUid(currentUid);
                            postMember.setUrl(url);
                            postMember.setType("vv");

                            // for video
                            String id = db2.push().getKey();
                            db2.child(id).setValue(postMember);

                            // for both
                            String id1 = db3.push().getKey();
                            db3.child(id1).setValue(postMember);

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Video Post Uploaded", Toast.LENGTH_SHORT).show();

                        }else{
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PostActivity.this, "Oops! Error", Toast.LENGTH_SHORT).show();
                        }


                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(PostActivity.this, "Please select any post", Toast.LENGTH_SHORT).show();
        }
        
    }

    
    @SuppressLint("IntentReset")
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/* video/* VIDEO/* IMAGE/*");
        startActivityForResult(intent, PICK_FILE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE || requestCode == RESULT_OK ||
                    data != null || data.getData() !=null){

            selectedUri = data.getData();

            if (selectedUri.toString().contains("image") || selectedUri.toString().contains("IMAGE") || selectedUri.toString().endsWith(".jpg") || selectedUri.toString().endsWith(".jpeg")){
                Picasso.get().load(selectedUri).into(imageView);
                imageView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.INVISIBLE);
                type = "iv";
                Toast.makeText(this, "Image file selected", Toast.LENGTH_SHORT).show();

            }else if (selectedUri.toString().contains("video") || selectedUri.toString().endsWith(".mp4")|| selectedUri.toString().endsWith(".MP4") || selectedUri.toString().contains("VIDEO")){
                videoView.setMediaController(mediaController);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                videoView.setVideoURI(selectedUri);
                videoView.start();
                type = "vv";
                Toast.makeText(this, "Video file selected", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return  mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    protected void onStart() {
        super.onStart();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("users").document(currentUid);

        documentReference.get()
                .addOnCompleteListener((task) -> {

                    if (task.getResult().exists()) {

                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");

                    }else{
                        Toast.makeText(PostActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

}