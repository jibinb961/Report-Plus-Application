package com.inhaler.report;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class ExamsFragment extends Fragment {

    private FirebaseUser currentUser;
    private String uid;
    private RecyclerView recyclerView;
    private DatabaseReference ExamsRef,StudentsRef,MarksRef,CoscholasticMarksRef;
    private String classid ="",staffkey="",staffid="";
    private Integer childrencount = 0,cochildrencount=0;
    private Spinner sortSpinner;
    private TextView noExams;
    private  HashMap<String,String> hm = new HashMap<>();
    private ImageButton backButton,addExaBtn;
    private View ExamsView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> sortItems,months;
    private ProgressDialog progressDialog;

    public ExamsFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ExamsView = inflater.inflate(R.layout.fragment_exams,container,false);
        sortItems = new ArrayList<>();
        months = new ArrayList<>();
        sortItems.add("Sort By Time");
        sortItems.add("Filter By Month");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        CoscholasticMarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("CoscholasticMarks");
        ExamsRef.keepSynced(true);
        recyclerView = (RecyclerView)  ExamsView.findViewById(R.id.exams_fragment_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        sortSpinner = (Spinner) ExamsView.findViewById(R.id.sort_spinner);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, sortItems);
        sortSpinner.setAdapter(adapter);

        noExams = (TextView) ExamsView.findViewById(R.id.exams_fragment_no_exams);
        addExaBtn = (ImageButton) ExamsView.findViewById(R.id.staff_add_exam_button);


        int yearSelected;
        int monthSelected;

//Set default values
        Calendar calendar = Calendar.getInstance();
        yearSelected = calendar.get(Calendar.YEAR);
        monthSelected = calendar.get(Calendar.MONTH);

        MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                .getInstance(monthSelected, yearSelected);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getSelectedItem().toString();
                if(selected.equals("Sort By Time"))
                {
                    LoadExamData("examname","");
                }
                else if(selected.equals("Filter By Month"))
                {
                    dialogFragment.show(getChildFragmentManager(),null);

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addExaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addstudent = new Intent(container.getContext(),AddExamActivity.class);
                addstudent.putExtra("examname","");
                addstudent.putExtra("examstartdate","");
                addstudent.putExtra("examenddate","");
                addstudent.putExtra("coactivities","");
                addstudent.putExtra("examid","");
                addstudent.putExtra("staffkey",staffkey);
                addstudent.putExtra("staffid",staffid);
                addstudent.putExtra("classid",classid);
                addstudent.putExtra("totalmarks","");
                addstudent.putExtra("subjects","");

                startActivity(addstudent);

            }
        });




        dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int monthOfYear) {
                String month = String.valueOf(monthOfYear);
                String yearselected = String.valueOf(year);
                LoadExamData("monthandyear",month+yearselected);
            }
        });




        return ExamsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        noExams.setVisibility(View.INVISIBLE);

        classid = getArguments().getString("classid");
        staffkey = getArguments().getString("staffkey");
        staffid = getArguments().getString("staffid");




    }

    private void LoadExamData(String examname,String monthandyear) {
        Query query = null;
        if(examname.equals("examname"))
        {
            query = ExamsRef.child(classid).orderByChild("timeinmilli");
        }
        else if(examname.equals("monthandyear"))
        {
            query = ExamsRef.child(classid).orderByChild("startmonthandyear").equalTo(monthandyear);
        }
        else
        {
            query = ExamsRef.child(classid).orderByChild(examname);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noExams.setVisibility(View.VISIBLE);
                    noExams.setText("No exams are created, click on '+' below to add a" +
                            " new exam to this class!");
                    noExams.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }

//                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Exams> options =
                new FirebaseRecyclerOptions.Builder<Exams>()
                        .setQuery(query,Exams.class)
                        .build();
        FirebaseRecyclerAdapter<Exams,UserViewHolder> adapter =
                new FirebaseRecyclerAdapter<Exams, UserViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Exams model) {

                        final String key = getRef(position).getKey();
                        holder.examname.setText(model.getExamname());
                        holder.examperiod.setText(model.getExamstartdate()+" - "+model.getExamenddate());
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent termIntent = new Intent(getContext(),EnterStudentMarksActivity.class);
                                termIntent.putExtra("staffid",staffid);
                                termIntent.putExtra("staffkey",staffkey);
                                termIntent.putExtra("classid",classid);
                                termIntent.putExtra("examid",key);
                                termIntent.putExtra("subjects",model.getSubjects());
                                termIntent.putExtra("totalmarks",model.getTotalmarks());
                                termIntent.putExtra("childrencount",String.valueOf(childrencount));
                                startActivity(termIntent);

                            }
                        });

                        holder.moreoptions.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                PopupMenu popupMenu = new PopupMenu(getContext(),holder.moreoptions);


                                // Inflating popup menu from popup_menu.xml file
                                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                                if(model.getCoscholastic()==null)
                                {
                                    popupMenu.getMenu().removeItem(R.id.add_co_marks);
                                }
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        if(menuItem.getItemId()==R.id.delete_exam)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                            builder.setTitle("Delete Exam");
                                            builder.setMessage("This action will remove all the exam references in classes and exam marks! \n" +
                                                    "Are you sure you want to delete?");

                                            // set dialog non cancelable
                                            builder.setCancelable(true);
                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    ExamsRef.child(classid).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                                    deleteAllExamRef(key);
                                                }
                                            });
                                            builder.show();
                                        }
                                        else if(menuItem.getItemId()==R.id.edit_exam)
                                        {

                                            Intent addexamintent = new Intent(getContext(),AddExamActivity.class);
                                            addexamintent.putExtra("examname",model.getExamname());
                                            addexamintent.putExtra("examstartdate",model.getExamstartdate());
                                            addexamintent.putExtra("examenddate",model.getExamenddate());
                                            addexamintent.putExtra("examid",key);
                                            addexamintent.putExtra("staffid",staffid);
                                            addexamintent.putExtra("classid",model.getClassid());
                                            addexamintent.putExtra("staffkey",staffkey);
                                            addexamintent.putExtra("totalmarks",model.getTotalmarks());
                                            addexamintent.putExtra("subjects",model.getSubjects());
                                            addexamintent.putExtra("coactivities","");

                                            startActivity(addexamintent);
                                        }
                                        else if(menuItem.getItemId()==R.id.add_co_marks)
                                        {
                                            Intent comarksIntent = new Intent(getContext(),EnterCoscholasticActivity.class);
                                            comarksIntent.putExtra("staffid",staffid);
                                            comarksIntent.putExtra("staffkey",staffkey);
                                            comarksIntent.putExtra("classid",classid);
                                            comarksIntent.putExtra("examid",key);
                                            comarksIntent.putExtra("activities",model.getCoscholastic());
                                            comarksIntent.putExtra("totalmarks",model.getTotalmarks());
                                            comarksIntent.putExtra("childrencount",String.valueOf(childrencount));
                                            startActivity(comarksIntent);
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
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_layout,parent,false);
                        UserViewHolder userViewHolder = new UserViewHolder(view);
                        return userViewHolder;
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
//        progressDialog.dismiss();
        LoadStudentCount();
    }

    private void deleteAllExamRef(String examid) {

        MarksRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.child(examid).exists())
                {
                    MarksRef.child(classid).child(snapshot.getKey()).child(examid).removeValue();
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

    public void LoadStudentCount()
    {
        MarksRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                childrencount = Math.toIntExact(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public static class UserViewHolder extends RecyclerView.ViewHolder
    {

        TextView examname,examperiod;
        ImageButton moreoptions;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;

            examname = itemView.findViewById(R.id.exam_name);
            examperiod = itemView.findViewById(R.id.exam_period);
            moreoptions = itemView.findViewById(R.id.options);
        }
    }


}