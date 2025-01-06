package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.kernel.geom.Line;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AddExamActivity extends AppCompatActivity{

    private EditText ExamName,ExamStartDate,ExamEndDate,CoscholasticInput,SelectClass,TotalMarks;
    final Calendar myCalendar= Calendar.getInstance();
    private Button addExamButton;
    private CheckBox coscholarCheckBox;
    private Integer subjectcountchip=0;
    private DatabaseReference ExamRef,CoscholasticRef,detailsRef,SubjectsRef,ClassesRef;
    private DatePickerDialog.OnDateSetListener startDateListener,endDateListener;
    private FirebaseUser currentUser;
    private String uid,examname,examstartdate,examenddate,cocurricularactivities,examid,AdminPinString="",userid="",classid="",startyear="",startmonth="",startmonthandyear="";
    private ArrayList<String> coscolasticactivities = new ArrayList<>();
    private String staffid,staffkey,totalmarks,subjects;
    private ArrayList<String> seletedCO = new ArrayList<>();
    private HashMap<String,String> subjectandsubids = new HashMap<>();
    private TextView addExamtext,modifiedby;
    private FrameLayout NoSub;
    private LinearLayout backButton;
    private ChipGroup subjectGroup;
    private ArrayList<String> allexamlist = new ArrayList<>();

    ArrayList<String> selectedSubjectIds = new ArrayList<>();
    ArrayList<String> selectedSubjectNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();


        examname = getIntent().getStringExtra("examname");
        examstartdate = getIntent().getStringExtra("examstartdate");
        examenddate = getIntent().getStringExtra("examenddate");

        cocurricularactivities = getIntent().getStringExtra("coactivities");
        examid = getIntent().getStringExtra("examid");
        staffid = getIntent().getStringExtra("staffid");
        classid  = getIntent().getStringExtra("classid");
        totalmarks = getIntent().getStringExtra("totalmarks");
        subjects = getIntent().getStringExtra("subjects");
        staffkey = getIntent().getStringExtra("staffkey");
        String[] selectedsubjects = subjects.split(",");
        for(int i=0;i<selectedsubjects.length;i++)
        {
            selectedsubjects[i] = selectedsubjects[i].trim();
        }

        addExamButton = (Button) findViewById(R.id.addexam_ok_button);
        ExamName = (EditText) findViewById(R.id.exam_name_input);
        backButton =  (LinearLayout) findViewById(R.id.add_exam_back_button);
        ExamStartDate = (EditText) findViewById(R.id.start_period_input);
        ExamEndDate = (EditText) findViewById(R.id.end_period_input);
        CoscholasticInput = (EditText) findViewById(R.id.exam_coscholastic_input);
        coscholarCheckBox = (CheckBox) findViewById(R.id.add_coscholar_activity_chekbox);
        addExamtext = (TextView) findViewById(R.id.addexam);
        SelectClass = (EditText) findViewById(R.id.addexam_select_class_input);
        subjectGroup = (ChipGroup) findViewById(R.id.addSubjectChipGroup);
        TotalMarks = (EditText) findViewById(R.id.addexam_total_marks_input);
        NoSub = (FrameLayout) findViewById(R.id.no_subjects_frame_exams);
        modifiedby = (TextView) findViewById(R.id.modified_by_textview);


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        ExamRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        CoscholasticRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Coscholastic");
        detailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");



        ExamRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                allexamlist.add(snapshot.child("examname").getValue(String.class).toLowerCase());


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
        //To retrieve subjectnames
        SubjectsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String subname = snapshot.child("subjectname").getValue(String.class);
                String subid = snapshot.getKey();
                subjectandsubids.put(subname,subid);
                Chip chip = new Chip(AddExamActivity.this);
                chip.setText(subname);
                chip.setCheckable(true);
                for(int j=0;j<selectedsubjects.length;j++)
                {
                    if(subname.equals(selectedsubjects[j]))
                    {
                        chip.setChecked(true);
                    }
                }

                subjectGroup.addView(chip);
                subjectcountchip++;

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

        CoscholasticRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String activityname = snapshot.child("activityname").getValue(String.class);
                coscolasticactivities.add(activityname);
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

        startDateListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                ExamStartDate.setText(dateFormat.format(myCalendar.getTime()));
                startyear = String.valueOf(year);
                startmonth = String.valueOf(month);
                startmonthandyear = startmonth+startyear;
            }
        };


        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                String myFormat="dd/MM/yyyy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                ExamEndDate.setText(dateFormat.format(myCalendar.getTime()));
            }
        };

        ExamStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(AddExamActivity.this, startDateListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();

            }
        });


        subjectGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = subjectGroup.findViewById(checkedId);
                String subid = chip.getText().toString();
                SubjectsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child(subid).exists())
                        {
                            Toast.makeText(AddExamActivity.this, "subject="+snapshot.child(subid).child("subjectname").getValue(String.class), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        coscholarCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    CoscholasticInput.setVisibility(View.VISIBLE);

                }
                else
                {
                    CoscholasticInput.setVisibility(View.GONE);

                }
            }
        });

        CoscholasticInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seletedCO.clear();
                String[] str = new String[coscolasticactivities.size()];

                for (int i = 0; i < coscolasticactivities.size(); i++) {
                    str[i] = coscolasticactivities.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(AddExamActivity.this);

                if(coscolasticactivities.isEmpty())
                {
                    builder.setTitle("No co-scholastic activities added, add one to see here");

                }
                else
                {
                    builder.setTitle("Select Co-scholastic Activities");
                }

                // set dialog non cancelable
                builder.setCancelable(true);
                boolean[] selectedLanguage = new boolean[str.length];

                builder.setMultiChoiceItems(str, selectedLanguage, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            seletedCO.add(str[i]);

                        } else {

                            seletedCO.remove(str[i]);
                        }
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String selectedactivites = seletedCO.toString();
                        selectedactivites = selectedactivites.substring(1,selectedactivites.length()-1);
                        CoscholasticInput.setText(selectedactivites);
                    }
                });
                builder.show();
            }
        });

        ExamEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog1 = new DatePickerDialog(AddExamActivity.this, endDateListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog1.show();
            }
        });

        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedSubjectIds.clear();
                selectedSubjectNames.clear();
                List<Integer> ids = subjectGroup.getCheckedChipIds();
                for (Integer id:ids){
                    Chip chip = subjectGroup.findViewById(id);
                    String subjectid = subjectandsubids.get(chip.getText().toString());
                    selectedSubjectNames.add(chip.getText().toString());
                    selectedSubjectIds.add(subjectid);


                }

                if(examid.equals(""))
                {
                    if(ExamName.getText().toString().isEmpty() || ExamStartDate.getText().toString().isEmpty() ||
                            ExamEndDate.getText().toString().isEmpty() || TotalMarks.getText().toString().isEmpty() || selectedSubjectNames.isEmpty())
                    {
                        Toast.makeText(AddExamActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                    }
                    else if(coscholarCheckBox.isChecked() && CoscholasticInput.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddExamActivity.this, "Select any co-scholar activities!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(CheckDuplicateExam(ExamName.getText().toString()))
                        {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = null,date2 = null;
                            try {
                                date1 = sdf.parse(ExamStartDate.getText().toString());
                                date2 = sdf.parse(ExamEndDate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long startmillis = date1.getTime(), endmillis=date2.getTime();

                            Log.d("millistart", String.valueOf(startmillis));
                            Log.d("milliend", String.valueOf(endmillis));
                            if(startmillis>endmillis)
                            {
                                Toast.makeText(AddExamActivity.this, "Enter a valid time period", Toast.LENGTH_SHORT).show();
                                Log.d("Error","Not valid time");
                            }
                            else
                            {
                                String subjects = selectedSubjectIds.toString();
                                String subjectnames = selectedSubjectNames.toString();
                                String selectedsubjects = subjects.substring(1,subjects.length()-1);
                                String selectedsubjectnames = subjectnames.substring(1,subjectnames.length()-1);
                                HashMap<String,String> exammap = new HashMap<>();
                                exammap.put("examname",ExamName.getText().toString().trim());
                                exammap.put("examstartdate",ExamStartDate.getText().toString());
                                exammap.put("examenddate",ExamEndDate.getText().toString());
                                exammap.put("startmonthandyear",startmonth+startyear);
                                exammap.put("timeinmilli",String.valueOf(startmillis));
                                exammap.put("classid",classid);
                                exammap.put("totalmarks",TotalMarks.getText().toString().trim());
                                exammap.put("subjects",selectedsubjectnames);
                                exammap.put("modifiedby",staffid);
                                if(coscholarCheckBox.isChecked())
                                {
                                    exammap.put("coscholastic",CoscholasticInput.getText().toString().trim());
                                }

                                ExamRef.child(classid).push().setValue(exammap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ExamName.setText("");
                                            ExamStartDate.setText("");
                                            ExamEndDate.setText("");
                                            CoscholasticInput.setText("");
                                            Toast.makeText(AddExamActivity.this, "Successfully created new exam!", Toast.LENGTH_SHORT).show();
                                            onBackPressed();

                                        }
                                        else
                                        {
                                            Toast.makeText(AddExamActivity.this, "Error:"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } 
                        }
                        else
                        {
                            Toast.makeText(AddExamActivity.this, "new There is already an exam with this name, Try a different name", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                }
                else
                {
                    if(ExamName.getText().toString().isEmpty() || ExamStartDate.getText().toString().isEmpty() ||
                            ExamEndDate.getText().toString().isEmpty() || TotalMarks.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddExamActivity.this, "Fill in all the fields", Toast.LENGTH_SHORT).show();
                    }
                    else if(coscholarCheckBox.isChecked() && CoscholasticInput.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddExamActivity.this, "Select any co-scholar activities!", Toast.LENGTH_SHORT).show();
                    }
                    else{



                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = null,date2 = null;
                            try {
                                date1 = sdf.parse(ExamStartDate.getText().toString());
                                date2 = sdf.parse(ExamEndDate.getText().toString());

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            long startmillis = date1.getTime(), endmillis=date2.getTime();

                            Log.d("millistart", String.valueOf(startmillis));
                            Log.d("milliend", String.valueOf(endmillis));
                            if(startmillis>endmillis)
                            {
                                Toast.makeText(AddExamActivity.this, "Enter a valid time period", Toast.LENGTH_SHORT).show();
                                Log.d("Error","Not valid time");
                            }
                            else
                            {
                                String subjects = selectedSubjectIds.toString();
                                String subjectnames = selectedSubjectNames.toString();
                                String selectedsubjects = subjects.substring(1,subjects.length()-1);
                                String selectedsubjectnames = subjectnames.substring(1,subjectnames.length()-1);

                                HashMap<String,String> exammap = new HashMap<>();
                                exammap.put("examname",ExamName.getText().toString());
                                exammap.put("examstartdate",ExamStartDate.getText().toString());
                                exammap.put("examenddate",ExamEndDate.getText().toString());
                                if(!startmonthandyear.isEmpty())
                                {
                                    exammap.put("startmonthandyear",startmonthandyear);
                                }
                                else
                                {
                                    exammap.put("startmonthandyear",startmonth+startyear);
                                }

                                exammap.put("classid",classid);
                                exammap.put("timeinmilli",String.valueOf(startmillis));
                                exammap.put("totalmarks",TotalMarks.getText().toString().trim());
                                exammap.put("subjects",selectedsubjectnames);
                                exammap.put("modifiedby",staffid);

                                if(coscholarCheckBox.isChecked())
                                {
                                    exammap.put("coscholastic",CoscholasticInput.getText().toString().trim());
                                }

                                ExamRef.child(classid).child(examid).setValue(exammap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            ExamName.setText("");
                                            ExamStartDate.setText("");
                                            ExamEndDate.setText("");
                                            CoscholasticInput.setText("");
                                            Toast.makeText(AddExamActivity.this, "Successfully updated exam!", Toast.LENGTH_SHORT).show();
                                            onBackPressed();

                                        }
                                        else
                                        {
                                            Toast.makeText(AddExamActivity.this, "Error:"+task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                        
                    }
                }


            }
        });



    }

    private boolean CheckDuplicateExamPlus(String toString) {
        Toast.makeText(this, "update"+allexamlist.toString(), Toast.LENGTH_SHORT).show();
        int count=0;
        for(int i=0;i<allexamlist.size();i++)
        {
            if(allexamlist.get(i).equals(toString))
            {
                count++;
            }
        }
        if(count==1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean CheckDuplicateExam(String exam)
    {
        if(allexamlist.contains(exam))
        {
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {

        super.onStart();

        SubjectsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0)
                {
                    NoSub.setVisibility(View.GONE);
                    subjectGroup.setVisibility(View.VISIBLE);
                }
                else
                {
                    NoSub.setVisibility(View.VISIBLE);
                    subjectGroup.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        CoscholasticInput.setVisibility(View.GONE);
        modifiedby.setVisibility(View.GONE);






            if(!classid.isEmpty())
            {
                ClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String classname = snapshot.child(classid).child("classname").getValue(String.class) + " " +
                                snapshot.child(classid).child("classsection").getValue(String.class);
                        SelectClass.setText(classname);
                        SelectClass.setEnabled(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }



        if(!(examname.equals("")))
        {
            modifiedby.setVisibility(View.VISIBLE);

            addExamtext.setText("Update Exam");
            addExamButton.setText("Update");
            ExamName.setText(examname);
            ExamStartDate.setText(examstartdate);
            ExamEndDate.setText(examenddate);
            TotalMarks.setText(totalmarks);
            ExamRef.child(classid).child(examid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("coscholastic").exists())
                    {
                        coscholarCheckBox.setChecked(true);
                        String coscholastic = snapshot.child("coscholastic").getValue(String.class);
                        CoscholasticInput.setText(coscholastic);

                    }
                    startmonthandyear = snapshot.child("startmonthandyear").getValue(String.class);
                    String staffmodified = snapshot.child("modifiedby").getValue(String.class);
                    modifiedby.setText("Last Modified by "+staffmodified);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }




    }
}