package com.example.aquariusmessenger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aquariusmessenger.R;
import com.example.aquariusmessenger.adapter.UsersAdapter;
import com.example.aquariusmessenger.adapter.OnItemClick;
import com.example.aquariusmessenger.models.ChatList;
import com.example.aquariusmessenger.models.notificationModel.Token;
import com.example.aquariusmessenger.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    RecyclerView chatRecycler;
    private UsersAdapter usersAdapter;
    private List<User> userList;
    private List<ChatList> chatList;

    OnItemClick onItemClick;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        chatRecycler = view.findViewById(R.id.chatRecyclerView);
        chatRecycler.setHasFixedSize(true);
        chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(chatRecycler.getContext(), DividerItemDecoration.VERTICAL);
        chatRecycler.addItemDecoration(itemDecoration);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        chatList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList list = dataSnapshot.getValue(ChatList.class);
                    chatList.add(list);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        updateToken(String.valueOf(FirebaseMessaging.getInstance().getToken()));

        return view;

    }

    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token newToken = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(newToken);
    }

    private void chatList() {
        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    for(ChatList chatList : chatList) {
                        if (user != null && user.getId() != null && chatList != null && chatList.getId() != null &&
                        user.getId().equals(chatList.getId())) {
                            userList.add(user);
                        }

                    }
                }

                usersAdapter = new UsersAdapter(getContext(), userList, true, onItemClick);
                chatRecycler.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}