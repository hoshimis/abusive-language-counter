package com.example.main.ui.recognition;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.ui.graph.CountSetting;
import com.example.main.ui.graph.GraphWeekFragment;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.setting_fragment, container, false);

        root.findViewById(R.id.alert_setting).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.word_add).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.count_setting).setOnClickListener(
                view -> {
                    Fragment toCountSetting = new CountSetting();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toCountSetting);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.back_button).setOnClickListener(
                view -> {
                    Fragment toRecognition = new RecognitionFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toRecognition);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        return root;
    }
}
