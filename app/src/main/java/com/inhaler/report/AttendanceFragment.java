package com.inhaler.report;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyboardShortcutGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;


public class AttendanceFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference StudentsRef,AttendanceRef,DetailsRef;
    private String classid ="",userid="",ClickedDate="";
    private TextView noStudent;
    private FirebaseUser currentUser;
    private String uid,ClassPinString;
    private ProgressDialog progressDialog;
    private ImageButton staffAddStudentButton;
    private ImageView backButton;
    private boolean present = true;
    private boolean isactivated =false;
    private RelativeLayout activatedLayout,upgradeLayout;
    private Integer studentCount;
    private HashMap<String, Boolean> attendanceMap = new HashMap<>();
    private View StudentsView;
    private Button saveAttButton,seeWhatsMore;
    private ArrayList<String> savedstudentkeys = new ArrayList<>();



    public AttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* starts before 1 month from now */








    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        StudentsView = inflater.inflate(R.layout.fragment_attendance,container,false);

        activatedLayout = StudentsView.findViewById(R.id.attendance_relative_layout);
        upgradeLayout = StudentsView.findViewById(R.id.attendance_upgrade_relative_layout);
        upgradeLayout.setVisibility(View.GONE);

        classid = getArguments().getString("classid");
        userid = getArguments().getString("userid");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        AttendanceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Attendance");
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("activationkey").exists())
                {
                    upgradeLayout.setVisibility(View.VISIBLE);
                    activatedLayout.setVisibility(View.GONE);
                }
                else
                {

                    upgradeLayout.setVisibility(View.GONE);
                    activatedLayout.setVisibility(View.VISIBLE);
                    isactivated = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recyclerView = (RecyclerView) StudentsView.findViewById(R.id.attendance_students_recyclerView);
        seeWhatsMore = (Button) StudentsView.findViewById(R.id.see_whats_more_button);
        seeWhatsMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent preintent = new Intent(getContext(), PremiumActivity.class);
                startActivity(preintent);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        StudentsRef.orderByChild("classid").equalTo(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerView.setItemViewCacheSize((int) snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        saveAttButton = StudentsView.findViewById(R.id.save_attendance_buton);


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Saving Attendance");
        noStudent = (TextView) StudentsView.findViewById(R.id.attendance_fragment_no_students);

        saveAttButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                for (Map.Entry<String, Boolean> entry : attendanceMap.entrySet()) {
                    String key = entry.getKey();
                    if(savedstudentkeys.contains(key) && entry.getValue())
                    {
                        AttendanceRef.child(classid).child(key).child(ClickedDate).removeValue();
                    }
                    if (!entry.getValue()) {
                        AttendanceRef.child(classid).child(key).child(ClickedDate).setValue("absent");
                    }
                }
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Successfully Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        Date date1 = new Date();
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(StudentsView.getRootView(), R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .build();
        String selectedmonth="";
        if(String.valueOf(date1.getMonth()).length()  == 1)
        {
            selectedmonth = "0"+String.valueOf(date1.getMonth()+1);
        }
        else
        {
            selectedmonth = String.valueOf(date1.getMonth());
        }
        ClickedDate =  String.valueOf(date1.getDate())+selectedmonth+String.valueOf(date1.getYear()+1900).substring(2,4);



        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
               long millis = date.getTime();
               String selectedmonth="";
               if(String.valueOf(date.getMonth()).length()  == 1)
               {
                   selectedmonth = "0"+String.valueOf(date.getMonth()+1);
               }
               else
               {
                   selectedmonth = String.valueOf(date.getMonth());
               }
               ClickedDate =  String.valueOf(date.getDate())+selectedmonth+String.valueOf(date.getYear()+1900).substring(2,4);
               savedstudentkeys.clear();
               attendanceMap.clear();
               GetStudentCountinAttendanceNode();
            }
        });






        return StudentsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        noStudent.setVisibility(View.INVISIBLE);

        saveAttButton.setVisibility(View.VISIBLE);

          GetStudentCountinAttendanceNode();




    }

    private void GetStudentCountinAttendanceNode() {
        progressDialog.show();
        AttendanceRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                studentCount = Math.toIntExact(snapshot.getChildrenCount());
                if(studentCount==0)
                {
                    LoadStudentData();
                }
                else
                {
                    LoadAttendanceDetails();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void LoadAttendanceDetails()
    {
        final int[] count = {0};
        //to retrive all student attendance info
        AttendanceRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child(ClickedDate).exists())
                {

                    attendanceMap.put(snapshot.getKey(),false);
                    savedstudentkeys.add(snapshot.getKey());
                }
                count[0] = count[0] +1;
                if(count[0] == studentCount)
                {
                    LoadStudentData();
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
    public void LoadStudentData()
    {
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
                            "Go to Students section to add a new student to this class");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    saveAttButton.setVisibility(View.GONE);
                    progressDialog.dismiss();

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerAdapter<Students,AttendanceViewHolder>  adapter =
                new FirebaseRecyclerAdapter<Students, AttendanceViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull AttendanceViewHolder holder, int position, @NonNull Students model) {
                        final String key = getRef(position).getKey();
                        holder.studentname.setText(model.getStudentname());
                        holder.rollnumber.setText("Roll no : "+model.rollnumber);

                        if(attendanceMap.get(key)!=null)
                        {
                            holder.statusImage.setImageResource(R.drawable.cancel);
                        }
                        else
                        {
                            attendanceMap.put(key,true);
                        }
                        holder.statusImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(Boolean.TRUE.equals(attendanceMap.get(key)))
                                {
                                    holder.statusImage.setImageResource(R.drawable.cancel);
                                    attendanceMap.put(key,false);

                                }
                                else {

                                    holder.statusImage.setImageResource(R.drawable.checked);
                                    attendanceMap.put(key,true);
                                }
                            }

                        });
                    }

                    @NonNull
                    @Override
                    public AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_student_layout,parent,false);
                        AttendanceFragment.AttendanceViewHolder attendanceViewHolder = new AttendanceFragment.AttendanceViewHolder(view);
                        return attendanceViewHolder;
                    }
                };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        View mView;
        ImageView statusImage;
        FrameLayout attendanceFrame;


        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.attendance_student_name);
            rollnumber = itemView.findViewById(R.id.attendance_student_roll);
            statusImage = itemView.findViewById(R.id.attendance_image );
            attendanceFrame = itemView.findViewById(R.id.attendance_mark_frame);
        }
    }



}