package com.example.blogapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.blogapp.Adapters.BlogDatabaseAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddBlogActivity extends AppCompatActivity {

    ImageButton btnImage;
    EditText etTitle;
    EditText etDescription;
    Bitmap bitmap;
    byte[] image;
    Button btnPost;
    BlogDatabaseAdapter db;
    SharedPreferences pref;

    public static byte[] getBitmapAsByteArray(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_blog);

        db = new BlogDatabaseAdapter(this);
        db.open();
        etTitle = findViewById(R.id.Title);
        etDescription = findViewById(R.id.Description);
        btnPost = findViewById(R.id.btnPost);
        btnImage = findViewById(R.id.addBlogImageBtn);
        pref = getSharedPreferences("user_details", MODE_PRIVATE);
        Intent iGallery = new Intent(Intent.ACTION_PICK);
        iGallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        btnImage.setImageURI(result.getData().getData());
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getData().getData());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        image = getBitmapAsByteArray(bitmap);
                    }
                }
        );

        btnImage.setOnClickListener(view -> {
            activityResultLauncher.launch(iGallery);
        });

        btnPost.setOnClickListener(view -> {
            String title = etTitle.getText().toString();
            String description = etDescription.getText().toString();
            int userId = Integer.parseInt(pref.getString("userId", ""));
            db.insert(userId, title, description, image);

            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
        });
    }
}