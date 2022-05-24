package com.nandivaleamol.socialmediaapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nandivaleamol.socialmediaapp.Model.FcmNotificationsSender;
import com.nandivaleamol.socialmediaapp.Model.NewMember;
import com.nandivaleamol.socialmediaapp.Model.ReportClass;
import com.nandivaleamol.socialmediaapp.Model.RequestMember;
import com.squareup.picasso.Picasso;

public class ShowUserActivity extends AppCompatActivity {


    TextView nametv,professiontv,biotv,emailtv,websitetv,requesttv,blockreporttv;
    ImageView imageView;
    FirebaseDatabase database;
    DatabaseReference databaseReference,databaseReference1,databaseReference2,
            postnoref,db1,db2,ntref,blockRef,reportRefUser,blocklistref;
    TextView button,followers_tv,posts_tv;
    CardView followers_cv,posts_cd;
    String url,name,age,email,privacy,p,website,bio,userid,usertoken;
    RequestMember requestMember;
    String name_result;
    String uidreq,namereq,urlreq,professionreq;

    Button sendmessage;
    int postNo ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference documentReference,documentReference1;

    DatabaseReference checkVideocallRef;
    String senderuid;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();

    int followercount,postiv,postvv;

    NewMember newMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        database = FirebaseDatabase.getInstance();

        sendmessage = findViewById(R.id.btn_sendmessage_showuser);
        requestMember = new RequestMember();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();


        checkIncoming();

        newMember = new NewMember();
        nametv = findViewById(R.id.name_tv_showprofile);
        professiontv = findViewById(R.id.age_tv_showprofile);
        biotv = findViewById(R.id.bio_tv_showprofile);
        emailtv = findViewById(R.id.email_tv_showProfile);
        imageView = findViewById(R.id.imageView_showprofile);
        websitetv = findViewById(R.id.website_tv_showprofile);
        button = findViewById(R.id.btn_requestshowprofile);
        requesttv = findViewById(R.id.tv_requestshowprofile);

        blockreporttv = findViewById(R.id.block_reporttv);

