package com.nandivaleamol.socialmediaapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

public class PrivacyActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    String [] status = {"Choose any one","public"," private"};
    TextView status_tv;
    Spinner spinner;
    Button button;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseUser user;

    String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        button = findViewById(R.id.btn_privacy);
        status_tv = findViewById(R.id.tv_status);
        spinner = findViewById(R.id.spinner);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();
        reference = db.collection("users").document(currentUid);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,status);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePrivacy();
            }
        });
        

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this, "Please select a value", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            String privacy_result = task.getResult().getString("privacy");
                            status_tv.setText(privacy_result);
                        }else{
                            Toast.makeText(PrivacyActivity.this,"Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void savePrivacy() {

        final String value = spinner.getSelectedItem().toString();

        if (value=="Choose any one"){
            Toast.makeText(this,"Please select a value", Toast.LENGTH_SHORT).show();
        }else{
            user = FirebaseAuth.getInstance().getCurrentUser();
            currentUid = user.getUid();

            final DocumentReference sDoc= db.collection("users").document(currentUid);

            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                DocumentSnapshot snapshot = transaction.get(sDoc);

                transaction.update(sDoc, "privacy", value);
                return null;
            }

            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   Toast.makeText(PrivacyActivity.this,"Status Updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PrivacyActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}