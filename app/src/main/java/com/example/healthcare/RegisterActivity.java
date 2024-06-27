package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText edUsername, edEmail, edPassword, edConfirm;
    Button btn;
    TextView tv;
    Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edUsername = findViewById(R.id.editTextBMBFullName);
        edPassword = findViewById(R.id.editTextBMBpincode);
        edConfirm = findViewById(R.id.editTextRegisterConfirmPassword);
        edEmail = findViewById(R.id.editTextBMBAddress);
        btn = findViewById(R.id.buttonBMBBooking);
        tv = findViewById(R.id.textViewExistingUser);
        db = new Database();

        tv.setOnClickListener(v -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));

        btn.setOnClickListener(v -> {
            String username = edUsername.getText().toString();
            String password = edPassword.getText().toString();
            String confirm = edConfirm.getText().toString();
            String email = edEmail.getText().toString();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty() || email.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please fill all the details", Toast.LENGTH_SHORT).show();
            } else {
                if (password.equals(confirm)) {
                    if (isValid(password)) {
                        db.register(username, email, password, new Database.DatabaseCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getApplicationContext(), "Data berhasil didaftarkan", Toast.LENGTH_SHORT).show();
                                // Navigasi ke Activity login
                                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                finish(); // Tutup Activity pendaftaran
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getApplicationContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "Password minimal 8 karakter, memiliki huruf, angka dan karakter spesial", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Password and confirm harus sama", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isValid(String passwordhere) {
        int f1 = 0, f2 = 0, f3 = 0;
        if (passwordhere.length() < 8) {
            return false;
        } else {
            for (int p = 0; p < passwordhere.length(); p++) {
                if (Character.isLetter(passwordhere.charAt(p))) {
                    f1 = 1;
                }
            }
            for (int r = 0; r < passwordhere.length(); r++) {
                if (Character.isDigit(passwordhere.charAt(r))) {
                    f2 = 1;
                }
            }
            for (int s = 0; s < passwordhere.length(); s++) {
                char c = passwordhere.charAt(s);
                if ((c > 33 && c <= 46) || c == 64) {
                    f3 = 1;
                }
            }
            return f1 == 1 && f2 == 1 && f3 == 1;
        }
    }
}