        followers_tv = findViewById(R.id.followerNo_tv);
        posts_tv = findViewById(R.id.postsNo_tv);
        followers_cv = findViewById(R.id.followers_cardview);
        posts_cd =findViewById(R.id.followers_cardview);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            url = extras.getString("u");
            name = extras.getString("n");
            userid = extras.getString("uid");
        }else {
            //   Toast.makeText(this, "Privact account", Toast.LENGTH_SHORT).show();
        }

        databaseReference = database.getReference("Requests").child(userid);
        databaseReference1 = database.getReference("followers").child(userid);
        documentReference = db.collection("users").document(userid);
        postnoref = database.getReference("User Posts").child(userid);
        databaseReference2  = database.getReference("followers");
        documentReference1 = db.collection("users").document(currentUserId);
        db1 = database.getReference("All images").child(userid);
        db2 = database.getReference("All videos").child(userid);

        blockRef = database.getReference("Block users").child(currentuid);
        blocklistref = database.getReference("Blocklist").child(currentuid);

        ntref = database.getReference("notification").child(currentUserId);

        blockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userid)){
                    blockreporttv.setText("Unblock");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ShowUserActivity.this,MessageActivity.class);
                intent.putExtra("n",name);
                intent.putExtra("u",url);
                intent.putExtra("uid",userid);
                startActivity(intent);
            }
        });


        websitetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    String url = websitetv.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                }catch (Exception e){
                    Toast.makeText(ShowUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        postnoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postNo = (int)snapshot.getChildrenCount();
                //   posts_tv.setText(Integer.toString(postNo));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = button.getText().toString();
                if (status.equals("Follow")){
                    follow();
                }else if (status.equals("Requested")){
                    delRequest();
                }else if (status.equals("Following")){
                    unFollow();
                }

            }
        });

        followers_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowUserActivity.this,FollowerActivity.class);
                intent.putExtra("u",userid);
                startActivity(intent);
            }
        });


        blockreporttv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet();

            }
        });

    }
    private void showBottomSheet() {
        final Dialog dialog = new Dialog(ShowUserActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_block_bs);

        ImageView report,block;


        report = dialog.findViewById(R.id.report_user);
        block = dialog.findViewById(R.id.block_user);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showReportSheet();

            }
        });

        block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                NewMember newMember =  new NewMember();

                newMember.setName(name);
                newMember.setUrl(url);
                newMember.setUid(userid);


                blockRef.child(userid).setValue("blocked");
                String key = blocklistref.push().getKey();
                blocklistref.child(key).setValue(newMember);
                Toast.makeText(ShowUserActivity.this, "User Blocked", Toast.LENGTH_SHORT).show();



            }
        });




        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showReportSheet() {

        final Dialog dialog = new Dialog(ShowUserActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_user_file_bs);


        reportRefUser = database.getReference("Report users");

        ReportClass reportClass = new ReportClass();

        LinearLayout pretendingll  = dialog.findViewById(R.id.pretending_ll);
        LinearLayout shoouldnotbehere  = dialog.findViewById(R.id.shouldnotberhere_ll);

        Button button = dialog.findViewById(R.id.btn_report_user);

        TextView pretendingtv = dialog.findViewById(R.id.pretending);
        TextView shouldnotberherTv = dialog.findViewById(R.id.shouldnothere);

        RadioButton spam = dialog.findViewById(R.id.spam);
        RadioButton suicide = dialog.findViewById(R.id.suicide);
        RadioButton nudity = dialog.findViewById(R.id.nudity);
        RadioButton hatespeech = dialog.findViewById(R.id.hatespeech);
        RadioButton violence = dialog.findViewById(R.id.violence);
        RadioButton bullying = dialog.findViewById(R.id.bullying);
        RadioButton falseinfo = dialog.findViewById(R.id.falseinfo);
        RadioButton me = dialog.findViewById(R.id.me);
        RadioButton someoneIknow = dialog.findViewById(R.id.someoneiknow);
        RadioButton celeberity = dialog.findViewById(R.id.celebrity);



        pretendingtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pretendingll.setVisibility(View.VISIBLE);
                shoouldnotbehere.setVisibility(View.GONE);

            }
        });


        shouldnotberherTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pretendingll.setVisibility(View.GONE);
                shoouldnotbehere.setVisibility(View.VISIBLE);

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spam.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("Spam");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();


                }else if (suicide.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("Suicide, Self injury");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }else if (nudity.isChecked()){
                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("nudity or sexual activity");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }else if (hatespeech.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("hate speech or Symbols");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }else if (violence.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("Violence");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }else if (bullying.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("Bullying or harassment");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }else if (falseinfo.isChecked()){

                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("false info");

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();


                }else if (me.isChecked()){
                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("pretending to be me");
                    reportClass.setType(currentuid);

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();

                }else if (someoneIknow.isChecked()){
                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("pretending to be someone I know");
                    reportClass.setType(currentuid);


                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();

                }else if (celeberity.isChecked()){
                    reportClass.setName(name);
                    reportClass.setUrl(url);
                    reportClass.setUid(userid);
                    reportClass.setIssue("pretending to be celebrity");
                    reportClass.setType(currentuid);

                    String key = reportRefUser.push().getKey();
                    reportRefUser.child(key).setValue(reportClass);
                    Toast.makeText(ShowUserActivity.this, "Reported", Toast.LENGTH_SHORT).show();
                }



            }
        });





        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.Bottomanim;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    private void delRequest() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();
        databaseReference.child(currentUserId).removeValue();
        button.setText("Follow ");
    }

    @Override
    protected void onStart() {
        super.onStart();


        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postiv = (int)snapshot.getChildrenCount();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postvv = (int)snapshot.getChildrenCount();
                String total = Integer.toString(postiv+postvv);
                posts_tv.setText(total);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            String name_result = task.getResult().getString("name");
                            String age_result = task.getResult().getString("prof");
                            String bio_result = task.getResult().getString("bio");
                            String email_result = task.getResult().getString("email");
                            String web_result = task.getResult().getString("web");
                            String Url = task.getResult().getString("url");
                            p = task.getResult().getString("privacy");


                            if (p.equals("Public")){
                                professiontv.setText(bio_result);
                                nametv.setText(name_result);
                                biotv.setText(age_result);
                                emailtv.setText(email_result);
                                websitetv.setText(web_result);
                                Picasso.get().load(Url).into(imageView);
                                requesttv.setVisibility(View.GONE);
                            }else {

                                String u = button.getText().toString();
                                if (u.equals("Following")){
                                    professiontv.setText(bio_result);
                                    nametv.setText(name_result);
                                    biotv.setText(age_result);
                                    emailtv.setText(email_result);
                                    websitetv.setText(web_result);
                                    Picasso.get().load(Url).into(imageView);
                                    requesttv.setVisibility(View.GONE);
                                }else {
                                    professiontv.setText("*****************");
                                    nametv.setText(name_result);
                                    biotv.setText("*****************");
                                    emailtv.setText("*****************");
                                    websitetv.setText("*****************");
                                    Picasso.get().load(Url).into(imageView);
                                    requesttv.setVisibility(View.VISIBLE);
                                }

                            }





                        }else {
                            Toast.makeText(ShowUserActivity.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        documentReference1.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.getResult().exists()){
                            namereq = task.getResult().getString("name");
                            professionreq = task.getResult().getString("age");
                            urlreq = task.getResult().getString("url");


                        }else {
                            //  Toast.makeText(ShowUser.this, "No Profile exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        db1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    postiv = (int)snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        db2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    postvv = (int)snapshot.getChildrenCount();
                    posts_tv.setText(Integer.toString(postiv+postvv));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // refernce for following
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    followercount = (int)snapshot.getChildrenCount();
                    followers_tv.setText(Integer.toString(followercount));


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(currentUserId)){
                    button.setText("Requested");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userid).hasChild(currentUserId)){
                    button.setText("Following");
                }else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    void follow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();


        if (p.equals("Public")){
            button.setText("Following");
            requestMember.setUserId(currentUserId);//////////////////////////////////
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);

            databaseReference1.child(currentUserId).setValue(requestMember);


            newMember.setName(name);
            newMember.setUid(currentUserId);
            newMember.setUrl(url);
            newMember.setSeen("no");
            newMember.setText("Started Following you ");

            sendNotification(userid,name_result);
            ntref.child(currentUserId+"f").setValue(newMember);


        }else {

            button.setText("Requested");
            requestMember.setUserId(currentUserId);///////////////////////////////
            requestMember.setProfession(professionreq);
            requestMember.setUrl(urlreq);
            requestMember.setName(namereq);
            databaseReference.child(currentUserId).setValue(requestMember);
            requesttv.setText("Wait Until your request is accepted");

            sendNotification2(userid,name_result);

        }
    }

    private void sendNotification2(String userid, String name_result) {

        FirebaseDatabase.getInstance().getReference().child(userid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usertoken = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationsSender notificationsSender =
                        new FcmNotificationsSender(usertoken,"Social Media",name_result+" Sent You Follow request",
                                getApplicationContext(),ShowUserActivity.this);

                notificationsSender.SendNotifications();

            }
        },3000);

    }

    private void unFollow() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        AlertDialog.Builder builder = new AlertDialog.Builder(ShowUserActivity.this);
        builder.setTitle("Unfollow")
                .setMessage("Are you sure to Unfollow?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseReference1.child(currentUserId).removeValue();
                        ntref.child(currentUserId+"f").removeValue();
                        button.setText("Follow");
                        followers_tv.setText("0");
                        Toast.makeText(ShowUserActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
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

    private void sendNotification(String userid,String name_result){

        FirebaseDatabase.getInstance().getReference().child(userid).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                usertoken = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                FcmNotificationsSender notificationsSender =
                        new FcmNotificationsSender(usertoken,"Social Media",name_result+" Started Following you",
                                getApplicationContext(),ShowUserActivity.this);

                notificationsSender.SendNotifications();

            }
        },3000);

    }

    public void checkIncoming(){

        checkVideocallRef = database.getReference("vc");


        try {

            checkVideocallRef.child(currentuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.exists()){

                        senderuid = snapshot.child("calleruid").getValue().toString();
                        Intent intent = new Intent(ShowUserActivity.this,VideoCallinComing.class);
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

               Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


    }


}

