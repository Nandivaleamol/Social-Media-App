 package com.nandivaleamol.socialmediaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

 public class UpdateProfileActivity extends AppCompatActivity {

    EditText etName, etBio, etProfession, etEmail, etWeb;
    Button button;
    ImageView ivUp;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

//        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
//        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user!=null){
//                    currentUid  = user.getUid();

                    user = auth.getInstance().getCurrentUser();
                    currentUid = user.getUid();
                    db = FirebaseFirestore.getInstance();
                    documentReference = db.collection("users").document(currentUid);

                    etBio = findViewById(R.id.et_bio_up);
                    etName = findViewById(R.id.et_name_up);
                    etEmail = findViewById(R.id.et_email_up);
                    etProfession = findViewById(R.id.et_profession_up);
                    etWeb = findViewById(R.id.et_website_up);
                    button = findViewById(R.id.btn_up);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateProfile();
                            Toast.makeText(UpdateProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(UpdateProfileActivity.this, ProfileFragment.class);
//                            startActivity(intent);
//                            finish();
                        }
                    });

                //}
//                else{
//                    Toast.makeText(getApplicationContext(), "Current user id not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };

    }

     @Override
     public void onStart() {
         super.onStart();

         documentReference.get()
                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                         if (task.getResult().exists()) {

                             // get values in fireStore
                             String nameResult = task.getResult().getString("name");
                             String bioResult = task.getResult().getString("bio");
                             String emailResult = task.getResult().getString("email");
                             String webResult = task.getResult().getString("web");
                             String profResult = task.getResult().getString("prof");
                             String url = task.getResult().getString("url");


                             // load image
                             //Picasso.get().load(url).into(imageView);

                             //set valued
                             etName.setText(nameResult);
                             etBio.setText(bioResult);
                             etEmail.setText(emailResult);
                             etWeb.setText(webResult);
                             etProfession.setText(profResult);


                         } else {
                             Toast.makeText(UpdateProfileActivity.this, "No profile", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
     }

     private void updateProfile() {
        String name = etName.getText().toString();
        String bio = etBio.getText().toString();
        String prof = etProfession.getText().toString();
        String web = etWeb.getText().toString();
        String email = etEmail.getText().toString();

//         UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//
//                 .setPhotoUri(Uri.parse(url))
//                 .build();

         documentReference = db.collection("users").document(currentUid);

         db.runTransaction(new Transaction.Function<Void>() {
             @Override
             public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                 //DocumentSnapshot snapshot = transaction.get(sDoc);

                 // Note: this could be done without a transaction
                 //       by updating the population using FieldValue.increment()
                 //double newPopulation = snapshot.getDouble("population") + 1;
                 transaction.update(documentReference, "name", name);
                 transaction.update(documentReference, "prof", prof);
                 transaction.update(documentReference, "email",email);
                 transaction.update(documentReference,"web",web);
                 transaction.update(documentReference,"bio",bio);

                 // Success
                 return null;
             }
         }).addOnSuccessListener(new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 Toast.makeText(UpdateProfileActivity.this, "Your Profile Updated", Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(UpdateProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
             }
         });
     }
 }