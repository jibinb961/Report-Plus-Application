package com.inhaler.report;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;

public class RegisterAcitvity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button RegisterButton;
    private ProgressBar progressBar;
    private EditText emailInput,passwordInput,passwordConfirmInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);
        
        RegisterButton = (Button) findViewById(R.id.register_button);
        emailInput = (EditText) findViewById(R.id.email_address_input);
        passwordInput = (EditText) findViewById(R.id.password_input);
        passwordConfirmInput = (EditText) findViewById(R.id.password_confirm_input);
        progressBar = (ProgressBar) findViewById(R.id.register_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        
        
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailid = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmpassword = passwordConfirmInput.getText().toString().trim();
                if(emailid.isEmpty() || password.isEmpty())
                {
                    Toast.makeText(RegisterAcitvity.this , "Check the credentials again", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmpassword))
                {
                    Toast.makeText(RegisterAcitvity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    RegisterButton.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(emailid,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userid = user.getUid();
                                Intent dashboardintent= new Intent(RegisterAcitvity.this,UserUpdateActivity.class);
                                dashboardintent.putExtra("uid",userid);
                                startActivity(dashboardintent);
                                finish();
                            } else {
                                RegisterButton.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegisterAcitvity.this, "Authentication failed."+task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

        
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if(!(currentuser ==null))
        {
            Intent homeintetn = new Intent(RegisterAcitvity.this,DashboardActivity.class);
            startActivity(homeintetn);
        }
    }
}