package com.inhaler.report;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class DeleteAccountFragment extends DialogFragment {
    private FirebaseUser currentUser;
    private String uid,AdminPIN;
    private Button deleteButton;
    private EditText emailInput,passwordInput,adminpinInput;
    private DatabaseReference UserRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_account_view, container, false);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        emailInput = (EditText) view.findViewById(R.id.delete_email_input);
        passwordInput = (EditText) view.findViewById(R.id.delete_password_input);
        deleteButton = (Button) view.findViewById(R.id.delete_account_ok_button);
        adminpinInput = (EditText) view.findViewById(R.id.delete_admin_pin_input); 
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        UserRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Details").child("schoolpin").exists())
                {
                    AdminPIN = snapshot.child("Details").child("schoolpin").getValue(String.class);

                }
                else
                {
                    AdminPIN = "notset";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String adminpin = adminpinInput.getText().toString().trim();
                if(adminpin.equals(AdminPIN))
                {
                    if (!(email.isEmpty() || password.isEmpty())) {
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(email, password);
                        currentUser.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                String uid = currentUser.getUid();
                                UserRef.child(uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                                        final StorageReference uploader = firebaseStorage.getReference(uid);
                                        firebaseStorage.getReference().child(uid).delete();
                                        currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(getContext(), "Successfully deleted account", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getActivity(),LoginActivity.class);
                                                startActivity(intent);
                                            }
                                        });

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Admin PIN is wrong! Try again", Toast.LENGTH_SHORT).show();
                }
                
            }
        });


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        

    }
}
