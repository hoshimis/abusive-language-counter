package com.example.main.settings.count;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.settings.SettingFragment;

public class SettingCountFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_setting_count, container, false);

        //週間の設定画面に遷移
        root.findViewById(R.id.week_count_setting).setOnClickListener(
                view -> {
                    Fragment toWeekCount = new SettingCountWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeekCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //月間の設定画面に遷移
        root.findViewById(R.id.month_count_setting).setOnClickListener(
                view -> {
                    Fragment toMonthCount = new SettingCountMonthFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonthCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //年間の設定画面に遷移
        root.findViewById(R.id.year_count_setting).setOnClickListener(
                view -> {
                    Fragment toYearCount = new SettingCountYearFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toYearCount);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //認識画面の色の変化の設定画面に遷移
        root.findViewById(R.id.count_color).setOnClickListener(
                view -> {
                    Fragment toSettingColor = new SettingCountColorFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toSettingColor);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //前の画面に戻る
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