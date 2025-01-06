package com.inhaler.report;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StudentsMarkFragment extends Fragment {

    private View StudentMarksView;
    private DatabaseReference MarksRef,ExamsRef;
    private View StudentsView;
    private FirebaseUser currentUser;
    private ListView listView;
    private String uid,ClassPinString;
    private String classid ="",studentid="";
    private TextView noStudent,absentDaysHeading;
    private Integer examcount =0,allexamcount=0;
    private ProgressDialog progressDialog;
    private ArrayList<String> exams = new ArrayList<>();
    private HashMap<String,String> allexamidsandnames = new HashMap();
    private HashMap<String,String> allexamnamesandids = new HashMap();



    public StudentsMarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StudentMarksView = inflater.inflate(R.layout.fragment_students_mark, container, false);

        classid = getArguments().getString("classid");
        studentid = getArguments().getString("studentid");
        examcount = Integer.valueOf(getArguments().getString("examcount"));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        listView = (ListView) StudentMarksView.findViewById(R.id.student_marks_analytics_listView);
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading Exams");
        progressDialog.show();
        if(examcount.equals(0))
        {
            progressDialog.dismiss();
        }

        noStudent = (TextView) StudentMarksView.findViewById(R.id.no_students_marks_analytics_view);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = listView.getItemAtPosition(position).toString();
                MarkDisplayFragment fragment = new MarkDisplayFragment(getContext());
                Bundle bundle = new Bundle();
                bundle.putString("examid",allexamnamesandids.get(item));
                bundle.putString("classid",classid);
                bundle.putString("studentid",studentid);
                fragment.setArguments(bundle);
                fragment.show(getChildFragmentManager(),"Delete Account");
            }
        });

        return StudentMarksView;
    }

    @Override
    public void onStart() {
        super.onStart();
        noStudent.setVisibility(View.GONE);
        ExamsRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allexamcount = Math.toIntExact(snapshot.getChildrenCount());
                GetAllExamNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void GetAllExamNames() {
        final int[] count = {0};
        ExamsRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                allexamidsandnames.put(snapshot.getKey(),snapshot.child("examname").getValue(String.class));
                allexamnamesandids.put(snapshot.child("examname").getValue(String.class),snapshot.getKey());
                count[0] = count[0] +1;
                if(count[0] ==allexamcount)
                {
                    getExamMarks();
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

    private void getExamMarks() {
        final Integer[] count = {0};
        MarksRef.child(classid).child(studentid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String examname = allexamidsandnames.get(snapshot.getKey());
                exams.add(examname);

                count[0] = count[0]+1;
                if(count[0].equals(examcount))
                {
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getContext(),
                            android.R.layout.simple_list_item_1,
                            exams );
                    listView.setAdapter(arrayAdapter);
                    progressDialog.dismiss();
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
}