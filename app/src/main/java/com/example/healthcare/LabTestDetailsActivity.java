package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LabTestDetailsActivity extends AppCompatActivity {

    TextView tvPackageName, tvTotalCost;
    EditText edDetails;

    Button btnAddToCart, btnBack;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lab_test_details);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvPackageName = findViewById(R.id.textViewBMCartTitle);
        tvTotalCost = findViewById(R.id.textViewLDTotalCost);
        edDetails = findViewById(R.id.editTextBMDTextMultiLine);
        btnAddToCart = findViewById(R.id.buttonBMCartCheckout);
        btnBack = findViewById(R.id.buttonBMDBack);

        edDetails.setKeyListener(null);

        Intent intent = getIntent();
        tvPackageName.setText(intent.getStringExtra("text1"));
        edDetails.setText(intent.getStringExtra("text2"));
        tvTotalCost.setText("Total Cost : " + intent.getStringExtra("text3") + "/-");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LabTestDetailsActivity.this, LabTestActivity.class));
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndAddToCart();
            }
        });
    }

    private void checkAndAddToCart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String product = tvPackageName.getText().toString();

            // Check if the product already exists in the user's cart
            DatabaseReference cartRef = mDatabase.child("carts").child(userId);
            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boolean productExists = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        CartItem item = snapshot.getValue(CartItem.class);
                        if (item != null && item.product.equals(product)) {
                            productExists = true;
                            break;
                        }
                    }

                    if (productExists) {
                        Toast.makeText(LabTestDetailsActivity.this, "This package is already in your cart", Toast.LENGTH_SHORT).show();
                    } else {
                        addToCart(userId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("LabTestDetailsActivity", "Error checking cart: " + databaseError.getMessage());
                }
            });
        } else {
            Toast.makeText(LabTestDetailsActivity.this, "Please sign in to add item to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCart(String userId) {
        String email = mAuth.getCurrentUser().getEmail();
        String username = email.split("@")[0];
        String product = tvPackageName.getText().toString();
        String price = tvTotalCost.getText().toString();
        String otype = "lab_test";

        DatabaseReference cartRef = mDatabase.child("carts").child(userId).push();
        cartRef.setValue(new CartItem(email, username, product, price, otype));

        Toast.makeText(LabTestDetailsActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(LabTestDetailsActivity.this, CartLabActivity.class);
        it.putExtra("otype", otype);
        startActivity(it);
    }

    // Define a model class for Cart Item
    public static class CartItem {
        public String email;
        public String username;
        public String product;
        public String price;
        public String otype;

        public CartItem() {
            // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
        }

        public CartItem(String email, String username, String product, String price, String otype) {
            this.email = email;
            this.username = username;
            this.product = product;
            this.price = price;
            this.otype = otype;
        }
    }
}
