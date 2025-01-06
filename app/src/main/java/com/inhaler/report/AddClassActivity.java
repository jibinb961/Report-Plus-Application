package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.api.Distribution;
import com.google.common.reflect.ClassPath;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {
    private DatabaseReference ClassesRef,SubjectsRef,ExamsRef;
    private Button addClass;
    private EditText ClassName,ClassSection,ClassStrength,ClassTeacher,ClassPin;
    private ProgressDialog progressDialog;
    private ImageButton classInfo;
    private LinearLayout backButton;
    private ChipGroup subjectGroup,examChipGroup;
    private ArrayList<String> allclasses = new ArrayList<>();
    private FirebaseUser currentUser;
    private String uid;
    private FrameLayout NoSub,NoExam,ClassPinFrame;
    private Integer subjectcountchip=0,examcountchip=0;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();


        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");


        ClassesRef.keepSynced(true);
        ClassName = (EditText) findViewById(R.id.class_name_input);
        ClassSection = (EditText) findViewById(R.id.class_section_input);
        ClassTeacher = (EditText) findViewById(R.id.classteacher_input);
        ClassStrength = (EditText) findViewById(R.id.class_strength_input);
        addClass = (Button) findViewById(R.id.addclass_ok_button);

        backButton = (LinearLayout) findViewById(R.id.addclass_back_button);
        progressDialog = new ProgressDialog(AddClassActivity.this);

        classInfo = (ImageButton) findViewById(R.id.class_pin_info_button);
        ClassPin = (EditText) findViewById(R.id.class_password_input);
        ClassPinFrame = (FrameLayout) findViewById(R.id.class_pin_frame);
        progressDialog.setTitle("Loading Subjects");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        classInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddClassActivity.this);
                builder.setTitle("Class Pin");
                builder.setMessage("Class Pin is used to secure a class and this pin will be later used to update or delete this class." +
                        "\n\nPlease remember the 4 digit PIN for future operations!");

                builder.setCancelable(true);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();
            }
        });

        ClassesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String classname = snapshot.child("classname").getValue(String.class)+
                        snapshot.child("classsection").getValue(String.class);
                allclasses.add(classname);

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


        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ClassName.getText().toString().isEmpty() || ClassSection.getText().toString().isEmpty() ||
                          ClassStrength.getText().toString().isEmpty() || ClassPin.getText().toString().isEmpty())
                {
                    Toast.makeText(AddClassActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
                else if( ClassSection.length()>1)
                {
                    Toast.makeText(AddClassActivity.this, "Please recheck your classname and section", Toast.LENGTH_SHORT).show();
                }
                else if(allclasses.contains(ClassName.getText().toString().trim()+ClassSection.getText().toString().trim()))
                {
                    Toast.makeText(AddClassActivity.this, "There is already a class with this setting", Toast.LENGTH_SHORT).show();
                }
                else if(!(ClassPin.getText().toString().length()==4))
                {
                    Toast.makeText(AddClassActivity.this, "Class Pin size should be 4!", Toast.LENGTH_SHORT).show();
                }
                else
                {

                        HashMap<String,String> classMap = new HashMap<>();
                        classMap.put("classname", ClassName.getText().toString().trim());
                        classMap.put("classteacher",ClassTeacher.getText().toString().trim());
                        classMap.put("classsection",ClassSection.getText().toString().trim());
                        classMap.put("classstrength",ClassStrength.getText().toString().trim());
                        classMap.put("classpin",ClassPin.getText().toString().trim());
                        classMap.put("a","80");
                        classMap.put("b","60");
                        classMap.put("aplus","90");
                        classMap.put("bplus","70");
                        classMap.put("c","60");
                        classMap.put("fail","50");

                        ClassesRef.push().setValue(classMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddClassActivity.this, "Successfully added", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });



                    
                }


            }
        });
        
        









    }

    private boolean checkclassduplicate(String classname,String section)
    {
        final boolean[] flag = new boolean[1];
        ClassesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String prevclassname = snapshot.child("classname").getValue(String.class);
                String prevsection = snapshot.child("classsection").getValue(String.class);
                String inputclass = classname+section;
                String prevclass = prevclassname+prevsection;
                if(prevclass.equals(inputclass))
                {
                    flag[0] = true;
                    Toast.makeText(AddClassActivity.this, "There is already a class with this name", Toast.LENGTH_SHORT).show();


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

        return flag[0];
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}