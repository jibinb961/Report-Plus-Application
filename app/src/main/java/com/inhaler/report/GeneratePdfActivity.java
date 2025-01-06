package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.internal.VisibilityAwareImageButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentResultListener;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeneratePdfActivity extends AppCompatActivity implements PaymentResultListener {
    private DatabaseReference ClassesRef,SubjectsRef,MarksRef,StudentsRef,CoscholasticRef,DetailsRef,ExamsRef;
    private Button generateButton;
    private String classid,studentid,OrderID="";
    private Integer pdcount =0;
    private TextView SelectTerm,SelectExams;
    private JSONObject jsonObject;
    private ImageButton editButton,saveButton;
    private ProgressBar progressBar;
    private ArrayList<String> subs = new ArrayList<>();
    private TextView selectedExamsList,selectedCoExamsList;
    private ArrayList<String> terms = new ArrayList<>();
    private ProgressDialog progressDialog;
    private CheckBox coscholasticAdd;
    private String Remarks = "";
    private EditText AcademicYear,RemarksInput;
    private OkHttpClient okHttpClient;
    private RewardedAd mRewardedAd;
    private HashMap<String,String> pdfMap = new HashMap<>();
    private HashMap<String,String> secondmidtermmarks = new HashMap<>();
    private HashMap<String,String> secondtermmarks = new HashMap<>();
    private HashMap<String,String> activitymarksmap = new HashMap<>();
    private HashMap<String,String> allactivitymarks = new HashMap<>();
    private HashMap<String,String> firsttermmarks = new HashMap<>();
    private HashMap<String,String> firstmidtermmarks = new HashMap<>();
    private HashMap<String,String> studentinfo = new HashMap<>();
    private ArrayList<String> includedterms = new ArrayList<>();
    private ArrayList<String> examsinclass = new ArrayList<>();
    private ArrayList<String> xsubs = new ArrayList<>();
    private HashMap<String,String> in_info = new HashMap<>();
    private ArrayList<String> selectedExams = new ArrayList<>();
    private ArrayList<String> selectedCoExams = new ArrayList<>();
    private ArrayList<String> totalsubjects;
    private ChipGroup examChipGroup;
    private boolean isExpired = false;
    private HashMap<String,String> examnameandexamid = new HashMap<>();
    private HashMap<String,String> examcodes = new HashMap<>();
    private HashMap<String,String> allmarks = new HashMap<>();
    private String classname,teachername,studentname,rollnumber,fathername,studentweight,studentheight;
    private String[] classsubjects,classexams,classactivities;
    private HashMap<String,String> subjectsinexam = new HashMap<>();
    private EditText StudentName,StudentClass,StudentTeacher,StudentFather,StudentRollNumber;
    private FirebaseUser currentUser;
    private ArrayList<String> examsubjects = new ArrayList<>();
    private ArrayList<String> examactivities = new ArrayList<>();
    private String uid;
    private Integer examcount=0;
    private Checkout checkout;
    public String url= "https://us-central1-philoreport.cloudfunctions.net/hello";
    private ArrayList<String> possibleyears = new ArrayList<>();
    private LinearLayout backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_pdf);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        okHttpClient = new OkHttpClient();
        Checkout.preload(getApplicationContext());
        checkout = new Checkout();

        // ...
        checkout.setKeyID("rzp_live_JjA8WqfHWWOuVT");


        classid = getIntent().getStringExtra("classid");
        studentid = getIntent().getStringExtra("studentid");
        examcount = Integer.valueOf(getIntent().getStringExtra("examcount"));


        progressDialog = new ProgressDialog(GeneratePdfActivity.this);
        progressBar = (ProgressBar) findViewById(R.id.loading_pdf_progressBar);

        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        SubjectsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        MarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Marks");
        StudentsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        CoscholasticRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("CoscholasticMarks");
        DetailsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        examChipGroup = (ChipGroup) findViewById(R.id.exam_chip_group);
        RemarksInput = (EditText) findViewById(R.id.remarks_input);
        generateButton = (Button) findViewById(R.id.generate_pdf_button);
        generateButton.setVisibility(View.GONE);
        AcademicYear = (EditText) findViewById(R.id.generate_academic_year);
