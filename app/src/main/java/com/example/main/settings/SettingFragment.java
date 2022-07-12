package com.example.main.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;
import com.example.main.settings.addword.AddWordDBFragment;
import com.example.main.settings.count.SettingCountFragment;
import com.example.main.settings.notification.SettingNotificationFragment;
import com.example.main.ui.recognition.RecognitionFragment;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        //DBに任意の単語を設定する画面に遷移する
        root.findViewById(R.id.word_add).setOnClickListener(
                view -> {
                    Fragment toAddDB = new AddWordDBFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toAddDB);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //通知を設定する画面に遷移する
        root.findViewById(R.id.alert_setting).setOnClickListener(
                view -> {
                    Fragment toSettingNotification = new SettingNotificationFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toSettingNotification);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //グラフに表示される顔の段階についての設定をする画面に追加する
        root.findViewById(R.id.count_setting).setOnClickListener(
                view -> {
                    Fragment toCountSetting = new SettingCountFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toCountSetting);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        //前の画面に戻る
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
