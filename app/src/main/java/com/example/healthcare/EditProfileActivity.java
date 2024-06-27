package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnSave, btnDelete, btnBack;
    private Database db;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        TextView tvUsername = findViewById(R.id.tvUsernameLabel);



        db = new Database();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.getUserData(new Database.DatabaseDataCallback() {
                @Override
                public void onDataReceived(User user) {
                    if (user != null) {
                        username = user.getUsername();
                        tvUsername.setText("Username: " + username);
                        etEmail.setText(user.getEmail());
                    } else {
                        Toast.makeText(EditProfileActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(EditProfileActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }

        btnSave.setOnClickListener(v -> {
            String newEmail = etEmail.getText().toString();
            String newPassword = etPassword.getText().toString();

            if (newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                db.updateProfile(newEmail, newPassword, new Database.DatabaseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(EditProfileActivity.this, "Profile updated successfully. Please log in again.", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setTitle("Delete Account");
            builder.setMessage("Are you sure you want to delete your account?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                db.deleteAccount(new Database.DatabaseCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(EditProfileActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent loginIntent = new Intent(EditProfileActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditProfileActivity.this, "Account deletion failed", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });


        findViewById(R.id.btnCancel).setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, HomeActivity.class));
        });

    }
}
