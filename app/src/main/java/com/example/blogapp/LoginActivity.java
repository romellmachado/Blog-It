package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.blogapp.Adapters.UserDatabaseAdapter;

public class LoginActivity extends AppCompatActivity {
    private Button btnNewAccount;
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;
    UserDatabaseAdapter db;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnNewAccount = findViewById(R.id.buttonNewAccount);
        btnLogin = findViewById(R.id.buttonLogin);
        etEmail = findViewById(R.id.editTextTextEmailAddressLogin);
        etPassword = findViewById(R.id.editTextTextPasswordLogin);
        db = new UserDatabaseAdapter(this);
        db.open();
        pref = getSharedPreferences("user_details", MODE_PRIVATE);

        btnLogin.setOnClickListener((view) -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            Boolean login = db.login(email, password);
            if (login) {
                int userId = db.getUserId(email);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("userId", "" + userId);
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Incorrect credentials, Please try again!", Toast.LENGTH_SHORT).show();
            }

        });

        btnNewAccount.setOnClickListener((view) -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}