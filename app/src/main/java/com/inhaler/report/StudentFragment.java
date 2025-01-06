package com.inhaler.report;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;


public class StudentFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,MarksRef,ClassesRef,ExamsRef,AttendanceRef;
    private ProgressDialog progressDialog;
    private String classid ="",userid="";
    private TextView noStudent;
    private FirebaseUser currentUser;
    private String uid,ClassPinString;
    private ImageButton staffAddStudentButton;
    private ImageView backButton;
    private View StudentsView;


    public StudentFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        StudentsView   = inflater.inflate(R.layout.fragment_student,container,false);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        recyclerView = (RecyclerView) StudentsView.findViewById(R.id.students_fragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        staffAddStudentButton = (ImageButton) StudentsView.findViewById(R.id.staff_add_student_button);

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        AttendanceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Attendance");
        StudentsRef.keepSynced(true);






        noStudent = (TextView) StudentsView.findViewById(R.id.student_fragment_no_student);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading Students");
        progressDialog.show();

        staffAddStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent editstudentintent = new Intent(getContext(),AddStudentActivity.class);
                editstudentintent.putExtra("staffclassid",classid);
                editstudentintent.putExtra("studentname","");
                editstudentintent.putExtra("rollnumber","");
                editstudentintent.putExtra("fathername","");
                editstudentintent.putExtra("studentid","");
                editstudentintent.putExtra("classid","");
                editstudentintent.putExtra("studentweight","");
                editstudentintent.putExtra("studentheight","");
                startActivity(editstudentintent);
            }
        });



        return StudentsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        noStudent.setVisibility(View.INVISIBLE);
        classid = getArguments().getString("classid");
        userid = getArguments().getString("userid");

        Query query = StudentsRef.orderByChild("classid").equalTo(classid);
        FirebaseRecyclerOptions<Students> options =
                new FirebaseRecyclerOptions.Builder<Students>()
                        .setQuery(query,Students.class)
                        .build();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No Students present in this class,\n click " +
                            "on the '+' button below to add a new Student to this class");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    progressDialog.dismiss();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       FirebaseRecyclerAdapter<Students,StudentsViewHolder> adapter =
               new FirebaseRecyclerAdapter<Students, StudentsViewHolder>(options) {
                   @Override
                   protected void onBindViewHolder(@NonNull StudentsViewHolder holder, int position, @NonNull Students model) {
                       final String key = getRef(position).getKey();
                       holder.studentname.setText(model.getStudentname());
                       holder.rollnumber.setText("Roll no : "+model.rollnumber);

                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               Intent editstudentintent = new Intent(getContext(),AddStudentActivity.class);
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
                       holder.editStudent.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               PopupMenu popupMenu = new PopupMenu(getContext(),holder.editStudent);

                               // Inflating popup menu from popup_menu.xml file
                               popupMenu.getMenuInflater().inflate(R.menu.student_popup, popupMenu.getMenu());
                               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                   @Override
                                   public boolean onMenuItemClick(MenuItem menuItem) {
                                       if(menuItem.getItemId()==R.id.delete_student)
                                       {
                                           new AlertDialog.Builder(getContext())
                                                   .setTitle("Delete entry")
                                                   .setMessage("Are you sure you want to delete "+ model.getStudentname())

                                                   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           StudentsRef.child(key).removeValue();
                                                           MarksRef.child(classid).child(key).removeValue();
                                                           AttendanceRef.child(classid).child(key).removeValue();

                                                       }
                                                   })

                                                   // A null listener allows the button to dismiss the dialog and take no further action.
                                                   .setNegativeButton(android.R.string.no, null)
                                                   .setIcon(android.R.drawable.ic_dialog_alert)
                                                   .show();
                                           return false;
                                       }
                                       else if(menuItem.getItemId()==R.id.edit_student)
                                       {
                                           Intent editstudentintent = new Intent(getContext(),AddStudentActivity.class);
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
                                       else if(menuItem.getItemId()==R.id.generate_pdf)
                                       {

                                           ExamsRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
                                               @Override
                                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                   String examcount = String.valueOf(snapshot.getChildrenCount());
                                                   Intent termIntent = new Intent(getContext(),GeneratePdfActivity.class);
                                                   termIntent.putExtra("studentid",key);
                                                   termIntent.putExtra("classid",classid);
                                                   termIntent.putExtra("examcount",examcount);
                                                   startActivity(termIntent);
                                               }

                                               @Override
                                               public void onCancelled(@NonNull DatabaseError error) {

                                               }
                                           });

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
                   public StudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlayout,parent,false);
                       StudentsViewHolder studentsViewHolder = new StudentsViewHolder(view);
                       return studentsViewHolder;
                   }
               };
       recyclerView.setAdapter(adapter);
       adapter.startListening();
        progressDialog.dismiss();
    }

    private void openWhatsapp() {


    }


    public static class StudentsViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        View mView;
        ImageButton editStudent;

        public StudentsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.name);
            rollnumber = itemView.findViewById(R.id.phone);
            editStudent = itemView.findViewById(R.id.edit_student_option);
        }
    }
}