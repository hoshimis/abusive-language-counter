package com.example.main.settings;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.main.R;

import java.util.Locale;

public class SettingNotificationFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {

    //テキストビューの宣言
    private TextView yesterdayNotificationTime, todayNotificationTime;

    /*
     2つの設定ボタンのうちどちらが押されたかを判別するための真偽値
     ほんとはタグ名で判別したかったけど、どうすればいいか分からなかった。
     ↓のサイトでも同じような質問があったけど、タグ名を使わずに真偽値を使った判別方法を使用していたのでそれに倣う。
     https://stackoverflow.com/questions/48927079/getting-tag-from-fragment-on-ontimeset
    */
    private Boolean isSelect;
    //共有プリファレンスの準備
    private SharedPreferences preferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_setting_notification, container, false);

        //共有プリファレンスの準備
        preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        SharedPreferences.Editor editor = preferences.edit();

        //XMLとの紐づけ
        //前日関連
        yesterdayNotificationTime = root.findViewById(R.id.notification_yesterday_time);
        LinearLayout yesterdaySetTime = root.findViewById(R.id.yesterday_set_time);
        //当日関連
        todayNotificationTime = root.findViewById(R.id.today_notification_time);
        LinearLayout todaySetTime = root.findViewById(R.id.today_set_time);
        //トグルボタンの紐づけ
        SwitchCompat switchRoot = root.findViewById(R.id.notification_root_button);
        SwitchCompat switchYesterday = root.findViewById(R.id.notification_yesterday_button);
        SwitchCompat switchToday = root.findViewById(R.id.notification_today_button);
        //LinerLayoutの紐づけ
        LinearLayout rootBox = root.findViewById(R.id.notification_box);
        LinearLayout yesterdayBox = root.findViewById(R.id.notification_yesterday_box);
        LinearLayout todayBox = root.findViewById(R.id.notification_today_box);

        //↓のサイトを参考にSwitchの挙動を実装した
        //https://pentan.info/android/app/sample/visibility_invisible_gone.html
        //一番上のトグルスイッチを押下状態で下の設定を表示するかを決める
        switchRoot.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                rootBox.setVisibility(View.VISIBLE);

                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isRootNotification", true);
            } else {
                //rootのボタンを押したらすべてfalseの状態にする
                rootBox.setVisibility(View.INVISIBLE);
                switchYesterday.setChecked(false);
                switchToday.setChecked(false);
                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isRootNotification", false);
                editor.putBoolean("isYesterdayNotification", false);
                editor.putBoolean("isTodayNotification", false);
            }
            editor.apply();
        });

        //昨日の通知をオンにしたら表示する
        switchYesterday.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                yesterdayBox.setVisibility(View.VISIBLE);
                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isYesterdayNotification", true);

            } else {
                yesterdayBox.setVisibility(View.GONE);
                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isYesterdayNotification", false);
            }
            editor.apply();
        });

        //当日の通知をオンにしたら表示する
        switchToday.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                todayBox.setVisibility(View.VISIBLE);

                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isTodayNotification", true);
            } else {
                todayBox.setVisibility(View.GONE);
                //チェック状態を共有プリファレンスに格納する
                editor.putBoolean("isTodayNotification", false);
            }
            editor.apply();
        });

        //通知する時間帯を設定する(前日)を押したときに挙動
        yesterdaySetTime.setOnClickListener(view -> {
            isSelect = true;
            DialogFragment newFragment = new TimePicker();
            newFragment.show(getChildFragmentManager(), "setYesterdayNotificationTime");
        });

        //通知する時間帯を設定する(当日)を押したときに挙動
        todaySetTime.setOnClickListener(view -> {
            isSelect = false;
            DialogFragment newFragment = new TimePicker();
            newFragment.show(getChildFragmentManager(), "setTodayNotificationTime");
        });

        //共有プリファレンスに格納されている時間を取得する
        int yesterdayHour = preferences.getInt("yesterdayNotificationTimeHour", -100);
        int yesterdayMinute = preferences.getInt("yesterdayNotificationTimeMinute", -100);

        int todayHour = preferences.getInt("todayNotificationTimeHour", -100);
        int todayMinute = preferences.getInt("todayNotificationTimeMinute", -100);

        //共有プリファレンスに格納されているトグルボタンのチェック状態を取得する
        boolean isRootNotification = preferences.getBoolean("isRootNotification", false);
        boolean isYesterdayNotification = preferences.getBoolean("isYesterdayNotification", false);
        boolean isTodayNotification = preferences.getBoolean("isTodayNotification", false);

        //値が格納されていなかったら"NoData"と表示する
        //格納されていたらそれを表示する
        if (yesterdayHour == -100 || yesterdayMinute == -100) {
            yesterdayNotificationTime.setText(getString(R.string.settings_notification_now_time));
        } else {
            String str = String.format(Locale.US, "%02d:%02d", yesterdayHour, yesterdayMinute);
            yesterdayNotificationTime.setText(str);
        }

        if (todayHour == -100 || todayMinute == -100) {
            todayNotificationTime.setText(getString(R.string.settings_notification_now_time));
        } else {
            String str = String.format(Locale.US, "%02d:%02d", todayHour, todayMinute);
            todayNotificationTime.setText(str);
        }

        //トグルボタンのそれぞれのチェック状態を前回の状態から引き継ぐ
        switchRoot.setChecked(isRootNotification);
        switchYesterday.setChecked(isYesterdayNotification);
        switchToday.setChecked(isTodayNotification);

        return root;
    }

    //TimePickerの入力がここに返される
    @Override
    public void onTimeSet(android.widget.TimePicker timePicker, int hourOfDay, int minute) {

        //isChecked
        //true -> 昨日の設定
        //false -> 当日の設定
        if (isSelect) {
            Log.d("ここに", "onTimeSet: ボタンが押されましたよ！！！");
            String str = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
            yesterdayNotificationTime.setText(str);

            //設定した時間を共有プリファレンスに格納する
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("yesterdayNotificationTimeHour", hourOfDay);
            editor.putInt("yesterdayNotificationTimeMinute", minute);

            //書き込みの確定処理
            editor.apply();
        } else {
            String str = String.format(Locale.US, "%02d:%02d", hourOfDay, minute);
            todayNotificationTime.setText(str);

            //設定した時間を共有プリファレンスに格納する
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("todayNotificationTimeHour", hourOfDay);
            editor.putInt("todayNotificationTimeMinute", minute);

            //書き込みの確定処理
            editor.apply();
        }
    }
}