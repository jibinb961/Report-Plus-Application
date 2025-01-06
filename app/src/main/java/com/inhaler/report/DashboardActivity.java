package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity implements PaymentResultListener {
    private CardView EditInfoButton,premiumOptionCard;
    private ImageButton LogoutButton;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private LinearLayout AddSubject,AddClass,AddStudent,AddMarks,GeneratePDF,AboutInfo,Coscholastic,About,Tutorial,AddStaff,StudentAnalytics;
    private DatabaseReference detailsref,subref,examref,studentsref,classref,feedBackRef;
    private LottieAnimationView refreshAnim;
    private ImageView refreshImage;
    private JSONObject jsonObject;
    private String uid,SchoolName,UserType,countrypartialcode;
    private TextView SubCount;
    private boolean isActivated = false;
    private TextView ClassCount;
    private ProgressDialog progressDialog;
    private Button tryPremiumButton,sentFeedbackButton;
    private Checkout checkout;
    private EditText feedbackInput;
    private TextView StuCount;
    public String url= "https://us-central1-philoreport.cloudfunctions.net/hello";
    private TextView InstitutionName;
    private HorizontalScrollView horizontalScrollView;
    private Integer StudentCount=0,ExamCount=0,ClassesCount=0,SubjectCount=0;
    Bitmap bitmap,signmap;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Checkout.preload(getApplicationContext());
        checkout = new Checkout();

        // ...
        checkout.setKeyID("rzp_live_JjA8WqfHWWOuVT");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();
        detailsref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Details");
        classref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        studentsref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Students");
        examref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");
        subref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Subjects");
        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizaontal_scrollview);
        premiumOptionCard = (CardView) findViewById(R.id.premium_option_card);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Payment Gateway");


        horizontalScrollView.postDelayed(new Runnable() {
            public void run() {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);

            }
        }, 10L);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            if (checkPermissionR()) {
//
//            } else {
//                requestPermisson();
//            }
        }else
        {
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
        }

        AddSubject = (LinearLayout) findViewById(R.id.add_subject);
        //AddClass = (LinearLayout) findViewById(R.id.addClass_cardView);
        AddStudent = (LinearLayout) findViewById(R.id.add_student);
        AddStaff = (LinearLayout) findViewById(R.id.addStaff_linear);
        AddMarks = (LinearLayout) findViewById(R.id.add_marks);
        GeneratePDF = (LinearLayout) findViewById(R.id.generate_pdf_card);
        EditInfoButton = (CardView) findViewById(R.id.edit_school_info);
        Coscholastic = (LinearLayout) findViewById(R.id.coscholasatic_card);
        feedbackInput = (EditText) findViewById(R.id.feedback_input);
        sentFeedbackButton = (Button) findViewById(R.id.send_feedback_button);
        StuCount = (TextView) findViewById(R.id.student_count);
        Tutorial = (LinearLayout) findViewById(R.id.tutorial_card);
        SubCount = (TextView) findViewById(R.id.subjects_count);
        ClassCount = (TextView) findViewById(R.id.classes_count);
        InstitutionName = (TextView) findViewById(R.id.institution_name);
        StudentAnalytics = (LinearLayout) findViewById(R.id.student_analytics_linear_layout);
        mAuth = FirebaseAuth.getInstance();
        refreshAnim = (LottieAnimationView) findViewById(R.id.refresh_animation);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        adView = (AdView) findViewById(R.id.ad_view_banner);
        About = (LinearLayout) findViewById(R.id.about_card);
    tryPremiumButton = (Button) findViewById(R.id.try_premium_button);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        sentFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback  = feedbackInput.getText().toString();
                if(!feedback.isEmpty())
                {
                    detailsref.child("Feedback").push().child("Feedback").setValue(feedback).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(DashboardActivity.this, "Thank you for the feedback, We'll review it and get back to you!", Toast.LENGTH_SHORT).show();
                            feedbackInput.setText("");
                        }
                    });
                }
                else
                {
                    Toast.makeText(DashboardActivity.this, "Type something, we are eagerly waiting for your feedback!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addstudentIntent = new Intent(DashboardActivity.this,StudentsActivity.class);
                startActivity(addstudentIntent);
            }
        });

        StudentAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActivated)
                {
                    Intent studentanalytics = new Intent(DashboardActivity.this,StudentAnalyticsActivity.class);
                    startActivity(studentanalytics);

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);

                    // set title
                    builder.setTitle("Premium Feature!");
                    builder.setMessage("This feature is only available for premium users!");
                    builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent preintent = new Intent(DashboardActivity.this, PremiumActivity.class);
                            startActivity(preintent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            }
        });

    tryPremiumButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent preintent = new Intent(DashboardActivity.this, PremiumActivity.class);
            startActivity(preintent);

            //new GetAPIResponseTask().execute(url);

        }
    });

        Coscholastic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addCoscholasticIntent = new Intent(DashboardActivity.this,CoscholasticActivity.class);
                startActivity(addCoscholasticIntent) ;
            }
        });

        AddMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addmarksIntent = new Intent(DashboardActivity.this,AddMarksActivity.class);
                startActivity(addmarksIntent);
            }
        });

        AddStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isActivated)
                {
                    Intent addStaffIntent = new Intent(DashboardActivity.this,StaffsActivity.class);
                    startActivity(addStaffIntent);
                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);

                    // set title
                    builder.setTitle("Premium Feature!");
                    builder.setMessage("This feature is only available for premium users!");
                    builder.setPositiveButton("Upgrade", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Intent preintent = new Intent(DashboardActivity.this, PremiumActivity.class);
                            startActivity(preintent);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }

            }
        });

