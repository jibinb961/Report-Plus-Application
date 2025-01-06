package com.inhaler.report;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class GradingFragment extends DialogFragment {

    private EditText aplusPer,bplusPer,aPer,bPer,fPer,cPer;
    private Button updateBtn;
    private DatabaseReference ClassesRef;
    private FirebaseUser currentUser;
    private String userid,classid;
    private Context context;

    public GradingFragment(Context context) {
        this.context = context;
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View gradingView = inflater.inflate(R.layout.fragment_grading, container, false);
        // Inflate the layout for this fragment

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userid = currentUser.getUid();
        classid  =getArguments().getString("classid");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userid).child("Classes").child(classid);
        ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String aplus = snapshot.child("aplus").getValue(String.class);
                String a = snapshot.child("a").getValue(String.class);
                String bplus = snapshot.child("bplus").getValue(String.class);
                String b = snapshot.child("b").getValue(String.class);
                String fail = snapshot.child("fail").getValue(String.class);
                String c = snapshot.child("c").getValue(String.class);
                aplusPer.setText(aplus);
                aPer.setText(a);
                bplusPer.setText(bplus);
                bPer.setText(b);
                fPer.setText(fail);
                cPer.setText(c);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        fPer = gradingView.findViewById(R.id.fail_percentage);
        aPer = gradingView.findViewById(R.id.a_percentage);
        aplusPer = gradingView.findViewById(R.id.aplus_percentage);
        bPer = gradingView.findViewById(R.id.b_percentage);
        bplusPer = gradingView.findViewById(R.id.bplus_percentage);
        updateBtn = gradingView.findViewById(R.id.grade_update_button);
        cPer = gradingView.findViewById(R.id.c_percentage);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String aplus = aplusPer.getText().toString().trim();
                String a = aPer.getText().toString().trim();
                String bplus = bplusPer.getText().toString().trim();
                String b = bPer.getText().toString().trim();
                String fail = fPer.getText().toString().trim();
                String c = cPer.getText().toString().trim();

                if(aplus.isEmpty() || a.isEmpty() || bplus.isEmpty() || b.isEmpty() || fail.isEmpty())
                {
                    Toast.makeText(getContext(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    

                    ClassesRef.child("a").setValue(a);
                    ClassesRef.child("aplus").setValue(aplus);
                    ClassesRef.child("b").setValue(b);
                    ClassesRef.child("bplus").setValue(bplus);
                    ClassesRef.child("fail").setValue(fail);
                    ClassesRef.child("c").setValue(c);
                    Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
                    getDialog().dismiss();

                    
                }
            }
        });



        return gradingView;
    }
}