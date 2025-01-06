package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
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

import org.w3c.dom.Text;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UpdateClassActivity extends AppCompatActivity {
    private DatabaseReference ClassesRef,SubjectsRef,MarksRef,StudentsRef,ExamsRef,DetailsRef;
    private Button addClass,deleteClass;
    private EditText ClassName,ClassSection,ClassStrength,ClassTeacher;
    private ProgressDialog progressDialog;
    private LinearLayout backButton;
    private MaterialCheckBox SubjectCheckbox;
    private ListView listView;
    private Integer count=0;
    private TextView forgotClassPin;
    private ChipGroup subjectGroup,examGroup;
    private ArrayList<Integer> idnumbers = new ArrayList<>();
    private ArrayList<Integer> examidnumbers = new ArrayList<>();
    private FirebaseUser currentUser;
    private String uid,ClassPINString,AdminPinString;

    private ArrayList<String> selectedSubjects = new ArrayList<>();
    private ArrayList<String> selectedExams = new ArrayList<>();
    private ArrayList<String> studentlist = new ArrayList<>();
    private String classid="",subjects="",exams="";
    private HashMap<String, Object> allsubjects = new HashMap<>();
    private HashMap<String,Object> allexams = new HashMap<>();
    private ArrayList<String> alreadyselectedsubjects = new ArrayList<>();
    private ArrayList<String> alreadyselectedexams = new ArrayList<>();
    private String[] checkedsubjectsarray,checkedexamsarray;
    private ArrayList<String> everysubjects = new ArrayList<>();
    private ArrayList<String> everyexams = new ArrayList<>();

    ArrayList<Integer> chipidnumbers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_class);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        classid = getIntent().getStringExtra("classid");

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");


        ClassesRef.keepSynced(true);
        ClassName = (EditText) findViewById(R.id.update_class_name_input);
        ClassSection = (EditText) findViewById(R.id.update_class_section_input);
        ClassTeacher = (EditText) findViewById(R.id.update_classteacher_input);
        ClassStrength = (EditText) findViewById(R.id.update_class_strength_input);
        addClass = (Button) findViewById(R.id.update_class_ok_button);
        deleteClass= (Button) findViewById(R.id.delete_class_ok_button);

        forgotClassPin = (TextView) findViewById(R.id.forgot_classpin_text);
        progressDialog = new ProgressDialog(UpdateClassActivity.this);
        progressDialog.setTitle("Loading Subjects");

        backButton = (LinearLayout) findViewById(R.id.update_class_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ClassesRef.child(classid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    ClassName.setText(snapshot.child("classname").getValue().toString());
                    ClassTeacher.setText(snapshot.child("classteacher").getValue().toString());
                    ClassStrength.setText(snapshot.child("classstrength").getValue().toString());
                    ClassSection.setText(snapshot.child("classsection").getValue().toString());
//                    subjects = snapshot.child("subjects").getValue().toString();


//                    if(snapshot.child("exams").exists())
//                    {
//                        exams = snapshot.child("exams").getValue(String.class);
//                    }
//                    checkedsubjectsarray = subjects.split(",");
//                    checkedexamsarray = exams.split(",");
//                    //for subjects
//                    for(int i=0;i<checkedsubjectsarray.length;i++)
//                    {
//                        checkedsubjectsarray[i] = checkedsubjectsarray[i].trim();
//                    }
//                    alreadyselectedsubjects= new ArrayList<String>(Arrays.asList(checkedsubjectsarray));
//                    List<Integer> ids = subjectGroup.getCheckedChipIds();
//                    for (Integer id:ids) {
//                        Chip chip = subjectGroup.findViewById(id);
//                        selectedSubjects.add(chip.getText().toString());
//                    }
//                    //for exams
//                    for(int i=0;i<checkedexamsarray.length;i++)
//                    {
//                        checkedexamsarray[i] = checkedexamsarray[i].trim();
//                    }
//                    alreadyselectedexams = new ArrayList<String>(Arrays.asList(checkedexamsarray));
//                    List<Integer> examids = examGroup.getCheckedChipIds();
//                    for(Integer id:examids)
//                    {
//                        Chip chip = examGroup.findViewById(id);
//                        selectedSubjects.add(chip.getText().toString());
//                    }

                }
                else
                {
                    Intent classintent = new Intent(UpdateClassActivity.this,ClassesActivity.class);
                    startActivity(classintent);
                    finish();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        forgotClassPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog builder = new AlertDialog.Builder(UpdateClassActivity.this).create();
                View dialogView = getLayoutInflater().inflate(R.layout.class_pin_layout, null);
                builder.setView(dialogView);
                builder.setCancelable(false);
                EditText editText = (EditText) dialogView.findViewById(R.id.enter_class_pin_input);
                Button okbutton = (Button) dialogView.findViewById(R.id.enter_class_pin_button);
                TextView classpinheading = (TextView) dialogView.findViewById(R.id.class_pin_heading);
                classpinheading.setText("Enter Admin PIN");
                okbutton.setText("Reset");
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
                            if(editText.getText().toString().equals(AdminPinString))
                            {
                                builder.cancel();
                                callfragment();

                            }
                            else
                            {
                                editText.setText("");
                                Toast.makeText(UpdateClassActivity.this, "Wrong Class PIN, Try again", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else if(ClassPINString.equals("notset") || ClassPINString.equals(null))
                        {

                        }
                        else
                        {
                            Toast.makeText(UpdateClassActivity.this, "Enter Class PIN", Toast.LENGTH_SHORT).show();

                        }
                    }
                });

                builder.show();
            }
        });

        StudentsRef.orderByChild("classid").equalTo(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                studentlist.add(snapshot.getKey());
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
        deleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ClassPINString.equals("notset"))
                {
                    AlertDialog builder = new AlertDialog.Builder(UpdateClassActivity.this).create();
                    View dialogView = getLayoutInflater().inflate(R.layout.class_pin_layout, null);
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    EditText editText = (EditText) dialogView.findViewById(R.id.enter_class_pin_input);
                    Button okbutton = (Button) dialogView.findViewById(R.id.enter_class_pin_button);
                    okbutton.setText("Delete");
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
                                    ClassesRef.child(classid).removeValue();
                                    MarksRef.child(classid).removeValue();
                                    if(!studentlist.isEmpty())
                                    {
                                        for(int j=0;j<studentlist.size();j++)
                                        {
                                            StudentsRef.child(studentlist.get(j)).removeValue();
                                        }
                                        Intent newintent = new Intent(UpdateClassActivity.this,ClassesActivity.class);
                                        builder.cancel();
                                        onBackPressed();
                                    }
                                }
                                else
                                {
                                    editText.setText("");
                                    Toast.makeText(UpdateClassActivity.this, "Wrong Class PIN, Try again", Toast.LENGTH_SHORT).show();
                                }

                            }
                            else if(ClassPINString.equals("notset") || ClassPINString.equals(null))
                            {
                                ClassesRef.child(classid).removeValue();
                                MarksRef.child(classid).removeValue();
                                if(!studentlist.isEmpty())
                                {
                                    for(int j=0;j<studentlist.size();j++)
                                    {
                                        StudentsRef.child(studentlist.get(j)).removeValue();
                                    }
                                    builder.cancel();
                                    onBackPressed();
                                }
                            }
                            else
                            {
                                Toast.makeText(UpdateClassActivity.this, "Enter Class PIN", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    builder.show();
                }
                else
                {
                    ClassesRef.child(classid).removeValue();
                    MarksRef.child(classid).removeValue();
                    ExamsRef.child(classid).removeValue();
                    if(!studentlist.isEmpty())
                    {
                        for(int j=0;j<studentlist.size();j++)
                        {
                            StudentsRef.child(studentlist.get(j)).removeValue();
                        }
                        Intent newintent = new Intent(UpdateClassActivity.this,ClassesActivity.class);
                        startActivity(newintent);
                        finish();
                    }
                }



//                    new AlertDialog.Builder(UpdateClassActivity.this)
//                            .setTitle("Delete entry")
//                            .setMessage("Are you sure you want to delete "+ClassName.getText().toString()+ClassSection.getText().toString())
//
//                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int which) {
//                                    ClassesRef.child(classid).removeValue();
//                                    MarksRef.child(classid).removeValue();
//                                    if(!studentlist.isEmpty())
//                                    {
//                                        for(int i=0;i<studentlist.size();i++)
//                                        {
//                                            StudentsRef.child(studentlist.get(i)).removeValue();
//                                        }
//                                        Intent newintent = new Intent(UpdateClassActivity.this,ClassesActivity.class);
//                                        startActivity(newintent);
//                                        finish();
//                                    }
//
//
//
//                                }
//                            })
//
//                            // A null listener allows the button to dismiss the dialog and take no further action.
//                            .setNegativeButton(android.R.string.no, null)
//                            .setIcon(android.R.drawable.ic_dialog_alert)
//                            .show();

                /*else
                {
                    Toast.makeText(UpdateClassActivity.this, "Contact admin to delete this class", Toast.LENGTH_SHORT).show();
                }*/
               
            }
        });

        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ClassName.getText().toString().isEmpty() || ClassSection.getText().toString().isEmpty() ||
                        ClassStrength.getText().toString().isEmpty())
                {
                    Toast.makeText(UpdateClassActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(!ClassPINString.equals("notset"))
                    {
                        AlertDialog builder = new AlertDialog.Builder(UpdateClassActivity.this).create();
                        View dialogView = getLayoutInflater().inflate(R.layout.class_pin_layout, null);
                        builder.setView(dialogView);
                        builder.setCancelable(false);
                        EditText editText = (EditText) dialogView.findViewById(R.id.enter_class_pin_input);
                        Button okbutton = (Button) dialogView.findViewById(R.id.enter_class_pin_button);
                        okbutton.setText("Update");
                        builder.setCancelable(true);
                        Button cancelbutton = (Button) dialogView.findViewById(R.id.cancel_class_pin_button);
                        builder.setCancelable(true);
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

                                        HashMap<String,String> classMap = new HashMap<>();
                                        classMap.put("classname", ClassName.getText().toString().trim());
                                        classMap.put("classteacher",ClassTeacher.getText().toString());
                                        classMap.put("classsection",ClassSection.getText().toString().trim());
                                        classMap.put("classstrength",ClassStrength.getText().toString().trim());
                                        classMap.put("classpin",ClassPINString);
                                        String subjects = selectedSubjects.toString();
                                        String exams = selectedExams.toString();
                                        exams = exams.substring(1,exams.length()-1);
                                        subjects = subjects.substring(1, subjects.length() - 1);
                                        classMap.put("subjects",subjects);
                                        classMap.put("exams",exams);

                                        ClassesRef.child(classid).setValue(classMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(UpdateClassActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                builder.cancel();
                                                onBackPressed();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        editText.setText("");
                                        Toast.makeText(UpdateClassActivity.this, "Wrong Class PIN, Try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else
                                {
                                    Toast.makeText(UpdateClassActivity.this, "Enter Class PIN", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                        builder.show();
                    }
                    else
                    {
                        HashMap<String,String> classMap = new HashMap<>();
                        classMap.put("classname", ClassName.getText().toString().trim());
                        classMap.put("classteacher",ClassTeacher.getText().toString());
                        classMap.put("classsection",ClassSection.getText().toString().trim());
                        classMap.put("classstrength",ClassStrength.getText().toString().trim());
                        classMap.put("classpin",ClassPINString);
                        String subjects = selectedSubjects.toString();
                        String exams = selectedExams.toString();
                        exams = exams.substring(1,exams.length()-1);
                        subjects = subjects.substring(1, subjects.length() - 1);
                        classMap.put("subjects",subjects);
                        classMap.put("exams",exams);

                        ClassesRef.child(classid).setValue(classMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(UpdateClassActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }




                }


            }
        });


//
//        SubjectCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                idnumbers.clear();
//                examidnumbers.clear();
//                selectedSubjects.clear();
//                selectedExams.clear();
//
//                if(isChecked)
//                {
//                    for(int i=0;i<everysubjects.size();i++)
//
//                    {
///*
//                        Toast.makeText(UpdateClassActivity.this, everysubjects.get(i), Toast.LENGTH_SHORT).show();
//*/
//
//                        for(int j=0;j<alreadyselectedsubjects.size();j++)
//                        {
///*
//                            Toast.makeText(UpdateClassActivity.this, everysubjects.get(i)+"="+alreadyselectedsubjects.get(j), Toast.LENGTH_SHORT).show();
//*/
//
//                            if(everysubjects.get(i).equals(alreadyselectedsubjects.get(j)))
//                            {
//                                String subject = everysubjects.get(i);
//                                int id= (int) allsubjects.get(subject);
///*
//                                Toast.makeText(UpdateClassActivity.this, "id ="+id, Toast.LENGTH_SHORT).show();
//*/
//                                idnumbers.add(id);
//                                subjectGroup.check(id);
//
//
//                            }
//                        }
//                    }
//                    for(int i=0;i<everyexams.size();i++)
//
//                    {
//
//
//                        for(int j=0;j<alreadyselectedexams.size();j++)
//                        {
//
//
//                            if(everyexams.get(i).equals(alreadyselectedexams.get(j)))
//                            {
//                                String exams = everyexams.get(i);
//                                int id= (int) allexams.get(exams);
//                                examidnumbers.add(id);
//                                examGroup.check(id);
//
//
//                            }
//                        }
//                    }
//                }
//                else
//                {
//                    subjectGroup.clearCheck();
//                    examGroup.clearCheck();
//                }
//
//            }
//        });






    }

    private void callfragment() {
        Bundle bundle = new Bundle();
        bundle.putString("classid",classid);

        ResetClassPinFragment fragment = new ResetClassPinFragment();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(),"Reset ClassPIN");
    }

    @Override
    protected void onStart() {
        super.onStart();
        idnumbers.clear();
        examidnumbers.clear();
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
    public void onBackPressed() {
        super.onBackPressed();
        everysubjects.clear();
        everyexams.clear();
        alreadyselectedsubjects.clear();
        alreadyselectedexams.clear();
        allsubjects.clear();
        allexams.clear();

    }

}