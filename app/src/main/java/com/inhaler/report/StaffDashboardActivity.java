package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StaffDashboardActivity extends AppCompatActivity {

    private LinearLayout staffClasses,studentAnallytics;
    private CardView editStaffProfile;
    private DatabaseReference StaffRef;
    private TextView staffNameDisplay;
    private String staffid,staffkey,uid;
    private TextView NumberOfClasses;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        staffid = getIntent().getStringExtra("userid");
        staffkey = getIntent().getStringExtra("staffkey");
        staffNameDisplay = (TextView) findViewById(R.id.staff_name_display);
        staffClasses = (LinearLayout) findViewById(R.id.staff_classes_linear);
        NumberOfClasses = (TextView) findViewById(R.id.staff_number_of_classes);
        editStaffProfile = (CardView) findViewById(R.id.edit_staff_info);
        studentAnallytics = (LinearLayout) findViewById(R.id.staff_student_analytics_linear);
        studentAnallytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentanalytics = new Intent(StaffDashboardActivity.this,StudentAnalyticsActivity.class);
                startActivity(studentanalytics);
            }
        });

        StaffRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs");

        staffClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent staffClassIntent = new Intent(StaffDashboardActivity.this,StaffClassActivity.class);
                staffClassIntent.putExtra("staffkey",staffkey);
                staffClassIntent.putExtra("staffid",staffid);
                startActivity(staffClassIntent);
            }
        });

        editStaffProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StaffDashboardActivity.this,EditInfoActivity.class);
                intent.putExtra("staffkey",staffkey);
                startActivity(intent);
            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        StaffRef.child(staffkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classes[] = snapshot.child("classes").getValue(String.class).split(",");
                String staffname = snapshot.child("staffname").getValue(String.class);
                staffNameDisplay.setText(staffname);
                String numberofclasses = "My Classes ("+classes.length+")";
                NumberOfClasses.setText(numberofclasses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}