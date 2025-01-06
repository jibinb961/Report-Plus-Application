package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.logging.LoggingMXBean;

public class UserSelectorActivity extends AppCompatActivity {
    private LinearLayout adminLoginLayout,staffLoginLayout;
    private Button loginButton;
    private ProgressBar loadingBar;
    private EditText adminPinInput,staffIDInput,staffPasswordInput;
    private RadioGroup userTypeGroup;
    private RadioButton staffRadio, adminRadio;
    private DatabaseReference DetailsRef,StaffRef;
    private FirebaseUser firebaseUser;
    private String uid,admin_password="",childrencount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selector);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        adminLoginLayout = (LinearLayout) findViewById(R.id.admin_login_linear_layout);
        staffLoginLayout = (LinearLayout) findViewById(R.id.staff_login_linear_layout);
        loginButton = (Button) findViewById(R.id.UserTypeLoginButton);
        loadingBar = (ProgressBar) findViewById(R.id.userTypeProgessingbar);
        adminPinInput = (EditText) findViewById(R.id.login_admin_pin_input);
        staffIDInput = (EditText)  findViewById(R.id.staff_id_input);
        staffPasswordInput = (EditText) findViewById(R.id.staff_password_input);
        userTypeGroup = (RadioGroup) findViewById(R.id.user_type_radio_group);
        adminRadio = (RadioButton) findViewById(R.id.admin_type_radio_button);
        staffRadio = (RadioButton) findViewById(R.id.staff_type_radio_button);
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        StaffRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs");




        userTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(adminRadio.getId()==checkedId)
                {
                    adminLoginLayout.setVisibility(View.VISIBLE);
                    staffLoginLayout.setVisibility(View.GONE);
                }
                else if(staffRadio.getId()==checkedId)
                {
                    staffLoginLayout.setVisibility(View.VISIBLE);
                    adminLoginLayout.setVisibility(View.GONE);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setVisibility(View.GONE);
                loadingBar.setVisibility(View.VISIBLE);
                if(userTypeGroup.getCheckedRadioButtonId()==adminRadio.getId())
                {
                    String PIN = adminPinInput.getText().toString().trim();
                    if(PIN.equals(admin_password))
                    {
                        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("admin_pin", admin_password);
                        myEdit.commit();
                        Toast.makeText(UserSelectorActivity.this, "Welcome, Admin", Toast.LENGTH_SHORT).show();
                        Intent dashboardIntent = new Intent(UserSelectorActivity.this,DashboardActivity.class);
                        dashboardIntent.putExtra("user","admin");
                        startActivity(dashboardIntent);
                        finish();
                    }
                    else
                    {
                        loadingBar.setVisibility(View.GONE);
                        loginButton.setVisibility(View.VISIBLE);
                        Toast.makeText(UserSelectorActivity.this, "Wrong Admin PIN, Try again!", Toast.LENGTH_SHORT).show();
                    }
                }
                else if(userTypeGroup.getCheckedRadioButtonId()==staffRadio.getId())
                {
                    HashMap<String,Integer> count = new HashMap<>();
                    count.put("count",0);
                    count.put("flag",0);
                    HashMap<String,String> key = new HashMap<>();
                    key.put("staffkey","");
                    StaffRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            count.put("count",count.get("count") + 1);
                            String staffid = snapshot.child("staffid").getValue(String.class);
                            String password = snapshot.child("staffpassword").getValue(String.class);
                            if(staffIDInput.getText().toString().trim().equals(staffid) && staffPasswordInput.getText().toString().trim()
                                    .equals(password))
                            {
                                count.put("flag",1);
                                key.put("staffkey",snapshot.getKey());

                            }
                            if(count.get("count").toString().equals(childrencount)){
                                loadingBar.setVisibility(View.INVISIBLE);
                                loginButton.setVisibility(View.VISIBLE);
                                if(count.get("flag")==1)
                                {
                                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                    myEdit.putString("staff_pin", staffIDInput.getText().toString());
                                    myEdit.putString("staff_key",key.get("staffkey"));
                                    myEdit.commit();
                                    Toast.makeText(UserSelectorActivity.this, "Welcome, "+staffIDInput.getText().toString(), Toast.LENGTH_SHORT).show();
                                    Intent dashboardIntent = new Intent(UserSelectorActivity.this,StaffDashboardActivity.class);
                                    dashboardIntent.putExtra("userid",staffIDInput.getText().toString());
                                    dashboardIntent.putExtra("staffkey",key.get("staffkey"));
                                    startActivity(dashboardIntent);
                                    finish();
                                }
                                else
                                {
                                    Toast.makeText(UserSelectorActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
                                }
                                    
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        adminLoginLayout.setVisibility(View.VISIBLE);
        staffLoginLayout.setVisibility(View.GONE);
        loadingBar.setVisibility(View.GONE);
        userTypeGroup.check(adminRadio.getId());






        StaffRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childrencount = String.valueOf(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("schoolpin").exists())
                {
                    admin_password = snapshot.child("schoolpin").getValue(String.class);
                }
                else
                {
                    Intent loginintent = new Intent(UserSelectorActivity.this,UserUpdateActivity.class);
                    loginintent.putExtra("uid",uid);
                    startActivity(loginintent);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}