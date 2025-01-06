package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
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

public class ExamsActivity extends AppCompatActivity {
    private FirebaseUser currentUser;
    private String uid;
    private RecyclerView recyclerView;
    private DatabaseReference ExamsRef;
    private Button addExamBtn;
    private TextView noExams;
    private LinearLayout backButton;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        ExamsRef.keepSynced(true);

        recyclerView = (RecyclerView)  findViewById(R.id.exams_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        addExamBtn = (Button) findViewById(R.id.add_exam_button);
        noExams = (TextView) findViewById(R.id.exams_no_exams_text);
        backButton= (LinearLayout) findViewById(R.id.exams_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(ExamsActivity.this);
        progressDialog.setTitle("Loading Exams");
        progressDialog.show();

        addExamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addstudent = new Intent(ExamsActivity.this,AddExamActivity.class);
                addstudent.putExtra("examname","");
                addstudent.putExtra("examstartdate","");
                addstudent.putExtra("examenddate","");
                addstudent.putExtra("coactivities","");
                addstudent.putExtra("examid","");
                addstudent.putExtra("classid","");
                addstudent.putExtra("userid","");
                addstudent.putExtra("subjects","");

                startActivity(addstudent);

            }
        });
        LoadUserData();


    }
    private void LoadUserData()
    {
        Query query = ExamsRef.orderByChild("examname");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noExams.setText("No exams are created, click on 'Add' below to add a" +
                            " new exam!");
                    noExams.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Exams> options =
                new FirebaseRecyclerOptions.Builder<Exams>()
                        .setQuery(query,Exams.class)
                        .build();

        FirebaseRecyclerAdapter<Exams,UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Exams, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Exams model) {
                        final String key = getRef(position).getKey();
                        holder.examname.setText(model.getExamname());
                        holder.examperiod.setText(model.getExamstartdate()+" - "+model.getExamenddate());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent addexamintent = new Intent(ExamsActivity.this,AddExamActivity.class);
                                addexamintent.putExtra("examname",model.getExamname());
                                addexamintent.putExtra("examstartdate",model.getExamstartdate());
                                addexamintent.putExtra("examenddate",model.getExamenddate());
                                addexamintent.putExtra("examid",key);
                                addexamintent.putExtra("coactivities","");

                                startActivity(addexamintent);
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
//        progressDialog.show();
//
//
//        LoadUserData();
        //adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();

    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView examname,examperiod;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            examname = itemView.findViewById(R.id.exam_name);
            examperiod = itemView.findViewById(R.id.exam_period);
        }
    }
}