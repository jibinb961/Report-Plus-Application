package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class    StaffClassActivity extends AppCompatActivity {
    private DatabaseReference ClassesRef,StaffRef;
    private FirebaseUser currentUser;
    private String uid,staffkey,classes,classcount,staffid;
    private RecyclerView recyclerView;
    private LinearLayout backLayout;
    ArrayList<MyClassesModel> staffClassModel = new ArrayList<>();
    MyClassesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_class);

        staffkey = getIntent().getStringExtra("staffkey");
        staffid  =getIntent().getStringExtra("staffid");

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        uid = currentUser.getUid();
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ClassesRef.keepSynced(true);
        backLayout = (LinearLayout) findViewById(R.id.staff_class_back_layout);
        StaffRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs").child(staffkey);
        StaffRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("classes").exists())
                {
                    classes = snapshot.child("classes").getValue(String.class);
                    String[] staffclasses = classes.split(",");
                    setDataToModel(staffclasses);
                }

                else
                {
                    Toast.makeText(StaffClassActivity.this, "No classes are assigned, please contact your admin for more details!", Toast.LENGTH_SHORT).show();
                }






            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.staff_classes_recyclerView);
        adapter = new MyClassesAdapter(StaffClassActivity.this,staffClassModel,staffkey,staffid);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(StaffClassActivity.this,2));

        backLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    public void setDataToModel(String[] classes)
    {
        for(int i=0;i<classes.length;i++)
        {
            String classid = classes[i].trim();
            ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String classname = snapshot.child(classid).child("classname").getValue(String.class);
                    String classteacher = snapshot.child(classid).child("classteacher").getValue(String.class);
                    String classsection = snapshot.child(classid).child("classsection").getValue(String.class);
                    String classstrength = snapshot.child(classid).child("classstrength").getValue(String.class);
                    if(adapter.getItemCount()<classes.length)
                    {

                        staffClassModel.add(new MyClassesModel(classname,classteacher,classstrength,classsection,classid));
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {


                    }



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}