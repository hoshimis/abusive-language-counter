package com.example.main.ui.recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.main.R;
import com.example.main.ui.graph.CountSetting;

public class CountColerSetting extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_count_coler_setting, container, false);
        root.findViewById(R.id.back_button).setOnClickListener(
                view -> {
                    Fragment toSetting = new CountSetting();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toSetting);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );


        return root;
    }
}