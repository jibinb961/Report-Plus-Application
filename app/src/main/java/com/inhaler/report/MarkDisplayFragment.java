package com.inhaler.report;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MarkDisplayFragment extends DialogFragment {

    private View MarksView;
    private Context context;
    private String examid,classid,studentid;
    private ListView listView;
    private FirebaseUser currentUser;
    private String uid;
    private Button cancelButton;
    private ArrayList<String> marksarray = new ArrayList<>();
    private DatabaseReference MarksRef,ExamsRef;
    private HashMap<String,String> subjectmarks = new HashMap<>();

    public MarkDisplayFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MarksView =  inflater.inflate(R.layout.fragment_mark_display, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        examid = getArguments().getString("examid");
        classid = getArguments().getString("classid");
        studentid = getArguments().getString("studentid");

        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");


        listView = (ListView) MarksView.findViewById(R.id.indiviudal_exam_marks_listview);
        cancelButton = (Button) MarksView.findViewById(R.id.cancel_button_marks_analytics);
        cancelButton.setVisibility(View.GONE);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        ExamsRef.child(classid).child(examid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] subjects = snapshot.child("subjects").getValue(String.class).split(",");
                for(int i=0;i<subjects.length;i++)
                {
                    subjects[i] =subjects[i].trim();
                    subjectmarks.put(subjects[i],"");
                }
                GetSubjectMarks();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return MarksView;
    }

    private void GetSubjectMarks() {
        MarksRef.child(classid).child(studentid).child(examid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (Map.Entry<String,String> mapElement : subjectmarks.entrySet()) {
                    String key = mapElement.getKey();
                    if(snapshot.child(key).exists())
                    {
                        subjectmarks.put(key,snapshot.child(key).getValue(String.class));
                    }
                    else
                    {
                        subjectmarks.put(key,"Not updated");
                    }



                }
                populateListView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void populateListView() {
        for (Map.Entry<String,String> mapElement : subjectmarks.entrySet()) {
            String subjectkey = mapElement.getKey();
            String subjectvalue = mapElement.getValue();

            String marks = subjectkey + "  :  "+subjectvalue;
            marksarray.add(marks);


        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getContext(),
                android.R.layout.simple_list_item_1,
                marksarray );
        listView.setAdapter(arrayAdapter);
        cancelButton.setVisibility(View.VISIBLE);


    }

    @Override
    public void onStart() {
        super.onStart();

    }
}