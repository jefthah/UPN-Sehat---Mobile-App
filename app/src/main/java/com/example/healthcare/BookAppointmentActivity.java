package com.example.healthcare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class BookAppointmentActivity extends AppCompatActivity {

    EditText ed1, ed2, ed3, ed4;
    TextView tv;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private Button dateButton, timeButton, btnBook, btnBack;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_appointment);

        // Initialize TextView and EditText
        tv = findViewById(R.id.textViewBMBPackageName);
        ed1 = findViewById(R.id.editTextBMBFullName);
        ed2 = findViewById(R.id.editTextBMBAddress);
        ed3 = findViewById(R.id.editTextBMBpincode);
        ed4 = findViewById(R.id.editTextAppFees);
        dateButton = findViewById(R.id.buttonCartTime);
        timeButton = findViewById(R.id.buttonAppTime);
        btnBook = findViewById(R.id.buttonBookAppointment);
        btnBack = findViewById(R.id.buttonAppBack);

        Intent it = getIntent();
        String title = it.getStringExtra("text1");
        String fullname = it.getStringExtra("text2");
        String address = it.getStringExtra("text3");
        String contact = it.getStringExtra("text4");
        String fees = it.getStringExtra("text5");

        tv.setText(title);
        ed1.setText(fullname);
        ed2.setText(address);
        ed3.setText(contact);
        ed4.setText(fees);

        initDatePicker();
        dateButton.setOnClickListener(view -> datePickerDialog.show());

        initTimePicker();
        timeButton.setOnClickListener(view -> timePickerDialog.show());

        btnBack.setOnClickListener(view -> startActivity(new Intent(BookAppointmentActivity.this, FindDoctorActivity.class)));

        btnBook.setOnClickListener(view -> {
            Database db = new Database();
            // Initialize Firebase Authentication
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                startActivity(new Intent(BookAppointmentActivity.this, LoginActivity.class));
                finish();
                return;
            }

            // Get the logged-in user's ID
            String userId = currentUser.getUid();
//            SharedPreferences sharedpreferences = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE);
//            String username = sharedpreferences.getString("username", "");

            db.checkAppointmentExists(userId, title + " => " + fullname, address, contact, dateButton.getText().toString(), timeButton.getText().toString(), new Database.DatabaseCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Appointment already booked", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Exception e) {
                    float feeAmount = Float.parseFloat(fees.replaceAll("[^\\d.]", ""));
                    OrderDetail order = new OrderDetail(
                            title + " => " + fullname,
                            address,
                            contact,
                            "0", // No pincode for appointment
                            dateButton.getText().toString(),
                            timeButton.getText().toString(),
                            feeAmount,
                            "appointment" // Assuming 'appointment' is the type
                    );
                    db.addOrder(userId, order, new Database.DatabaseCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getApplicationContext(), "Your appointment is done successfully", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(BookAppointmentActivity.this, OrderDetailsActivity.class).putExtra("userId", userId));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to book appointment", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            dateButton.setText(day + "/" + month + "/" + year);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() + 86400000);
    }

    private void initTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = (timePicker, hour, minute) -> timeButton.setText(String.format("%02d:%02d", hour, minute));

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
    }
}
