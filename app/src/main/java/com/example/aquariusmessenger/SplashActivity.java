package com.example.aquariusmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Fade;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;

import androidx.core.view.ViewCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    ImageView app_logo_image;
    TextView app_name_text;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        app_logo_image = findViewById(R.id.app_logo_iv);
        app_name_text = findViewById(R.id.app_name_tv);

        Fade fade = new Fade();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        new Handler().postDelayed(() -> {
            if (mUser != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                Intent registerActivity = new Intent(SplashActivity.this, RegisterActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this,
                        app_logo_image, Objects.requireNonNull(ViewCompat.getTransitionName(app_logo_image)));
                startActivity(registerActivity, optionsCompat.toBundle());
            }
            finish();
        }, 560);
    }
}