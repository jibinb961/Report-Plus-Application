package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class AddMarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference ClassesRef;
    private ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String uid;
    private TextView noStudent;
    private Button addClassBtn;
    private LinearLayout backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();
        ClassesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Classes");
        ClassesRef.keepSynced(true);

        recyclerView = (RecyclerView)  findViewById(R.id.addmarks_classes_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        noStudent = (TextView) findViewById(R.id.marks_no_classes_text);
        noStudent.setVisibility(View.INVISIBLE);
        progressDialog = new ProgressDialog(AddMarksActivity.this);
        progressDialog.setTitle("Loading Classes");
        backButton = (LinearLayout) findViewById(R.id.mark_update_class_back_button);

        addClassBtn = (Button) findViewById(R.id.add_class_button);
        addClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addClassIntent = new Intent(AddMarksActivity.this,AddClassActivity.class);
                startActivity(addClassIntent);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        LoadUserData();

    }
    private void LoadUserData()
    {
        Query query = ClassesRef.orderByChild("classname");


       FirebaseRecyclerOptions<Classes> options =
               new FirebaseRecyclerOptions.Builder<Classes>()
               .setQuery(query,Classes.class)
               .build();

       FirebaseRecyclerAdapter<Classes,UserViewHolder> adapter =
               new FirebaseRecyclerAdapter<Classes, UserViewHolder>(options) {
                   @Override
                   protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Classes model) {
                       final String key = getRef(position).getKey();
                       holder.classname.setText(model.getClassname() + model.getClasssection());
                       holder.teachername.setText(model.getClassteacher());
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               ClassesRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       if(snapshot.child("classpin").exists())
                                       {
                                           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AddMarksActivity.this);
                                           String name = preferences.getString(key, "");
                                           if(!name.equalsIgnoreCase(""))
                                           {
                                               Intent listStudentsIntent= new Intent(AddMarksActivity.this,StaffModuleActivity.class);
                                               listStudentsIntent.putExtra("classid",key);
                                               listStudentsIntent.putExtra("staffid","Admin");
                                               listStudentsIntent.putExtra("staffkey",uid);
                                               startActivity(listStudentsIntent);
                                           }
                                           else
                                           {

                                               Bundle bundle = new Bundle();
                                               bundle.putString("id",key);
                                               bundle.putString("heading","Enter Class PIN");
                                               BottomPinFragment bottomPinFragment= new BottomPinFragment();
                                               bottomPinFragment.setArguments(bundle);
                                               bottomPinFragment.show(getSupportFragmentManager(),"Bottom Pin Fragment");
                                           }
                                       }
                                       else
                                       {
                                           Intent listStudentsIntent= new Intent(AddMarksActivity.this,StaffModuleActivity.class);
                                           listStudentsIntent.putExtra("classid",key);
                                           listStudentsIntent.putExtra("staffid","Admin");
                                           listStudentsIntent.putExtra("staffkey",uid);
                                           startActivity(listStudentsIntent);
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError error) {

                                   }
                               });





                           }
                       });

                       holder.editOption.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               PopupMenu popupMenu = new PopupMenu(AddMarksActivity.this,holder.editOption);

                               // Inflating popup menu from popup_menu.xml file
                               popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                               popupMenu.getMenu().removeItem(R.id.delete_exam);
                               popupMenu.getMenu().removeItem(R.id.add_co_marks);
                               popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                   @Override
                                   public boolean onMenuItemClick(MenuItem menuItem) {
                                       if(menuItem.getItemId()==R.id.edit_exam)
                                       {

                                           Intent editclassintent = new Intent(AddMarksActivity.this,UpdateClassActivity.class);
                                           editclassintent.putExtra("classid",key);
                                           startActivity(editclassintent);
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

                       return new UserViewHolder(view);
                   }
               };
        recyclerView.setAdapter(adapter);
        adapter.startListening();



    }


    @Override
    protected void onStart() {
        super.onStart();


        LoadUserData();
        //adapter.startListening();
        noStudent.setVisibility(View.INVISIBLE);
        Query query = ClassesRef.orderByChild("classname");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noStudent.setVisibility(View.VISIBLE);
                    noStudent.setText("No classes are created, Tap the below 'Add' Button to add a" +
                            " new class");
                    noStudent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView classname,teachername;
        View mView;
        ImageButton editOption;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            classname = itemView.findViewById(R.id.exam_name);
            teachername = itemView.findViewById(R.id.exam_period);
            editOption = itemView.findViewById(R.id.options);
        }
    }
}