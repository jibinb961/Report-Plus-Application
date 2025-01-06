package com.inhaler.report;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SettingsFragment extends Fragment {

    private DatabaseReference ClassesRef,StudentsRef,DetailsRef;;
    private FirebaseUser currentUser;
    private String uid,classid="",userid="",ClassPINString,AdminPinString;
    private TextView classTeacherName,classStrength,gradingType,Resetclasspin;


    public SettingsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View SettingsView = inflater.inflate(R.layout.fragment_settings, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        classid = getArguments().getString("classid");
        userid = getArguments().getString("userid");

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");

        classTeacherName = SettingsView.findViewById(R.id.settings_class_teacher_name);
        classStrength = SettingsView.findViewById(R.id.settings_class_strength);
        gradingType = SettingsView.findViewById(R.id.grading_type_textview);
        Resetclasspin = SettingsView.findViewById(R.id.Reset_Class_pin_TextView);

        Resetclasspin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builder = new AlertDialog.Builder(SettingsView.getContext()).create();
                View dialogView = getLayoutInflater().inflate(R.layout.class_pin_layout, null);
                builder.setView(dialogView);
                builder.setCancelable(false);
                EditText editText = (EditText) dialogView.findViewById(R.id.enter_class_pin_input);
                Button okbutton = (Button) dialogView.findViewById(R.id.enter_class_pin_button);
                TextView classpinheading = (TextView) dialogView.findViewById(R.id.class_pin_heading);
                classpinheading.setText("Enter Class PIN");
                okbutton.setText("Submit");
                builder.setCancelable(true);
                Button cancelbutton = (Button) dialogView.findViewById(R.id.cancel_class_pin_button);
                cancelbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        builder.cancel();
                    }
                });
                okbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!(editText.getText().toString().isEmpty()))
                        {
                            if(editText.getText().toString().equals(ClassPINString))
                            {
                                builder.cancel();
                                callfragment();

                            }
                            else
                            {
                                editText.setText("");
                                Toast.makeText(SettingsView.getContext(), "Wrong Class PIN, Try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else if(ClassPINString.equals("notset") || ClassPINString.equals(null))
                        {

                        }
                        else
                        {
                            Toast.makeText(SettingsView.getContext(), "Enter Class PIN", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                builder.show();
            }

        });


        gradingType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GradingFragment fragment = new GradingFragment(getContext());
                Bundle bundle = new Bundle();
                bundle.putString("classid",classid);
                fragment.setArguments(bundle);
                fragment.show(getChildFragmentManager(),"Delete Account");
            }
        });

        StudentsRef.orderByChild("classid").equalTo(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String count = "Class Strength : "+ String.valueOf(snapshot.getChildrenCount());
                classStrength.setText(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ClassesRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String teachername = "Class Teacher : "+snapshot.child("classteacher").getValue(String.class);
                classTeacherName.setText(teachername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return SettingsView;
    }

    private void callfragment() {
        Bundle bundle = new Bundle();
        bundle.putString("classid",classid);

        ResetClassPinFragment fragment = new ResetClassPinFragment();
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(),"Reset ClassPIN");
    }

    @Override
    public void onStart() {
        super.onStart();
        ClassPINString="";
        ClassesRef.child(classid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("classpin").exists())
                {
                    if(!snapshot.child("classpin").getValue(String.class).equals("notset"))
                    {
                        ClassPINString = snapshot.child("classpin").getValue(String.class);
                    }
                    else
                    {
                        ClassPINString="notset";
                    }

                }
                else{
                    ClassPINString = "notset";
                    callfragment();

                }
                DetailsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("schoolpin").exists())
                        {
                            AdminPinString = snapshot.child("schoolpin").getValue(String.class);
                        }
                        else
                        {
                            AdminPinString = "notset";
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
}