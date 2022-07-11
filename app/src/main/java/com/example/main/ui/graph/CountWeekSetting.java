package com.example.main.ui.graph;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;

import static com.example.main.ui.recognition.RecognitionFragment.count;

public class CountWeekSetting extends Fragment {
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        View root = inflater.inflate(R.layout.activity_count_week_setting, container, false);

        root.findViewById(R.id.save_button).setOnClickListener(

                view -> {
                    SharedPreferences.Editor data = preferences.edit();
                    EditText WeekCountLevel1 = (EditText) root.findViewById(R.id.week_level_1);
                    EditText WeekCountLevel2 = (EditText) root.findViewById(R.id.week_level_2);
                    EditText WeekCountLevel3 = (EditText) root.findViewById(R.id.week_level_3);

                    if (WeekCountLevel1.getText().toString().isEmpty() || WeekCountLevel2.getText().toString().isEmpty() || WeekCountLevel3.getText().toString().isEmpty()) {
                        Toast.makeText(requireContext(), "数値を入力してください。", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //入力された文字が数字だったら
                    if (WeekCountLevel1.getText().toString().matches("[0-9]+") && WeekCountLevel2.getText().toString().matches("[0-9]+") && WeekCountLevel3.getText().toString().matches("[0-9]+")) {

                        data.putInt("WeekCountLevel1", Integer.parseInt(WeekCountLevel1.getText().toString()));
                        data.putInt("WeekCountLevel2", Integer.parseInt(WeekCountLevel2.getText().toString()));
                        data.putInt("WeekCountLevel3", Integer.parseInt(WeekCountLevel3.getText().toString()));
                        data.commit();
                    } else {
                        Toast.makeText(getActivity(), "半角で数字を入力してください", Toast.LENGTH_SHORT).show();

                        return;
                    }


                    Toast.makeText(getActivity(), "保存されました!!", Toast.LENGTH_SHORT).show();
                }
        );
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