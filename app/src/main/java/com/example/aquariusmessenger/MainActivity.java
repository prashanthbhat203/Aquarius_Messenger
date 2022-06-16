package com.example.aquariusmessenger;

import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.aquariusmessenger.adapter.OnItemClick;
import com.example.aquariusmessenger.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnItemClick {

    TabLayout tabLayout;
    ViewPager2 viewPager2;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    OnItemClick onItemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPager2.setAdapter(new ViewPagerAdapter(this));

        this.onItemClick = this;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(
                tabLayout, viewPager2, (tab, position) -> {
            switch (position) {
                case 0: {
                    tab.setText("Chats");
                    break;
                }
                case 1: {
                    tab.setText("Users");
                    break;
                }
                case 2: {
                    tab.setText("Profile");
                    break;
                }

            }
        }
        );
        tabLayoutMediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        putStatusToDatabase("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        putStatusToDatabase("offline");
    }

    @Override
    public void onItemClick(String uid, View view) {

    }

    private void putStatusToDatabase(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }
}