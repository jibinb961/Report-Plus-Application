package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Map;

public class EnterStudentMarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,MarksRef,ClassesRef,ExamsRef,SubjectsRef;
    private ProgressDialog progressDialog,savingDialog;
    private String classid ="",staffkey="",examid="",staffid="",totalexammarks="";
    private TextView noStudent;
    private FirebaseUser currentUser;
    private boolean isEdited = false,isLoaded=false;
    private Spinner subjectSelectorSpinner;
    private FirebaseRecyclerAdapter<Students,AddMarksViewHolder> adapter;
    private String uid,ClassPinString,subjects,ClickedSubject="";
    private ArrayList<String> subjectsarray;
    private HashMap<String,String> studentmarks = new HashMap<>();
    private HashMap<String,String> subidandsubs = new HashMap<>();
    private LinearLayout backButton;
    private EditText totalMarks;
    private HashMap<String,String> subandsubids =new HashMap<>();
    private ArrayAdapter<String> dataAdapter;
    private View StudentsView;
    private Button saveMarks;
    private Integer subjectcount=0,childrencount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_student_marks);

        classid =  getIntent().getStringExtra("classid");


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();


        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        StudentsRef.keepSynced(true);
        backButton = (LinearLayout) findViewById(R.id.enter_student_marks_back_button);
        subjectSelectorSpinner = (Spinner) findViewById(R.id.subject_selector_spinner);
        totalMarks = (EditText) findViewById(R.id.students_mark_total_marks_input);
        saveMarks = (Button) findViewById(R.id.save_marks_button);
        noStudent = (TextView) findViewById(R.id.students_add_marks_no_student);

        recyclerView = (RecyclerView) findViewById(R.id.students_add_marks_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        StudentsRef.orderByChild("classid").equalTo(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerView.setItemViewCacheSize((int) snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        subjectsarray = new ArrayList<>();

        dataAdapter = new ArrayAdapter<String>(this,
               R.layout.spinner_item, subjectsarray);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Students");
        progressDialog.show();

        savingDialog = new ProgressDialog(this);
        savingDialog.setTitle("Saving....");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        subjectSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                ClickedSubject = selectedItem;
                studentmarks.clear();
                PopulateMarks();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        saveMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingDialog.show();
                saveMarks.setVisibility(View.GONE);
                String totalmarks = totalMarks.getText().toString().trim();
                if(!totalmarks.isEmpty())
                {
                    for (Map.Entry<String, String> entry : studentmarks.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        if(!value.isEmpty())
                        {
                            if(value.equals("Absent"))
                            {
                                MarksRef.child(classid).child(key).child(examid).child(ClickedSubject).setValue("?,?")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                            else if(value.equals("0"))
                            {
                                MarksRef.child(classid).child(key).child(examid).child(ClickedSubject).setValue("0,0")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }
                            else
                            {
                                MarksRef.child(classid).child(key).child(examid).child(ClickedSubject).setValue(value+","+totalmarks)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                            }


                        }
                        else
                        {
                            savingDialog.dismiss();
                            saveMarks.setVisibility(View.VISIBLE);
                        }
                    }
                    Toast.makeText(EnterStudentMarksActivity.this, "Successfully updated marks for "+ClickedSubject, Toast.LENGTH_SHORT).show();
                    savingDialog.dismiss();
                    saveMarks.setVisibility(View.VISIBLE);

                }
                else
                {
                    Toast.makeText(EnterStudentMarksActivity.this, "Enter total marks", Toast.LENGTH_SHORT).show();
                    savingDialog.dismiss();
                    saveMarks.setVisibility(View.VISIBLE);
                }

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        noStudent.setVisibility(View.INVISIBLE);



        staffid = getIntent().getStringExtra("staffid");
        staffkey = getIntent().getStringExtra("staffkey");
        examid = getIntent().getStringExtra("examid");
        subjects = getIntent().getStringExtra("subjects");
        totalexammarks = getIntent().getStringExtra("totalmarks");
        totalMarks.setText(totalexammarks);
        childrencount = Integer.valueOf(getIntent().getStringExtra("childrencount"));


        //code fragment to select populate subject dropdown adapter.
        if(subjectsarray.isEmpty())
        {
            String subs[] = subjects.split(",");
            ClickedSubject = subs[0].trim();
            for(int i=0;i<subs.length;i++)
            {
                subs[i] = subs[i].trim();
                subjectsarray.add(subs[i]);

            }
            subjectSelectorSpinner.setAdapter(dataAdapter);
        }

        MarksRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    LoadUserData();
                }
                else
                {
                    PopulateMarks();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void PopulateMarks()
    {
        final int[] count = {0};


        MarksRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.hasChildren())
                {

                    String studentkey = snapshot.getKey().toString();
                    MarksRef.child(classid).child(studentkey).child(examid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.child(ClickedSubject).exists())
                            {
                                String marks[] = snapshot.child(ClickedSubject).getValue(String.class).split(",");
                                studentmarks.put(studentkey,marks[0]);

                            }
                            else
                            {
                                studentmarks.put(studentkey,"0");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    count[0] = count[0]+1;

                    if(count[0]==(childrencount))
                    {
                        LoadUserData();
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
        LoadUserData();
    }
    public void  LoadUserData()
    {
        Query query = StudentsRef.orderByChild("classid").equalTo(classid);
        FirebaseRecyclerOptions<Students> options =
                new FirebaseRecyclerOptions.Builder<Students>()
                        .setQuery(query,Students.class)
                        .build();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No Students are assigned to this class, go to add student module to add a" +
                            " new student to this class");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    progressDialog.dismiss();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter =
                new FirebaseRecyclerAdapter<Students, AddMarksViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AddMarksViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText("Roll no : "+model.rollnumber);

                        if(studentmarks.get(key)!=null)
                        {
                            if(!studentmarks.get(key).equals("0"))
                            {
                                if(studentmarks.get(key).equals("?"))
                                {
                                    holder.marksInput.setText("Absent");
                                    studentmarks.put(key,"Absent");
                                    holder.absentCheckbox.setChecked(true);
                                }
                                else
                                {
                                    holder.marksInput.setText(studentmarks.get(key));

                                }

                            }

                        }
                        else
                        {

                        }

                        holder.marksInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                String marks = s.toString();
                                studentmarks.put(key,marks);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });


                        holder.absentCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if(isChecked)
                                {
                                    studentmarks.put(key,"Absent");
                                    holder.marksInput.setEnabled(false);
                                    holder.marksInput.setText("Absent");
                                    holder.marksInput.setFocusable(false);
                                    holder.marksInput.setFocusableInTouchMode(false);
                                }
                                else
                                {
                                    holder.marksInput.setEnabled(true);
                                    holder.marksInput.setText("0");
                                    holder.marksInput.setFocusable(true);
                                    holder.marksInput.setFocusableInTouchMode(true);
                                }
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public AddMarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_mark_layout,parent,false);
                        return new AddMarksViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        progressDialog.dismiss();
    }


    public static class AddMarksViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        CheckBox absentCheckbox;
        EditText marksInput;
        View mView;

        public AddMarksViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.mark_student_name);
            rollnumber = itemView.findViewById(R.id.mark_student_roll);
            absentCheckbox = itemView.findViewById(R.id.mark_absent_checkbox);
            marksInput = itemView.findViewById(R.id.marks_subject_mark_entry);
        }
    }


}
