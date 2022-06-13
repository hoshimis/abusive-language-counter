package com.example.main.ui.recognition;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;

import static android.Manifest.permission.RECORD_AUDIO;

public class RecognitionFragment extends Fragment {

    private RecognitionViewModel homeViewModel;
    /**
     * 以下音声認識に使う変数
     */
    private final int PERMISSIONS_RECORD_AUDIO = 1000;
    private SpeechRecognizer speechRecognizer;
    private TextView mText;
    private TextView titleView;

    //log.dで使う文字列
    private final String TAG = "MainActivity";


    /**
     * ActivityのようにFragmentにもライフサイクルがある
     * Viewを生成するたタイミングで呼ばれるのがonCreateView()
     * onCreateView()で渡されるLayoutInflaterにFragmentのレイアウトを挿入して返す
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(RecognitionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recognition, container, false);


        /**
         * デフォルトで生成されるコード意味はまた、後で調べたい
         */
//        final TextView titleView = root.findViewById(R.id.text_Recognition);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                titleView.setText(s);
//            }
//        });

        //認識した音声をテキスト化して表示するテキストビューを紐づけ
        mText = (TextView) root.findViewById(R.id.recognize_text_view);

        //音声認識の状態を表示する部分のテキストビューを紐づけ
        titleView = (TextView) root.findViewById(R.id.text_Recognition);

        //データベースとの紐づけ
        WordDatabase wordDatabase = WordDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        CountDatabase countDatabase = CountDatabaseSingleton.getInstance(getActivity().getApplicationContext());

        //speechRecognizerにnullを代入
        speechRecognizer = null;

        /**
         * 音声機能についてパーミッションを明示的にユーザにリクエストする処理
         * */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO}, PERMISSIONS_RECORD_AUDIO);
        }

        /**
         * 以下ボタンを押されたときの挙動
         * startButton -> 音声識別開始
         * stopButton -> 音声識別終了
         */
        root.findViewById(R.id.recognize_start_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startRecording();
                    }
                }
        );
        root.findViewById(R.id.recognize_stop_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        stopRecording();
                    }
                }
        );

        /**
         * SpeechRecognizerを使用することができるか確認する
         */
        checkSpeechRecognizer();
        return root;
    }

    /**
     * checkSpeechRecognizer()
     * 音声認識サポート端末であるかどうかを調べるメソッド
     * サポート端末ではなかったらその旨のテキストを表示(string.xmlから)
     * 録音が許可されていなかったらユーザに許可を求めるシステムダイアログを表示する。
     **/
    public Boolean checkSpeechRecognizer() {

        //音声認識が可能かチェックする
        if (!SpeechRecognizer.isRecognitionAvailable(getActivity().getApplicationContext())) {
            //mAlert.setMessage(getString(R.string.speech_not_available));
            //mAlert.show();
            mText.setText(getString(R.string.speech_not_available));
            return false;
        }

        //録音機能のパーミッションが許可されていなかったら許可するようにアラートを出す
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                mText.setText(getString(R.string.speech_not_granted));
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{
                                Manifest.permission.RECORD_AUDIO
                        },
                        PERMISSIONS_RECORD_AUDIO);
                return false;
            }
        }
        return true;
    }


    /**
     * 許可ダイアログの承認結果を受け取る
     */
    @Override
    public void onRequestPermissionsResult(
            int requestcode, String[] permission, int[] grantResults) {
        super.onRequestPermissionsResult(requestcode, permission, grantResults);
        Log.d(TAG, "onRequestPermissionResult");

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
     * SpeechRecognizerを生成して開始する。
     * スピーチ中でも変換を受け取る（onPartialResultが呼ばれる）ためには、
     * intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULT, true)とする
     */
    public void startRecording() {
        if (speechRecognizer == null && checkSpeechRecognizer()) {
            titleView.setText(getString(R.string.prepare_speech));
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            speechRecognizer.setRecognitionListener(new listener());
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    getActivity().getPackageName());
            //以下指定で途中の認識を拾う
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            speechRecognizer.startListening(intent);
        }
    }

    /**
     * SpeechRecognizerをキャンセルして破棄
     */
    public void stopRecording() {
        titleView.setText("停止中");
        if (speechRecognizer != null && checkSpeechRecognizer()) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }


    /**
     * 一度発話が終わっても継続的に音声を認識している
     */

    public void continuationRecording() {
        startRecording();
    }

    /**
     * 必要がなくなった SpeechRecognizer は破棄する
     * SpeechRecognizer の破棄は destroy で行う
     * 使い終わったらdestroyで破棄する
     */
//    public void onDestroy() {
//
//        super.onDestroy();
//        speechRecognizer.destroy();
//    }


    //以下からデータベース接続などの非同期処理
        //メソッドとして、doInBackgroundを実装している
    private static class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {

        //コンストラクタ―
        public DataStoreAsyncTask() {

        }

        //AsyncTaskの実装
        //非同期処理(Activityの処理を止めることなく実行したい処理)
        //Activityに何も書いてないけどどうなんでしょうね？？
        //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
        @Override
        protected Integer doInBackground(Void... aVoid) {

            return null;
        }

        //非同期処理の後で実行される処理。
        //今回は、データベースへのアクセス処理を行う
        protected void onPostExecute(Integer code) {
        }
    }

    /**
     * 以下がRecognitionListenerの実装
     * onCreate内のsetRecognitionListenerの引数にlistenerを設定するために、新しくクラスを宣言してそのインスタンスを渡して上げる。
     */
    class listener implements RecognitionListener {

        //準備が整いユーザが発話してもよくなったら呼び出される
        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech");
        }


        //ユーザが発話を始めたら呼び出される
        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            titleView.setText("開始");
            mText.setText("");
        }

        //音声レベルの変化されたら、呼び出される
        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "onRmsChanged");
        }

        //音声が受信出来たら呼び出される
        @Override
        public void onBufferReceived(byte[] aByte) {
            Log.d(TAG, "onBufferReceived");
        }

        //ユーザが発話を終えたら呼びされる
        @Override
        public void onEndOfSpeech() {
            titleView.setText("停止");
            Log.d(TAG, "onEndOfSpeech");
            stopRecording();
            continuationRecording();
        }

        //ネットワークエラー、音声入力に関するエラーが発生したら呼び出される
        @Override
        public void onError(int i) {
            Log.d(TAG, "onError=" + i);

            mText.setText(getString(R.string.speech_error) + "\nエラーコード：" + i);
            stopRecording();
        }

        //音声入力が終わり、結果が準備できたら呼び出される
        @Override
        public void onResults(Bundle bundle) {
            Log.d(TAG, "onResults:");
        }

        /**
         * 　ここに認識した結果がかえってくる
         * 部分的な認識結果が利用可能な時に呼び出される。
         * TODO:DBと認識した音声を照合して一致したらカウントする機能を実装する
         */
        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d(TAG, "onPartialResults");
            String str = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
            if (str.length() > 0) {
                mText.setText(str);

            }
        }

        //追加イベントを受信したら呼び出される。
        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent: eventType=" + i);

        }
    }

}