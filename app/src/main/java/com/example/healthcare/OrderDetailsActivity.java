package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewOD;
    private TextView totalCostTextView;
    private OrderDetailsAdapter adapter;
    private ArrayList<OrderDetail> orderList;
    private DatabaseReference ordersRef;
    private Button btnBack;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        // Initialize UI components
        recyclerViewOD = findViewById(R.id.recyclerViewOD);
        totalCostTextView = findViewById(R.id.textViewTotalCost);
        btnBack = findViewById(R.id.buttonBMDBack); // Ensure this matches the ID in the XML

        // Set up RecyclerView
        recyclerViewOD.setLayoutManager(new LinearLayoutManager(this));

        // Get username from intent or shared preferences
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        if (userId == null) {
            // Fallback to shared preferences if username is not passed in the intent
            userId = getSharedPreferences("shared_prefs", MODE_PRIVATE).getString("userId", null);
        }

        if (userId== null) {
            Toast.makeText(this, "UserId is required to fetch order details", Toast.LENGTH_LONG).show();
            Log.e("OrderDetailsActivity", "UserId is null, finishing activity");
            finish();
            return;
        }

        Log.d("OrderDetailsActivity", "Fetching orders for username: " + userId);

        // Initialize Firebase reference
        ordersRef = FirebaseDatabase.getInstance().getReference().child("orders").child(userId);

        // Initialize order list
        orderList = new ArrayList<>();

        // Fetch orders from Firebase
        fetchOrdersFromFirebase();

        // Set listener for back button
        btnBack.setOnClickListener(view -> {
            Intent backIntent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
            startActivity(backIntent);
            finish(); // Optional: To prevent going back to this activity
        });
    }

    private void fetchOrdersFromFirebase() {
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("OrderDetailsActivity", "DataSnapshot received: " + dataSnapshot.toString());
                orderList.clear();
                float totalCost = 0;
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    if (orderSnapshot.exists()) {
                        try {
                            String name = orderSnapshot.child("name").getValue(String.class);
                            String contact = orderSnapshot.child("contact").getValue(String.class);
                            String address = orderSnapshot.child("address").getValue(String.class);
                            String date = orderSnapshot.child("date").getValue(String.class);
                            String time = orderSnapshot.child("time").getValue(String.class);
                            String pincode = orderSnapshot.child("pincode").getValue(String.class);
                            Float price = orderSnapshot.child("price").getValue(Float.class);
                            String type = orderSnapshot.child("type").getValue(String.class); // Get the type field

                            Log.d("OrderDetailsActivity", "Order item fields: " +
                                    "name=" + name + ", contact=" + contact + ", address=" + address +
                                    ", date=" + date + ", time=" + time + ", pincode=" + pincode +
                                    ", price=" + price + ", type=" + type);

                            if (name != null && contact != null && address != null &&
                                    date != null && pincode != null && price != null && type != null) {
                                // Time might be null for medicine orders, so we handle it accordingly
                                if (time == null) {
                                    time = "";
                                }
                                OrderDetail orderDetail = new OrderDetail(name, address, contact, pincode, date, time, price, type);
                                orderList.add(orderDetail);
                                totalCost += price;

                                // Log each order item details
                                Log.d("OrderDetailsActivity", "Fetched order item: " + orderDetail);
                            } else {
                                Log.d("OrderDetailsActivity", "Order item has null fields, skipping: " + orderSnapshot.getKey());
                            }
                        } catch (Exception e) {
                            Log.e("OrderDetailsActivity", "Error fetching order data: " + e.getMessage(), e);
                        }
                    } else {
                        Log.d("OrderDetailsActivity", "No order data found in snapshot");
                    }
                }
                displayOrderDetails(totalCost);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OrderDetailsActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderDetailsActivity", "Database error: " + databaseError.getMessage(), databaseError.toException());
            }
        });
    }

    private void displayOrderDetails(float totalCost) {
        if (orderList != null && !orderList.isEmpty()) {
            Log.d("OrderDetailsActivity", "Displaying order details with size: " + orderList.size());
            adapter = new OrderDetailsAdapter(orderList);
            recyclerViewOD.setAdapter(adapter);
            totalCostTextView.setText("Total Cost: " + totalCost);
        } else {
            Log.d("OrderDetailsActivity", "Order list is empty");
            totalCostTextView.setText("Total Cost: 0");
        }
    }
}
