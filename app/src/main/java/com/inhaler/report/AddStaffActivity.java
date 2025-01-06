package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddStaffActivity extends AppCompatActivity {

    private DatabaseReference StaffsRef,ClassesRef;
    private EditText staffFirstName, staffSalary, staffPhone;
    private Button staffAddButton;
    private LinearLayout staffBackButton;
    private CheckBox passwordCheck;
    private String uid,staffname,staffid,staffkey,classes,staffpass;
    private FirebaseUser currentUser;
    private ChipGroup classesGroup;
    private TextView noClassText,addStaffHeading,staffPassword;
    private ArrayList<String> selectedClasses = new ArrayList<>();
    private HashMap<String,String> classAndids =  new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        staffkey = getIntent().getStringExtra("staffkey");
        staffid  = getIntent().getStringExtra("staffid");
        staffname = getIntent().getStringExtra("staffname");
        classes = getIntent().getStringExtra("classes");
            String[] selectedclasses = classes.split(",");
            for(int i=0;i<selectedclasses.length;i++)
            {
                selectedclasses[i] = selectedclasses[i].trim();
            }




        staffFirstName = (EditText) findViewById(R.id.add_staff_name_input);
        staffSalary = (EditText) findViewById(R.id.add_staff_salary_input);
        staffPhone = (EditText)  findViewById(R.id.add_staff_phone_input);
        staffAddButton = (Button)  findViewById(R.id.add_staff_ok_button);
        staffBackButton = (LinearLayout) findViewById(R.id.add_staff_back_button);
        classesGroup = (ChipGroup) findViewById(R.id.addClassChipGroup);
        noClassText = (TextView) findViewById(R.id.addStaffNoClassText);
        addStaffHeading = (TextView) findViewById(R.id.add_staff_heading);
        passwordCheck = (CheckBox) findViewById(R.id.view_password_checkbox);
        staffPassword = (TextView) findViewById(R.id.staff_password_text);
        passwordCheck.setVisibility(View.GONE);
        staffPassword.setVisibility(View.GONE);


        StaffsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");

        //To retrieve classes
        ClassesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String value = snapshot.child("classname").getValue(String.class);
                String section = snapshot.child("classsection").getValue(String.class);
                String classid = snapshot.getKey();
                classAndids.put(value+section,classid);
                Chip chip = new Chip(AddStaffActivity.this);
                chip.setText(value+section);
                chip.setCheckable(true);
                classesGroup.addView(chip);

                for(int j=0;j<selectedclasses.length;j++)
                {
                    if(classid.equals(selectedclasses[j]))
                    {
                        chip.setChecked(true);
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

        staffAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClasses.clear();
                if(!staffkey.equals(""))
                {
                    List<Integer> ids = classesGroup.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = classesGroup.findViewById(id);
                        String classid = classAndids.get(chip.getText().toString());
                        selectedClasses.add(classid);

                    }
                    String fname = staffFirstName.getText().toString().trim();
                    String salary = staffSalary.getText().toString().trim();
                    String phone = staffPhone.getText().toString().trim();

                    if(fname.isEmpty() || salary.isEmpty() || phone.isEmpty() || selectedClasses.isEmpty() || phone.length() < 5)
                    {
                        Toast.makeText(AddStaffActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                    }
                    else if(fname.length() < 3)
                    {
                        Toast.makeText(AddStaffActivity.this, "Staff Name too small", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        String classes = selectedClasses.toString();
                        String classesString = classes.substring(1,classes.length()-1);
                        String substring = fname.toLowerCase().replace(" ", "").substring(0, 3);
                        String id = substring
                                + phone.substring(0,2) + "@reportplus.com";
                        String password = substring + phone.substring(0,4);

                        HashMap<String,String> staffMap = new HashMap<>();
                        staffMap.put("staffname",fname);
                        staffMap.put("staffphone",phone);
                        staffMap.put("staffsalary",salary);
                        staffMap.put("staffid",id);
                        staffMap.put("staffpassword",password);
                        staffMap.put("classes",classesString);

                        StaffsRef.child(staffkey).setValue(staffMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isComplete())
                                {
                                    Toast.makeText(AddStaffActivity.this, "Successfully Updated Staff Details!", Toast.LENGTH_SHORT).show();
                                    staffPhone.setText("");
                                    staffFirstName.setText("");
                                    staffSalary.setText("");
                                    classesGroup.clearCheck();
                                    onBackPressed();
                                }
                                else
                                {
                                    Toast.makeText(AddStaffActivity.this, "Error :"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
                else
                {
                    List<Integer> ids = classesGroup.getCheckedChipIds();
                    for (Integer id:ids){
                        Chip chip = classesGroup.findViewById(id);
                        String classid = classAndids.get(chip.getText().toString());
                        selectedClasses.add(classid);

                    }
                    String fname = staffFirstName.getText().toString().trim();
                    String salary = staffSalary.getText().toString().trim();
                    String phone = staffPhone.getText().toString().trim();

                    if(fname.isEmpty() || salary.isEmpty() || phone.isEmpty() || selectedClasses.isEmpty() || phone.length() < 5)
                    {
                        Toast.makeText(AddStaffActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
                    }
                    else if(fname.length() < 3)
                    {
                        Toast.makeText(AddStaffActivity.this, "Staff Name too small", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        String classes = selectedClasses.toString();
                        String classesString = classes.substring(1,classes.length()-1);
                        String substring = fname.toLowerCase().replace(" ", "").substring(0, 3);
                        String id = substring
                                + phone.substring(0,2) + "@reportplus.com";
                        String password = substring + phone.substring(0,4);

                        HashMap<String,String> staffMap = new HashMap<>();
                        staffMap.put("staffname",fname);
                        staffMap.put("staffphone",phone);
                        staffMap.put("staffsalary",salary);
                        staffMap.put("staffid",id);
                        staffMap.put("staffpassword",password);
                        staffMap.put("classes",classesString);

                        StaffsRef.push().setValue(staffMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isComplete())
                                {
                                    Toast.makeText(AddStaffActivity.this, "Successfully added new staff!", Toast.LENGTH_SHORT).show();
                                    staffPhone.setText("");
                                    staffFirstName.setText("");
                                    staffSalary.setText("");
                                    classesGroup.clearCheck();
                                    onBackPressed();
                                }
                                else
                                {
                                    Toast.makeText(AddStaffActivity.this, "Error :"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }





            }
        });

        passwordCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    staffPassword.setText(staffpass);
                    staffPassword.setVisibility(View.VISIBLE);
                }
                else
                {
                    staffPassword.setVisibility(View.GONE);
                }
            }
        });

        staffBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check whether there are any classes available
        if(!staffkey.equals(""))
        {
            addStaffHeading.setText("Update Staff");
            staffAddButton.setText("Update Staff");
            passwordCheck.setVisibility(View.VISIBLE);
            StaffsRef.child(staffkey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("staffsalary").exists())
                    {
                        String staffsalary = snapshot.child("staffsalary").getValue(String.class);
                        staffSalary.setText(staffsalary);
                        String staffphone = snapshot.child("staffphone").getValue(String.class);
                        staffPhone.setText(staffphone);
                        staffFirstName.setText(staffname);
                        staffpass = snapshot.child("staffpassword").getValue(String.class);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        ClassesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0)
                {
                    noClassText.setVisibility(View.GONE);
                    classesGroup.setVisibility(View.VISIBLE);
                }
                else
                {
                    noClassText.setVisibility(View.VISIBLE);
                    classesGroup.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}