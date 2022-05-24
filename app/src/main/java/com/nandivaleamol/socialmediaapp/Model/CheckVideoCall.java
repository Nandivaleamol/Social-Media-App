package com.nandivaleamol.socialmediaapp.Model;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nandivaleamol.socialmediaapp.VideoCallinComing;

public class CheckVideoCall {
    DatabaseReference checkVideoCallRef;
    String senderUid;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUid = user.getUid();

    Context context = null;

    public  void checkInComing(){
        checkVideoCallRef = database.getReference("vc");

        try{
            checkVideoCallRef.child(currentUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        senderUid = snapshot.child("callerUid").getValue().toString();
                        Intent intent = new Intent(context, VideoCallinComing.class)
                                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("suid",senderUid);
                        context.startActivity(intent);
                    }else{
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }catch (Exception e){
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
