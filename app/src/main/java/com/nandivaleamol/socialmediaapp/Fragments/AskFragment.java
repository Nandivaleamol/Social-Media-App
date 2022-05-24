package com.nandivaleamol.socialmediaapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.nandivaleamol.socialmediaapp.AskActivity;
import com.nandivaleamol.socialmediaapp.BottomSheetF2;
import com.nandivaleamol.socialmediaapp.Model.QuestionMember;
import com.nandivaleamol.socialmediaapp.Model.Viewholder_Question;
import com.nandivaleamol.socialmediaapp.R;
import com.nandivaleamol.socialmediaapp.ReplyActivity;
import com.squareup.picasso.Picasso;


public class AskFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fb;
    ImageView imageView;
    RecyclerView recyclerView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference reference;
    FirebaseUser user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference, fvrtref, fvrt_listRef;
    Boolean fvrtChecker = false;

    QuestionMember member;
    String currentUid;

    public AskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_ask, container, false);
        return  view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        recyclerView = getActivity().findViewById(R.id.rv_af);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // database for all questions
        databaseReference = database.getReference("All Questions");

        member = new QuestionMember();
        // database for favourites
        fvrtref = database.getReference("Favourites");

        // for favourites list
        fvrt_listRef = database.getReference("FavouriteList").child(currentUid);

        imageView = getActivity().findViewById(R.id.iv_af);
        fb = getActivity().findViewById(R.id.floatingActionButton);
        reference = db.collection("users").document(currentUid);

        fb.setOnClickListener(this);
        imageView.setOnClickListener(this);

        FirebaseRecyclerOptions<QuestionMember> options =
                new FirebaseRecyclerOptions.Builder<QuestionMember>()
                .setQuery(databaseReference, QuestionMember.class)
                .build();

        FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<QuestionMember, Viewholder_Question>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull Viewholder_Question holder, int position, @NonNull QuestionMember model) {

                        // firebase
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        currentUid = user.getUid();
                        final String postKey = getRef(position).getKey();

                        // for question asking
                        holder.setItem(getActivity(), model.getName(), model.getUrl(), model.getUserId(), model.getKey(),
                                model.getQuestion(), model.getPrivacy(), model.getTime());

                        String que = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        String url = getItem(position).getUrl();
                        String time = getItem(position).getTime();
                        String privacy = getItem(position).getPrivacy();
                        String userId = getItem(position).getUserId();

                        //for replay functionality
                        holder.replyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), ReplyActivity.class);
                                intent.putExtra("uid",userId);
                                intent.putExtra("question",que);
                                intent.putExtra("post_key",postKey);
                                //intent.putExtra("key",privacy);
                                startActivity(intent);
                            }
                        });


                        // for favourite functionality
                        holder.favouriteChecker(postKey);
                        holder.fvrt_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fvrtChecker = true;

                                fvrtref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (fvrtChecker.equals(true)){
                                            if (snapshot.child(postKey).hasChild(currentUid)){
                                                fvrtref.child(postKey).child(currentUid).removeValue();
                                                delete(time);
                                                fvrtChecker = false;
                                            }else{
                                                fvrtref.child(postKey).child(currentUid).setValue(true);
                                                member.setKey(postKey);
                                                member.setName(name);
                                                member.setTime(time);
                                                member.setPrivacy(privacy);
                                                member.setUserId(userId);
                                                member.setUrl(url);
                                                member.setQuestion(que);

                                                //String id = fvrt_listRef.push().getKey();
                                                fvrt_listRef.child(postKey).setValue(member);///////////
                                                fvrtChecker = false;
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
                    public Viewholder_Question onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.question_item, parent, false);
                        return new Viewholder_Question(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);

    }
    void delete(String time){
        Query query = fvrt_listRef.orderByChild("time").equalTo(time);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren() ){
                    dataSnapshot1.getRef().removeValue();

                    Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.iv_af:
                BottomSheetF2 bottomSheetF2 = new BottomSheetF2();
                bottomSheetF2.show(getChildFragmentManager(),"bottom");
                break;

            case R.id.floatingActionButton:
                Intent intent = new Intent(getActivity(), AskActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        reference.get()
                .addOnCompleteListener((task)->{
                    if (task.getResult().exists()){
                        String url = task.getResult().getString("url");
                        
                        // load image
                        Picasso.get().load(url).into(imageView);
                    }else {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}