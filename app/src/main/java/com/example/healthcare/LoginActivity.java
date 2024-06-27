package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText edEmail, edPassword;
    Button btn;
    TextView tv, tvForgotPassword;
    Database db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edEmail = findViewById(R.id.editTextEmail);
        edPassword = findViewById(R.id.editTextPassword);
        btn = findViewById(R.id.buttonLogin);
        tv = findViewById(R.id.buttonRegister);
        tvForgotPassword = findViewById(R.id.textViewForgotPassword);
        db = new Database();
        auth = FirebaseAuth.getInstance();

        btn.setOnClickListener(v -> {
            String email = edEmail.getText().toString();
            String password = edPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            } else {
                db.login(email, password, new Database.DatabaseCallback() {
                    @Override
                    public void onSuccess() {
                        // Retrieve user data from the database
                        db.getUserData(new Database.DatabaseDataCallback() {
                            @Override
                            public void onDataReceived(User user) {
                                // Save username and email to SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", user.getEmail());
                                editor.putString("username", user.getUsername());
                                editor.apply();

                                Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();

                                // Pass the username in the intent to HomeActivity
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("username", user.getUsername());
                                intent.putExtra("email", user.getEmail());
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getApplicationContext(), "Email atau password salah", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        tvForgotPassword.setOnClickListener(v -> {
            String email = edEmail.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show();
            } else {
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to send reset email", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
