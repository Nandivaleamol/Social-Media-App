package com.nandivaleamol.socialmediaapp.Model;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

public class Viewholder_Question extends RecyclerView.ViewHolder {

    ImageView imageView;
    TextView time_result, name_result, question_result;
    public TextView replyBtn;
    public TextView replyBtn1;
    public TextView deleteBtn;
    public ImageButton fvrt_btn;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference favouriteref;
    public Viewholder_Question(@NonNull View itemView) {
        super(itemView);

    }
    public void setItem(FragmentActivity activity, String name, String url, String userId, String key,String question,
                        String privacy, String time){

        imageView = itemView.findViewById(R.id.iv_que_item);
        time_result = itemView.findViewById(R.id.time_que_item_tv);
        name_result = itemView.findViewById(R.id.name_que_item_tv);
        question_result = itemView.findViewById(R.id.que_item_tv);
        replyBtn = itemView.findViewById(R.id.reply_item_que);

        // load image
        Picasso.get().load(url).into(imageView);
        time_result.setText(time);
        name_result.setText(name);
        question_result.setText(question);
    }

    // for related_item resource file --> Related questions
    public void setItemRelated(Application activity, String name, String url, String userId, String key, String question,
                               String privacy, String time){

        TextView timeTv = itemView.findViewById(R.id.time_related_item_tv);
        ImageView imageView = itemView.findViewById(R.id.iv_related_item);
        TextView nameTv = itemView.findViewById(R.id.name_related_item_tv);
        TextView queTv = itemView.findViewById(R.id.que_related_item_tv);
        replyBtn1 = itemView.findViewById(R.id.view_reply_item_related);

        // loading image
        Picasso.get().load(url).into(imageView);
        nameTv.setText(name);
        timeTv.setText(time);
        queTv.setText(question);

    }

//    // for your_questions_item resource file --> Your questions
    public void setYourQuestion(Application activity, String name, String url, String userId, String key, String question,
                                String privacy, String time){

        TextView timeTv = itemView.findViewById(R.id.time_your_questions_item_tv);
        deleteBtn = itemView.findViewById(R.id.delete_your_questions_item);
        TextView queTv = itemView.findViewById(R.id.que_your_questions_item_tv);
        TextView nameTv = itemView.findViewById(R.id.name_your_questions_item_tv);
        replyBtn = itemView.findViewById(R.id.reply_item_que);


        // loading image
        //Picasso.get().load(url).into(imageView);
        timeTv.setText(time);
        queTv.setText(question);
        nameTv.setText(name);

    }

    // to check question is your favourite or not
    public void favouriteChecker(String postKey) {
        fvrt_btn = itemView.findViewById(R.id.fvrt_btn_item);

        favouriteref = database.getReference("Favourites");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final  String uid = user.getUid();

        favouriteref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postKey).hasChild(uid)){
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_24);
                }else{
                    fvrt_btn.setImageResource(R.drawable.ic_baseline_turned_in_not_24);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
