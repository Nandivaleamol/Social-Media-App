package com.nandivaleamol.socialmediaapp.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.nandivaleamol.socialmediaapp.Model.PostMember;
import com.nandivaleamol.socialmediaapp.Model.PostViewHolder;
import com.nandivaleamol.socialmediaapp.PostActivity;
import com.nandivaleamol.socialmediaapp.R;

import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {

   // Button button;
    FloatingActionButton button;
    RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference, likeref;
    Boolean likeChecker = false;
    ImageButton btncomment;

    // database references
    DatabaseReference db1, db2, db3;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = user.getUid();

    LinearLayoutManager linearLayoutManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        button = getActivity().findViewById(R.id.create_post_home);
        reference = database.getReference("All posts");
        likeref = database.getReference("post likes");

        btncomment =getActivity().findViewById(R.id.comment_button_posts);

        recyclerView = getActivity().findViewById(R.id.rv_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        db1 = database.getReference("All images");
        db2 = database.getReference("All videos");
        db3 = database.getReference("All posts");
        db3.keepSynced(true);

        // posts retrieve to latest time date.
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.create_post_home:
                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PostMember> options =
                new FirebaseRecyclerOptions.Builder<PostMember>()
                        .setQuery(reference, PostMember.class)
                        .build();

        FirebaseRecyclerAdapter<PostMember, PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<PostMember, PostViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull PostViewHolder holder, int position, @NonNull PostMember model) {
                        // firebase
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String currentUid = user.getUid();
                        final String postKey = getRef(position).getKey();

                        // for posts
                        holder.setPost(getActivity(), model.getName(), model.getUrl(), model.getPostUri(), model.getTime(),
                                model.getUid(), model.getType(),model.getDesc());

//                        String que = getItem(position).getQuestion();
                        String name = getItem(position).getName();
                        //String url = getItem(position).getUrl();
                        final String time = getItem(position).getTime();
                        String userId = getItem(position).getUid ();
                        String type = getItem(position).getType();
                        String url = getItem(position).getPostUri();
                        
                        // more options
                        holder.menuOption.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showDialog(name,url,time,userId, type);
                            }
                        });

                        // for favourite functionality
                        holder.likeChecker(postKey);
                        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                likeChecker = true;

                                likeref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        if (likeChecker.equals(true)) {
                                            if (snapshot.child(postKey).hasChild(currentUid)) {
                                                likeref.child(postKey).child(currentUid).removeValue();
                                               // delete(time);
                                                likeChecker = false;
                                            } else {
                                                likeref.child(postKey).child(currentUid).setValue(true);

                                                likeChecker = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
//                        btncomment.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Toast.makeText(getActivity(), "This function coming soon", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }

                    @NonNull
                    @Override
                    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.post_layout, parent, false);
                        return new PostViewHolder(view) {
                        };
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    // Alert Dialog (More Options)
    private void showDialog(String name, String url, String time, String userId,String type) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.post_options,null);

        TextView download = view.findViewById(R.id.download_tv_post);
        TextView share = view.findViewById(R.id.share_tv_post);
        TextView delete = view.findViewById(R.id.delete_tv_post);
        TextView copyurl = view.findViewById(R.id.copyurl_tv_post);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
        alertDialog.show();

        // delete btn validating
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        if (userId.equals(currentUserId)){
            delete.setVisibility(View.VISIBLE);
        }else{
            delete.setVisibility(View.INVISIBLE);
        }

        // deleting post
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // delete post from (All Images) Collection
                Query query = db1.orderByChild("time").equalTo(time);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();
                            //Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

                // delete from All videos
                Query query1 = db2.orderByChild("time").equalTo(time);
                query1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();
                            //Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                //delete from All posts
                Query query2 = db3.orderByChild("time").equalTo(time);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()){
                            dataSnapshot1.getRef().removeValue();
                            //Toast.makeText(getActivity(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();

                    }
                });

                // post deleting from firebase storage database
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Deleted from firebase storage", Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.dismiss();

            }
        });

        // download posts
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Runtime user storage permission
                PermissionListener permissionListener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                        // checking condition given file image or video
                        if (type.equals("iv")) {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading image...");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + System.currentTimeMillis() + ".jpg");
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                            alertDialog.dismiss();
                        } else {
                            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                            request.setTitle("Download");
                            request.setDescription("Downloading video...");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + System.currentTimeMillis() + ".mp4");
                            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            manager.enqueue(request);

                            Toast.makeText(getActivity(), "Downloading", Toast.LENGTH_SHORT).show();

                            alertDialog.dismiss();
                        }

                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getActivity(), "No permission", Toast.LENGTH_SHORT).show();
                    }
                };

                TedPermission.with(getActivity())
                        .setPermissionListener(permissionListener)
                        .setPermissions(Manifest.permission.INTERNET, Manifest.permission.READ_EXTERNAL_STORAGE)
                        .check();
            }
        });

        // share posts other apps like whatsApp etc.
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareText = name+"\n" + "\n"+url;
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(intent.createChooser(intent,"share via"));
                
                alertDialog.dismiss();
            }
        });

        // copy post url
        copyurl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cp = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("String", url);
                cp.setPrimaryClip(clipData);
                clipData.getDescription();
                Toast.makeText(getActivity(), "Url copied", Toast.LENGTH_SHORT).show();

                alertDialog.dismiss();
            }
        });

    }


}