package com.example.aquariusmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aquariusmessenger.R;
import com.example.aquariusmessenger.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final List<Chat> mChat;
    private final String imageUrl;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> mChat, String imageUrl) {
        this.context = context;
        this.mChat = mChat;
        this.imageUrl = imageUrl;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT)
            view = LayoutInflater.from(context).inflate(R.layout.message_item_right, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.message_item_left, parent, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Chat chat = mChat.get(position);
        holder.showMessage.setText(chat.getMessage());
        if (chat.getTime() != null && !chat.getTime().trim().equals("")) {
            holder.timeTv.setText(holder.convertTime(chat.getTime()));
        }

        if (position == mChat.size() - 1) {
            if (chat.isSeen()) {
                holder.textSeen.setText("Seen");
            } else holder.textSeen.setText("Delivered");
        } else holder.textSeen.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView showMessage, textSeen, timeTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.tvMessage);
            textSeen = itemView.findViewById(R.id.tvMessageSeen);
            timeTv = itemView.findViewById(R.id.tvTime);

        }

        public String convertTime(String time) {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            return format.format(new Date(Long.parseLong(time)));
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else return MSG_TYPE_LEFT;
    }
}
