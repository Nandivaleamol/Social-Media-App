package com.nandivaleamol.socialmediaapp.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nandivaleamol.socialmediaapp.ChatActivity;
import com.nandivaleamol.socialmediaapp.CreateProfileActivity;
import com.nandivaleamol.socialmediaapp.FollowerActivity;
import com.nandivaleamol.socialmediaapp.ImageActivity;
import com.nandivaleamol.socialmediaapp.IndividualPostActivity;
import com.nandivaleamol.socialmediaapp.LoginActivity1;
import com.nandivaleamol.socialmediaapp.NotificationActivity;
import com.nandivaleamol.socialmediaapp.PrivacyActivity;
import com.nandivaleamol.socialmediaapp.R;
import com.nandivaleamol.socialmediaapp.SettingsActivity;
import com.nandivaleamol.socialmediaapp.StoryActivity;
import com.nandivaleamol.socialmediaapp.UpdateProfileActivity;
import com.nandivaleamol.socialmediaapp.VideoCallinComing;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    ImageView imageView;
    TextView nameEt, profEt, bioEt, emailEt, webEt, tv_addStores_pf, storyAdd, followerTv, newTv;
    TextView tv_posts_pf;
    ImageButton ib_edit,ib_menu ;

    //FirebaseDatabaseReference userRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    String currentUid;

    DocumentReference reference;
    FirebaseFirestore firestore;
    Button btnsendmessage;
    Uri imageUri;
    String url,userid;
    private static int PICK_IMAGE=1;
    int followerno,post1,post2,newcount;

    DatabaseReference checkVideocallRef;
    String senderuid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    FirebaseAuth mAuth;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference db1,db2,db3,ntRef;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userid = user.getUid();

        firestore = FirebaseFirestore.getInstance();
        reference = firestore.collection("users").document(userid);

        mAuth= FirebaseAuth.getInstance();

        db1 = database.getReference("followers").child(userid);
        db2 = database.getReference("All images").child(userid);
        db3 = database.getReference("All videos").child(userid);

        ntRef = database.getReference("notification").child(userid);

        imageView = getActivity().findViewById(R.id.iv_pf);
        nameEt = getActivity().findViewById(R.id.tv_name_pf);
        profEt = getActivity().findViewById(R.id.tv_prof_pf);
        bioEt = getActivity().findViewById(R.id.tv_bio_pf);
        emailEt = getActivity().findViewById(R.id.tv_email_pf);
        webEt = getActivity().findViewById(R.id.tv_web_pf);
        //tv_addStores_pf = getActivity().findViewById(R.id.tv_addstories_pf);
        tv_posts_pf = getActivity().findViewById(R.id.tv_posts_pf);
        storyAdd = getActivity().findViewById(R.id.tv_addstories_pf);
        btnsendmessage = getActivity().findViewById(R.id.btn_sendmessage_pf);
        followerTv = getActivity().findViewById(R.id.tv_followers_pf);
        newTv = getActivity().findViewById(R.id.tv_new_pf);

        ib_edit = getActivity().findViewById(R.id.ib_edit_fp);
        ib_menu = getActivity().findViewById(R.id.ib_menu_fp);

        // update image button click listener
        ib_edit.setOnClickListener(this);
        ib_menu.setOnClickListener(this);
        imageView.setOnClickListener(this);
        webEt.setOnClickListener(this);
        tv_posts_pf.setOnClickListener(this);
        storyAdd.setOnClickListener(this);
        followerTv.setOnClickListener(this);
        newTv.setOnClickListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("all");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ib_edit_fp:
//                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_menu_fp:
//                BottomSheetMenu bottomSheetMenu = new BottomSheetMenu();
//                bottomSheetMenu.show(getFragmentManager(),"bottomsheet");
                showBottomSheet();
                break;

            case R.id.iv_pf:
                Intent intent1 = new Intent(getActivity(), ImageActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_posts_pf:
                Intent intent3 = new Intent(getActivity(), IndividualPostActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_web_pf:
                 try {
                     String url = webEt.getText().toString();
                     if (!url.isEmpty()){
                         Intent intent2 = new Intent(Intent.ACTION_VIEW);
                         intent2.setData(Uri.parse(url));
                         startActivity(intent2);
                     }
                     else{
                         Toast.makeText(getActivity(), "Web url is empty", Toast.LENGTH_SHORT).show();
                         webEt.setError("Website url null");
                     }

                 }catch (Exception e){
                     Toast.makeText(getActivity(),"Invalid Url",Toast.LENGTH_SHORT).show();
                 }
                 break;
            case R.id.tv_new_pf:
                Intent intent4 = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent4);
                changeSeen();
                break;

            case R.id.btn_sendmessage_pf:
                Intent intent5 = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent5);
                break;
            case R.id.tv_addstories_pf:
