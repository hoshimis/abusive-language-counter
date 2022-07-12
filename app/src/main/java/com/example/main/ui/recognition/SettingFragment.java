package com.example.main.ui.recognition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;
import com.example.main.settings.AddWordDBFragment;
import com.example.main.settings.SettingNotificationFragment;
import com.example.main.ui.graph.CountSetting;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.setting_fragment, container, false);

        root.findViewById(R.id.word_add).setOnClickListener(
                view -> {
                    Fragment toAddDB = new AddWordDBFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toAddDB);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.alert_setting).setOnClickListener(
                view -> {
                    Fragment toSettingNotification = new SettingNotificationFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toSettingNotification);
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
