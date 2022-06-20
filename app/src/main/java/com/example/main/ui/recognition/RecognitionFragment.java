package com.example.main.ui.recognition;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;
import com.example.main.layout.CircleView;

import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;

public class RecognitionFragment extends Fragment {


    //以下フィールド
    private RecognitionViewModel homeViewModel;
    //以下音声認識に使う変数
    private final int PERMISSIONS_RECORD_AUDIO = 1000;
    private SpeechRecognizer speechRecognizer;
    private TextView mText;
    private TextView titleView;
    private TextView countText;
    private ListView listView;
    //log.dで使う文字列
    private final String TAG = "MainActivity";
    private WordDatabase wordDatabase;
    private CountDatabase countDatabase;

    //リスト構造を宣言
    ArrayList<String> data = new ArrayList<>();

    /**
     * ActivityのようにFragmentにもライフサイクルがある
     * Viewを生成するたタイミングで呼ばれるのがonCreateView()
     * onCreateView()で渡されるLayoutInflaterにFragmentのレイアウトを挿入して返す
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(RecognitionViewModel.class);
        View root = inflater.inflate(R.layout.fragment_recognition, container, false);

        //デフォルトで生成されるコード意味は後で調べる
//        final TextView titleView = root.findViewById(R.id.text_Recognition);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                titleView.setText(s);
//            }
//        });

        // mText -> 認識した音声をテキスト化して表示するテキストビューを紐づけ
        //titleView -> 音声認識の状態を表示する部分のテキストビューを紐づけ
        //countText -> 単語DBと何回マッチしたかを表示するテキストビューの紐づけ
        //listView -> その日になんの言葉を話したか表示するListViewの紐づけ

        mText = (TextView) root.findViewById(R.id.recognize_text_view);
        titleView = (TextView) root.findViewById(R.id.text_Recognition);
        countText = (TextView) root.findViewById(R.id.count_text);
        listView = (ListView) root.findViewById(R.id.listView);

        //データベースとの紐づけ
        wordDatabase = WordDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        countDatabase = CountDatabaseSingleton.getInstance(getActivity().getApplicationContext());

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

        //以下のボタンは開発ように作成したもの
//        root.findViewById(R.id.recognize_start_button).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startRecording();
//                    }
//                }
//        );
//        root.findViewById(R.id.recognize_stop_button).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        stopRecording();
//                    }
//                }
//        );

        CircleView circleView = (CircleView) root.findViewById(R.id.circle_view);
        circleView.setColor(R.color.purple);

        //SpeechRecognizerを使用することができるか確認する
        checkSpeechRecognizer();
        //使用できるならば、レコーディングを開始する
        if (checkSpeechRecognizer()) {
            startRecording();
        }




        // Adapterとリスト構造の結びつけ
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);

        //xmlとの紐づけ
        final ListView listView = (ListView) root.findViewById(R.id.listView);

        listView.setAdapter(adapter);

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


    // 許可ダイアログの承認結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestcode, String[] permission, int[] grantResults) {
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
            speechRecognizer.setRecognitionListener(new listener(countDatabase, wordDatabase, countText));
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

    //SpeechRecognizerをキャンセルして破棄
    public void stopRecording() {
        titleView.setText("停止中");
        if (speechRecognizer != null && checkSpeechRecognizer()) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    //一度発話が終わっても継続的に音声を認識している
    public void continuationRecording() {
        startRecording();
    }

    /**
     * 必要がなくなった SpeechRecognizer は破棄する
     * SpeechRecognizer の破棄は destroy で行う
     * 使い終わったらdestroyで破棄する
     */
//    public void onDestroy() {
//        super.onDestroy();
//        speechRecognizer.destroy();
//    }

    /**
     * 以下がRecognitionListenerの実装
     * onCreate内のsetRecognitionListenerの引数にlistenerを設定するために、新しくクラスを宣言してそのインスタンスを渡して上げる。
     */
    class listener implements RecognitionListener {
        private CountDatabase countDatabase;
        private WordDatabase wordDatabase;
        private TextView CountTextView;

        //コンストラクタ―
        //インスタンス化されたときに渡された引数をフィールドに代入する
        listener(CountDatabase countDatabase, WordDatabase wordDatabase, TextView CountTextView) {
            this.wordDatabase = wordDatabase;
            this.countDatabase = countDatabase;
            this.CountTextView = CountTextView;
        }

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

        //ここに認識した結果がかえってくる部分的な認識結果が利用可能な時に呼び出される。
        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d(TAG, "onPartialResults");
            String str = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
            if (str.length() > 0) {
                mText.setText(str);
                //ここで、認識した言葉を非同期処理に渡して、マッチするかを確認する
                new DataStoreAsyncTask(getActivity(), countDatabase, wordDatabase, str, CountTextView).execute();
                new GetCountAsyncTask(getActivity(), countDatabase, data);

            }
        }

        //追加イベントを受信したら呼び出される。
        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent: eventType=" + i);

        }
    }

}