package com.example.aquariusmessenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mobile_layout;
    private TextInputEditText mobile_edit;
    private ImageView send_mobile_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mobile_layout = findViewById(R.id.mobile_number_layout);
        mobile_edit = findViewById(R.id.mobile_number_et);
        send_mobile_btn = findViewById(R.id.register_mobile_btn);

        send_mobile_btn.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mobile_edit.getText().toString())) {
                Toast.makeText(this, "Enter your mobile number", Toast.LENGTH_SHORT).show();
            } else if (mobile_edit.getText().toString().length() < 10) {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show();
            } else {
                Intent otpActivity = new Intent(this, OTPActivity.class);
                otpActivity.putExtra("mobile_number", mobile_edit.getText().toString());
                startActivity(otpActivity);
                overridePendingTransition(R.anim.slide_in_right,
                        R.anim.slide_out_left);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
    }
}