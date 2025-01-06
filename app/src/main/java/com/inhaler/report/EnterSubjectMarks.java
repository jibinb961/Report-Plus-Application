package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.grpc.MethodDescriptor;

public class EnterSubjectMarks extends AppCompatActivity {

    private DatabaseReference ClassesRef,MarksRef,Coscholastic,ExamsRef,CoscholasticMarksRef;
    private ArrayList<String> subjects = new ArrayList<>();
    private String classid,studentid,term,examid;
    private EditText TotalMarks,MaximumMarks,ScholasticGradeInput;
    private TextView SubjectNameView;
    private ImageView backButton;
    private CheckBox absentCheckbox;
    private String[] subjectarray={""};
    private HashMap<String,String> scholasticgrades = new HashMap<>();
    private String selectedactivity;
    ArrayList<String> subjectitems=new ArrayList<String>();
    ArrayAdapter<String> adapter,coasticadapter;
    private HashMap<String,String> subjectmarks = new HashMap<>();
    private ListView SubjectList;
    private FrameLayout scholasticFrame;
    private Spinner subjectSpinner,CoscholasticSpinner;
    private Button updateMarksButton,UpdateTerm,LoadMarks,UpdateGrade;
    private String currenselectedsubject="";
    private Integer subjectcount=0;
    private boolean isdataexisting = false;
    private ArrayList<String> selectedActivities = new ArrayList<String>();
    private FirebaseUser currentUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_subject_marks);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        classid = getIntent().getStringExtra("classid");
        studentid = getIntent().getStringExtra("studentid");
        term = getIntent().getStringExtra("term");
        examid = getIntent().getStringExtra("examid");


        scholasticFrame = (FrameLayout) findViewById(R.id.scholastic_frame);
        UpdateGrade = (Button) findViewById(R.id.update_grade_button);
        ScholasticGradeInput = (EditText) findViewById(R.id.scholastic_grade_input);
        CoscholasticSpinner = (Spinner) findViewById(R.id.coscholastic_spinner);

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ClassesRef.keepSynced(true);
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        Coscholastic = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Coscholastic");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        CoscholasticMarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("CoscholasticMarks");

        subjectSpinner = (Spinner) findViewById(R.id.subject_spinner);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,subjectitems);
        subjectSpinner.setAdapter(adapter);

        TotalMarks = (EditText) findViewById(R.id.subject_mark_input);
        MaximumMarks = (EditText) findViewById(R.id.maximum_mark_input);
        SubjectNameView = (TextView) findViewById(R.id.selected_subject_textview);
        updateMarksButton = (Button) findViewById(R.id.update_marks_button);
        UpdateTerm = (Button) findViewById(R.id.update_term_button);
        LoadMarks = (Button) findViewById(R.id.load_saved_marks_button);
        backButton = (ImageView) findViewById(R.id.addmarks_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        absentCheckbox = (CheckBox) findViewById(R.id.absent_checkbox);

        ExamsRef.child(examid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("coscholastic").exists())
                {
                    scholasticFrame.setVisibility(View.VISIBLE);
                    String[] coscolastic = snapshot.child("coscholastic").getValue(String.class).split(",");
                    for(int i=0;i<coscolastic.length;i++)
                    {
                        coscolastic[i] = coscolastic[i].trim();
                        selectedActivities.add(coscolastic[i]);
                        coasticadapter.notifyDataSetChanged();
                    }
                }
                else
                {
                    scholasticFrame.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        CoscholasticMarksRef.child(classid).child(studentid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(examid).exists())
                {
                    for(int i=0;i<selectedActivities.size();i++)
                    {
                        scholasticgrades.put(selectedActivities.get(i),
                                snapshot.child(examid).child(selectedActivities.get(i)).getValue(String.class));
                    }
                }
                else
                {
                    for(int i=0;i<selectedActivities.size();i++)
                    {
                        scholasticgrades.put(selectedActivities.get(i),
                               "");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        coasticadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,selectedActivities);
        CoscholasticSpinner.setAdapter(coasticadapter);

        MarksRef.child(classid).child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(examid))
                {
                    isdataexisting = true;


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        absentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    TotalMarks.setTextColor(getResources().getColor(R.color.absent));
                    MaximumMarks.setTextColor(getResources().getColor(R.color.absent));
                    MaximumMarks.setText("Absent");
                    TotalMarks.setText("Absent");
                    TotalMarks.setEnabled(false);
                    MaximumMarks.setEnabled(false);
                }
                else
                {
                    TotalMarks.setTextColor(getResources().getColor(R.color.black));
                    MaximumMarks.setTextColor(getResources().getColor(R.color.black));
                    TotalMarks.setEnabled(true);
                    MaximumMarks.setEnabled(true);
                    MaximumMarks.setText("0");
                    TotalMarks.setText("0");


                }
            }
        });

        CoscholasticSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String grade = ScholasticGradeInput.getText().toString().trim();
                if(!grade.isEmpty())
                {
                    scholasticgrades.put(selectedactivity,grade);
                }
                else
                {
                    scholasticgrades.put(selectedactivity,"");
                }
                return false;
            }
        });



        CoscholasticSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedactivity = parent.getItemAtPosition(position).toString();
                ScholasticGradeInput.setText(scholasticgrades.get(selectedactivity));


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ClassesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String subjectlist = snapshot.child(classid).child("subjects").getValue(String.class);
                subjectarray = subjectlist.split(",");
                for(int i=0;i<subjectarray.length;i++)
                {
                    subjectitems.add(subjectarray[i]);
                    subjectmarks.put(subjectarray[i],"");


                }


                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        MarksRef.child(classid).child(studentid).child(examid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(int i=0;i<subjectitems.size();i++)
                    {
                        String subjectmark = snapshot.child(subjectitems.get(i)).getValue(String.class);
                        if(subjectmark==null)
                        {
                            subjectmarks.put(subjectitems.get(i),"0,0");
                        }
                        else
                        {
                            subjectmarks.put(subjectitems.get(i),subjectmark);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                absentCheckbox.setChecked(false);

                SubjectNameView.setText(parent.getItemAtPosition(position).toString());
                currenselectedsubject = parent.getItemAtPosition(position).toString();
                if(isdataexisting)
                {
                    if(subjectmarks.get(parent.getItemAtPosition(position).toString()).equals(""))
                    {
                        MarksRef.child(classid).child(studentid).child(examid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String marks = snapshot.child(parent.getItemAtPosition(position).toString()).getValue(String.class);
                                if(!(marks==null))
                                {
                                    if(marks.equals("0"))
                                    {

                                        TotalMarks.setText("0");
                                        MaximumMarks.setText("0");

                                    }
                                    else
                                    {
                                        String[] totalnmaxmarks = marks.split(",");
                                        subjectmarks.put(parent.getItemAtPosition(position).toString(),totalnmaxmarks[0]+","+totalnmaxmarks[1]);
                                        TotalMarks.setText(totalnmaxmarks[0]);
                                        MaximumMarks.setText(totalnmaxmarks[1]);
                                       
                                    }

                                }
                                else
                                {
                                    TotalMarks.setText("0");
                                    MaximumMarks.setText("0");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else if(subjectmarks.get(parent.getItemAtPosition(position).toString()).equals("0"))
                    {
                        TotalMarks.setText("0");
                        MaximumMarks.setText("0");

                    }
                    else 
                    {
                        String updatedmarks = subjectmarks.get(parent.getItemAtPosition(position).toString());
                        String[] totalnmaxmarks = updatedmarks.split(",");
                        if(totalnmaxmarks[0].equals("?") || totalnmaxmarks[1].equals("?"))
                        {
                            absentCheckbox.setChecked(true);
                        }
                        else
                        {
                            subjectmarks.put(parent.getItemAtPosition(position).toString(),updatedmarks);
                            TotalMarks.setText(totalnmaxmarks[0]);
                            MaximumMarks.setText(totalnmaxmarks[1]);
                        }


                    }

                }
                else
                {
                   if(subjectmarks.get(currenselectedsubject)==null || subjectmarks.get(currenselectedsubject).length()<1)
                   {

                   }
                   else
                   {
                       String[] marks = subjectmarks.get(currenselectedsubject).split(",");
                       TotalMarks.setText(marks[0]);
                       MaximumMarks.setText(marks[1]);
                   }
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        LoadMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subjectcount>1)
                {
                    subjectSpinner.setSelection(1);

                }
                else
                {
                }
                LoadMarks.setVisibility(View.INVISIBLE);
            }
        });


        updateMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String totalmarks = TotalMarks.getText().toString();
                String maxmarks = MaximumMarks.getText().toString();
                if(totalmarks.equals("Absent") && maxmarks.equals("Absent"))
                {
                    totalmarks = "?";
                    maxmarks ="?";
                    subjectmarks.put(currenselectedsubject,totalmarks+","+maxmarks);
                    Toast.makeText(EnterSubjectMarks.this, "Successfully updated marks for "+currenselectedsubject, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(totalmarks.isEmpty() || maxmarks.isEmpty())
                    {
                        Toast.makeText(EnterSubjectMarks.this, "Enter total marks and maximum marks", Toast.LENGTH_SHORT).show();
                    }
                    else if(Float.parseFloat(totalmarks) > Integer.parseInt(maxmarks))
                    {
                        Toast.makeText(EnterSubjectMarks.this, "Total marks cannot be greater than maximum marks", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {

                        subjectmarks.put(currenselectedsubject,totalmarks+","+maxmarks);
                        Toast.makeText(EnterSubjectMarks.this, "Successfully updated marks for "+currenselectedsubject, Toast.LENGTH_SHORT).show();

                    }
                }


            }
        });

        UpdateTerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = true;
                for (Map.Entry mapElement : subjectmarks.entrySet()) {
                    String key = (String)mapElement.getKey();
                    String value = (mapElement.getValue().toString());
                    if(value.equals("") || value.equals("0") || value==null || value.equals("0,0"))
                    {
                        subjectmarks.put(key,"0,0");
                        flag = true;
                    }
                    else
                    {

                    }
                }
                if(flag)
                {

//                    MarksRef.child(classid).child(studentid).child(examid).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            if(!snapshot.exists())
//                            {
//                                MarksRef.child(classid).child(studentid).child(examid).setValue(subjectmarks).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        Toast.makeText(EnterSubjectMarks.this, "Successfully updated term", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                });
//                            }
//                            else
//                            {
//                                for (String s : subjectarray) {
//
//
//                                        MarksRef.child(classid).child(studentid).child(examid).child(s)
//                                                .setValue(subjectmarks.get(s));
//
//
//                                }
//                                Toast.makeText(EnterSubjectMarks.this, "Successfully updated subject marks to term", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
                    if(isdataexisting)
                    {
                        for (String s : subjectarray) {


                            MarksRef.child(classid).child(studentid).child(examid).child(s)
                                    .setValue(subjectmarks.get(s));


                        }
                        Toast.makeText(EnterSubjectMarks.this, "Successfully updated marks", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        MarksRef.child(classid).child(studentid).child(examid).setValue(subjectmarks).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(EnterSubjectMarks.this, "Successfully updated term", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                }



            }
        });

        UpdateGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedcoactivity =  coasticadapter.getItem(CoscholasticSpinner.getSelectedItemPosition());
                String marks = ScholasticGradeInput.getText().toString();
                if(marks.isEmpty())
                {
                    Toast.makeText(EnterSubjectMarks.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    scholasticgrades.put(selectedcoactivity,marks);

                    CoscholasticMarksRef.child(classid).child(studentid).child(examid).setValue(scholasticgrades).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(EnterSubjectMarks.this, "Grades Updated Succesfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });






    }

    @Override
    protected void onStart() {
        super.onStart();


       adapter.notifyDataSetChanged();
       LoadMarks.setVisibility(View.VISIBLE);

       ClassesRef.child(classid).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               String subjects = snapshot.child("subjects").getValue(String.class);
               String[] subjectarray = subjects.split(",");
               subjectcount = subjectarray.length;
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });



    }
}