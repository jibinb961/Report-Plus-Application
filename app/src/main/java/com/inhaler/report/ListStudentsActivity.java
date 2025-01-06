package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.auth.User;

import org.w3c.dom.Text;

public class ListStudentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,MarksRef,ClassesRef;
    private ProgressDialog progressDialog;
    private String classid ="",userid="";
    private TextView noStudent;
    private FirebaseUser currentUser;
    private String uid,ClassPinString;
    private LinearLayout backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_students);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        classid = getIntent().getStringExtra("classid");
        userid = getIntent().getStringExtra("userid");

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        StudentsRef.keepSynced(true);
        recyclerView = (RecyclerView)  findViewById(R.id.addmarks_students_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButton = (LinearLayout) findViewById(R.id.list_students_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        noStudent = (TextView) findViewById(R.id.no_student_text);

        progressDialog = new ProgressDialog(ListStudentsActivity.this);
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
        
        FirebaseRecyclerAdapter<Students,UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Students, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText("Roll no : "+model.rollnumber);
                        
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent termIntent = new Intent(ListStudentsActivity.this,SelectTermActivity.class);
                                termIntent.putExtra("studentid",key);
                                termIntent.putExtra("classid",classid);
                                startActivity(termIntent);
                            }
                        });
                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {

                                new AlertDialog.Builder(ListStudentsActivity.this)
                                        .setTitle("Delete entry")
                                        .setMessage("Are you sure you want to delete "+ model.getStudentname())
                                        
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                               StudentsRef.child(key).removeValue();
                                               MarksRef.child(classid).child(key).removeValue();

                                            }
                                        })

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .setNegativeButton(android.R.string.no, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                                return false;
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
    


    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.name);
            rollnumber = itemView.findViewById(R.id.phone);
        }
    }
}