package com.example.main;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;
import com.example.main.settings.notification.TodayAlarmNotification;
import com.example.main.settings.notification.YesterdayAlarmNotification;
import com.example.main.util.GetDay;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        muteAudio();
    }

    //アクティビティが作られるときに最初に実行されるメソッド
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //スーパークラスの呼び出し
        super.onCreate(savedInstanceState);

        //データベースとの紐づけ
        //DBの宣言
        WordDatabase wordDatabase = WordDatabaseSingleton.getInstance(getApplicationContext());

        //XMLとこのActivityの紐づけ
        setContentView(R.layout.activity_main);

        //ナビゲーションバーの紐づけ
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // 各メニューのIDをIdsのセットとして渡す
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_recognition, R.id.navigation_graph, R.id.navigation_bbs)
                .build();

        //上部のアクションバーを非表示にする
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // ステータスバーを消す
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //下部のナビゲーションバー諸々・変更の必要なし
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //アラームの起動
        setYesterdayNotification();
        setTodayNotification();

        //初回起動時のみにデータベースに値を入れる
        if (checkInitBoot()) {
            try {
                new InitWordDataBase(this, readFromFile(), wordDatabase).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unMuteAudio();
    }

    private boolean checkInitBoot() {
        //共有プリファレンスの準備
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        boolean isInitBoolean = preferences.getBoolean("initBoot", true);
        editor.putBoolean("initBoot", false);

        editor.apply();
        return isInitBoolean;
    }

    private String readFromFile() throws IOException {
        //InputStream inputStream = openFileInput("読み込むファイル名.txt");
        //-> openFileInputによって,InputStreamオブジェクトを取得する

        //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        //-> InputStreamReaderインスタンスに変換することで、文字列として扱えるようにする

        //BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        //-> inputStreamReaderをバッファリングして、効率的に読み込めるようにする

        String result = "";
        InputStream inputStream = this.getAssets().open("BadWord.txt");

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String tempString;
            StringBuilder stringBuilder = new StringBuilder();

            while ((tempString = bufferedReader.readLine()) != null) {
                stringBuilder.append(tempString).append(",");
            }
            inputStream.close();
            result = stringBuilder.toString();

        }
        return result;
    }

    public void setYesterdayNotification() {
        //共有プリファレンスの準備
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //通知の設定状態を取得する
        boolean rootNotification = preferences.getBoolean("isRootNotification", false);
        boolean yesterdayNotification = preferences.getBoolean("isYesterdayNotification", false);
        //通知設定（前日）の時間設定を取得する
        int hour = preferences.getInt("yesterdayNotificationTimeHour", 10);
        int minute = preferences.getInt("yesterdayNotificationTimeMinute", 10);

        //日付取得
        GetDay gt = new GetDay();
        int year = Integer.parseInt(gt.getDate(GetDay.TODAY, "yyyy"));
        int month = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));
        int date = Integer.parseInt(gt.getDate(GetDay.TODAY, "dd"));

        //通知設定(前日)がオンになっていたら実行
        if (rootNotification && yesterdayNotification) {

            //カレンダークラスのインスタンスを取得する
            Calendar calendar = Calendar.getInstance();
            //現在の時間を取得する
            calendar.setTimeInMillis(System.currentTimeMillis());

            /*
            指定した時間に通知を出す、指定する時間をユーザから受け取れるようにしてあげればいい
            →値をずっと保存しておきたいから共有プリファレンスを用いる
            calendar.add(Calendar.MINUTE, 1 );
            https://akira-watson.com/android/alarm-notificationmanager.html
            ↑sampleの記事だとaddメソッドで加減していたけども、
            指定した時間に通知を出したいからsetメソッドを使うことにする↓
            https://www.javadrive.jp/start/calendar/index3.html
            calender.set(iny year, int month, int date, int hourOfDay, int minute, int minute);
            年月日と時間、分を指定してそのフィールドにタイル値を設定する。
            Calendarを扱う際は、Monthは0から始めるから気を付ける
            */

            calendar.set(year, month - 1, date, hour, minute, 0);
//            calendar.set(2022, 6, 4, 10, 26, 0);

            //intentの生成
            Intent intent = new Intent(getApplicationContext(), YesterdayAlarmNotification.class);
            //追加機能（アラーム・通知）
            //リクエストコードは複数アラームを使いたいときに分ける必要がある
            int requestCode = 1;
            intent.putExtra("RequestCode", requestCode);

            /*
            PendingIntentは作成したIntentえおタイミングをみて他のアプリケーションに渡す場合に使う。
            BroadCastにメッセージを送るための設定
            Android12からは、PendingIntentのmutability(可変性)を指定するためにFLAG_IMMUTABLEを設定する
            PendingIntentを受け取った側が指定されていないパラメータを付加して投げることができるために、
             明示的に指定して上書きできないようにする
            */

            PendingIntent pending =
                    PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            //アラームをセットする
            //AlarmManagerは直接インスタンス化してはいけない -> getSystemService(ALARM_SERVICE) を使う
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (am != null) {
                //API level19で追加されたsetメソッドよりも正確なワンショットアラーム
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

                //トースで設定されたことを表示する
                Toast.makeText(getApplicationContext(), "alarm start", Toast.LENGTH_SHORT).show();

                Log.d("アラーム機能の準備中だよ！！", "アラームがセットされるよ！！");
            }
        }
    }

    public void setTodayNotification() {
        //共有プリファレンスの準備
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //通知の設定状態を取得する
        boolean rootNotification = preferences.getBoolean("isRootNotification", false);
        boolean todayNotification = preferences.getBoolean("isTodayNotification", false);
        //通知設定（前日）の時間設定を取得する
        int hour = preferences.getInt("todayNotificationTimeHour", 10);
        int minute = preferences.getInt("todayNotificationTimeMinute", 10);

        //日付取得
        GetDay gt = new GetDay();
        int year = Integer.parseInt(gt.getDate(GetDay.TODAY, "yyyy"));
        int month = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));
        int date = Integer.parseInt(gt.getDate(GetDay.TODAY, "dd"));

        //通知設定(前日)がオンになっていたら実行
        if (rootNotification && todayNotification) {

            //カレンダークラスのインスタンスを取得する
            Calendar calendar = Calendar.getInstance();
            //現在の時間を取得する
            calendar.setTimeInMillis(System.currentTimeMillis());

            /*
            指定した時間に通知を出す、指定する時間をユーザから受け取れるようにしてあげればいい
            →値をずっと保存しておきたいから共有プリファレンスを用いる
            calendar.add(Calendar.MINUTE, 1 );
            https://akira-watson.com/android/alarm-notificationmanager.html
            ↑sampleの記事だとaddメソッドで加減していたけども、
            指定した時間に通知を出したいからsetメソッドを使うことにする↓
            https://www.javadrive.jp/start/calendar/index3.html
            calender.set(iny year, int month, int date, int hourOfDay, int minute, int minute);
            年月日と時間、分を指定してそのフィールドにタイル値を設定する。
            Calendarを扱う際は、Monthは0から始めるから気を付ける
            */

            calendar.set(year, month - 1, date, hour, minute, 0);
//            calendar.set(2022, 6, 4, 10, 26, 0);

            //intentの生成
            Intent intent = new Intent(getApplicationContext(), TodayAlarmNotification.class);
            //追加機能（アラーム・通知）
            //リクエストコードは複数アラームを使いたいときに分ける必要がある
            int requestCode = 200;
            intent.putExtra("RequestCode", requestCode);

            /*
            PendingIntentは作成したIntentえおタイミングをみて他のアプリケーションに渡す場合に使う。
            BroadCastにメッセージを送るための設定
            Android12からは、PendingIntentのmutability(可変性)を指定するためにFLAG_IMMUTABLEを設定する
            PendingIntentを受け取った側が指定されていないパラメータを付加して投げることができるために、
             明示的に指定して上書きできないようにする
            */

            PendingIntent pending =
                    PendingIntent.getBroadcast(getApplicationContext(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            //アラームをセットする
            //AlarmManagerは直接インスタンス化してはいけない -> getSystemService(ALARM_SERVICE) を使う
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (am != null) {
                //API level19で追加されたsetメソッドよりも正確なワンショットアラーム
                am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pending);

                //トースで設定されたことを表示する
                Toast.makeText(getApplicationContext(), "alarm start", Toast.LENGTH_SHORT).show();

                Log.d("アラーム機能の準備中だよ！！", "アラームがセットされるよ！！");
            }
        }
    }

    public void muteAudio() {
        AudioManager alarmManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    //ミュートを解除する
    public void unMuteAudio() {
        AudioManager alarmManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
        alarmManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }
}