package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SelectTermActivity extends AppCompatActivity {

    private String classid,studentid;
    private CardView FirstMidTerm,FirstTerm,SecondMidTerm,SecondTerm;
    private EditText studentHeight,studentWeight;
    private Button okButton;
    private DatabaseReference StudentsRef,ExamsRef,ClassesRef;
    private FirebaseUser currentUser;
    private ListView listView;
    private String exams = "",examid="";
    private String[] examsarray;
    private ImageView backButton;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_term);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        classid = getIntent().getStringExtra("classid");
        studentid = getIntent().getStringExtra("studentid");

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Coscholastic");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes").child(classid);




        listView = (ListView) findViewById(R.id.select_exams_listview);
        backButton = (ImageView) findViewById(R.id.select_term_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ClassesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("exams").exists())
                {
                 exams = snapshot.child("exams").getValue(String.class);
                 examsarray = exams.split(",");

                }
                for(int i=0;i<examsarray.length;i++)
                {
                    examsarray[i] = examsarray[i].trim();
                }
                ArrayAdapter<String> examadapter = new ArrayAdapter<>(SelectTermActivity.this, android.R.layout.simple_list_item_1,examsarray);
                listView.setAdapter(examadapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectedexam = parent.getItemAtPosition(position).toString();
                ExamsRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        if(snapshot.child("examname").getValue().toString().equals(selectedexam))
                        {
                            examid = snapshot.getKey();
                            String term="twomid";
                            Intent entermarkintent = new Intent(SelectTermActivity.this,EnterSubjectMarks.class);
                            entermarkintent.putExtra("classid",classid);
                            entermarkintent.putExtra("studentid",studentid);
                            entermarkintent.putExtra("term",term);
                            entermarkintent.putExtra("examid",examid);
                            startActivity(entermarkintent);
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
            }
        });

//        StudentsRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(classid).child(studentid).child("studentweight").exists() &&
//                        snapshot.child(classid).child(studentid).child("studentheight").exists() )
//                {
//                   String studentheight = snapshot.child(classid).child(studentid).child("studentheight").getValue(String.class);
//                   String studentweight = snapshot.child(classid).child(studentid).child("studentweight").getValue(String.class);
//                   studentWeight.setText(studentweight);
//                   studentHeight.setText(studentheight);
//                }
//                else
//                {
//                    String studentheight = "";
//                    String studentweight = "";
//                    studentWeight.setText(studentweight);
//                    studentHeight.setText(studentheight);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        FirstMidTerm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String term = "onemid";
//                Intent entermarkintent = new Intent(SelectTermActivity.this,EnterSubjectMarks.class);
//                entermarkintent.putExtra("classid",classid);
//                entermarkintent.putExtra("studentid",studentid);
//                entermarkintent.putExtra("term",term);
//                startActivity(entermarkintent);
//
//            }
//        });
//        SecondMidTerm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String term="twomid";
//                Intent entermarkintent = new Intent(SelectTermActivity.this,EnterSubjectMarks.class);
//                entermarkintent.putExtra("classid",classid);
//                entermarkintent.putExtra("studentid",studentid);
//                entermarkintent.putExtra("term",term);
//                startActivity(entermarkintent);
//            }
//        });
//        FirstTerm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String term="oneterm";
//                Intent entermarkintent = new Intent(SelectTermActivity.this,EnterSubjectMarks.class);
//                entermarkintent.putExtra("classid",classid);
//                entermarkintent.putExtra("studentid",studentid);
//                entermarkintent.putExtra("term",term);
//                startActivity(entermarkintent);
//
//            }
//        });
//        SecondTerm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String term="twoterm";
//                Intent entermarkintent = new Intent(SelectTermActivity.this,EnterSubjectMarks.class);
//                entermarkintent.putExtra("classid",classid);
//                entermarkintent.putExtra("studentid",studentid);
//                entermarkintent.putExtra("term",term);
//                startActivity(entermarkintent);
//
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}