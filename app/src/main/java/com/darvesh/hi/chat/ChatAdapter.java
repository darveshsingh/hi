package com.darvesh.hi.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.darvesh.hi.R;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<com.darvesh.hi.chat.ChatAdapter.ChatViewHolder> {

    ArrayList<MessageObject> messageList;
    public ChatAdapter(ArrayList<MessageObject> messageList){
        this.messageList=messageList;
    }

    @NonNull
    @Override
    public com.darvesh.hi.chat.ChatAdapter.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        com.darvesh.hi.chat.ChatAdapter.ChatViewHolder rcv = new com.darvesh.hi.chat.ChatAdapter.ChatViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final com.darvesh.hi.chat.ChatAdapter.ChatViewHolder holder, final int position) {
        holder.mMessage.setText(messageList.get(position).getMessage());
        holder.mSender.setText(messageList.get(position).getSenderId());

        if(messageList.get(holder.getAdapterPosition()).getMediaUrlList().isEmpty()){
            holder.mDisplayMedia.setVisibility(View.GONE);
        }

        holder.mDisplayMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messageList.get(holder.getAdapterPosition()).getMediaUrlList())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder{
        TextView mMessage,
                    mSender;
        Button mDisplayMedia;
        public ChatViewHolder(View view){
            super(view);

            mMessage=view.findViewById(R.id.message);
            mSender=view.findViewById(R.id.sender);
            mDisplayMedia=view.findViewById(R.id.displayMedia);
        }
    }
}
