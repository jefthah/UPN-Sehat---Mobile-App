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

public class LabTestBookActivity extends AppCompatActivity {

    private EditText edname, edaddress, edcontact, edpincode;
    private Button btnBooking;
    private static final String TAG = "LabTestBookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_test_book);

        edname = findViewById(R.id.editTextBMBFullName);
        edaddress = findViewById(R.id.editTextBMBAddress);
        edcontact = findViewById(R.id.editTextBMBContactNumber);
        edpincode = findViewById(R.id.editTextBMBpincode);
        btnBooking = findViewById(R.id.buttonBMBBooking);

        Intent intent = getIntent();
        String[] priceParts = intent.getStringExtra("price").split(":");
        String priceStr = priceParts[1].replaceAll("[^\\d.]", ""); // Extracting only the numerical value
        float price = Float.parseFloat(priceStr);
        String date = intent.getStringExtra("date");
        String time = intent.getStringExtra("time");
        String userId = intent.getStringExtra("userId"); // Get the userId from intent
        String otype = intent.getStringExtra("otype");

        btnBooking.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Username not found", Toast.LENGTH_LONG).show();
                return;
            }

            Database db = new Database();
            OrderDetail order = new OrderDetail(
                    edname.getText().toString(),
                    edaddress.getText().toString(),
                    edcontact.getText().toString(),
                    edpincode.getText().toString(),
                    date,
                    time,
                    price,
                    "Lab Test"
            );

            // Di dalam btnBooking.setOnClickListener di LabTestBookActivity
            db.addOrder(userId, order, new Database.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Order placed successfully. Attempting to clear cart.");
                    db.clearCart(userId, otype,new Database.DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Cart cleared successfully.");
                            Toast.makeText(getApplicationContext(), "Pemesanan berhasil!", Toast.LENGTH_LONG).show();
                            Intent orderIntent = new Intent(LabTestBookActivity.this, OrderDetailsActivity.class);
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

        });
    }
}