//        SelectTerm = (TextView)  findViewById(R.id.generate_terms_input_text);
        SelectExams = (TextView) findViewById(R.id.generate_coscholastic_exams_input);
        selectedExamsList =(TextView) findViewById(R.id.selected_exams_textview);
        backButton = (LinearLayout) findViewById(R.id.generate_pdf_back_button);
        coscholasticAdd = (CheckBox) findViewById(R.id.add_coscholastic_checkbox);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        selectedCoExamsList = (TextView) findViewById(R.id.selected_coscholastic_exams_textview);


        StudentClass = (EditText) findViewById(R.id.generate_student_class_textview);
        StudentName = (EditText) findViewById(R.id.generate_student_name_textview);
        StudentTeacher = (EditText) findViewById(R.id.generate_class_teacher_textview);
        StudentFather = (EditText) findViewById(R.id.generate_student_father_textview);
        StudentRollNumber = (EditText) findViewById(R.id.generate_student_rollnumber_textview);
        editButton = (ImageButton) findViewById(R.id.edit_student_info_button);
        saveButton = (ImageButton)findViewById(R.id.save_student_info_button);

        progressDialog.setTitle("Loading Exams");

        LoadExamChips();

        //function to get all marks from checked exam name



        DetailsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists())
                {
                    if(snapshot.child("PdfSettings").exists())
                    {
                        if(snapshot.child("PdfSettings").child("signature").exists())
                        {
                            pdfMap.put("signature","true");
                            pdfMap.put("signeename",snapshot.child("PdfSettings").child("signeename").getValue(String.class));
                            pdfMap.put("signeerole",snapshot.child("PdfSettings").child("signeerole").getValue(String.class));
                        }
                        else
                        {
                            pdfMap.put("signature","false");
                        }
                        String borderwidth = snapshot.child("PdfSettings").child("borderwidth").getValue(String.class);
                        String bordertype = snapshot.child("PdfSettings").child("bordertype").getValue(String.class);
                        String tableColorValues = snapshot.child("PdfSettings").child("tablecolor").getValue(String.class);
                        String coTableColorValues = snapshot.child("PdfSettings").child("cotablecolor").getValue(String.class);
                        String bgColorValues = snapshot.child("PdfSettings").child("fontcolor").getValue(String.class);
                        String logosize = snapshot.child("PdfSettings").child("logosize").getValue(String.class);


                        pdfMap.put("borderwidth",borderwidth);
                        pdfMap.put("bordertype",bordertype);
                        pdfMap.put("tablecolor",tableColorValues);
                        pdfMap.put("fontcolor",bgColorValues);
                        pdfMap.put("enabled","true");
                        pdfMap.put("cotablecolor",coTableColorValues);
                        pdfMap.put("logosize",logosize);
                    }
                    else
                    {
                        pdfMap.put("enabled","false");
                        pdfMap.put("signature","false");
                        pdfMap.put("borderwidth","1");
                        pdfMap.put("bordertype","Groove Border");
                        pdfMap.put("tablecolor","36,113,163");
                        pdfMap.put("fontcolor","255,255,255");
                        pdfMap.put("cotablecolor","34,129,8");
                        pdfMap.put("logosize","60");
                    }
                    String in_name = snapshot.child("schoolname").getValue(String.class);
                    String in_contact = snapshot.child("schoolcontact").getValue(String.class);
                    String in_address = snapshot.child("schooladdress").getValue(String.class);
                    String in_logo  = snapshot.child("schoollogo").getValue(String.class);
                    String abspath = snapshot.child("abspath").getValue(String.class);
                    in_info.put("schoolname",in_name);
                    in_info.put("schooladdress",in_address);
                    in_info.put("schoollogo",in_logo);
                    in_info.put("schoolcontact",in_contact);
                    in_info.put("abspath",abspath);


                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ClassesRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 classname = snapshot.child("classname").getValue(String.class)+snapshot.child("classsection").getValue(String.class);
                 teachername = snapshot.child("classteacher").getValue(String.class);

                StudentClass.setText(classname);
                StudentTeacher.setText(teachername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editButton.setVisibility(View.GONE);
                saveButton.setVisibility(View.VISIBLE);
                StudentName.setEnabled(true);
                StudentName.setFocusableInTouchMode(true);
                StudentRollNumber.setEnabled(true);
                StudentRollNumber.setFocusableInTouchMode(true);
                StudentFather.setEnabled(true);
                StudentFather.setFocusableInTouchMode(true);
                StudentTeacher.setEnabled(true);
                StudentTeacher.setFocusableInTouchMode(true);
                StudentClass.setEnabled(true);
                StudentClass.setFocusableInTouchMode(true);


            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StudentName.getText().toString().isEmpty() || StudentClass.getText().toString().isEmpty() ||
                    StudentFather.getText().toString().isEmpty() || StudentTeacher.getText().toString().isEmpty() )
                {
                    Toast.makeText(GeneratePdfActivity.this, "Fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    studentname = StudentName.getText().toString().trim();
                    classname = StudentClass.getText().toString().trim();
                    teachername = StudentTeacher.getText().toString().trim();
                    fathername = StudentFather.getText().toString().trim();
                    rollnumber = StudentRollNumber.getText().toString().trim();
                }
                saveButton.setVisibility(View.GONE);
                editButton.setVisibility(View.VISIBLE);
                StudentName.setEnabled(false);
                StudentName.setFocusable(false);
                StudentRollNumber.setEnabled(false);
                StudentRollNumber.setFocusable(false);
                StudentFather.setEnabled(false);
                StudentFather.setFocusable(false);
                StudentTeacher.setEnabled(false);
                StudentTeacher.setFocusable(false);
                StudentClass.setEnabled(false);
                StudentClass.setFocusable(false);

            }
        });
