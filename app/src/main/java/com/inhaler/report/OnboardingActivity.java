package com.inhaler.report;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OnboardingActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout linearLayout;
    Button backBtn,nextBtn,skipBtn;
    TextView[] dots;
    ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        skipBtn = (Button) findViewById(R.id.skip_onboard_button);
        viewPager = (ViewPager) findViewById(R.id.onboarding_viewpager);




        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dashboardintent = new Intent(OnboardingActivity.this,DashboardActivity.class);
                startActivity(dashboardintent);
                finish();
            }
        });

        linearLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        viewPagerAdapter = new ViewPagerAdapter(OnboardingActivity.this);

        viewPager.setAdapter(viewPagerAdapter);

        setupIndicators(0);
        viewPager.addOnPageChangeListener(pageChangeListener);


    }

    public void setupIndicators(int position){

        dots = new TextView[8];
        linearLayout.removeAllViews();

        for(int i =0;i< dots.length;i++)
        {
            dots[i] = new TextView(OnboardingActivity.this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.inactive,getApplicationContext().getTheme()));
            linearLayout.addView(dots[i]);
        }

        dots[position].setTextColor(getResources().getColor(R.color.active,getApplicationContext().getTheme()));

    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setupIndicators(position);


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getitem(int i){
        return viewPager.getCurrentItem() + 1;
    }
}