package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private EditText EmailInput, PasswordInput;
    private Button LoginButton;
    private FirebaseAuth mAuth;
    private DatabaseReference DetailsRef,ParentsRef;
    private ProgressBar progressBar;
    private String currentuser;
    private FirebaseAuth Auth;
    private TextView forgotPassword,NewUserButton,ParentLogin;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        EmailInput = (EditText) findViewById(R.id.emailInput);
        PasswordInput = (EditText) findViewById(R.id.passwordInput);
        LoginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (ProgressBar) findViewById(R.id.progessingbar);
        progressBar.setVisibility(View.INVISIBLE);
        NewUserButton = (TextView) findViewById(R.id.new_user_button);
        DetailsRef = FirebaseDatabase.getInstance().getReference();
        ParentsRef = FirebaseDatabase.getInstance().getReference().child("Parents");
        forgotPassword = (TextView) findViewById(R.id.forgot_password_text);

        mAuth = FirebaseAuth.getInstance();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = EmailInput.getText().toString().trim();
                String password = PasswordInput.getText().toString().trim();

                if(email.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(LoginActivity.this, "Fill the fields", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    NewUserButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    LoginButton.setVisibility(View.GONE);
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String uid = mAuth.getCurrentUser().getUid().toString();

                                Toast.makeText(LoginActivity.this, "Logged in Successfully",   Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this,UserSelectorActivity.class);
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(homeIntent);
                                finish();

                            }
                            else
                            {
                                NewUserButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                LoginButton.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Sorry wrong password", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }


            }
        });





        NewUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerintent = new Intent(LoginActivity.this,RegisterAcitvity.class);
                startActivity(registerintent);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    showRecoverPasswordDialog();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        NewUserButton.setVisibility(View.VISIBLE);
        LoginButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        if(mAuth.getCurrentUser()!=null)
        {
            SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            String s1 = sh.getString("admin_pin", "");
            String s2 = sh.getString("staff_pin","");
            String staffkey = sh.getString("staff_key","");
            if(!s1.equals(""))
            {
                Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(LoginActivity.this,DashboardActivity.class);
                homeIntent.putExtra("user","admin");
                startActivity(homeIntent);
                finish();
            }
            else if(!s2.equals(""))
            {
                Toast.makeText(this, "Welcome, "+s2, Toast.LENGTH_SHORT).show();
                Intent homeIntent = new Intent(LoginActivity.this,StaffDashboardActivity.class);
                homeIntent.putExtra("userid",s2);
                homeIntent.putExtra("staffkey",staffkey);
                startActivity(homeIntent);
                finish();
            }
            else
            {

                Intent userSelectorIntent = new Intent(LoginActivity.this, UserSelectorActivity.class);
                startActivity(userSelectorIntent);
            }




        }



    }
    ProgressDialog loadingBar;



    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Recover Password");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setHint("Enter your email address");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();

                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(LoginActivity.this,"Done sent",Toast.LENGTH_LONG).show();
                }
                else {
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(LoginActivity.this,e.getMessage().trim(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }
}