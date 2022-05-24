package com.nandivaleamol.socialmediaapp.Model;

import android.app.Application;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nandivaleamol.socialmediaapp.R;
import com.squareup.picasso.Picasso;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public TextView senderTv;
    public TextView receiverTv;
    public ImageView iv_sender;
    public ImageView iv_receiver;
    public ImageButton playSender;
    public ImageButton playReceiver;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }
    public void setMessage(Application application, String message, String time, String date, String type,
                            String senderuid, String receiveruid, String sendername, String audio, String image){

        senderTv = itemView.findViewById(R.id.sender_tv);
        receiverTv = itemView.findViewById(R.id.receiver_tv);

        playReceiver = itemView.findViewById(R.id.play_message_receiver);
        playSender = itemView.findViewById(R.id.play_message_sender);
        LinearLayout llsender = itemView.findViewById(R.id.ll_sender);
        LinearLayout llreceiver = itemView.findViewById(R.id.ll_receiver);

        iv_receiver = itemView.findViewById(R.id.iv_receiver);
        iv_sender = itemView.findViewById(R.id.iv_sender);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        if (currentUid.equals(senderuid)){
            if (type.equals("i")){
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
                Picasso.get().load(image).into(iv_sender);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
            }else if (type.equals("t")){
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.VISIBLE);
                senderTv.setText(message);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            }else if (type.equals("a")){
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.VISIBLE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            }

        }else if (currentUid.equals(receiveruid)){
            if(type.equals("i")){
                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.VISIBLE);
                Picasso.get().load(image).into(iv_receiver);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
            }else if (type.equals("t")){

                receiverTv.setVisibility(View.VISIBLE);
                senderTv.setVisibility(View.GONE);
                receiverTv.setText(message);
                llreceiver.setVisibility(View.GONE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);

            }else if (type.equals("a")){

                receiverTv.setVisibility(View.GONE);
                senderTv.setVisibility(View.GONE);
                llreceiver.setVisibility(View.VISIBLE);
                llsender.setVisibility(View.GONE);
                iv_sender.setVisibility(View.GONE);
                iv_receiver.setVisibility(View.GONE);
            }

        }
    }

//    public void setMessage(Application application, String message, String time, String date, String type, String senderuid, String receiveruid, String sendername, String audio, String image) {
//
//    }
}
