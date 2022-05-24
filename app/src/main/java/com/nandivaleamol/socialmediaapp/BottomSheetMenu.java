package com.nandivaleamol.socialmediaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BottomSheetMenu extends BottomSheetDialogFragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference df;
    DocumentReference reference;
    CardView cv_privacy, cv_logout, cv_delete;
    FirebaseAuth auth;
    FirebaseUser user;
    String currentUid;
    String url;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_menu, null);

        cv_delete = view.findViewById(R.id.cv_delete);
        cv_logout = view.findViewById(R.id.cv_logout);
        cv_privacy = view.findViewById(R.id.cv_privacy);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user.getUid();

        // database reference
        df = FirebaseDatabase.getInstance().getReference("All_Users");

        // firestore database collection reference
        reference = db.collection("users").document(currentUid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        try {
                            if (task.getResult().exists()){
                                url = task.getResult().getString("url");
                            }
                            else{
                                Toast.makeText(getActivity(),"Url not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(getActivity(), "Error : "+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // click listener for logout
        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // click listener for privacy
        cv_privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                privacy();
            }
        });

        // click listener for delete
        cv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Delete Profile")
                        .setMessage("Are you sure to delete")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                reference.delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {

                                            @Override
                                            public void onSuccess(Void unused) {

                                                // query for deleting image in Database
                                                Query query= df.orderByChild("uid").equalTo(currentUid);
                                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                                            dataSnapshot.getRef().removeValue();
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                                                ref.delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                Toast.makeText(getActivity(), "Profile Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                            }
                                        });

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                
                            }
                        });
                builder.create();
                builder.show();

            }
        });

        return view;

    }

    private void privacy() {
        startActivity(new Intent(getActivity(), PrivacyActivity.class));
    }



    private void logout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Logout")
                .setMessage("Are you sure to logout")
                .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        auth.signOut();
                        startActivity(new Intent(getActivity(), LoginActivity1.class));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create();
        builder.show();
    }
}
