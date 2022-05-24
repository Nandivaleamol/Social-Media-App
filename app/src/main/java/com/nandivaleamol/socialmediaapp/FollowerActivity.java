package com.nandivaleamol.socialmediaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.nandivaleamol.socialmediaapp.Model.ProfileViewHolder;
import com.nandivaleamol.socialmediaapp.Model.RequestMember;

public class FollowerActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String userId;
    DatabaseReference databaseReference;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference checkVideoCallRef;
    String senderUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower);

        checkIncoming();

        recyclerView = findViewById(R.id.rv_followers);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(FollowerActivity.this));
        Bundle bundle = getIntent().getExtras();

        if (bundle !=null){
            userId =bundle.getString("u");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        databaseReference = database.getReference("followers").child(userId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<RequestMember> options1 =
                new FirebaseRecyclerOptions.Builder<RequestMember>()
                .setQuery(databaseReference, RequestMember.class)
                .build();

        FirebaseRecyclerAdapter<RequestMember, ProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<RequestMember, ProfileViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull RequestMember model) {
                        final String postKey = getRef(position).getKey();

                        String name = getItem(position).getName();
                        String prof = getItem(position).getProfession();
                        String url = getItem(position).getUrl();
                        String currentUserId = getItem(position).getUserId();

                        holder.setFollower(getApplication(), model.getName(), model.getUrl(), model.getProfession(),model.getBio(),
                                model.getPrivacy(),model.getEmail(), model.getFollowers(), model.getWebsite());

                        holder.vpfollower.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FollowerActivity.this, ShowUserActivity.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("p", prof);
                                intent.putExtra("uid",currentUserId);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.follower_layout,parent,false);
                        return new ProfileViewHolder(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();;
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void checkIncoming() {

        checkVideoCallRef = database.getReference("vc");

        try {
            checkVideoCallRef.child(currentUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        senderUid = snapshot.child("calleruid").getValue().toString();
                        Intent intent = new Intent(FollowerActivity.this, VideoCallinComing.class);
                        intent.putExtra("uid",senderUid);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        //Toast.makeText(getApplication(), "Not exist", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplication(), "Error"+error, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplication(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}