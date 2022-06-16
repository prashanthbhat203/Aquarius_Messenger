package com.example.aquariusmessenger;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.aquariusmessenger.models.User;
import com.example.aquariusmessenger.utils.OfflineStore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsernameActivity extends AppCompatActivity {

    FrameLayout select_profile_image;
    CircleImageView profile_image;
    TextInputLayout username_layout;
    TextInputEditText username_et;
    ImageView move_to_mainActivity;
    TextView tvSet;
    ConstraintLayout upload_progress_layout;

    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Uri imageUri;
    StorageTask uploadTask;
    String bio = "Hey there! I'm using Aquarius";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        select_profile_image = findViewById(R.id.add_profile_image_layout);
        profile_image = findViewById(R.id.profile_image_view);
        username_layout = findViewById(R.id.username_layout);
        username_et = findViewById(R.id.username_et);
        move_to_mainActivity = findViewById(R.id.all_done_btn);
        tvSet = findViewById(R.id.tvSet);
        upload_progress_layout = findViewById(R.id.progress_layout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        select_profile_image.setOnClickListener(view -> {
            if (isReadStoragePermissionGranted()) {
                mGetContent.launch("image/*");
            } else {
                Toast.makeText(this, "Grant the permission to set a image", Toast.LENGTH_SHORT).show();
            }

        });

        move_to_mainActivity.setOnClickListener(view -> {
            if (TextUtils.isEmpty(Objects.requireNonNull(username_et.getText()).toString())) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show();
            } else {
                //upload username and image to firebase
                if (uploadTask != null && uploadTask.isInProgress()) {
                    move_to_mainActivity.setVisibility(View.GONE);
                    tvSet.setVisibility(View.GONE);
                    upload_progress_layout.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Please wait!", Toast.LENGTH_SHORT).show();
                } else uploadImageAndUsername();

                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        imageUri = result;
                        Glide.with(UsernameActivity.this).load(imageUri).into(profile_image);
                    } else {
                        Toast.makeText(UsernameActivity.this, "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private void uploadImageAndUsername() {

        OfflineStore.setUsername(getApplicationContext(), Objects.requireNonNull(username_et.getText()).toString());
        String userId = mUser.getUid();
        String username = username_et.getText().toString();
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    upload_progress_layout.setVisibility(View.GONE);
                    move_to_mainActivity.setVisibility(View.VISIBLE);
                    tvSet.setVisibility(View.VISIBLE);
                    Toast.makeText(UsernameActivity.this, "Please wait!", Toast.LENGTH_LONG).show();
                }, 500);

                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    User user = new User(userId, username, uri.toString(), "offline", username.toLowerCase(), bio);
                    databaseReference.child(mUser.getUid()).setValue(user);
                });
            }).addOnFailureListener(e -> Toast.makeText(UsernameActivity.this, "Please try again!", Toast.LENGTH_SHORT).show())
                    .addOnCompleteListener(task -> {
                        upload_progress_layout.setVisibility(View.GONE);
                        move_to_mainActivity.setVisibility(View.VISIBLE);
                        tvSet.setVisibility(View.VISIBLE);
                    });

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        return false;
    }
}