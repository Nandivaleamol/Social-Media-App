package com.nandivaleamol.socialmediaapp.Model;

import android.app.Application;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nandivaleamol.socialmediaapp.R;
import com.squareup.picasso.Picasso;

public class ProfileViewHolder extends RecyclerView.ViewHolder {

    public TextView textViewName,textViewProfession,viewUserprofile,sendmessagebtn;
    TextView namell;
    TextView vp_ll;
    TextView namefollower;
    public TextView vpfollower;
    TextView professionFollower;
    DatabaseReference blockref;
    ImageView imageView,iv_ll,iv_follower;
    CardView cardView;
    LinearLayout llprofile;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    public ProfileViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    // setProfile() method
    public  void setProfile(FragmentActivity fragmentActivity, String name, String uid, String prof, String url){

        cardView = itemView.findViewById(R.id.cardview_profile);
        textViewName = itemView.findViewById(R.id.tv_name_profile);
        textViewProfession = itemView.findViewById(R.id.tv_profession_profile);
        viewUserprofile = itemView.findViewById(R.id.viewUser_profile);
        imageView = itemView.findViewById(R.id.profile_imageview);
        llprofile = itemView.findViewById(R.id.ll_profile);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentuid = user.getUid();

        blockref = database.getReference("Block users").child(currentuid);

        blockref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(uid)){

                    textViewProfession.setText("");
                    textViewName.setText("App user");
                    viewUserprofile.setVisibility(View.GONE);


                }else {

                    Picasso.get().load(url).into(imageView);
                    textViewProfession.setText(prof);
                    textViewName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    public void setProfileInchat(Application fragmentActivity, String name, String uid, String prof, String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();

        ImageView imageView = itemView.findViewById(R.id.iv_ch_item);
        TextView nametv = itemView.findViewById(R.id.name_ch_item_tv);
        TextView proftv = itemView.findViewById(R.id.ch_itemprof_tv);
        sendmessagebtn = itemView.findViewById(R.id.send_messagech_item_btn);
        CardView cv_chat = itemView.findViewById(R.id.cv_chat_profile);
        LinearLayout linearLayout = itemView.findViewById(R.id.ll_chat_profile);


        blockref = database.getReference("Block users").child(userid);

        blockref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.hasChild(uid)){
                    cv_chat.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);
                }else {

                    if (userid.equals(uid)){
                        Picasso.get().load(url).into(imageView);
                        nametv.setText(name);
                        proftv.setText(prof);
                        sendmessagebtn.setVisibility(View.INVISIBLE);
                        cv_chat.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.GONE);
                    }else {
                        Picasso.get().load(url).into(imageView);
                        nametv.setText(name);
                        proftv.setText(prof);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void setLikeduser(Application fragmentActivity, String name, String uid, String prof,
                             String url){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userid = user.getUid();


        vp_ll = itemView.findViewById(R.id.vp_ll);
        namell = itemView.findViewById(R.id.name_ll);
        iv_ll = itemView.findViewById(R.id.iv_ll);

        Picasso.get().load(url).into(iv_ll);
        namell.setText(name);


    }

    public void setFollower( Application application, String name, String url,
                             String profession, String bio, String privacy, String email, String followers, String website){

        iv_follower = itemView.findViewById(R.id.iv_follower);
        professionFollower = itemView.findViewById(R.id.profession_follower);
        namefollower = itemView.findViewById(R.id.name_follower);
        vpfollower = itemView.findViewById(R.id.vp_follower);

        Picasso.get().load(url).into(iv_follower);
        namefollower.setText(name);
        professionFollower.setText(profession);
    }

}
