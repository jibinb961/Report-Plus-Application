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

public class StaffModuleActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String classid,staffid,staffkey;
    Bundle bundle;
    private TextView classHeading,classStrength;
    private DatabaseReference ClassesRef,StudentsRef;
    private FirebaseUser currentUser;
    private String uid;
    private ImageView backView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_module);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        StudentsRef =FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");

        classid = getIntent().getStringExtra("classid");
        staffid = getIntent().getStringExtra("staffid");
        staffkey = getIntent().getStringExtra("staffkey");

        bundle = new Bundle();
        bundle.putString("classid",classid);
        bundle.putString("staffid",staffid);
        bundle.putString("staffkey",staffkey);

        classHeading = (TextView) findViewById(R.id.staff_class_name_heading);
        classStrength = (TextView) findViewById(R.id.staff_class_strength_heading);
        backView = (ImageView) findViewById(R.id.staff_module_back_button);

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        classHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        StudentsRef.orderByChild("classid").equalTo(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String studencount = String.valueOf(snapshot.getChildrenCount());
                classStrength.setText(studencount+ " Students");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String classname = snapshot.child(classid).child("classname").getValue(String.class)
                        + snapshot.child(classid).child("classsection").getValue(String.class);
                classHeading.setText(classname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_bar_staff);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId())
                {
                    case R.id.exams:
                        fragmentReplacer(new ExamsFragment());
                        break;
                    case R.id.students:
                        fragmentReplacer(new StudentFragment());
                        break;
                    case R.id.attendance:
                        fragmentReplacer(new AttendanceFragment());
                        break;
                    case R.id.settings:
                        fragmentReplacer(new SettingsFragment());
                        break;
                }
                
                return true;
            }
        });
        fragmentReplacer(new StudentFragment());

    }

    public void fragmentReplacer(Fragment fragment)
    {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.staff_frame_layout,fragment);
        fragment.setArguments(bundle);
        fragmentTransaction.commit();
    }
}