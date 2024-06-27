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

public class BuyMedicineDetailsActivity extends AppCompatActivity {

    TextView tvPackageName, tvTotalCost;
    EditText edDetails;
    Button btnBack, btnAddToCart;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buy_medicine_details);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        tvPackageName = findViewById(R.id.textViewBMCartTitle);
        edDetails = findViewById(R.id.editTextBMDTextMultiLine);
        edDetails.setKeyListener(null);
        tvTotalCost = findViewById(R.id.textViewBMDTotalCost);
        btnBack = findViewById(R.id.buttonBMDBack);
        btnAddToCart = findViewById(R.id.buttonBMDAddToCart);

        Intent intent = getIntent();
        tvPackageName.setText(intent.getStringExtra("text1"));
        edDetails.setText(intent.getStringExtra("text2"));
        tvTotalCost.setText("Total Cost: " + intent.getStringExtra("text3") + "/-");

        btnBack.setOnClickListener(v -> startActivity(new Intent(BuyMedicineDetailsActivity.this, BuyMedicineActivity.class)));

//        btnAddToCart.setOnClickListener(v -> {
//            SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
//            String username = sharedpreferences.getString("username", "");
//
//            if (username.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "Username tidak ditemukan. Mohon login terlebih dahulu.", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            String encodedUsername = Utils.encodeUsername(username);  // Encode the username
//
//            String product = tvPackageName.getText().toString();
//            float price = Float.parseFloat(intent.getStringExtra("text3"));
//
//            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(encodedUsername).push();
//            CartItem cartItem = new CartItem(product, String.valueOf(price));
//
//            cartRef.setValue(cartItem)
//                    .addOnSuccessListener(aVoid -> {
//                        Toast.makeText(getApplicationContext(), "Product Added to Cart", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(BuyMedicineDetailsActivity.this, BuyMedicineActivity.class));
//                    })
//                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to add product to cart: " + e.getMessage(), Toast.LENGTH_LONG).show());
//        });

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
                        LabTestDetailsActivity.CartItem item = snapshot.getValue(LabTestDetailsActivity.CartItem.class);
                        if (item != null && item.product.equals(product)) {
                            productExists = true;
                            break;
                        }
                    }

                    if (productExists) {
                        Toast.makeText(BuyMedicineDetailsActivity.this, "This package is already in your cart", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(BuyMedicineDetailsActivity.this, "Please sign in to add item to cart", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToCart(String userId) {
        String email = mAuth.getCurrentUser().getEmail();
        String username = email.split("@")[0];
        String product = tvPackageName.getText().toString();
        String price = tvTotalCost.getText().toString();
        String otype = "medicine";

        DatabaseReference cartRef = mDatabase.child("carts").child(userId).push();
        cartRef.setValue(new BuyMedicineDetailsActivity.CartItem(email, username, product, price, otype));

        Toast.makeText(BuyMedicineDetailsActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
        Intent it = new Intent(BuyMedicineDetailsActivity.this, CartBuyMedicineActivity.class);
        it.putExtra("otype", otype);
        startActivity(it);
    }

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
