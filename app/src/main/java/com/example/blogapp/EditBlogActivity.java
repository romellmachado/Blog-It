package com.example.blogapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.blogapp.Adapters.BlogDatabaseAdapter;
import com.example.blogapp.models.Blog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditBlogActivity extends AppCompatActivity {

    ImageButton btnImage;
    EditText etEditBlogTitle;
    EditText etEditBlogDescription;
    ImageButton etEditBlogImage;
    Button btnEdit;
    Button btnDelete;
    byte[] image;
    Bitmap bitmap;
    BlogDatabaseAdapter db;

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
        setContentView(R.layout.activity_edit_blog);

        db = new BlogDatabaseAdapter(this);
        db.open();
        etEditBlogTitle = findViewById(R.id.editBlogTitle);
        etEditBlogDescription = findViewById(R.id.editBlogDescrption);
        etEditBlogImage = findViewById(R.id.editBlogimageBtn);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        if(getIntent().hasExtra("selected_blog")) {
            String id = getIntent().getStringExtra("selected_blog");
            Blog blog = db.getBlogWithId(id);
            etEditBlogTitle.setText(blog.getTitle());
            etEditBlogDescription.setText(blog.getDescription());
            Bitmap bitmap = getImage(blog.getImage());
            etEditBlogImage.setImageBitmap(bitmap);
        }

        btnImage = findViewById(R.id.editBlogimageBtn);
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

        btnEdit.setOnClickListener(view -> {
            if (image == null) {
                Blog blog = db.getBlogWithId(getIntent().getStringExtra("selected_blog"));
                image = blog.getImage();
            }
            String title = etEditBlogTitle.getText().toString();
            String description = etEditBlogDescription.getText().toString();
            String id = getIntent().getStringExtra("selected_blog");
            db.updateBlogWithId(id, title, description, image);
            Toast.makeText(this, "Blog updated successfully!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(view -> {
            String id = getIntent().getStringExtra("selected_blog");

            db.deleteBlogWithId(id);

            Toast.makeText(this, "Blog deleted successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, NavigationActivity.class);
            startActivity(intent);
        });
    }
}