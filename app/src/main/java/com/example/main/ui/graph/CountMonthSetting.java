package com.example.main.ui.graph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.main.R;

public class CountMonthSetting extends Fragment {
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_count_month_setting, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        root.findViewById(R.id.save_button).setOnClickListener(
                view -> {
                    SharedPreferences.Editor data = preferences.edit();
                    EditText MonthCountLevel1 = (EditText) root.findViewById(R.id.month_level_1);
                    EditText MonthCountLevel2 = (EditText) root.findViewById(R.id.month_level_2);
                    EditText MonthCountLevel3 = (EditText) root.findViewById(R.id.month_level_3);

                    if(MonthCountLevel1.getText().toString().isEmpty()||MonthCountLevel2.getText().toString().isEmpty()||MonthCountLevel3.getText().toString().isEmpty()){
                        Toast.makeText(requireContext(),"数値を入力してください。",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //入力された文字が数字だったら
                    if (MonthCountLevel1.getText().toString().matches("[0-9]+") && MonthCountLevel2.getText().toString().matches("[0-9]+") && MonthCountLevel3.getText().toString().matches("[0-9]+")) {

                        data.putInt("WeekCountLevel1", Integer.parseInt(MonthCountLevel1.getText().toString()));
                        data.putInt("WeekCountLevel2", Integer.parseInt(MonthCountLevel2.getText().toString()));
                        data.putInt("WeekCountLevel3", Integer.parseInt(MonthCountLevel3.getText().toString()));
                        data.commit();
                    } else {
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