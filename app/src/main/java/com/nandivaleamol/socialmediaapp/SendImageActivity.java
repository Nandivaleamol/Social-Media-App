package com.nandivaleamol.socialmediaapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nandivaleamol.socialmediaapp.Model.MessageMember;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SendImageActivity extends AppCompatActivity {

    String url,receiver_name,sender_uid,receiver_uid;
    ImageView imageView;
    Uri imageurl;
    ProgressBar progressBar;
    Button button;
    UploadTask uploadTask;

    DatabaseReference checkVideoCallRef;
    String senderUid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    TextView textView;
    StorageReference storageReference;
    FirebaseStorage firebaseStorage;
    DatabaseReference rootRef1, rootRef2;
    private Uri uri;

    MessageMember messageMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_image);

        messageMember = new MessageMember();

        // Message images database
        storageReference = firebaseStorage.getInstance().getReference("Message Images");
        imageView = findViewById(R.id.iv_sendImage);
        button = findViewById(R.id.btn_sendImage);
        progressBar = findViewById(R.id.pb_sendImage);
        textView = findViewById(R.id.tv_dont);

        checkIncomng();

        Bundle bundle = getIntent().getExtras();
        if (bundle !=null){
            url = bundle.getString("u");
            receiver_name = bundle.getString("n");
            receiver_uid = bundle.getString("ruid");
            sender_uid = bundle.getString("suid");
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        // loading image from firebase storage
        Picasso.get().load(url).into(imageView);
        imageurl = Uri.parse(url);

        rootRef1 = database.getReference("Message").child(sender_uid).child(receiver_uid);
        rootRef2 = database.getReference("Message").child(receiver_uid).child(sender_uid);

        // button onClick listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
                textView.setVisibility(View.VISIBLE);
            }
        });

    }
    private  String getFileExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType((contentResolver.getType(uri)));
    }

    private void sendImage() {

        if (imageurl !=null){
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child(System.currentTimeMillis()+"."+getFileExt(imageurl));
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        Calendar cdate = Calendar.getInstance();
                        SimpleDateFormat currentdate = new SimpleDateFormat("dd-MMMM-yyyy");
                        final  String savedate = currentdate.format(cdate.getTime());

                        Calendar ctime = Calendar.getInstance();
                        SimpleDateFormat currenttime = new SimpleDateFormat("HH:mm:ss");
                        final String savetime = currenttime.format(ctime.getTime());

                        String time = savedate +":"+ savetime;

                        long deletetime = System.currentTimeMillis();

                        messageMember.setDate(savedate);
                        messageMember.setTime(savetime);
                        messageMember.setImage(downloadUri.toString());
                        messageMember.setReceiveruid(receiver_uid);
                        messageMember.setSenderuid(sender_uid);
                        messageMember.setType("i");
                        messageMember.setDelete(deletetime);

                        String id = rootRef1.push().getKey();
                        rootRef1.child(id).setValue(messageMember);

                        String id1 = rootRef2.push().getKey();
                        rootRef2.child(id1).setValue(messageMember);
                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SendImageActivity.this, MessageActivity.class);
                                startActivity(intent);
                            }
                        },2000);
                    }
                }
            });
        }else{
            Toast.makeText(this, "Please select something", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIncomng() {
        checkVideoCallRef = database.getReference("vc");

        try {
            checkVideoCallRef.child(currentUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        senderUid = snapshot.child("callerUid").getValue().toString();
                        Intent intent = new Intent(SendImageActivity.this, VideoCallinComing.class);
                        intent.putExtra("uid",senderUid);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SendImageActivity.this, "not found", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
            Toast.makeText(SendImageActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}