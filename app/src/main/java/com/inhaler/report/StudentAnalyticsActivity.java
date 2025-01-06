package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class StudentAnalyticsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,MarksRef,ClassesRef;
    private ProgressDialog progressDialog;
    private TextView noStudent;
    private EditText findStudentinput;
    private Button addButton;
    private HashMap<String,String> allclasses = new HashMap<>();
    private FirebaseUser currentUser;
    private String uid;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_analytics);



        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();


        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        StudentsRef.keepSynced(true);
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        recyclerView = (RecyclerView)  findViewById(R.id.analytics_students_recycler_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noStudent = (TextView) findViewById(R.id.analytics_no_students_textview);

        findStudentinput = (EditText) findViewById(R.id.analytics_find_student_input);
        backButton = (ImageView) findViewById(R.id.analytics_students_back_button );
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(StudentAnalyticsActivity.this);
        progressDialog.setTitle("Loading Students");

        ClassesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                allclasses.put(snapshot.getKey().toString(),snapshot.child("classname").getValue(String.class)+snapshot.child("classsection").getValue(String.class));

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

        findStudentinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (s.toString()!=null)
                {
                    LoadUserData(s.toString());

                }
                else
                {
                    LoadUserData("");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void LoadUserData(String data) {

        noStudent.setVisibility(View.GONE);
        Query query = StudentsRef.orderByChild("studentname").startAt(data).endAt(data+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No students are added to any class, Go to All Students module in  " +
                            " Dashboard or go inside a class to create a new student!");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerOptions<Students> options =
                new FirebaseRecyclerOptions.Builder<Students>()
                        .setQuery(query,Students.class)
                        .build();

        FirebaseRecyclerAdapter<Students,StudentAnalyticsViewHolder > adapter =
                new FirebaseRecyclerAdapter<Students, StudentAnalyticsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StudentAnalyticsViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        holder.options.setVisibility(View.GONE);
                        String classid = model.getClassid();
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText("Class : "+allclasses.get(classid));
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent studentmodule = new Intent(StudentAnalyticsActivity.this,IndividualAnalyticsActivity.class);
                                studentmodule.putExtra("studentid",key);
                                studentmodule.putExtra("classid",model.getClassid());
                                startActivity(studentmodule);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public StudentAnalyticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_layout,parent,false);

                        return new StudentAnalyticsViewHolder(view);
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class StudentAnalyticsViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        ImageButton options;
        View mView;
        public StudentAnalyticsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.exam_name);
            rollnumber = itemView.findViewById(R.id.exam_period);
            options = itemView.findViewById(R.id.options);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadUserData("");
    }
}