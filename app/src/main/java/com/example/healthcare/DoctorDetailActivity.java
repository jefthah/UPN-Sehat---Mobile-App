package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorDetailActivity extends AppCompatActivity {
    private String[][] doctor_details1 =
            {
                    {"Dr. Richard Le", "Rumah Sakit Depok", "Masa: 7thn", "081317716656", "Biaya: 100000"},
                    {"Dr. Clara Johnson", "Rumah Sakit Jakarta", "Masa: 10thn", "082134567890", "Biaya: 120000"},
                    {"Dr. Ahmad Fauzi", "Rumah Sakit Bandung", "Masa: 5thn", "081223344556", "Biaya: 90000"},
                    {"Dr. Siti Nurhaliza", "Rumah Sakit Surabaya", "Masa: 8thn", "083356789012", "Biaya: 110000"},
                    {"Dr. William Tan", "Rumah Sakit Yogyakarta", "Masa: 12thn", "085674321098", "Biaya: 130000"}
            };

    private String[][] doctor_details2 =
            {
                    {"Dr. Lisa Putri", "Rumah Sakit Bali", "Masa: 6thn", "087123456789", "Biaya: 95000"},
                    {"Dr. Kevin Hartono", "Rumah Sakit Medan", "Masa: 9thn", "081345678901", "Biaya: 115000"},
                    {"Dr. Maria Lim", "Rumah Sakit Semarang", "Masa: 11thn", "082178945612", "Biaya: 125000"},
                    {"Dr. David Suryanto", "Rumah Sakit Makassar", "Masa: 4thn", "083456789012", "Biaya: 88000"},
                    {"Dr. Anita Wijaya", "Rumah Sakit Pontianak", "Masa: 3thn", "081234567890", "Biaya: 85000"}
            };

    private String[][] doctor_details3 =
            {
                    {"Dr. Hannah Chandra", "Rumah Sakit Malang", "Masa: 5thn", "085712345678", "Biaya: 90000"},
                    {"Dr. Yusuf Pratama", "Rumah Sakit Palembang", "Masa: 7thn", "081987654321", "Biaya: 105000"},
                    {"Dr. Grace Tanoto", "Rumah Sakit Manado", "Masa: 8thn", "083123498765", "Biaya: 110000"},
                    {"Dr. Arif Budiman", "Rumah Sakit Balikpapan", "Masa: 6thn", "085678901234", "Biaya: 100000"},
                    {"Dr. Nina Ardianti", "Rumah Sakit Banjarmasin", "Masa: 10thn", "081234598765", "Biaya: 120000"}
            };

    private String[][] doctor_details4 =
            {
                    {"Dr. Rina Mahardika", "Rumah Sakit Maluku", "Masa: 5thn", "082167892345", "Biaya: 90000"},
                    {"Dr. Bambang Setiawan", "Rumah Sakit Aceh", "Masa: 8thn", "081276543210", "Biaya: 110000"},
                    {"Dr. Diana Kusuma", "Rumah Sakit Lampung", "Masa: 7thn", "083456712345", "Biaya: 105000"},
                    {"Dr. Andi Syahputra", "Rumah Sakit Pekanbaru", "Masa: 9thn", "081234567654", "Biaya: 115000"},
                    {"Dr. Tania Wijayanti", "Rumah Sakit Jambi", "Masa: 4thn", "082178945678", "Biaya: 88000"}
            };

    private String[][] doctor_details5 =
            {
                    {"Dr. Fajar Santoso", "Rumah Sakit Ternate", "Masa: 3thn", "081356789012", "Biaya: 85000"},
                    {"Dr. Lina Purwanti", "Rumah Sakit Kupang", "Masa: 6thn", "083245678901", "Biaya: 95000"},
                    {"Dr. Rizky Aditya", "Rumah Sakit Gorontalo", "Masa: 8thn", "081234567890", "Biaya: 110000"},
                    {"Dr. Maya Sari", "Rumah Sakit Bengkulu", "Masa: 10thn", "085678912345", "Biaya: 120000"},
                    {"Dr. Doni Saputra", "Rumah Sakit Papua", "Masa: 2thn", "082167890123", "Biaya: 80000"}
            };



    TextView tv;
    Button btn;
    String[][] doctor_details ={};
    HashMap<String,String> item;
    ArrayList list;
    SimpleAdapter sa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_doctor_detail);

        tv = findViewById(R.id.textViewODTitle);
        btn = findViewById(R.id.buttonLTBack);

        Intent it = getIntent();
        String title = it.getStringExtra("title");
        tv.setText(title);

        if (title.compareTo("Family Physician")==0)
            doctor_details =doctor_details1;
        else
        if (title.compareTo("Dietician")==0)
            doctor_details =doctor_details2;
        else
        if (title.compareTo("Dentist")==0)
            doctor_details =doctor_details3;
        else
        if (title.compareTo("Surgeon")==0)
            doctor_details =doctor_details4;
        else
            doctor_details =doctor_details5;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DoctorDetailActivity.this, FindDoctorActivity.class));
            }
        });

        list = new ArrayList();
        for (int i=0; i<doctor_details.length; i++){
            item = new HashMap<String,String>();
            item.put("line1", doctor_details[i][0]);
            item.put("line2", doctor_details[i][1]);
            item.put("line3", doctor_details[i][2]);
            item.put("line4", doctor_details[i][3]);
            item.put("line5", "Cons Fees: " +doctor_details[i][4] + "/-");
            list.add(item);
        }
        sa = new SimpleAdapter(this,list,
                R.layout.multi_lines,
                new String[]{"line1", "line2", "line3", "line4", "line5"},
                new int[]{R.id.line_a, R.id.line_b, R.id.line_c,R.id.line_d, R.id.line_e}
        );

        ListView lst = findViewById(R.id.listViewOD);
        lst.setAdapter(sa);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                Intent it = new Intent(DoctorDetailActivity.this, BookAppointmentActivity.class);
                it.putExtra("text1", title);
                it.putExtra("text2", doctor_details[i][0]);
                it.putExtra("text3", doctor_details[i][1]);
                it.putExtra("text4", doctor_details[i][3]);
                it.putExtra("text5", doctor_details[i][4]);
                startActivity(it);
            }
        });
    }
}