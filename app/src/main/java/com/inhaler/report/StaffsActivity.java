package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class StaffsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference StaffsRef;
    private ProgressDialog progressDialog;
    private Button addStaffBtn;
    private FirebaseUser currentUser;
    private String uid;
    private EditText findStaffinput;
    private TextView nostaff;
    private LinearLayout backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staffs);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        StaffsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Staffs");
        StaffsRef.keepSynced(true);

        recyclerView = (RecyclerView)  findViewById(R.id.staffs_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backButton = (LinearLayout) findViewById(R.id.staffs_back_button);
        findStaffinput = (EditText) findViewById(R.id.find_staff_input);
        addStaffBtn = (Button) findViewById(R.id.add_staff_button_staffs);
        nostaff = (TextView) findViewById(R.id.staffs_no_staffs_text);


        addStaffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent staffintent = new Intent(StaffsActivity.this,AddStaffActivity.class);
                staffintent.putExtra("staffkey","");
                staffintent.putExtra("staffname","");
                staffintent.putExtra("staffid","");
                staffintent.putExtra("classes","");
                startActivity(staffintent);
            }
        });


        findStaffinput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

                if (s.toString()!=null)
                {
                    LoadStaffData(s.toString());

                }
                else
                {
                    LoadStaffData("");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        LoadStaffData("");
    }

    private void LoadStaffData(String data) {
        nostaff.setVisibility(View.GONE);
        Query query = StaffsRef.orderByChild("staffname").startAt(data).endAt(data+"\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    nostaff.setText("No staffs are added, \n click on the below 'Add' button to add a new staff");
                    nostaff.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    nostaff.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseRecyclerOptions<Staffs> options =
                new FirebaseRecyclerOptions.Builder<Staffs>()
                        .setQuery(query,Staffs.class)
                        .build();

        FirebaseRecyclerAdapter<Staffs,StaffViewHolder> adapter =
                new FirebaseRecyclerAdapter<Staffs, StaffViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull StaffViewHolder holder, int position, @NonNull Staffs model) {
                        final String key = getRef(position).getKey();
                        holder.studentname.setText(model.getStaffname());
                        holder.rollnumber.setText("id : " +model.getStaffid());
                        holder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent staffintent = new Intent(StaffsActivity.this,AddStaffActivity.class);
                                staffintent.putExtra("staffkey",key);
                                staffintent.putExtra("staffname",model.getStaffname());
                                staffintent.putExtra("staffid",model.getStaffid());
                                staffintent.putExtra("staffsalary",model.getStaffsalary());
                                staffintent.putExtra("classes",model.getClasses());
                                startActivity(staffintent);
                            }
                        });
                        holder.options.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                PopupMenu popupMenu = new PopupMenu(StaffsActivity.this,holder.options);

                                // Inflating popup menu from popup_menu.xml file
                                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                                popupMenu.getMenu().removeItem(R.id.edit_exam);
                                popupMenu.getMenu().removeItem(R.id.add_co_marks);
                                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        if(menuItem.getItemId()==R.id.delete_exam)
                                        {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(StaffsActivity.this);
                                            builder.setTitle("Delete Staff");
                                            builder.setMessage("This action will delete this staff from the database! \n" +
                                                    "Are you sure you want to delete?");

                                            // set dialog non cancelable
                                            builder.setCancelable(true);
                                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    StaffsRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            Toast.makeText(StaffsActivity.this, "Successfully deleted", Toast.LENGTH_SHORT).show();

                                                        }
                                                    });
                                                }
                                            });
                                            builder.show();
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
                    public StaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlayout,parent,false);

                        return new StaffViewHolder(view);
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class StaffViewHolder extends RecyclerView.ViewHolder
    {
        TextView studentname,rollnumber;
        ImageButton options;
        View mView;
        public StaffViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            studentname = itemView.findViewById(R.id.name);
            rollnumber = itemView.findViewById(R.id.phone);
            options = itemView.findViewById(R.id.edit_student_option);
        }
    }

}