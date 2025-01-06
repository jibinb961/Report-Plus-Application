package com.inhaler.report;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


public class UpgradeFragment extends DialogFragment {


    private LinearLayout updatePremium,cancelPremium;


    public UpgradeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View upgradeView = inflater.inflate(R.layout.fragment_upgrade,container,false);

        updatePremium = upgradeView.findViewById(R.id.upgrade_premium_linear_layout);
        cancelPremium = upgradeView.findViewById(R.id.cancel_premium_linear_layout);

        updatePremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancelPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getDialog().dismiss();
            }
        });

        return upgradeView;
    }
}