package com.inhaler.report;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewPagerAdapter extends PagerAdapter {

    Context context;


    int images[] = {
            R.drawable.logo_eport,
            R.drawable.subjects,
            R.drawable.exam_onboardng,
            R.drawable.add_class,
            R.drawable.employee_one,
            R.drawable.immigration,
            R.drawable.coscholastic,
            R.drawable.pdf

    };

    int heading[] = {
        R.string.report_intro,
            R.string.subject_heading,
            R.string.exams_heading,
            R.string.classes_heading,
            R.string.student_heading,
            R.string.marking_heading,
            R.string.coscholastic_heading,
            R.string.pdf_heading
    };

    int desc[] = {
            R.string.report_desc,
            R.string.subject_desc,
            R.string.exams_desc,
            R.string.classes_desc,
            R.string.student_desc,
            R.string.marking_desc,
            R.string.coscholastic_desc,
            R.string.pdf_desc

    };

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }



    @Override
    public int getCount() {
        return heading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);

        ImageView imageView = (ImageView) view.findViewById(R.id.circular_image_onboarding);
        TextView headingtext = (TextView) view.findViewById(R.id.step_onboarding);
        TextView descriptons = (TextView) view.findViewById(R.id.onboarding_description);

        imageView.setImageResource(images[position]);
        headingtext.setText(heading[position]);
        descriptons.setText(desc[position]);

        container.addView(view);
        return view;


    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((LinearLayout)object);
    }
}
