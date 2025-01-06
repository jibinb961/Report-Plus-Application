package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class PDFClassActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference ClassesRef;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String uid;
    private LinearLayout backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfclass);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ClassesRef.keepSynced(true);

        recyclerView = (RecyclerView)  findViewById(R.id.pdf_classes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        backButton = (LinearLayout) findViewById(R.id.class_pdf_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        progressDialog = new ProgressDialog(PDFClassActivity.this);
        progressDialog.setTitle("Loading Classes");

        LoadUserData();
    }
    private void LoadUserData()
    {
        Query query = ClassesRef.orderByChild("classname");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    new AlertDialog.Builder(PDFClassActivity.this)
                            .setTitle("No Classes")
                            .setMessage("No classes are added, go to class module to add a new class?")

                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent addclassintetn = new Intent(PDFClassActivity.this,AddClassActivity.class);
                                    startActivity(addclassintetn);

                                }
                            })

                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Classes> options =
                new FirebaseRecyclerOptions.Builder<Classes>()
                        .setQuery(query,Classes.class)
                        .build();

        FirebaseRecyclerAdapter<Classes,PDFClassActivity.UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Classes, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Classes model) {

                        final String key = getRef(position).getKey();
                        holder.classname.setText(model.getClassname() + model.getClasssection());
                        holder.teachername.setText(model.getClassteacher());
                        holder.pins.setVisibility(View.GONE);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent listStudentsIntent= new Intent(PDFClassActivity.this,PDFStudentActivity.class);
                                listStudentsIntent.putExtra("classid",key);
                                startActivity(listStudentsIntent);
                            }
                        });



                    }

                    @NonNull
                    @Override
                    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_layout,parent,false);

                        return new PDFClassActivity.UserViewHolder(view);
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
        ImageButton pins;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            classname = itemView.findViewById(R.id.classnumber);
            teachername = itemView.findViewById(R.id.classteacher);
            pins = itemView.findViewById(R.id.edit_class_option);
        }
    }
}