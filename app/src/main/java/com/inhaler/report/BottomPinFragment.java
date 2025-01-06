package com.inhaler.report;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class BottomPinFragment extends BottomSheetDialogFragment {


    private LinearLayout n1,n2,n3,n4,n5,n6,n7,n8,n9,n0,ndelete,nok;
    private EditText PinInput;
    private ArrayList<String> pin = new ArrayList<>();
    private String heading = "Enter Admin PIN",uid,classid,ClassPIN,AdminPIN;
    private DatabaseReference classRef,detailsRef;
    private FirebaseUser currentUser;

    public BottomPinFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_pin, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        classRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        detailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        
        
      

        StringBuffer sbf1 = new StringBuffer("");
        n1 = (LinearLayout) view.findViewById(R.id.number_one);
        n2 = (LinearLayout) view.findViewById(R.id.number_two);
        n3 = (LinearLayout) view.findViewById(R.id.number_three);
        n4 = (LinearLayout) view.findViewById(R.id.number_four);
        n5 = (LinearLayout) view.findViewById(R.id.number_five);
        n6 = (LinearLayout) view.findViewById(R.id.number_six);
        n7 = (LinearLayout) view.findViewById(R.id.number_seven);
        n8 = (LinearLayout) view.findViewById(R.id.number_eight);
        n9 = (LinearLayout) view.findViewById(R.id.number_nine);
        n0 = (LinearLayout) view.findViewById(R.id.number_zero);
        ndelete = (LinearLayout) view.findViewById(R.id.number_delete);
        nok = (LinearLayout) view.findViewById(R.id.number_ok);

        PinInput = (EditText)  view.findViewById(R.id.bottom_enter_class_pin_input);
        
        

        n1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sbf1.append("1");
                PinInput.setText(sbf1);
            }
        });
        n2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("2");
                PinInput.setText(sbf1);            }
        });
        n3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("3");
                PinInput.setText(sbf1);            }
        });
        n4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("4");
                PinInput.setText(sbf1);            }
        });
        n5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("5");
                PinInput.setText(sbf1);            }
        });
        n6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("6");
                PinInput.setText(sbf1);            }
        });
        n7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("7");
                PinInput.setText(sbf1);            }
        });
        n8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("8");
                PinInput.setText(sbf1);            }
        });
        n9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("9");
                PinInput.setText(sbf1);            }
        });
        n0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sbf1.append("0");
                PinInput.setText(sbf1);            }
        });
        ndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sbf1.length()>0)
                {
                    sbf1.deleteCharAt(sbf1.length()-1);
                    PinInput.setText(sbf1);
                }

            }
        });
        nok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                
                String password = sbf1.toString();
                if(!password.isEmpty())
                {
                    if(password.length()==4)
                    {
                        if(heading.equals("Enter Class PIN"))
                        {
                            if(ClassPIN.equals(password))
                            {
                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(classid,password);
                                editor.apply();
                                Intent listStudentsIntent= new Intent(getContext(),StaffModuleActivity.class);
                                listStudentsIntent.putExtra("staffid","Admin");
                                listStudentsIntent.putExtra("staffkey",uid);
                                listStudentsIntent.putExtra("classid",classid);
                                startActivity(listStudentsIntent);
                                dismiss();
                            }
                            else
                            {
                                sbf1.setLength(0);
                                PinInput.setText(sbf1);
                                Toast.makeText(getContext(), "Wrong PIN, Try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(heading.equals("Enter Admin PIN"))
                        {
                            if(AdminPIN.equals(password))
                            {
                                Toast.makeText(getContext(), "Success!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                sbf1.setLength(0);
                                PinInput.setText(sbf1);
                                Toast.makeText(getContext(), "Wrong PIN, Try again!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(), "PIN should be 4 digits long!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Enter PIN!", Toast.LENGTH_SHORT).show();
                }
            }
        });




        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        
        classid = getArguments().getString("id");
        heading = getArguments().getString("heading");
        if(heading.equals("Enter Class PIN"))
        {
            classRef.child(classid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("classpin").exists())
                    {
                        ClassPIN = snapshot.child("classpin").getValue(String.class);

                    }
                    else
                    {
                        ClassPIN = "notset";


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else if(heading.equals("Enter Admin PIN"))
        {
            detailsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("schoolpin").exists())
                    {
                        AdminPIN = snapshot.child("schoolpin").getValue(String.class);
                    }
                    else{
                        AdminPIN = "notset";

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        
        
        
        
    }
}