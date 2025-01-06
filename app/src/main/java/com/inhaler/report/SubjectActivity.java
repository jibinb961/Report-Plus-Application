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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.UserDataReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SubjectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference SubjectsRef;
    private Button addSubject;
    private EditText userInput;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private LinearLayout backButton;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        SubjectsRef.keepSynced(true);
        addSubject = (Button) findViewById(R.id.add_sub_button);
        progressDialog = new ProgressDialog(SubjectActivity.this);
        progressDialog.setTitle("Loading Subjects");
        progressDialog.show();
        recyclerView = (RecyclerView) findViewById(R.id.subject_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userInput = (EditText) findViewById(R.id.subjectInput);
        backButton = (LinearLayout) findViewById(R.id.subjects_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("subjectid","");

                SubjectFragment fragment = new SubjectFragment();
                    fragment.setArguments(bundle);
                    fragment.show(getSupportFragmentManager(),"Add Subject");



            }
        });


        userInput.addTextChangedListener(new TextWatcher() {
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
        LoadUserData("");



    }
    private void LoadUserData(String data)
    {
        Query query = SubjectsRef.orderByChild("subjectname").startAt(data).endAt(data+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!(snapshot.exists()))
                {
                    progressDialog.dismiss();
                    Toast.makeText(SubjectActivity.this, "No subjects added, add a new subject to see here", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Subjects> options =
                new FirebaseRecyclerOptions.Builder<Subjects>()
                .setQuery(query,Subjects.class)
                .build();
        FirebaseRecyclerAdapter<Subjects, UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Subjects, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Subjects model) {
                        final String key = getRef(position).getKey();
                        holder.name.setText(model.getSubjectname());
                        holder.phone.setText(model.getModified());
                        holder.editSubject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putString("subjectid",key);


                                SubjectFragment subjectFragment = new SubjectFragment();
                                subjectFragment.setArguments(bundle);
                                subjectFragment.
                                        show(getSupportFragmentManager(),"Add Subject");

                            }
                        });
//                        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                            @Override
//                            public boolean onLongClick(View v) {
//
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("subjectid",key);
//
//
//                                SubjectFragment subjectFragment = new SubjectFragment();
//                                subjectFragment.setArguments(bundle);
//                                subjectFragment.
//                                        show(getSupportFragmentManager(),"Add Subject");
//
//                                return false;
//                            }
//                        });

                        if(position>0)
                        {
                            progressDialog.dismiss();
                        }
                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.userlayout,parent,false);
                        return new UserViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();


    }


    @Override
    protected void onStart() {
        super.onStart();
//
//
//        progressDialog.show();
//        LoadUserData("");
        //adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,phone;
        View mView;
        ImageButton editSubject;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            editSubject = itemView.findViewById(R.id.edit_student_option);
        }
    }


}