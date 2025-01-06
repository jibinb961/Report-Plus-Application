package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.HashMap;

public class StudentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,MarksRef,ClassesRef;
    private ProgressDialog progressDialog;
    private TextView noStudent;
    private EditText findStudentinput;
    private Button addButton;
    private HashMap<String,String> allclasses = new HashMap<>();
    private FirebaseUser currentUser;
    private String uid;
    private LottieAnimationView studentdown;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        getWindow().setAllowEnterTransitionOverlap(false);
        getWindow().setAllowReturnTransitionOverlap(false);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();


        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        StudentsRef.keepSynced(true);
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        recyclerView = (RecyclerView)  findViewById(R.id.find_students_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = (Button) findViewById(R.id.add_student_button);

        noStudent = (TextView) findViewById(R.id.students_no_students_text);

        findStudentinput = (EditText) findViewById(R.id.find_student_input);
        studentdown =  (LottieAnimationView) findViewById(R.id.students_down_animation);
        backButton = (ImageView) findViewById(R.id.students_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        studentdown.setVisibility(View.GONE);

        progressDialog = new ProgressDialog(StudentsActivity.this);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addstudent = new Intent(StudentsActivity.this,AddStudentActivity.class);
                addstudent.putExtra("studentname","");
                addstudent.putExtra("studentid","");
                addstudent.putExtra("fathername","");
                addstudent.putExtra("rollnumber","");
                addstudent.putExtra("classid","");
                addstudent.putExtra("staffclassid","");

                startActivity(addstudent);
            }
        });
        LoadUserData("");




    }

    private void LoadUserData(String data)
    {
        studentdown.setVisibility(View.GONE);
        noStudent.setVisibility(View.GONE);
        Query query = StudentsRef.orderByChild("studentname").startAt(data).endAt(data+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No students are added to any class, click on 'Add' below to add a" +
                            " new student");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    studentdown.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Students> options   =
                new FirebaseRecyclerOptions.Builder<Students>()
                .setQuery(query,Students.class)
                .build();

        FirebaseRecyclerAdapter<Students,UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Students, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        String classid = model.getClassid();
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText(allclasses.get(classid));
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent editstudentintent = new Intent(StudentsActivity.this,AddStudentActivity.class);
                                editstudentintent.putExtra("studentname",model.getStudentname());
                                editstudentintent.putExtra("rollnumber",model.getRollnumber());
                                editstudentintent.putExtra("fathername",model.getFathername());
                                editstudentintent.putExtra("studentid",key);
                                editstudentintent.putExtra("classid",model.getClassid());
                                editstudentintent.putExtra("studentweight",model.getStudentweight());
                                editstudentintent.putExtra("studentheight",model.getStudentheight());
                                editstudentintent.putExtra("staffclassid","");
                                startActivity(editstudentintent);
                            }
                        });

                        holder.options.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu(StudentsActivity.this,holder.options);

                                // Inflating popup menu from popup_menu.xml file
                                popupMenu.getMenuInflater().inflate(R.menu.student_popup, popupMenu.getMenu());
                                popupMenu.getMenu().removeItem(R.id.edit_student);
                                popupMenu.getMenu().removeItem(R.id.generate_pdf);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        if(menuItem.getItemId()==R.id.delete_student)
                                        {
                                            new AlertDialog.Builder(StudentsActivity.this)
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

                                        return true;
                                    }
                                });
                                // Showing the popup menu
                                popupMenu.show();
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

    }


    @Override
    protected void onStart() {
        super.onStart();

//
//        progressDialog.show();
//        LoadUserData("");
//        //adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        ImageButton options;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.name);
            rollnumber = itemView.findViewById(R.id.phone);
            options = itemView.findViewById(R.id.edit_student_option);
        }
    }
}