package com.example.healthcare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CartLabActivity extends AppCompatActivity {
    private ArrayList<HashMap<String, String>> list;
    private SimpleAdapter sa;
    private TextView tvTotal;
    private ListView cartListView;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton, timeButton, btnCheckout, btnBack, btnDeleteCart;
    private FirebaseAuth mAuth;
    private static final String TAG = "CartLabActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_lab);

        // Initialize UI components
        cartListView = findViewById(R.id.listViewBMCart);
        dateButton = findViewById(R.id.buttonBMCartDate);
        timeButton = findViewById(R.id.buttonCartTime);
        btnCheckout = findViewById(R.id.buttonBMCartCheckout);
        btnBack = findViewById(R.id.buttonBMCartBack);
        btnDeleteCart = findViewById(R.id.buttonDeleteCart);
        tvTotal = findViewById(R.id.textViewBMCartTotalCost);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(CartLabActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // Get the logged-in user's ID
        String userId = currentUser.getUid();
        Intent intent = getIntent();
        String otype = intent.getStringExtra("otype");

        // Get reference to the logged-in user's cart
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts").child(userId);

        // Listen for changes in the cart data
        Query query = cartRef.orderByChild("otype").equalTo(otype);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                float totalAmount = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CartItem item = snapshot.getValue(CartItem.class);
                    if (item != null) {
                        HashMap<String, String> map = new HashMap<>();
                        map.put("line1", "Package: " + item.getProduct());
                        map.put("line2", "Total Cost: " + item.getPrice());
                        map.put("line3", "Username: " + item.getUsername());
                        map.put("line4", "Type: " + item.getOtype());
                        list.add(map);
                        try {
                            totalAmount += Float.parseFloat(item.getPrice().replace("Total Cost : ", "").replace("/-", ""));
                        } catch (NumberFormatException e) {
                            Toast.makeText(CartLabActivity.this, "Invalid cost format: " + item.getPrice(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                displayCartData(totalAmount); // Display cart data after getting it
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(CartLabActivity.this, "Error fetching cart data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Set listener for back button
        btnBack.setOnClickListener(view -> startActivity(new Intent(CartLabActivity.this, LabTestActivity.class)));

        // Set listener for checkout button
        btnCheckout.setOnClickListener(view -> {
            if (list != null && !list.isEmpty()) { // Check if the cart is not empty
                Intent it = new Intent(CartLabActivity.this, LabTestBookActivity.class);
                it.putExtra("price", tvTotal.getText().toString());
                it.putExtra("date", dateButton.getText().toString());
                it.putExtra("time", timeButton.getText().toString());
                it.putExtra("userId", userId); // Pass the userId to the next activity
                it.putExtra("otype", otype);
                startActivity(it);
            } else {
                Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize date picker
        initDatePicker();
        dateButton.setOnClickListener(view -> datePickerDialog.show());

        // Initialize time picker
        initTimePicker();
        timeButton.setOnClickListener(view -> timePickerDialog.show());

        btnDeleteCart.setOnClickListener(view -> {
            Database db = new Database();
            db.clearCart(userId, otype, new Database.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(CartLabActivity.this, "Cart deleted successfully", Toast.LENGTH_SHORT).show();
                    list.clear();
                    displayCartData(0);

                    // Navigate to LabTestActivity
                    Intent intent = new Intent(CartLabActivity.this, LabTestActivity.class);
                    startActivity(intent);
                    finish(); // Optional: To prevent going back to this activity
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(CartLabActivity.this, "Failed to delete cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Method to display cart data in ListView
    private void displayCartData(float totalAmount) {
        if (list != null && !list.isEmpty()) {
            sa = new SimpleAdapter(this, list,
                    R.layout.multi_lines,
                    new String[]{"line1", "line2", "line3", "line4"},
                    new int[]{R.id.line_a, R.id.line_b, R.id.line_c, R.id.line_e});
            cartListView.setAdapter(sa);
            tvTotal.setText("Total Cost: " + totalAmount);
        } else {
            tvTotal.setText("Total Cost: 0");
            Toast.makeText(getApplicationContext(), "Cart is empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to initialize date picker
    private void initDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> dateButton.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1), year, month, day);
    }

    // Method to initialize time picker
    private void initTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> timeButton.setText(String.format("%02d:%02d", hourOfDay, minute1)), hour, minute, true);
    }
}
