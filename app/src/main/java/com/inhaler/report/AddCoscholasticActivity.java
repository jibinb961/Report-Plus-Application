package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddCoscholasticActivity extends AppCompatActivity {

    private LinearLayout numericalSystem,alphaSystem;
    private EditText activityName,activitygradingSystem;
    private Button addActivity,deleteActivityButton;
    private DatabaseReference CoscholasticRef,CoscholasticMarksRef,ExamsRef;
    private FirebaseUser currentUser;
    private String uid,activityname,activitygrade,activityid;
    private TextView coscholasticHeading;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coscholastic);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        numericalSystem = (LinearLayout) findViewById(R.id.number_system);
        alphaSystem = (LinearLayout) findViewById(R.id.alphabetical_system);
        addActivity = (Button) findViewById(R.id.add_activity_ok_button);
        activitygradingSystem = (EditText) findViewById(R.id.coscholastic_grading_input);
        activityName = (EditText) findViewById(R.id.activity_name_input);
        deleteActivityButton = (Button) findViewById(R.id.delete_activity_button);
        CoscholasticRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Coscholastic");
        CoscholasticMarksRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("CoscholasticMarks");
        ExamsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Exams");

        coscholasticHeading = (TextView) findViewById(R.id.add_coscholastic_textview);
        backButton = (ImageView) findViewById(R.id.add_coscholastic_back_button);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        activitygradingSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AddCoscholasticActivity.this, activitygradingSystem);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.grading_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        // Toast message on menu item clicked
                        if(menuItem.getTitle().toString().equals("Numerical"))
                        {
                            alphaSystem.setVisibility(View.GONE);
                            activitygradingSystem.setText(menuItem.getTitle().toString());
                            numericalSystem.setVisibility(View.VISIBLE);
                        }
                        else if(menuItem.getTitle().toString().equals("Alphabetical"))
                        {
                            numericalSystem.setVisibility(View.GONE);
                            activitygradingSystem.setText(menuItem.getTitle().toString());
                            alphaSystem.setVisibility(View.VISIBLE);
                        }


                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activityid.equals(""))
                {
                    if(activityName.getText().toString().isEmpty() || activitygradingSystem.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddCoscholasticActivity.this, "Fill in all the input fields", Toast.LENGTH_SHORT).show();
                    }
                    
                    else
                    {
                        Pattern p = Pattern.compile(
                                "[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
                        Matcher m = p.matcher(activityName.getText().toString());

                        boolean res = m.find();
                        if(!res)
                        {
                            HashMap<String,String> activitymap = new HashMap<>();
                            activitymap.put("activityname",activityName.getText().toString().trim());
                            activitymap.put("activitygrade",activitygradingSystem.getText().toString().trim());

                            CoscholasticRef.push().setValue(activitymap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    activityName.setText("");
                                    activitygradingSystem.setText("");
                                    numericalSystem.setVisibility(View.GONE);
                                    alphaSystem.setVisibility(View.GONE);
                                    Toast.makeText(AddCoscholasticActivity.this, "Activity added successfully", Toast.LENGTH_SHORT).show();
                                    Intent dashboardintet = new Intent(AddCoscholasticActivity.this,CoscholasticActivity.class);
                                    startActivity(dashboardintet);
                                    finish();
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(AddCoscholasticActivity.this, "Activity name has special characters", Toast.LENGTH_SHORT).show();
                        }

                        

                    }
                }
                else
                {
                    if(activityName.getText().toString().isEmpty() || activitygradingSystem.getText().toString().isEmpty())
                    {
                        Toast.makeText(AddCoscholasticActivity.this, "Fill in all the input fields", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        HashMap<String,String> activitymap = new HashMap<>();
                        activitymap.put("activityname",activityName.getText().toString().trim());
                        activitymap.put("activitygrade",activitygradingSystem.getText().toString().trim());

                        CoscholasticRef.child(activityid).setValue(activitymap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                activityName.setText("");
                                activitygradingSystem.setText("");
                                numericalSystem.setVisibility(View.GONE);
                                alphaSystem.setVisibility(View.GONE);
                                Toast.makeText(AddCoscholasticActivity.this, "Activity updated successfully", Toast.LENGTH_SHORT).show();
                                Intent dashboardintet = new Intent(AddCoscholasticActivity.this,CoscholasticActivity.class);
                                startActivity(dashboardintet);
                                finish();
                            }
                        });

                    }
                }

            }
        });
        deleteActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddCoscholasticActivity.this);
                builder.setTitle("Delete Activity");
                builder.setMessage("This action will remove all this activity's references in exams! \n" +
                        "Are you sure you want to delete?");

                // set dialog non cancelable
                builder.setCancelable(true);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        CoscholasticRef.child(activityid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddCoscholasticActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        });
                    }
                });
                builder.show();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        numericalSystem.setVisibility(View.GONE);
        alphaSystem.setVisibility(View.GONE);
        deleteActivityButton.setVisibility(View.GONE);
        activityname = getIntent().getStringExtra("activityname");
        activitygrade = getIntent().getStringExtra("activitygrade");
        activityid = getIntent().getStringExtra("activityid");

        if(!(activityname.equals("")))
        {
            deleteActivityButton.setVisibility(View.VISIBLE);
            coscholasticHeading.setText("Update Coscholastic ");
            addActivity.setText("Update");
            activityName.setText(activityname);
            activitygradingSystem.setText(activitygrade);
            if(activitygrade.equals("Numerical"))
            {
                numericalSystem.setVisibility(View.VISIBLE);
            }
            else
            {
                alphaSystem.setVisibility(View.VISIBLE);
            }

        }


    }
}