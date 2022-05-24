package com.nandivaleamol.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nandivaleamol.socialmediaapp.Model.AnsViewHolder;
import com.nandivaleamol.socialmediaapp.Model.AnswerMember;
import com.squareup.picasso.Picasso;

public class ReplyActivity extends AppCompatActivity {

    // for database
    String uid, question, post_key, key;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference,reference2;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference votesref, Allquestions;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    // declaring variables for to identify resources
    TextView nameTv, questionTv, replyTv;
    ImageView imageViewQue, imageViewUser;
    RecyclerView recyclerView;

    String currentUid;
    Boolean voteChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        nameTv = findViewById(R.id.name_replay_tv);
        questionTv = findViewById(R.id.question_replay_tv);
        imageViewQue = findViewById(R.id.iv_que_user);
        imageViewUser = findViewById(R.id.iv_reply_user);
        replyTv = findViewById(R.id.answer_tv);

        // for recyclerView
        recyclerView = findViewById(R.id.rv_ans);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReplyActivity.this));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        currentUid = firebaseUser.getUid();

        // getting string using intent
        Bundle extra = getIntent().getExtras();
        if (extra != null){
            uid = extra.getString("uid");
//            post_key = extra.getString("post_key");
            post_key = extra.getString("post_key");
            question = extra.getString("question");
            //key = extra.getString("key");
        }else{
            Toast.makeText(this, "oops something went wrong", Toast.LENGTH_SHORT).show();
        }

        Allquestions = database.getReference("All Questions").child(post_key).child("Answer");
        votesref = database.getReference("votes");

        reference = db.collection("users").document(uid);
        reference2 = db.collection("users").document(currentUid);

        replyTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReplyActivity.this, AnswerActivity.class);
                intent.putExtra("u",uid);
                //intent.putExtra("q",question);
                intent.putExtra("p",post_key);
                //intent.putExtra("key",privacy);
                startActivity(intent);
                //Toast.makeText(ReplyActivity.this, "Oops", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // onStart() method
    @Override
    protected void onStart() {
        super.onStart();

        // question user reference
        reference.get()
                .addOnCompleteListener((task)->{
                    if (task.getResult().exists()){
                        String url = task.getResult().getString("url");
                        String name = task.getResult().getString("name");

                        // loading image
                        Picasso.get().load(url).into(imageViewQue);

                        nameTv.setText(name);
                        questionTv.setText(question);
                    }
                    else{
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });

        //reference for answer
        reference2.get()
                .addOnCompleteListener((task)->{
                    if (task.getResult().exists()){
                        String url = task.getResult().getString("url");

                        // loading image
                        Picasso.get().load(url).into(imageViewUser);

                    }
                    else{
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                    }
                });

        FirebaseRecyclerOptions<AnswerMember> options =
                new FirebaseRecyclerOptions.Builder<AnswerMember>()
                        .setQuery(Allquestions, AnswerMember.class)
                        .build();

        FirebaseRecyclerAdapter<AnswerMember, AnsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<AnswerMember, AnsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AnsViewHolder holder, int position, @NonNull AnswerMember model) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        final String currentUserId = user.getUid();

                        final String postKey = getRef(position).getKey();

                        holder.setAnswer(getApplication(), model.getName(),model.getAnswer(),
                                model.getUid(), model.getTime(), model.getUrl());

                        holder.upvoteChecker(postKey);
                        holder.upvoteTv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                voteChecker = true;
                                votesref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (voteChecker.equals(true)){
                                            if (snapshot.child(postKey).hasChild(currentUid)){
                                                votesref.child(postKey).child(currentUserId).removeValue();

                                                voteChecker = false;
                                            }else{
                                                votesref.child(postKey).child(currentUserId).setValue(true);

                                                voteChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public AnsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.ans_layout, parent, false);
                        return new AnsViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}