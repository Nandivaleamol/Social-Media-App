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
import com.nandivaleamol.socialmediaapp.Model.QuestionMember;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AskActivity extends AppCompatActivity {

    EditText editText;
    Button button;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AllQuestions, UserQuestions;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference;

    String name, url, privacy, uid;

    FirebaseUser user;

    String currentUid;

    QuestionMember member;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        editText = findViewById(R.id.ask_et_question);
        button = findViewById(R.id.btn_submit);

        documentReference = db.collection("users").document(currentUid);

        AllQuestions = database.getReference("All Questions");
        UserQuestions = database.getReference("User Questions").child(currentUid);

        member = new QuestionMember();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = editText.getText().toString();

                Calendar cDate = Calendar.getInstance();
                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                final String saveDate = currentDate.format(cDate.getTime());

                Calendar cTime = Calendar.getInstance();
                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
               final  String saveTime = currentTime.format(cTime.getTime());

               String time = saveDate +":" +saveTime;

               // Question input validation.....
               if (question!=null){

                   if (question.startsWith(" ")){
                       Toast.makeText(AskActivity.this,"You cannot start question an empty spaces",Toast.LENGTH_SHORT).show();
                   }
                   else if (question.length()>=5){
//                       Toast.makeText(AskActivity.this, "Question length must be more than 5 characters", Toast.LENGTH_SHORT).show();
                       if (!question.endsWith(" ")) {

                           member.setQuestion(question);
                           member.setName(name);
                           member.setPrivacy(privacy);
                           member.setUrl(url);
                           member.setUserId(uid);
                           member.setTime(time);

                           String id = UserQuestions.push().getKey();
                           UserQuestions.child(id).setValue(member);

                           String child = AllQuestions.push().getKey();
                           member.setKey(id);
                           AllQuestions.child(child).setValue(member);

                           Toast.makeText(AskActivity.this, "Your Question Submitted", Toast.LENGTH_SHORT).show();
                           editText.setText("");
                           editText.setHint("Ask more questions...");
                       }else {
                           Toast.makeText(AskActivity.this,"Empty spaces are not allowed",Toast.LENGTH_SHORT).show();
                       }
                   }
                   else{
                       Toast.makeText(AskActivity.this, "Question length must be more than 5 characters", Toast.LENGTH_SHORT).show();

                   }

               }else{
                   Toast.makeText(AskActivity.this, "Please ask a question", Toast.LENGTH_SHORT).show();
               }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        
        documentReference.get()
                .addOnCompleteListener((task) -> {
                    
                    if (task.getResult().exists()) {

                        name = task.getResult().getString("name");
                        url = task.getResult().getString("url");
                        privacy = task.getResult().getString("privacy");
                        uid = task.getResult().getString("uid");
                    }else{
                        Toast.makeText(AskActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    
                });
    }
}