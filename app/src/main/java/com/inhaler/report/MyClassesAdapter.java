package com.inhaler.report;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.awt.font.TextAttribute;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyClassesAdapter extends RecyclerView.Adapter<MyClassesAdapter.MyViewHolder> {

    Context context;
    ArrayList<MyClassesModel> classesModel;
    String staffkey,staffid;
    public MyClassesAdapter(Context context, ArrayList<MyClassesModel> classesModel, String staffkey,String staffid)
    {
        this.context = context;
        this.classesModel = classesModel;
        this.staffkey = staffkey;
        this.staffid = staffid;
    }

    @NonNull
    @Override
    public MyClassesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.exam_layout,parent,false);
        return new MyClassesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyClassesAdapter.MyViewHolder holder, int position) {

        holder.className.setText(classesModel.get(position).getClassName() + classesModel.get(position).getClassSection());
        holder.classTeacher.setText(classesModel.get(position).getClassTeacher());
        holder.moreptions.setVisibility(View.GONE);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String classid = classesModel.get(holder.getAdapterPosition()).getClassId();
                Intent listStudents = new Intent(context,StaffModuleActivity.class);
                listStudents.putExtra("classid",classid);
                listStudents.putExtra("staffid",staffid);
                listStudents.putExtra("staffkey",staffkey);
                context.startActivity(listStudents);

            }
        });
    }

    @Override
    public int getItemCount() {
        return classesModel.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView className,classTeacher;
        LinearLayout linearLayout;
        ImageButton moreptions;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            className = itemView.findViewById(R.id.exam_name);
            classTeacher = itemView.findViewById(R.id.exam_period);
            linearLayout = itemView.findViewById(R.id.order_linear_layout);
            moreptions = itemView.findViewById(R.id.options);
        }
    }
}
