package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IndividualAnalyticsActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String studentid,classid,staffkey;
    Bundle bundle;
    private TextView studentName,studentRollNumber,studentClass;
    private DatabaseReference ClassesRef,StudentsRef,AttendanceRef,MarksRef;
    private FirebaseUser currentUser;
    private String uid,attendancecount,examcount;
    private ImageView backView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_analytics);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        StudentsRef =FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        AttendanceRef =FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Attendance");
        MarksRef =FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");



        studentid = getIntent().getStringExtra("studentid");
        classid = getIntent().getStringExtra("classid");

        studentName = (TextView) findViewById(R.id.analytics_student_name);
        studentClass = (TextView) findViewById(R.id.analytics_student_class);
        studentRollNumber = (TextView) findViewById(R.id.analytics_student_roll_number);
        backView = (ImageView) findViewById(R.id.analytics_students_individual_back_button);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bundle = new Bundle();
        bundle.putString("studentid",studentid);
        bundle.putString("classid",classid);




        AttendanceRef.child(classid).child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                attendancecount = String.valueOf(snapshot.getChildrenCount());
                bundle.putString("attendancecount",attendancecount);
                fragmentReplacer(new StudentAttendanceFragment());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        StudentsRef.child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studentname = "Name : "+ snapshot.child("studentname").getValue(String.class);
                String roll = "Roll No : "+snapshot.child("rollnumber").getValue(String.class);
                studentName.setText( studentname);
                studentRollNumber.setText(roll);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ClassesRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String classname = "Class : "+ snapshot.child("classname").getValue(String.class)   + " "+
                        snapshot.child("classsection").getValue(String.class);
                studentClass.setText(classname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_bar_analytics);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.attendance_analytics:

                        fragmentReplacer(new StudentAttendanceFragment());
                        break;
                    case R.id.marks_analytics:
                        MarksRef.child(classid).child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                examcount = String.valueOf(snapshot.getChildrenCount());
                                bundle.putString("examcount",examcount);
                                fragmentReplacer(new StudentsMarkFragment());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        break;

                }

                return true;
            }
        });

    }

    private void fragmentReplacer(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.analytics_frame_layout,fragment);
        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }
}