//        AboutInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent aboutintent = new Intent(DashboardActivity.this,ExamsActivity.class);
//                startActivity(aboutintent);
//            }
//        });

        refreshAnim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAnim.playAnimation();
                refreshAnalytics();

            }
        });

        Tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,PdfSettingsActivity.class);
                startActivity(intent);


            }
        });


        AddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(DashboardActivity.this, SubjectActivity.class));
//                overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
                Intent subjectIntent = new Intent(DashboardActivity.this,SubjectActivity.class);;
                startActivity(subjectIntent);
            }
        });

        GeneratePDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent generatepdf = new Intent(DashboardActivity.this,PDFClassActivity.class);
                startActivity(generatepdf);
            }
        });


//        AddClass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent addclassIntent = new Intent(DashboardActivity.this, ClassesActivity.class);;
//                startActivity(addclassIntent);
//            }
//        });

        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,OnboardingActivity.class);
                startActivity(intent);

            }
        });


        detailsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("schoollogo").exists())
                {
                    if(snapshot.child("PdfSettings").child("signature").exists())
                    {
                        String sign = snapshot.child("PdfSettings").child("signature").getValue(String.class);
                        signmap = returnBitMap(sign,"sign"  );
                    }

                    String logo = snapshot.child("schoollogo").getValue(String.class);
                    String schoolname = snapshot.child("schoolname").getValue(String.class);
                    bitmap = returnBitMap(logo,schoolname);

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this,EditInfoActivity.class);
                intent.putExtra("staffkey","");
                startActivity(intent);
            }
        });

        //checking if user email is verified
        if(!(firebaseUser.isEmailVerified()))
        {
            new AlertDialog.Builder(DashboardActivity.this)
                    .setTitle("Warning")
                    .setMessage("Email address not verified! \n\nA verification link will be sent to your email id, check in spam folder also :( " +
                            "\n\nAlready verified? \nTry logging out and login")
                    .setIcon(R.drawable.ic_baseline_error)

                    .setPositiveButton("Sent", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(DashboardActivity.this, "A verification email has been sent to your email id, please verify it", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();




        }



    }




    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(DashboardActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DashboardActivity.this, new String[] { permission }, requestCode);
        }
        else {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    boolean checkPermissionR() {

        return Environment.isExternalStorageManager();


    }

    @Override
    protected void onStart() {
        super.onStart();

        horizontalScrollView.postDelayed(new Runnable() {
            public void run() {
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_LEFT);

            }
        }, 1000L);



        detailsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    Intent userupdateintent = new Intent(DashboardActivity.this,UserUpdateActivity.class);
                    userupdateintent.putExtra("uid",uid);
                    startActivity(userupdateintent);
                }
                else
                {
                    if(snapshot.child("activationkey").exists())
                    {
                        premiumOptionCard.setVisibility(View.GONE);
                        adView.setVisibility(View.GONE);
                        isActivated= true;
                    }
                    else
                    {
                        premiumOptionCard.setVisibility(View.VISIBLE);
                        adView.setVisibility(View.VISIBLE);
                        isActivated = false;
                    }
                    String name = snapshot.child("schoolname").getValue(String.class);
                    countrypartialcode = snapshot.child("countrypartialcode").getValue(String.class);
                    InstitutionName.setText(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        refreshAnalytics();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public Bitmap returnBitMap(final String url, final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL imageurl = null;
                try {
                    imageurl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection)imageurl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is );
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    File mypath;
                    if(name.equals("sign"))
                    {
                        mypath=new File(directory,uid+"_sign.png");
                    }
                    else
                    {
                        mypath=new File(directory,uid+".png");
                    }

                    FileOutputStream fos = null;

                    try {
                        fos = new FileOutputStream(mypath);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return bitmap; }


    public void refreshAnalytics()
    {

        classref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ClassesCount = Math.toIntExact(snapshot.getChildrenCount());
                ClassCount.setText(String.valueOf(ClassesCount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        subref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SubjectCount = Math.toIntExact(snapshot.getChildrenCount());
                SubCount.setText(String.valueOf(SubjectCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        studentsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StudentCount = Math.toIntExact(snapshot.getChildrenCount());
                StuCount.setText(String.valueOf(StudentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


//

                    checkout.open(DashboardActivity.this, options);

                } catch(Exception e) {
                    Log.e(TAG, "Error in starting Razorpay Checkout", e);
                }
                finally {
                    progressDialog.dismiss();
                }
            }

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Completed Successfully, Enjoy Premium Features"+s, Toast.LENGTH_SHORT).show();
        detailsref.child("activationkey").setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed, Try again!", Toast.LENGTH_SHORT).show();

    }

}