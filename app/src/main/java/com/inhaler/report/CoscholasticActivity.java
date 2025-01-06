package com.inhaler.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class CoscholasticActivity extends AppCompatActivity {

    private Button addCoscholasticBtn;
    private RecyclerView recyclerView;
    private DatabaseReference coscholasticRef;
    private FirebaseUser currentUser;
    private String uid;
    private TextView noActivity;
    private ProgressDialog progressDialog;
    private ImageView backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coscholastic);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = currentUser.getUid();

        addCoscholasticBtn = (Button)  findViewById(R.id.add_coscholastic_button);
        recyclerView = (RecyclerView) findViewById(R.id.coscholastic_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noActivity = (TextView) findViewById(R.id.coscholastic_no_activites_text);
        backButton = (ImageView) findViewById(R.id.coscholastic_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        coscholasticRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Coscholastic");

        progressDialog = new ProgressDialog(CoscholasticActivity.this);
        progressDialog.setTitle("Loading Coscholastic Activities..");
        progressDialog.show();
        addCoscholasticBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addactivitintent = new Intent(CoscholasticActivity.this,AddCoscholasticActivity.class);
                addactivitintent.putExtra("activityname","");
                addactivitintent.putExtra("activitygrade","");
                addactivitintent.putExtra("activityid","");
                startActivity(addactivitintent);
            }
        });

        LoadUserData();

    }
    private void LoadUserData()
    {
        Query query = coscholasticRef.orderByChild("activityname");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.hasChildren())
                {
                    noActivity.setText("No activites are created, click on 'Add' below to add a" +
                            " new activity!");
                    noActivity.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       FirebaseRecyclerOptions<Coscholastic> options =
               new FirebaseRecyclerOptions.Builder<Coscholastic>()
                       .setQuery(query,Coscholastic.class)
                               .build();

       FirebaseRecyclerAdapter<Coscholastic,UserViewHolder> adapter =
               new FirebaseRecyclerAdapter<Coscholastic, UserViewHolder>(options) {
                   @Override
                   protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Coscholastic model) {
                       final String key = getRef(position).getKey();
                       holder.activityname.setText(model.getActivityname());
                       holder.activitygrade.setText("Grading Mode : "+model.getActivitygrade());
                       holder.itemView.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {

                               Intent addactivityintent = new Intent(CoscholasticActivity.this,AddCoscholasticActivity.class);
                               addactivityintent.putExtra("activityname",model.getActivityname());
                               addactivityintent.putExtra("activitygrade",model.getActivitygrade());
                               addactivityintent.putExtra("activityid",key);
                               startActivity(addactivityintent);
                           }
                       });
                   }

                   @NonNull
                   @Override
                   public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.userlayout,parent,false);

                       return new CoscholasticActivity.UserViewHolder(view);
                   }
               };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    @Override
    protected void onStart() {
        super.onStart();
//
//    progressDialog.show();
//        LoadUserData();
//        //adapter.startListening();



    }

    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder
    {
        TextView activityname,activitygrade;
        View mView;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            activityname = itemView.findViewById(R.id.name);
            activitygrade = itemView.findViewById(R.id.phone);
        }
    }
}