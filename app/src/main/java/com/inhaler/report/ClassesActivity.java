package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
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

public class ClassesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference ClassesRef;
    private ProgressDialog progressDialog;
    private Button addClassBtn;
    private FirebaseUser currentUser;
    private String uid;
    private TextView noStudent;
    private LinearLayout backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ClassesRef.keepSynced(true);

        recyclerView = (RecyclerView)  findViewById(R.id.classes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        backButton = (LinearLayout) findViewById(R.id.classes_back_button);


        noStudent = (TextView) findViewById(R.id.classes_no_classes_text);
        noStudent.setVisibility(View.INVISIBLE);


        progressDialog = new ProgressDialog(ClassesActivity.this);
        progressDialog.setTitle("Loading Classes");
        progressDialog.show();

        addClassBtn = (Button) findViewById(R.id.add_class_button_classes);
        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addClassIntent = new Intent(ClassesActivity.this,AddClassActivity.class);
                startActivity(addClassIntent);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LoadUserData();


    }
    private void LoadUserData()
    {
        Query query = ClassesRef.orderByChild("classname");


        FirebaseRecyclerOptions<Classes> options =
                new FirebaseRecyclerOptions.Builder<Classes>()
                .setQuery(query,Classes.class)
                .build();
        FirebaseRecyclerAdapter<Classes,UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Classes, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Classes model) {
                        final String key = getRef(position).getKey();

                        holder.classname.setText("Class : "+model.getClassname() + model.getClasssection());
                        holder.teachername.setText("Class Teacher : "+model.getClassteacher());
                        holder.editthreedot.setVisibility(View.GONE);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent editclassintent = new Intent(ClassesActivity.this,UpdateClassActivity.class);
                                editclassintent.putExtra("classid",key);
                                startActivity(editclassintent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_layout,parent,false);
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
//        progressDialog.show();
//        LoadUserData();
//        //adapter.startListening();
//
        noStudent.setVisibility(View.INVISIBLE);
        Query query = ClassesRef.orderByChild("classname");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No classes are created, click on 'Add' below to add a" +
                            " new class");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView classname,teachername;
        View mView;
        ImageButton editthreedot;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            classname = itemView.findViewById(R.id.exam_name);
            teachername = itemView.findViewById(R.id.exam_period);
            editthreedot = itemView.findViewById(R.id.options);
        }
    }
}