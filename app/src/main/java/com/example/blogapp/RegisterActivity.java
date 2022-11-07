package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blogapp.Adapters.UserDatabaseAdapter;

public class RegisterActivity extends AppCompatActivity {
    private Button btnBackLogin;
    private Button btnRegister;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etName;
    private EditText etConfirmPassword;
    UserDatabaseAdapter db;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new UserDatabaseAdapter(this);
        db.open();
        btnBackLogin = findViewById(R.id.buttonBackLogin);
        btnRegister = findViewById(R.id.buttonRegister);
        etEmail = findViewById(R.id.editTextTextEmailAddressRegister);
        etName = findViewById(R.id.editTextTextNameRegister);
        etPassword = findViewById(R.id.editTextTextPasswordRegister);
        etConfirmPassword = findViewById(R.id.editTextTextConfirmPasswordRegister);
        pref = getSharedPreferences("user_details", MODE_PRIVATE);

        btnRegister.setOnClickListener((view) -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();
            String name = etName.getText().toString();
            Boolean emailExists = db.getUserEmail(email);
            if (emailExists == true) {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Register Activity", "password: " + password + " confirm password: " + confirmPassword);
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, "passwords do not match!", Toast.LENGTH_SHORT).show();
                } else {
                    db.insert(name, email, password, null);
                    int userId = db.getUserId(email);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userId", "" + userId);
                    editor.commit();
                    Toast.makeText(this, "User registered successfully!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnBackLogin.setOnClickListener((view) -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}