//                Intent intentstory = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intentstory.setType("image/*");
//                startActivityForResult(intentstory,PICK_IMAGE);
                Toast.makeText(getActivity(), "This functionality under development", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_followers_pf:
                Intent follower = new Intent(getActivity(), FollowerActivity.class);
                follower.putExtra("u",userid);
                startActivity(follower);
                break;
        }
    }

    private void showBottomSheet() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_menu);

//        ImageView logout,privacy,settings,delete;
        CardView logout,privacy,settings,delete;

        logout = dialog.findViewById(R.id.cv_logout);
        privacy = dialog.findViewById(R.id.cv_privacy);
        delete = dialog.findViewById(R.id.cv_delete);
        settings = dialog.findViewById(R.id.cv_settings);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Logout")
                        .setMessage("Are you sure to Logout")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mAuth.signOut();

                                FirebaseDatabase.getInstance().getReference("Token").child(userid).child("token").removeValue();
                                startActivity(new Intent(getActivity(), LoginActivity1.class));

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
                builder.show();

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Profile")
                        .setMessage("Are you sure to delete?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //   StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl()

                                deleteImage();
                                reference.delete()

                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Toast.makeText(getActivity(), "Profile deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                Toast.makeText(getActivity(), "Profile delete failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                builder.create();
                builder.show();



            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), PrivacyActivity.class));

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), SettingsActivity.class));

            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void deleteImage() {
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()) {

                            String Url = task.getResult().getString("url");
                            StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(Url);
                            reference.delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                        }
                                    });

                        }else{

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == PICK_IMAGE || resultCode == RESULT_OK ||
                    data != null || data.getData() != null) {
                imageUri = data.getData();

                String url = imageUri.toString();
                Intent intent = new Intent(getActivity(), StoryActivity.class);
                intent.putExtra("u",url);
                startActivity(intent);
            }else {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){

            Toast.makeText(getActivity(), "error"+e, Toast.LENGTH_SHORT).show();
        }


    }

    private void changeSeen(){

        Map<String,Object > profile = new HashMap<>();
        profile.put("seen","yes");

        ntRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    dataSnapshot.getRef().updateChildren(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    // check incoming call
    public void checkIncoming(){

        checkVideocallRef = database.getReference("vc");

        try {

            checkVideocallRef.child(currentuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){

                        senderuid = snapshot.child("calleruid").getValue().toString();
                        Intent intent = new Intent(getActivity(), VideoCallinComing.class);
                        intent.putExtra("uid",senderuid );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }catch (Exception e){

            //   Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser =firebaseAuth.getCurrentUser();
        currentUid = firebaseUser.getUid();

        // get current user details
        DocumentReference reference;
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        reference = firestore.collection("users").document(currentUid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){

                            // get values in fireStore
                            String nameResult = task.getResult().getString("name");
                            String bioResult = task.getResult().getString("bio");
                            String emailResult = task.getResult().getString("email");
                            String webResult = task.getResult().getString("web");
                            String profResult = task.getResult().getString("prof");
                            url = task.getResult().getString("url");

                            // load image
                            Picasso.get().load(url).into(imageView);

                            //set valued
                            nameEt.setText(nameResult);
                            bioEt.setText(bioResult);
                            emailEt.setText(emailResult);
                            webEt.setText(webResult);
                            profEt.setText(profResult);

                        }
                        else{
                            Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed : "+e, Toast.LENGTH_SHORT).show();
                    }
                });

        // Query
        Query query = ntRef.orderByChild("seen").equalTo("no");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    newcount = (int) snapshot.getChildrenCount();
                    newTv.setText(Integer.toString(newcount)+"New");
                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followerno = (int) snapshot.getChildrenCount();
                followerTv.setText(Integer.toString(followerno)+" Followers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post1 = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        db3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                post2 = (int) snapshot.getChildrenCount();
                String total = Integer.toString(post1+post2);
                tv_posts_pf.setText(total +" Posts");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}