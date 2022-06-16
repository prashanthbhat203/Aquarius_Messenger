package com.example.aquariusmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aquariusmessenger.MessagingActivity;
import com.example.aquariusmessenger.R;
import com.example.aquariusmessenger.models.Chat;
import com.example.aquariusmessenger.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ChatsViewHolder> {

    Context context;
    List<User> userList;
    boolean isChat;
    OnItemClick onItemClick;

    String lastMessage;

    public UsersAdapter(Context context, List<User> userList, boolean isChat, OnItemClick onItemClick) {
        this.context = context;
        this.userList = userList;
        this.isChat = isChat;
        this.onItemClick = onItemClick;

    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_item, parent, false);
        return new ChatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        final User user = userList.get(position);
        holder.username.setText(user.getUsername());
        if (user.getImageUrl().equals("default"))
            holder.profile_image.setImageResource(R.drawable.user);
        else Glide.with(context).load(user.getImageUrl()).into(holder.profile_image);

        if (isChat) {
            lastMessage(holder.lastMessage);
        } else holder.lastMessage.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent messagingIntent = new Intent(context, MessagingActivity.class);
            messagingIntent.putExtra("userId", user.getId());
            context.startActivity(messagingIntent);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class ChatsViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profile_image;
        TextView username, lastMessage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_image = itemView.findViewById(R.id.image_view_profile);
            username = itemView.findViewById(R.id.friends_tv);
            lastMessage = itemView.findViewById(R.id.message_tv);
        }
    }

    private void lastMessage(final TextView last_msg) {
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        lastMessage = chat.getMessage();
                    }
                }

                if ("default".equals(lastMessage)) {
                    last_msg.setText("No message");
                } else {
                    last_msg.setText(lastMessage);
                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
