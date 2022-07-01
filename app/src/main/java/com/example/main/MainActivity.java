package com.example.main;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    //音声をミュートにする為の変数を宣言する
    private AudioManager mAudioManager;
    private int mStreamVolume = 0;

    //アクティビティが作られるときに最初に実行されるメソッド
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //スーパークラスの呼び出し
        super.onCreate(savedInstanceState);

        //XMLとこのActivityの紐づけ
        setContentView(R.layout.activity_main);

        //ナビゲーションバーの紐づけ
        BottomNavigationView navView = findViewById(R.id.nav_view);

        /*
         * 各メニューのIDをIdsのセットとして渡すのは、各メニューが
         * メニューはトップレベルの目的地とみなされるからである。
         */
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

        //音声をミュートにする
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);


        //下部のナビゲーションバー諸々・変更の必要なし
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}