//
        StudentsRef.child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 studentname = snapshot.child("studentname").getValue(String.class);
                 rollnumber = snapshot.child("rollnumber").getValue(String.class);
                 fathername = snapshot.child("fathername").getValue(String.class);
                 studentweight = snapshot.child("studentweight").getValue(String.class);
                 studentheight = snapshot.child("studentheight").getValue(String.class);
                StudentName.setText(studentname);
                StudentFather.setText(fathername);
                StudentRollNumber.setText(rollnumber);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//
//
//
//
        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remarks = RemarksInput.getText().toString();
                LoadActivityMarks();

            }

        });

    }



    private void LoadExamChips() {
        final int[] count = {0}; //exam counter variable

        //Getting exam data from Firebase and populate chipGroup
        ExamsRef.child(classid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    String examname = snapshot.child("examname").getValue(String.class);
                    String examid = snapshot.getKey();
                    String exammonthandyear = snapshot.child("startmonthandyear").getValue(String.class);
                    String subjects[] = snapshot.child("subjects").getValue(String.class).split(",");
                    String coscholastics[] = {};
                    if(snapshot.child("coscholastic").exists())
                    {
                        coscholastics = snapshot.child("coscholastic").getValue(String.class).split(",");

                    }

                for (String coscholastic : coscholastics) {
                    if (!examactivities.contains(coscholastic.trim())) {
                        examactivities.add(coscholastic.trim());
                    }
                }
                    ///adding subjects in exams and checks for duplicate subject names
                    for (String subject : subjects) {
                        if (!examsubjects.contains(subject.trim())) {
                            examsubjects.add(subject.trim());
                        }
                    }

                    examnameandexamid.put(examname,examid);
                    Chip chip = new Chip(GeneratePdfActivity.this);
                    chip.setText(examname);
                    chip.setCheckable(true);
                    examChipGroup.addView(chip); //adding chips to chip group

                    count[0] = count[0] + 1; //incrementing counter variable to get the count

                    //if exams are finished loading, stop progress bar
                    if(count[0]==examcount)
                    {
                        generateButton.setVisibility(View.VISIBLE);
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
    
    

    private void LoadActivityMarks()
    {
        allmarks.clear();
        allactivitymarks.clear();
        CoscholasticRef.child(classid).child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(coscholasticAdd.isChecked())
                {


                    List<Integer> ids = examChipGroup.getCheckedChipIds();
                    for (Integer id:ids) {
                        Chip chip = examChipGroup.findViewById(id);
                        String examname = chip.getText().toString();
                        String examid = examnameandexamid.get(examname);
                        String activitymarks = "";
                        
                        

                        activitymarksmap.clear();
                        for(int i=0;i<examactivities.size();i++)
                        {
                            if(snapshot.child(examid).child(examactivities.get(i).trim()).exists())
                            {
                                activitymarks = snapshot.child(examid).child(examactivities.get(i).trim()).getValue(String.class);
                                if(activitymarks.equals("0,0") || activitymarks.equals("") || activitymarks.equals("0"))
                                {
                                    activitymarks = "-,-";
                                }

                            }
                            else
                            {
                                activitymarks = "-,-";
                            }
                            activitymarksmap.put(examactivities.get(i).trim(),activitymarks);
                        }
                        allactivitymarks.put(examname,activitymarksmap.toString());
                    }
                    LoadExamMarks();
                }
                else
                {

                    LoadExamMarks();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    

    private void LoadExamMarks()
    {
        allmarks.clear();
        MarksRef.child(classid).child(studentid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Integer> ids = examChipGroup.getCheckedChipIds();
                for (Integer id:ids) {
                    Chip chip = examChipGroup.findViewById(id);
                    String examname = chip.getText().toString();
                    String examid = examnameandexamid.get(examname);
                    String subjectmarks = "";
                    secondtermmarks.clear();
                    for(int i=0;i<examsubjects.size();i++)
                    {
                        if(snapshot.child(examid).child(examsubjects.get(i).trim()).exists())
                        {
                            subjectmarks = snapshot.child(examid).child(examsubjects.get(i).trim()).getValue(String.class);
                            if(subjectmarks.equals("0,0") || subjectmarks.equals(""))
                            {
                                subjectmarks = "-,-";
                            }
                            else if(subjectmarks.equals("?,?"))
                            {
                                subjectmarks="?,?";
                            }
                        }
                        else
                        {
                            subjectmarks = "-,-";
                        }
                        secondtermmarks.put(examsubjects.get(i).trim(),subjectmarks);
                    }
                    allmarks.put(examname,secondtermmarks.toString());
                }
                GenerateMarks();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void GenerateMarks() {
        final Activity activity = this;
        if(!isExpired)
        {
            generateButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            Map<String,String> mymap = new TreeMap<>(allmarks);
            String academic = AcademicYear.getText().toString().trim();
            studentinfo.put("studentname",studentname);
            studentinfo.put("classteacher",teachername);
            studentinfo.put("classname",classname);
            studentinfo.put("fathername",fathername);
            studentinfo.put("rollnumber",rollnumber);
            studentinfo.put("studentweight",studentweight);
            studentinfo.put("studentheight",studentheight);
            studentinfo.put("academicyear",academic);
            Intent intent = new Intent(GeneratePdfActivity.this,MainActivity.class);
            intent.putExtra("studentinfo",studentinfo);
            intent.putExtra("includedterms",includedterms);
            intent.putExtra("classid",classid);
            intent.putExtra("studentid",studentid);
            intent.putExtra("allmarks",allmarks);
            intent.putExtra("allactivemarks",allactivitymarks);
            intent.putExtra("classsubjects",classsubjects);
            intent.putExtra("schoolinfo",in_info);
            intent.putExtra("pdfsettings",pdfMap);
            intent.putExtra("remarks",Remarks);
            startActivity(intent);
        }
        else
        {
            new AlertDialog.Builder(GeneratePdfActivity.this)
                    .setTitle("Free Pdfs Limit Reached!")
                    .setMessage("Upgrade to Premium or watch an AD to get 5 more pdf creations ")

                    .setPositiveButton("Uprade", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //upgrade code

                            Intent preintent = new Intent(GeneratePdfActivity.this, PremiumActivity.class);
                            startActivity(preintent);


                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("Watch AD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.setTitle("Loading AD");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            LoadAD();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();


        }

    }

    private class GetAPIResponseTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                // Read response from the API
                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    return "Error: " + responseCode;
                }
                InputStream inputStream = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } catch (IOException e) {
                return e.getMessage();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // Show response as a Toast message
            String orderid = "";
            try {
                jsonObject = new JSONObject(result);
                orderid = jsonObject.get("id").toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            finally {
                try {
                                                    JSONObject options = new JSONObject();

                                                    options.put("name", "Report Plus");
                                                    options.put("description", "Report Plus Premium Package");
                                                    options.put("image", "https://firebasestorage.googleapis.com/v0/b/philoreport.appspot.com/o/ic_launcher_foreground.png?alt=media&token=6475d1df-4164-4525-85f8-2e13315f0112");
                                                    options.put("order_id", orderid);//from response of step 3.
                                                    options.put("theme.color", "#3399cc");
                                                    progressDialog.dismiss();

                                                    checkout.open(GeneratePdfActivity.this, options);

                                                } catch(Exception e) {
                                                    Log.e(TAG, "Error in starting Razorpay Checkout", e);
                                                }
            }

        }
    }


    private void LoadAD() {
        AdRequest adRequest = new AdRequest.Builder().build();
        //ca-app-pub-8836288417309387/6214862471
        RewardedAd.load(this, "ca-app-pub-8836288417309387/6214862471",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("ErrorAd", loadAdError.toString());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("ErrorAd", "Ad was loaded.");
                        if (mRewardedAd != null) {
                            progressDialog.dismiss();
                            Activity activityContext = GeneratePdfActivity.this;
                            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                                @Override
                                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                    // Handle the reward.
                                    Log.d("ErrorAd", "The user earned the reward.");
                                    int rewardAmount = rewardItem.getAmount();
                                    Toast.makeText(activityContext, "Congrats, you've earned "+String.valueOf(rewardAmount)+" more pdf creations!", Toast.LENGTH_SHORT).show();
                                    pdcount = pdcount - rewardAmount;
                                    DetailsRef.child("pdfcount").setValue(String.valueOf(pdcount)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            generateButton.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.VISIBLE);
                                            Map<String,String> mymap = new TreeMap<>(allmarks);
                                            String academic = AcademicYear.getText().toString().trim();
                                            studentinfo.put("studentname",studentname);
                                            studentinfo.put("classteacher",teachername);
                                            studentinfo.put("classname",classname);
                                            studentinfo.put("fathername",fathername);
                                            studentinfo.put("rollnumber",rollnumber);
                                            studentinfo.put("studentweight",studentweight);
                                            studentinfo.put("studentheight",studentheight);
                                            studentinfo.put("academicyear",academic);
                                            Intent intent = new Intent(GeneratePdfActivity.this,MainActivity.class);
                                            intent.putExtra("studentinfo",studentinfo);
                                            intent.putExtra("includedterms",includedterms);
                                            intent.putExtra("classid",classid);
                                            intent.putExtra("studentid",studentid);
                                            intent.putExtra("allmarks",allmarks);
                                            intent.putExtra("allactivemarks",allactivitymarks);
                                            intent.putExtra("classsubjects",classsubjects);
                                            intent.putExtra("schoolinfo",in_info);
                                            intent.putExtra("pdfsettings",pdfMap);
                                            intent.putExtra("remarks",Remarks);
                                            startActivity(intent);
                                            String rewardType = rewardItem.getType();
                                        }
                                    });


                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(GeneratePdfActivity.this, "Coundn't Load Ad!", Toast.LENGTH_SHORT).show();
                            Log.d("ErrorAd", "The rewarded ad wasn't ready yet.");
                        }
                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.GONE);
        selectedExams.clear();
        selectedExamsList.setVisibility(View.GONE);
        selectedExams.clear();
        subjectsinexam.clear();
        examsinclass.clear();
        selectedCoExamsList.setVisibility(View.GONE);
        includedterms.clear();
        saveButton.setVisibility(View.GONE);
        StudentName.setEnabled(false);
        StudentName.setFocusable(false);
        StudentRollNumber.setEnabled(false);
        StudentRollNumber.setFocusable(false);
        StudentFather.setEnabled(false);
        StudentFather.setFocusable(false);
        StudentTeacher.setEnabled(false);
        StudentTeacher.setFocusable(false);
        StudentClass.setEnabled(false);
        StudentClass.setFocusable(false);
        generateButton.setVisibility(View.VISIBLE);
        SelectExams.setVisibility(View.GONE);
        
        ExamsRef.child(classid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {
                    Toast.makeText(GeneratePdfActivity.this, "No exams are assigned to this student's class!", Toast.LENGTH_SHORT).show(); 
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        DetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("activationkey").exists())
                {
                    if(snapshot.child("pdfcount").exists())
                    {
                        String count = snapshot.child("pdfcount").getValue(String.class);
                        pdcount = Integer.parseInt(count);
                        if(pdcount > 0)
                        {
                            isExpired = true;

                        }
                        else
                        {
                            isExpired = false;
                        }
                    }
                }
                if(!snapshot.child("schoollogo").exists())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GeneratePdfActivity.this);

                    // set title
                    builder.setTitle("Warning!");
                    builder.setMessage("No Logo has been uploaded. For better look, upload your institution logo!");
                    builder.setPositiveButton("Later!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    builder.setNegativeButton("Upload Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(GeneratePdfActivity.this,EditInfoActivity.class);
                            intent.putExtra("staffkey","");
                            startActivity(intent);
                        }
                    });
                    builder.show();
                }



                
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Completed Successfully, Enjoy Premium Features"+s, Toast.LENGTH_SHORT).show();
        DetailsRef.child("activationkey").setValue(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed, Try again!", Toast.LENGTH_SHORT).show();

    }



}