package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PDFStudentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,ExamsRef;
    private ProgressDialog progressDialog;
    private String classid ="";
    private FirebaseUser currentUser;
    private String uid;
    private LinearLayout backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfstudent);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        classid = getIntent().getStringExtra("classid");

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        StudentsRef.keepSynced(true);
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        recyclerView = (RecyclerView)  findViewById(R.id.pdf_students_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButton = (LinearLayout) findViewById(R.id.pdf_students_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        progressDialog = new ProgressDialog(PDFStudentActivity.this);
        progressDialog.setTitle("Loading Students");
        progressDialog.show();
        LoadUserData("");
    }

    private void LoadUserData(String data)
    {
        Query query = StudentsRef.orderByChild("classid").equalTo(classid);

        FirebaseRecyclerOptions<Students> options =
                new FirebaseRecyclerOptions.Builder<Students>()
                        .setQuery(query,Students.class)
                        .build();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    new AlertDialog.Builder(PDFStudentActivity.this)
                            .setTitle("Class Empty")
                            .setMessage("No students in this class")

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    onBackPressed();

                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseRecyclerAdapter<Students,PDFStudentActivity.UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Students, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        holder.threeDoubt.setVisibility(View.GONE );
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText("Roll no : "+model.rollnumber);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ExamsRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String examcount = String.valueOf(snapshot.getChildrenCount());
                                        Intent termIntent = new Intent(PDFStudentActivity.this,GeneratePdfActivity.class);
                                        termIntent.putExtra("studentid",key);
                                        termIntent.putExtra("classid",classid);
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

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlayout,parent,false);
                        return new UserViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        progressDialog.dismiss();
    }


    @Override
    protected void onStart() {
        super.onStart();


        progressDialog.show();
        LoadUserData("");
        //adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        View mView;
        ImageButton threeDoubt;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            threeDoubt=itemView.findViewById(R.id.edit_student_option);
            studentname = itemView.findViewById(R.id.name);
            rollnumber = itemView.findViewById(R.id.phone);
        }
    }
}