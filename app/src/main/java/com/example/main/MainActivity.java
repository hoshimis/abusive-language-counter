/**
 * Author:Koike
 *
 */

package com.example.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  {

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

        //
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


        //タイトルバーを隠す処理
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

}