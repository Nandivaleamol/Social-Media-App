package com.nandivaleamol.socialmediaapp.Model;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nandivaleamol.socialmediaapp.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public abstract class PostViewHolder extends RecyclerView.ViewHolder{

    ImageView imageViewProfile, iv_post;
    TextView tv_time, tv_desc, tv_likes, tv_comment, tv_nameProfile;
    public ImageButton likeBtn;
    public ImageButton menuOption;
    ImageButton commentsBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference likesref;

    DatabaseReference commentref,blockref;
    CardView cardView;
    LinearLayout linearLayout;

    int likesCount, commentcount;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setPost(FragmentActivity activity, String name, String url, String postUrl, String time,
                        String uid, String type, String desc){

      imageViewProfile = itemView.findViewById(R.id.iv_profile_post);
      iv_post = itemView.findViewById(R.id.iv_post_item);
      tv_comment = itemView.findViewById(R.id.tv_comment_post);
      tv_desc = itemView.findViewById(R.id.tv_desc_post);
      commentsBtn = itemView.findViewById(R.id.comment_button_posts);
      likeBtn = itemView.findViewById(R.id.like_button_posts);
      tv_likes = itemView.findViewById(R.id.tv_likes_post);
      menuOption = itemView.findViewById(R.id.more_button_posts);
      tv_nameProfile = itemView.findViewById(R.id.tv_name_post);
      tv_time = itemView.findViewById(R.id.tv_time_post);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        blockref = database.getReference("Block users").child(currentuid);

        blockref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(uid)){
                    cardView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // exoPlayer
        SimpleExoPlayer exoPlayer;
        PlayerView playerView = itemView.findViewById(R.id.exoplayer_item_post);

        // check imageView or videoView
        if (type.equals("iv")){

            playerView.setVisibility(View.INVISIBLE);
            iv_post.setVisibility(View.VISIBLE);
            // profile image load in imageViewProfile
            Picasso.get().load(url).into(imageViewProfile);

            // load posts
            Picasso.get().load(postUrl).into(iv_post);

            tv_nameProfile.setText(name);
            tv_time.setText(time);
            tv_desc.setText(desc);

        }else if (type.equals("vv")){

            playerView.setVisibility(View.VISIBLE);

             iv_post.setVisibility(View.INVISIBLE);
             tv_desc.setText(desc);
             tv_time.setText(time);
             tv_nameProfile.setText(name);

             Picasso.get().load(url).into(imageViewProfile);

            try {
                SimpleExoPlayer simpleExoPlayer = new SimpleExoPlayer.Builder(activity).build();
                playerView.setPlayer(simpleExoPlayer);
                MediaItem mediaItem = MediaItem.fromUri(postUrl);
                simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
                simpleExoPlayer.prepare();
                simpleExoPlayer.setPlayWhenReady(false);

            }catch (Exception e){
                Toast.makeText(activity, "Error", Toast.LENGTH_SHORT).show();
            }

        }
    }
    // to check question is your favourite or not
    public void likeChecker(String postKey) {
        likeBtn = itemView.findViewById(R.id.like_button_posts);

        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final  String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(uid)){
                    likeBtn.setImageResource(R.drawable.ic_like);
                    likesCount = (int) snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likesCount)+" likes");
                }else{
                    likeBtn.setImageResource(R.drawable.ic_dislike);
                    likesCount = (int) snapshot.child(postKey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likesCount)+" likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // work date 21-05-2022
    public void likeschecker(final String postkey) {
        likeBtn = itemView.findViewById(R.id.like_button_posts);


        likesref = database.getReference("post likes");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        likesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(postkey).hasChild(uid)) {
                    likeBtn.setImageResource(R.drawable.ic_like);
                    likesCount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likesCount) + "likes");
                } else {
                    likeBtn.setImageResource(R.drawable.ic_dislike);
                    likesCount = (int) snapshot.child(postkey).getChildrenCount();
                    tv_likes.setText(Integer.toString(likesCount) + "likes");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void commentchecker(final String postkey) {

        tv_comment = itemView.findViewById(R.id.tv_comment_post);


        commentref = database.getReference("All Posts").child(postkey).child("Comments");

        commentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                commentcount = (int) snapshot.getChildrenCount();
                tv_comment.setText(Integer.toString(commentcount)+" Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }// 21-05-2022
}
