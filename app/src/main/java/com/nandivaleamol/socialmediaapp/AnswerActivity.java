package com.nandivaleamol.socialmediaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nandivaleamol.socialmediaapp.Model.AnswerMember;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AnswerActivity extends AppCompatActivity {

    // declaring variables
    String uid, que, postKey;
    EditText editText;
    Button button;
    String name , url, time;
    //String id;

    // database related
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference Allquestions;
    FirebaseUser user;
    //String userUid;

    //Creating AnswerMember object
    AnswerMember answerMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        answerMember = new AnswerMember();
        editText = findViewById(R.id.answer_et);
        button = findViewById(R.id.btn_answer_submit);

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            uid = bundle.getString("u");
            postKey = bundle.getString("p");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }

//        Allquestions = database.getReference("All Questions").child(postKey).child("Answer");
        Allquestions = database.getReference("All Questions").child(postKey).child("Answer");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAnswer();
            }
        });
    }

    // saveAnswer()
    private void saveAnswer() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();

        String answer = editText.getText().toString();
        if (answer!=null){

            Calendar cdate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveDate = currentDate.format(cdate.getTime());
            
            Calendar ctime= Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            final String saveTime = currentTime.format(ctime.getTime());
            
            time = saveDate+":"+saveTime;

            answerMember.setAnswer(answer);
            answerMember.setTime(time);
            answerMember.setName(name);
            answerMember.setUid(userUid);
            answerMember.setUrl(url);

            // push answers in realtime database
            String id = Allquestions.push().getKey();
            Allquestions.child(id).setValue(answerMember);

            Toast.makeText(this, "Successfully Submitted", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "Please write answer", Toast.LENGTH_SHORT).show();
        }

    }

    // onStart() method

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user.getUid();
        FirebaseFirestore d = FirebaseFirestore.getInstance();
        DocumentReference reference;

        reference = d.collection("users").document(userUid);

        reference.get()
                .addOnCompleteListener((task)->{
                    if (task.getResult().exists()){
                        url = task.getResult().getString("url");
                        name = task.getResult().getString("name");

                    }
                    else{
                        Toast.makeText(AnswerActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });

    }
}