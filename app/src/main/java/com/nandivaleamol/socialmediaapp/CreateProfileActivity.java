 package com.nandivaleamol.socialmediaapp;

 import android.content.ContentResolver;
 import android.content.Intent;
 import android.net.Uri;
 import android.os.Bundle;
 import android.os.Handler;
 import android.text.TextUtils;
 import android.view.View;
 import android.webkit.MimeTypeMap;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.ProgressBar;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.Continuation;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.OnSuccessListener;
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
 import com.nandivaleamol.socialmediaapp.Fragments.ProfileFragment;
 import com.nandivaleamol.socialmediaapp.Model.All_UserMember;
 import com.squareup.picasso.Picasso;

 import java.util.HashMap;
 import java.util.Map;

 public class CreateProfileActivity extends AppCompatActivity {
    // Widget instances/objects
    EditText etname, etBio, etProfession, etEmail, etWeb;
    Button button;
    ImageView imageView;
    ProgressBar progressBar;
    Uri imageUri;

    // Firebase databases instance/objects
    UploadTask uploadTask;
    StorageReference storageReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    private static final int PICK_IMAGE = 100;
    All_UserMember member;

    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        member = new All_UserMember();

        // widget Resource id
       imageView = findViewById(R.id.iv_cp);
       etBio = findViewById(R.id.et_bio_cp);
       etEmail = findViewById(R.id.et_email_cp);
       etname = findViewById(R.id.et_name_cp);
       etProfession = findViewById(R.id.et_profession_cp);
       etWeb = findViewById(R.id.et_website_cp);
       button = findViewById(R.id.btn_cp);
       progressBar = findViewById(R.id.et_progressbr_cp);

       // firebase current user with authentication
       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       currentUserId = user.getUid();

       // firestore database collection
       documentReference = db.collection("users").document(currentUserId);
       // to save profile images in firebase database storage
       storageReference = FirebaseStorage.getInstance().getReference("Profile_images");
       // to save all users details in realtime database
       databaseReference = database.getReference("All_Users");

       // click listeners
       button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             if (imageUri != null){/////////////////////////////////
                uploadData();
             }
             else {/////////////////////////////////////////////////////////////////////
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CreateProfileActivity.this, "Please profile picture is required", Toast.LENGTH_SHORT).show();
             }////////////////////////////////////////////////////////////
          }
       });

       imageView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
             Intent intent = new Intent();
             intent.setType("image/*");
             intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,PICK_IMAGE);
          }
       });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       try {
          if (requestCode == PICK_IMAGE || resultCode == RESULT_OK || data!=null ||
                     data.getData() != null){

             imageUri = data.getData();

             Picasso.get().load(imageUri).into(imageView);
          }
       }catch (Exception e){
          Toast.makeText(CreateProfileActivity.this, "Error : "+e, Toast.LENGTH_SHORT).show();
       }
    }

    private String getFileExt(Uri uri){
       ContentResolver contentResolver = getContentResolver();
       MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
       return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void uploadData() {
       String name = etname.getText().toString();
       String bio = etBio.getText().toString();
       String web = etWeb.getText().toString();
       String prof = etProfession.getText().toString();
       String email = etEmail.getText().toString();


       //|| !TextUtils.isEmpty(web)--> removed fro temp
//       if (!TextUtils.isEmpty(name) || !TextUtils.isEmpty(bio) ||bio.length()<=50  || !TextUtils.isEmpty(prof) ||
//               !TextUtils.isEmpty(email) || imageUri!=null){

          if (TextUtils.isEmpty(name)){
             etname.setError("Please enter name");
          }else if (TextUtils.isEmpty(prof)){
             etProfession.setError("Please enter your profession");
          }else if (prof.length()>=500){
             etProfession.setError("Please enter only 500 character");
          }
          else if (TextUtils.isEmpty(email)){
             etEmail.setError("Please enter email");
          }else {
             progressBar.setVisibility(View.VISIBLE);
             final StorageReference reference = storageReference.child(System.currentTimeMillis() + "." + getFileExt(imageUri));
             uploadTask = reference.putFile(imageUri);

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

                      Map<String, String> profile = new HashMap<>();
                      profile.put("name", name);
                      profile.put("prof", prof);
                      profile.put("url", downloadUri.toString());
                      profile.put("email", email);
                      profile.put("web", web);
                      profile.put("bio", bio);
                      profile.put("uid", currentUserId);
                      profile.put("privacy", "public");

                      // setters
                      member.setName(name);
                      member.setProf(prof);
                      member.setUid(currentUserId);
                      member.setUrl(downloadUri.toString());

                      databaseReference.child(currentUserId).setValue(member);

                      documentReference.set(profile)
                              .addOnSuccessListener(new OnSuccessListener<Void>() {
                                 @Override
                                 public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(CreateProfileActivity.this, "Profile created", Toast.LENGTH_SHORT).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                       @Override
                                       public void run() {
                                          Intent intent = new Intent(CreateProfileActivity.this, ProfileFragment.class);
                                          startActivity(intent);
                                          finish();

                                       }
                                    }, 2000);
                                 }
                              });
                   }else {
                      progressBar.setVisibility(View.INVISIBLE);
                      Toast.makeText(CreateProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                   }
                }
             });
          }
//          else {
//          progressBar.setVisibility(View.INVISIBLE);
//          Toast.makeText(CreateProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
//
//       }

    }
 }