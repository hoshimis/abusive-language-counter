package com.example.app_nav;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import static android.Manifest.permission.RECORD_AUDIO;

public class MainActivity extends AppCompatActivity {

    private final int PERMISSIONS_RECORD_AUDIO = 1000;

    private SpeechRecognizer speechRecognizer;
    private TextView mText;
    private Button startButton;
    private Button stopButton;
    private AlertDialog.Builder mAlert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        mText = (TextView) findViewById(R.id.recognize_text_view);
        startButton = (Button) findViewById(R.id.recognize_start_button);
        stopButton = (Button) findViewById(R.id.recognize_stop_button);
        speechRecognizer = null;


//        音声機能についてパーミッションを明示的にユーザにリクエストする処理
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, PERMISSIONS_RECORD_AUDIO);
        }


        checkSpeechRecognizer();
    }

    /**
     * checkSpeechRecognizer()
     * 音声認識サポート端末であるかどうかを調べるメソッド
     * サポート端末ではなかったらその旨のテキストを表示(string.xmlから)
     * 録音が許可されていなかったらユーザに許可を求めるシステムダイアログを表示する。
     **/
    public Boolean checkSpeechRecognizer() {

        //音声認識が可能かチェックする
        if (!SpeechRecognizer.isRecognitionAvailable(getApplicationContext())) {
            //mAlert.setMessage(getString(R.string.speech_not_available));
            //mAlert.show();
            mText.setText(getString(R.string.speech_not_available));
            return false;
        }

        //録音機能のパーミッションが許可されていなかったら許可するようにアラートを出す
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                mText.setText(getString(R.string.speech_not_granted));
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.RECORD_AUDIO
                        },
                        PERMISSIONS_RECORD_AUDIO);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(
            int requestcode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestcode, permission, grantResults);
        Log.d("MainActivity", "onRequestPermissionResult");

        if (grantResults.length <= 0) {
            return;
        }

        switch (requestcode) {
            case PERMISSIONS_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mText.setText("");
                }
                break;
        }
    }

    /**
     * 必要がなくなった SpeechRecognizer は破棄する
     * SpeechRecognizer の破棄は destory で行う
     * 使い終わったらdestroyで破棄する
     * */
    public void onDestroy() {

        super.onDestroy();
        speechRecognizer.destroy();
    }

}