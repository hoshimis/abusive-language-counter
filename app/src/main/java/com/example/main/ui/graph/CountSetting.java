package com.example.main.ui.graph;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.ui.recognition.SettingFragment;

public class CountSetting extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_count_setting, container, false);
        root.findViewById(R.id.week_count_setting).setOnClickListener(

                view -> {
                    Fragment toWeekCount = new CountWeekSetting();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeekCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.month_count_setting).setOnClickListener(
                view -> {
                    Fragment toMonthCount = new CountMonthSetting();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonthCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.year_count_setting).setOnClickListener(
                view -> {
                    Fragment toYearCount = new CountYearSetting();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toYearCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        root.findViewById(R.id.back_button).setOnClickListener(
                view -> {
                    Fragment toDialog = new SettingFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toDialog);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        return root;
    }
}