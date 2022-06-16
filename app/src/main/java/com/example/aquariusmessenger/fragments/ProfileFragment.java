package com.example.aquariusmessenger.fragments;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.aquariusmessenger.R;
import com.example.aquariusmessenger.models.User;
import com.example.aquariusmessenger.utils.ProgressDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    FrameLayout edit_profile_image;
    CircleImageView profile_image;
    TextInputLayout username_layout, bio_layout;
    TextInputEditText username_et, bio_et;

    DatabaseReference databaseReference;
    StorageReference storageReference;

    FirebaseUser mUser;
    ProgressDialog progressDialog;

    Uri imageUri;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        edit_profile_image = view.findViewById(R.id.add_profile_image_layout);
        profile_image = view.findViewById(R.id.profile_image_view);
        username_layout = view.findViewById(R.id.username_profile_layout);
        bio_layout = view.findViewById(R.id.bio_layout);
        username_et = view.findViewById(R.id.username_profile_et);
        bio_et = view.findViewById(R.id.bio_et);


        mUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded()) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    username_et.setText(user.getUsername());
                    bio_et.setText(user.getBio());
                    Glide.with(requireContext()).load(user.getImageUrl()).into(profile_image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        edit_profile_image.setOnClickListener(click -> {
            if (isReadStoragePermissionGranted()) {
                selected.launch("image/*");

            }

        });

        username_layout.setEndIconOnClickListener(v -> databaseReference.child("username").setValue(Objects.requireNonNull(username_et.getText()).toString().trim()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Username Updated", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Username update failed", Toast.LENGTH_SHORT).show();
        }));

        bio_layout.setEndIconOnClickListener(v -> databaseReference.child("bio").setValue(Objects.requireNonNull(bio_et.getText()).toString().trim()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Bio Updated", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Bio Update Failed", Toast.LENGTH_SHORT).show();
        }));
        return view;
    }

    ActivityResultLauncher<String> selected = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        imageUri = result;
                        Glide.with(ProfileFragment.this).load(imageUri).into(profile_image);
                    } else {
                        Toast.makeText(getContext(), "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = fileReference.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Handler handler = new Handler();
                handler.postDelayed(() -> Toast.makeText(getContext(), "Please wait!", Toast.LENGTH_LONG).show(), 500);

                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d("Image", "Image Uploading");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("imageUrl", imageUri.toString());
                    databaseReference.updateChildren(map);
                    progressDialog.dismissDialog();
                });
            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Please try again!", Toast.LENGTH_SHORT).show());

        } else Log.e("ImageUri", "Its null");
    }

    private boolean isReadStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requireActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
                return false;
            }
        }
        return false;
    }
}