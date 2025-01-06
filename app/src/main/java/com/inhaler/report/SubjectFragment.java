package com.inhaler.report;

import android.content.AbstractThreadedSyncAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SubjectFragment extends DialogFragment {

    private EditText subjectName;
    private FirebaseUser currentUser;
    String uid,AdminPinString="";

    private Button okButton;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference dr,detailsRef;
    private ArrayList<String> subjectlist = new ArrayList<>();
    private String subjectid="";
    private TextView heading;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        dr = db.getReference().child("Users").child(uid).child("Subjects");
        View view = inflater.inflate(R.layout.report_view, container, false);
        subjectName = (EditText) view.findViewById(R.id.subject_input);
        heading = (TextView) view.findViewById(R.id.add_subject_heading);

        okButton = (Button) view.findViewById(R.id.report_ok_button);
        detailsRef = db.getReference().child("Users").child(uid).child("Details");

        dr.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    subjectlist.add(snapshot.child("subjectname").getValue(String.class).toLowerCase());
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

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(subjectid.equals(""))
                {

                    if(subjectName.getText().toString().isEmpty())
                    {
                        Toast.makeText(getContext(), "Enter a subject name", Toast.LENGTH_SHORT).show();
                    }
                    else if(subjectlist.contains(subjectName.getText().toString().trim().toLowerCase()))
                    {
                        Toast.makeText(getContext(), "Subject Duplication, try another name", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Pattern p = Pattern.compile(
                                "[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(subjectName.getText().toString());

                        boolean res = m.find();
                        if(!res)
                        {
                            String subject = subjectName.getText().toString().trim();
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                            LocalDateTime now = LocalDateTime.now();
                            String modified_date = dtf.format(now);
                            HashMap<String,String> subjectMap = new HashMap<>();
                            subjectMap.put("subjectname",subject);
                            subjectMap.put("modified",modified_date);
                            dr.push().setValue(subjectMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dismiss();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Subject name has special characters!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                else
                {
                    Pattern p = Pattern.compile(
                            "[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                    Matcher m = p.matcher(subjectName.getText().toString());

                    boolean res = m.find();
                    if(!res)
                    {
                        dr.child(subjectid).child("subjectname").
                                setValue(subjectName.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dismiss();

                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Subject name has special characters!", Toast.LENGTH_SHORT).show();

                    }

                }



            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        subjectid = getArguments().getString("subjectid");

        if(!subjectid.equals(""))
        {
            okButton.setText("Update");
            heading.setText("Edit Subject Name");
            dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String subjectname = snapshot.child(subjectid).child("subjectname").getValue(String.class);
                    subjectName.setText(subjectname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
//        detailsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child("schoolpin").exists())
//                {
//                    AdminPinString = snapshot.child("schoolpin").getValue(String.class);
//                    AlertDialog builder = new AlertDialog.Builder(getContext()).create();
//                    View dialogView = getLayoutInflater().inflate(R.layout.class_pin_layout, null);
//                    builder.setView(dialogView);
//                    EditText editText = (EditText) dialogView.findViewById(R.id.enter_class_pin_input);
//                    Button okbutton = (Button) dialogView.findViewById(R.id.enter_class_pin_button);
//                    TextView textView = (TextView) dialogView.findViewById(R.id.class_pin_heading);
//                    textView.setText("Enter Admin PIN");
//                    okbutton.setText("Enter");
//                    Button cancelbutton = (Button) dialogView.findViewById(R.id.cancel_class_pin_button);
//                    builder.setCancelable(false);
//                    cancelbutton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            builder.cancel();
//                            dismiss();
//                        }
//                    });
//
//                    okbutton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if(!(editText.getText().toString().isEmpty()))
//                            {
//                                if(editText.getText().toString().equals(AdminPinString))
//                                {
//                                    builder.cancel();
//                                }
//                                else
//                                {
//                                    editText.setText("");
//                                    Toast.makeText(getContext(), "Wrong Admin PIN, Try again", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                            else
//                            {
//                                Toast.makeText(getContext(), "Enter Admin PIN", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    });
//                    builder.show();
//
//                }
//                else
//                {
//                    AdminPinString = "notset";
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
}