package com.example.blogapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.blogapp.Adapters.BlogDatabaseAdapter;
import com.example.blogapp.Adapters.UserDatabaseAdapter;
import com.example.blogapp.models.Blog;
import com.example.blogapp.models.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private UserDatabaseAdapter db;
    private BlogDatabaseAdapter blogDb;
    EditText etName, etEmail, etPassword;
    ImageView ivProfile;
    Button btnEditProfile;
    byte[] image;
    SharedPreferences pref;
    Bitmap bitmap;

    public static byte[] getBitmapAsByteArray(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new UserDatabaseAdapter(getContext());
        db.open();
        blogDb = new BlogDatabaseAdapter(getContext());
        blogDb.open();
        pref = getContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
        String userId = pref.getString("userId", "");
        User user = db.getUserWithId(userId);
        image = user.getImage();
        etName = view.findViewById(R.id.editTextTextPersonName);
        etEmail = view.findViewById(R.id.editTextTextPersonEmail);
        etPassword = view.findViewById(R.id.editTextTextPersonPassword);
        ivProfile = view.findViewById(R.id.imageView3);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());
        if(image != null) {
            ivProfile.setImageBitmap(getImage(image));
        }

        btnEditProfile.setOnClickListener(view1 -> {
            String name = etName.getText().toString();
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            db.updateUserWithId(userId, name, email, password, image);

            Intent intent = new Intent(getContext(), NavigationActivity.class);
            startActivity(intent);
        });



        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        ivProfile.setImageURI(result.getData().getData());
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getData().getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        image = getBitmapAsByteArray(bitmap);
                    }
                }
        );

        ivProfile.setOnClickListener(view1 -> {
            activityResultLauncher.launch(iGallery);
        });

        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.top_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                SharedPreferences pref = getContext().getSharedPreferences("user_details", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);

        return view;
    }
}