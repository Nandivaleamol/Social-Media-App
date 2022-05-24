package com.nandivaleamol.socialmediaapp;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nandivaleamol.socialmediaapp.Model.All_UserMember;
import com.nandivaleamol.socialmediaapp.Model.CheckVideoCall;
import com.nandivaleamol.socialmediaapp.Model.ProfileViewHolder;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    DatabaseReference profileRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RecyclerView recyclerView;
    EditText searchEt;
    CheckVideoCall checkVideoCall;
    DatabaseReference checkVideoCallRef;
    String senderUid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        searchEt = findViewById(R.id.search_user_ch);
        recyclerView = findViewById(R.id.rv_ch);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));

        profileRef = database.getReference("All Users");

        checkIncoming();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchEt.getText().toString().toUpperCase();
                Query search = profileRef.orderByChild("name").startAt(query).endAt(query+"\uf0ff");

                FirebaseRecyclerOptions<All_UserMember> options1 =
                        new FirebaseRecyclerOptions.Builder<All_UserMember>()
                        .setQuery(search,All_UserMember.class)
                        .build();

                FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                            @Override
                            protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                                final  String postKey = getRef(position).getKey();

                                holder.setProfileInchat(getApplication(), model.getName(), model.getUid(),model.getProf(),
                                        model.getUrl());

                                String name = getItem(position).getName();
                                String url = getItem(position).getUrl();
                                String uid = getItem(position).getUid();

                                holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                                        intent.putExtra("n",name);
                                        intent.putExtra("u",url);
                                        intent.putExtra("uid",uid);
                                        startActivity(intent);
                                    }
                                });
                            }

                            @NonNull
                            @Override
                            public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.chat_profile_item,parent,false);
                                return  new ProfileViewHolder(view);
                            }
                        };
                firebaseRecyclerAdapter.startListening();
                recyclerView.setAdapter(firebaseRecyclerAdapter);
            }
        });
    }

    private void checkIncoming() {
        
        checkVideoCallRef = database.getReference("vc");
        
        try {
            checkVideoCallRef.child(currentUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    
                    if (snapshot.exists()){
                        senderUid = snapshot.child("callerId").getValue().toString();
                        Intent intent = new Intent(ChatActivity.this, VideoCallinComing.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(ChatActivity.this, "Not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        
    }

    // onStart()

    @Override
    protected void onStart() {
        super.onStart();

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        };

        TedPermission.with(ChatActivity.this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();

        FirebaseRecyclerOptions<All_UserMember> options1 =
                new FirebaseRecyclerOptions.Builder<All_UserMember>()
                .setQuery(profileRef, All_UserMember.class)
                .build();

        FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<All_UserMember, ProfileViewHolder>(options1) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProfileViewHolder holder, int position, @NonNull All_UserMember model) {

                        final String postKey = getRef(position).getKey();
                        holder.setProfileInchat(getApplication(), model.getName(),model.getUid(),model.getProf(),model.getUrl());

                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String uid = getItem(position).getUid();

                        holder.sendmessagebtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ChatActivity.this, MessageActivity.class);
                                intent.putExtra("n",name);
                                intent.putExtra("u",url);
                                intent.putExtra("uid",uid);
                                startActivity(intent);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.chat_profile_item, parent,false);
                        return new ProfileViewHolder(view);
                    }
                };

        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
}