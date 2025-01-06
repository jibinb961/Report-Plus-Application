package com.inhaler.report;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.HashMap;

public class StudentAttendanceFragment extends Fragment {

    private DatabaseReference AttendanceRef;
    private View StudentsView;
    private FirebaseUser currentUser;
    private ListView listView;
    private ImageView noAbsentImage;
    private String uid,ClassPinString;
    private String classid ="",studentid="";
    private TextView noStudent,absentDaysHeading;
    private Integer attendanceCount =0;
    private ArrayList<String> absentdays = new ArrayList<>();
    private HashMap<String,String> hm = new HashMap();



    public StudentAttendanceFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        StudentsView   = inflater.inflate(R.layout.fragment_student_attendance,container,false);


        hm.put("01", "January");
        hm.put("02", "Febraury");
        hm.put("03", "March");
        hm.put("04", "April");
        hm.put("05", "May");
        hm.put("06", "June");
        hm.put("07", "July");
        hm.put("08", "August");
        hm.put("09", "September");
        hm.put("10", "October");
        hm.put("11", "November");
        hm.put("12", "December");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        listView = (ListView) StudentsView.findViewById(R.id.absent_list_listView);
        absentDaysHeading = (TextView) StudentsView.findViewById(R.id.absent_days_textView);
        AttendanceRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Attendance");
        noStudent = (TextView) StudentsView.findViewById(R.id.no_students_attendance_analytics_view);

        noAbsentImage = (ImageView) StudentsView.findViewById(R.id.no_absence_image);



        return StudentsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        noStudent.setVisibility(View.INVISIBLE);
        noAbsentImage.setVisibility(View.INVISIBLE);

        classid = getArguments().getString("classid");
        studentid = getArguments().getString("studentid");
        attendanceCount = Integer.valueOf(getArguments().getString("attendancecount"));
        if(attendanceCount.equals(0))
        {
            noStudent.setVisibility(View.VISIBLE);
            noAbsentImage.setVisibility(View.VISIBLE);
        }
        absentDaysHeading.setText("Absent Days ( "+String.valueOf(attendanceCount)+" )");


        final Integer[] count = {0};
        AttendanceRef.child(classid).child(studentid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String absentday = snapshot.getKey();
                if(!absentday.isEmpty())
                {
                    if(absentday.length()==6)
                    {
                        String day = absentday.substring(0,2);
                        String month = hm.get(absentday.substring(2,4));
                        String year = "20"+absentday.substring(4,6);
                        absentdays.add(day+" "+month+", "+year);
                    }
                    else if(absentday.length()==5)
                    {
                        String day = absentday.substring(0,1);
                        String month = hm.get(absentday.substring(1,3));
                        String year = "20"+absentday.substring(3,5);
                        absentdays.add(day+" "+month+", "+year);
                    }
                }
                count[0] = count[0] +1;
                if(count[0].equals(attendanceCount))
                {
                    if(!absentdays.isEmpty())
                    {

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                getContext(),
                                android.R.layout.simple_list_item_1,
                                absentdays );
                        listView.setAdapter(arrayAdapter);

                    }
                    else
                    {
                        noStudent.setVisibility(View.VISIBLE);
                        noAbsentImage.setVisibility(View.VISIBLE);
                    }

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