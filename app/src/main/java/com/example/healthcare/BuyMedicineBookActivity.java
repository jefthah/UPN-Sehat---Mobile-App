package com.example.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BuyMedicineBookActivity extends AppCompatActivity {

    private EditText edname, edaddress, edcontact, edpincode;
    private Button btnBooking;
    private static final String TAG = "BuyMedicineBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_medicine_book);

        edname = findViewById(R.id.editTextBMBFullName);
        edaddress = findViewById(R.id.editTextBMBAddress);
        edcontact = findViewById(R.id.editTextBMBContactNumber);
        edpincode = findViewById(R.id.editTextBMBBpincode);
        btnBooking = findViewById(R.id.buttonBMBBBooking);

        Intent intent = getIntent();
        String priceString = intent.getStringExtra("price");
        String date = intent.getStringExtra("date");
        String userId = intent.getStringExtra("userId");
        String otype = intent.getStringExtra("otype");

        // Clean the price string before parsing
        String cleanedPriceString = priceString.replace("Total Cost: ", "").trim();
        float price = 0.0f;
        try {
            price = Float.parseFloat(cleanedPriceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid price format: " + cleanedPriceString, Toast.LENGTH_SHORT).show();
            return; // Exit if the price format is invalid
        }

        float finalPrice = price;
        btnBooking.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username tidak ditemukan. Mohon login terlebih dahulu.", Toast.LENGTH_LONG).show();
                return;
            }

            Database db = new Database();
            OrderDetail order = new OrderDetail(
                    edname.getText().toString(),
                    edaddress.getText().toString(),
                    edcontact.getText().toString(),
                    edpincode.getText().toString(),
                    date,
                    "",
                    finalPrice,
                    "medicine"
            );

            // Di dalam btnBooking.setOnClickListener di LabTestBookActivity
            db.addOrder(userId, order, new Database.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Order placed successfully. Attempting to clear cart.");
                    db.clearCart(userId, otype, new Database.DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Cart cleared successfully.");
                            Toast.makeText(getApplicationContext(), "Pemesanan berhasil!", Toast.LENGTH_LONG).show();
                            Intent orderIntent = new Intent(BuyMedicineBookActivity.this, OrderDetailsActivity.class);
                            orderIntent.putExtra("orderDetail", order);
                            orderIntent.putExtra("username", username);
                            orderIntent.putExtra("userId", userId);// Add username to intent
                            startActivity(orderIntent);
                            finish(); // Finish current activity after successful checkout
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "Failed to clear cart: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Failed to clear cart", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to place order: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Pemesanan gagal", Toast.LENGTH_LONG).show();
                }
            });
//            if (validateInput()) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference ordersRef = database.getReference("orders").child(username).push();
//                DatabaseReference cartRef = database.getReference("carts").child("amVmdGFh"); // Ganti "amVmdGFh" dengan ID pengguna yang ingin Anda gunakan
//
//                OrderDetail orderDetail = new OrderDetail(
//                        edname.getText().toString(),
//                        edaddress.getText().toString(),
//                        edcontact.getText().toString(),
//                        edpincode.getText().toString(),
//                        date,
//                        "",  // Time can be set to an empty string if not applicable
//                        finalPrice,
//                        "medicine"  // Assuming 'medicine' is the type
//                );
//
//                // Remove the entire cart for the user
//                cartRef.removeValue()
//                        .addOnSuccessListener(aVoid1 -> {
//                            Log.d(TAG, "Cart cleared successfully for user: " + username);
//                            // Proceed with order placement
//                            placeOrder(username, orderDetail);
//                        })
//                        .addOnFailureListener(e -> {
//                            Log.e(TAG, "Failed to clear cart: " + e.getMessage());
//                            Toast.makeText(getApplicationContext(), "Gagal menghapus keranjang: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                        });
//            } else {
//                Toast.makeText(getApplicationContext(), "Mohon isi semua detail", Toast.LENGTH_LONG).show();
//            }
        });
    }

    private void placeOrder(String username, OrderDetail orderDetail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ordersRef = database.getReference("orders").child(username).push();
        DatabaseReference cartRef = database.getReference("carts").child("amVmdGFh"); // Ganti "amVmdGFh" dengan ID pengguna yang ingin Anda gunakan

        ordersRef.setValue(orderDetail)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Order placed successfully for user: " + username);
                    Toast.makeText(getApplicationContext(), "Pemesanan berhasil!", Toast.LENGTH_LONG).show();
                    Intent orderIntent = new Intent(BuyMedicineBookActivity.this, OrderDetailsActivity.class);
                    orderIntent.putExtra("orderDetail", orderDetail);
                    orderIntent.putExtra("username", username); // Add username to intent
                    startActivity(orderIntent);
                    finish(); // Finish current activity after successful checkout
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to place order: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Gagal melakukan pemesanan: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private boolean validateInput() {
        return !edname.getText().toString().isEmpty()
                && !edaddress.getText().toString().isEmpty()
                && !edcontact.getText().toString().isEmpty()
                && !edpincode.getText().toString().isEmpty();
    }
}
