package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;
import com.itextpdf.layout.renderer.ImageRenderer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class AddStudentActivity extends AppCompatActivity implements ListDialogFragment.OnItemSelectedListener{

    private Spinner classSpinner;
    private DatabaseReference ClassesRef,StudentsRef,ExamsRef,DetailsREf;
    private EditText StudentName,RollNumber,FatherName,StudentWeight,StudentHeight,ParentPhone;
    private Button StudentAddButton,GeneratePDF,ImportContacts;
    ArrayList<String> classitems=new ArrayList<String>();
    ArrayList<String> classid=new ArrayList<String>();
    private String SelectedSubjectId="";
    private HashMap<String,String> contactandnumbers = new HashMap<>();
    private List<String> contacts = new ArrayList<>();
    private HashMap<String,String> subjectandid = new HashMap<>();
    private FirebaseUser currentUser;
    private String uid,studentname,rollnumber,fathername,classidentity,studentid,studentweight,studentheight,staffclassid,countryphonecode,parentphonenumber;
    private AdView bannerAd;
    private ArrayAdapter<String> adapter;
    private TextView addstudentText;
    private Integer studentcount=0;
    private ListView contactList;
    private FrameLayout contactsFrame;
    private LinearLayout backButton;
    private ArrayAdapter<String> contactsAdapter;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        DetailsREf = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");


        StudentName = (EditText) findViewById(R.id.student_name_input);
        StudentAddButton = (Button) findViewById(R.id.add_student_ok_button);
        RollNumber = (EditText) findViewById(R.id.rollnumber_input);
        FatherName = (EditText) findViewById(R.id.student_father_input);
        ParentPhone = (EditText) findViewById(R.id.student_parent_number_input);
        StudentWeight = (EditText) findViewById(R.id.student_weight);
        StudentHeight = (EditText) findViewById(R.id.student_height);
        bannerAd = (AdView) findViewById(R.id.add_stundent_banner);
        addstudentText = (TextView) findViewById(R.id.addStudent_update);
        GeneratePDF = (Button) findViewById(R.id.student_generate_pdf_button);
        backButton = (LinearLayout) findViewById(R.id.add_student_back_button);
        ImportContacts = (Button) findViewById(R.id.import_contact_button);

        ImportContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contacts.clear();
                contactandnumbers.clear();
                AskContactPermission();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAd.loadAd(adRequest);

        classSpinner = (Spinner) findViewById(R.id.class_spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, classitems);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedSubjectId =  subjectandid.get(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SelectedSubjectId = "";

            }
        });
        ClassesRef.orderByChild("classname").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String classname = snapshot.child("classname").getValue(String.class);
                String section = snapshot.child("classsection").getValue(String.class);
                String id = snapshot.getKey();
                subjectandid.put(classname+section,id);
                classitems.add(classname+section);
                adapter.notifyDataSetChanged();

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

        StudentAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if(classidentity.equals(""))
                {
                    String studentname = StudentName.getText().toString().trim();
                    String rollnumber = RollNumber.getText().toString().trim();
                    String fathername = FatherName.getText().toString().trim();
                    String studentweight = StudentWeight.getText().toString();
                    String studentheight = StudentHeight.getText().toString();
                    String parentphone = ParentPhone.getText().toString();


                    if(studentname.isEmpty() || rollnumber.isEmpty() || fathername.isEmpty() || SelectedSubjectId.isEmpty())
                    {
                        Toast.makeText(AddStudentActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    }
                    else if(parentphone.length()>15)
                    {
                        Toast.makeText(AddStudentActivity.this, "Enter valid parent phone number", Toast.LENGTH_SHORT).show();

                    }
                    else if(studentcount>25)
                    {
                        String finalParentphone = parentphone;
                        DetailsREf.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.child("activationkey").exists())
                                {
                                    //code to validate the activation key
                                    pushnewStudent(studentname,studentheight,rollnumber,fathername,studentweight, finalParentphone);
                                }
                                else
                                {
                                    Toast.makeText(AddStudentActivity.this, "End of free version limit! \nUpgrade to premium to add unlimited students", Toast.LENGTH_SHORT).show();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {

                        if(ParentPhone.getText().toString().isEmpty())
                        {
                            parentphone = "";
                        }
                        HashMap<String,String> studentMap = new HashMap<>();
                        studentMap.put("studentname",studentname);
                        studentMap.put("rollnumber",rollnumber);
                        studentMap.put("fathername",fathername);
                        studentMap.put("studentweight",studentweight);
                        studentMap.put("studentheight",studentheight);
                        studentMap.put("parentphone",parentphone);
                        studentMap.put("classid",SelectedSubjectId);
                        StudentsRef.push().setValue(studentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                StudentName.setText("");
                                FatherName.setText("");
                                RollNumber.setText("");
                                StudentWeight.setText("");
                                StudentHeight.setText("");
                                ParentPhone.setText("");
                                Toast.makeText(AddStudentActivity.this, "Successfully added!", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                    
                }
                else
                {
                    String studentname = StudentName.getText().toString().trim();
                    String rollnumber = RollNumber.getText().toString().trim();
                    String fathername = FatherName.getText().toString().trim();
                    String studentweight = StudentWeight.getText().toString();
                    String studentheight = StudentHeight.getText().toString();
                    String parentphone = ParentPhone.getText().toString();

                    if(studentname.isEmpty() || rollnumber.isEmpty() || fathername.isEmpty())
                    {
                        Toast.makeText(AddStudentActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    }

                    else
                    {
                        if(ParentPhone.getText().toString().isEmpty())
                        {
                            parentphone = "";
                        }
                        HashMap<String,String> studentMap = new HashMap<>();
                        studentMap.put("studentname",studentname);
                        studentMap.put("rollnumber",rollnumber);
                        studentMap.put("fathername",fathername);
                        studentMap.put("studentweight",studentweight);
                        studentMap.put("studentheight",studentheight);
                        studentMap.put("classid",SelectedSubjectId);
                        studentMap.put("parentphone",parentphone);
                        StudentsRef.child(studentid).setValue(studentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddStudentActivity.this, "Successfully updated student info", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                    
                }

                
            }
        });

        GeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamsRef.child(classidentity).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String examcount = String.valueOf(snapshot.getChildrenCount());
                        Intent termIntent = new Intent(AddStudentActivity.this,GeneratePdfActivity.class);
                        termIntent.putExtra("studentid",studentid);
                        termIntent.putExtra("classid",classidentity);
                        termIntent.putExtra("examcount",examcount);
                        startActivity(termIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });



    }

    private void pushnewStudent(String studentname, String studentheight, String rollnumber, String fathername, String studentweight, String parentphon) {
        HashMap<String,String> studentMap = new HashMap<>();
        studentMap.put("studentname",studentname);
        studentMap.put("rollnumber",rollnumber);
        studentMap.put("fathername",fathername);
        studentMap.put("studentweight",studentweight);
        studentMap.put("studentheight",studentheight);
        studentMap.put("classid",SelectedSubjectId);
        studentMap.put("parentphone",parentphon);
        StudentsRef.push().setValue(studentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                StudentName.setText("");
                FatherName.setText("");
                RollNumber.setText("");
                StudentWeight.setText("");
                StudentHeight.setText("");
                ParentPhone.setText("");
                Toast.makeText(AddStudentActivity.this, "Successfully added!", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

    }

    public void AskContactPermission()
    {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {
            GetContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GetContacts();
            } else {
                Toast.makeText(this, "Permission Denied! Allow contact permission to access contacts.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void GetContacts() {
        String contactName = StudentName.getText().toString().trim();
        if(!contactName.isEmpty())
        {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};
            String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like'%" + contactName + "%'";
            String[] selectionArgs = null;
            String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

            Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contacts.add(displayName);
                    contactandnumbers.put(displayName,phoneNumber);


                }
                cursor.close();
                if(!contacts.isEmpty())
                {

                    ListDialogFragment fragment = ListDialogFragment.newInstance(contacts);
                    fragment.show(getSupportFragmentManager(), "ListDialogFragment");
                }
                else
                {
                    Toast.makeText(this, "No contacts found with the name", Toast.LENGTH_SHORT).show();
                }


            } 
        }
        else

        {
            Toast.makeText(this, "Please fill in student name field for searching contacts!", Toast.LENGTH_SHORT).show();
        }
        

        

    }

    @Override
    protected void onStart() {
        super.onStart();
        GeneratePDF.setVisibility(View.GONE);

        studentname = getIntent().getStringExtra("studentname");
        fathername = getIntent().getStringExtra("fathername");
        rollnumber = getIntent().getStringExtra("rollnumber");
        classidentity = getIntent().getStringExtra("classid");
        studentid = getIntent().getStringExtra("studentid");
        studentheight = getIntent().getStringExtra("studentheight");
        studentweight = getIntent().getStringExtra("studentweight");
        staffclassid = getIntent().getStringExtra("staffclassid");




        DetailsREf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countryphonecode = snapshot.child("countryphonecode").getValue(String.class);
                if(snapshot.child("activationkey").exists())
                {
                    bannerAd.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(!staffclassid.equals(""))
        {

            ClassesRef.child(staffclassid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String classname = snapshot.child("classname").getValue(String.class)+snapshot.child("classsection").getValue(String.class);
                    classSpinner.setSelection(adapter.getPosition(classname));
                    classSpinner.setEnabled(false);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        if(!studentname.equals(""))
        {
            addstudentText.setText("Update Student Info");
            StudentAddButton.setText("Update");
            GeneratePDF.setVisibility(View.VISIBLE);
            StudentName.setText(studentname);
            FatherName.setText(fathername);
            RollNumber.setText(rollnumber);
            StudentHeight.setText(studentheight);
            StudentWeight.setText(studentweight);
            StudentsRef.child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    studentcount = Math.toIntExact(snapshot.getChildrenCount());
                    parentphonenumber = snapshot.child("parentphone").getValue(String.class);
                    ParentPhone.setText(parentphonenumber);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            ClassesRef.child(classidentity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String classname = snapshot.child("classname").getValue(String.class)+snapshot.child("classsection").getValue(String.class);
                    classSpinner.setSelection(adapter.getPosition(classname));


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.getChildrenCount()>0))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddStudentActivity.this);

                    // set title
                    builder.setTitle("No classes are added! \n Go to Classes module to add a new class?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(AddStudentActivity.this,AddClassActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    @Override
    public void onItemSelected(String item) {
        ParentPhone.setText("");
        String phone = contactandnumbers.get(item).replace("+","");
        ParentPhone.setText(phone.trim());
    }
}