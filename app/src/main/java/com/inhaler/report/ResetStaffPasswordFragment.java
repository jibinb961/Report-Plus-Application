package com.inhaler.report;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ResetStaffPasswordFragment extends DialogFragment {
    private Button changeButton;
    private FirebaseUser currentUser;
    private String uid,oldpass,staffkey;
    private EditText oldPassword,newPassword,confirmPassword;
    private DatabaseReference StaffRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reset_staff_view, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        staffkey = getArguments().getString("staffkey");
        StaffRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs");

        StaffRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oldpass = snapshot.child(staffkey).child("staffpassword").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        oldPassword = (EditText) view.findViewById(R.id.reset_old_password_input);
        newPassword = (EditText) view.findViewById(R.id.reset_new_password_input);
        changeButton = (Button) view.findViewById(R.id.staff_change_password_button);
        confirmPassword = (EditText) view.findViewById(R.id.reset_confirm_password_input);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpasswordentered = oldPassword.getText().toString().trim();
                String newpasswordentered = newPassword.getText().toString().trim();
                String confirmpasswordentered = confirmPassword.getText().toString().trim();
                if(oldpasswordentered.equals(oldpass))
                {
                    if(newpasswordentered.equals(confirmpasswordentered))
                    {
                        if(!newpasswordentered.equals(oldpass))
                        {
                            StaffRef.child(staffkey).child("staffpassword").setValue(newpasswordentered).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Toast.makeText(getContext(), "Successfully Changed password!", Toast.LENGTH_SHORT).show();
                                        getDialog().dismiss();
                                    }
                                }
                            });  
                        }
                        else
                        {
                            Toast.makeText(getContext(), "This is one of your old passwords, try new one!", Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                    else
                    {
                        Toast.makeText(getContext(), "New passwords are not matching!", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getContext(), "Old Password is incorrect! Try again", Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



    }
}
