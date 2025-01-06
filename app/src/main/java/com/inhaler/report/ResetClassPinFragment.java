package com.inhaler.report;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ResetClassPinFragment extends DialogFragment {

    TextView classpinheading;
    EditText classpininput;
    Button updateButton,cancelbutton;
    private DatabaseReference Classes;
    private FirebaseUser currentUser;
    String uid,classid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        Classes = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        View view = inflater.inflate(R.layout.class_pin_layout, container, false);
        classpinheading = (TextView) view.findViewById(R.id.class_pin_heading);
        classpininput = (EditText) view.findViewById(R.id.enter_class_pin_input);
        updateButton = (Button) view.findViewById(R.id.enter_class_pin_button);
        cancelbutton = (Button) view.findViewById(R.id.cancel_class_pin_button);
        classpinheading.setText("Reset Class PIN");
        updateButton.setText("Update");
        cancelbutton.setEnabled(true);

        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(classpininput.getText().toString().length()!=4)
                {
                    Toast.makeText(getContext(), "Enter 4 digit PIN", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Classes.child(classid).child("classpin").setValue(classpininput.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(), "Reset PIN completed successfully!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        classid = getArguments().getString("classid");
    }
}