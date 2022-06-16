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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.db.dayscount.DaysCount;
import com.example.main.db.dayscount.DaysCountDao;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    //log.dで使う文字列
    private final String TAG = "MainActivity";
    private WordDatabase wordDatabase;
    private CountDatabase countDatabase;

    static int count;

    /**
     * ActivityのようにFragmentにもライフサイクルがある
     * Viewを生成するたタイミングで呼ばれるのがonCreateView()
     * onCreateView()で渡されるLayoutInflaterにFragmentのレイアウトを挿入して返す
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
        //countText -> 単語DBと何回マッチしたかを表示するテキストビューの貼り付け
        mText = (TextView) root.findViewById(R.id.recognize_text_view);
        titleView = (TextView) root.findViewById(R.id.text_Recognition);
        countText = (TextView) root.findViewById(R.id.count_text);

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

        //SpeechRecognizerを使用することができるか確認する
        checkSpeechRecognizer();
        //使用できるならば、レコーディングを開始する
        if (checkSpeechRecognizer()) {
            startRecording();
        }

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
//
//        super.onDestroy();
//        speechRecognizer.destroy();
//    }

    //以下からデータベース接続などの非同期処理
    //メソッドとして、doInBackgroundを実装している
    private static class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakReference;
        //データベース二つとの紐づけ
        private CountDatabase countDatabase;
        private WordDatabase wordDatabase;
        //音声認識した言葉をここに入力する
        private String speechText;
        //マッチした回数を表示するテキストビュー
        private TextView countText;
        //1日 = 86400000ms
        //日付取得の時に使う
        final long DAY = 86400000;

        //コンストラクタ―
        public DataStoreAsyncTask(Activity activity, CountDatabase countDatabase, WordDatabase wordDatabase, String speechText, TextView countText) {
            //ここで、インスタンス化した時に渡された引数の値をフィールドの値に代入する
            weakReference = new WeakReference<>(activity);
            this.countDatabase = countDatabase;
            this.wordDatabase = wordDatabase;
            this.speechText = speechText;
            this.countText = countText;
        }

        //今日の日付を取得するメソッド
        private String getToday(int days) {
            DateFormat df = new SimpleDateFormat("yyyy/ MM/ dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis() + DAY * days);

            return df.format(date);
        }

        //今日の日付を取得するメソッド
        private String getDay(int days) {
            DateFormat df = new SimpleDateFormat("yyy/ MM/ dd");
            Date date = new Date(System.currentTimeMillis() + DAY * days);

            return df.format(date);
        }

        //AsyncTaskの実装
        //非同期処理(Activityの処理を止めることなく実行したい処理)
        //Activityに何も書いてないけどどうなんでしょうね？？
        //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
        @Override
        protected Integer doInBackground(Void... params) {
            WordTableDao wordTableDao = wordDatabase.wordTableDao();
            DaysCountDao daysCountDao = countDatabase.daysCountDao();

            //データベースに値を入れる処理
//            wordTableDao.insert(new WordTable("バカ"));
//            wordTableDao.insert(new WordTable("アホ"));
//            wordTableDao.insert(new WordTable("死ね"));
//            wordTableDao.insert(new WordTable("消えろ"));
//            wordTableDao.insert(new WordTable("ハゲ"));

            //単語DBからすべてのワードを取得してリストに代入する。
            List<WordTable> atList = wordTableDao.getAll();

            for (WordTable at : atList) {
                //単語を１つずつ取り出して、正規表現のパターンに当てはめる
                Pattern pattern = Pattern.compile(".*" + at.getWord() + ".*");
                Matcher matcher = pattern.matcher(speechText);

                //正規表現にマッチしたらtrueしなかったらfalseになる
                if (matcher.find()) {
                    //カウントDBに今日の日付をDBに挿入する
                    //最終的に対応する日付の行数をカウントすればその日の日付がカウントされる
                    daysCountDao.insert(new DaysCount(getToday(0), speechText));
                    //今日の日付の分を今日行った暴言の回数として扱う
                    count = daysCountDao.getCount("%" + getDay(0) + "%");

                    Log.d("AAA", String.valueOf(count));
                    //ループされても困るので、一回正規表現にマッチすればループから抜けるようにする。
                    break;
                }
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakReference.get();
            DaysCountDao daysCountDao = countDatabase.daysCountDao();
            if (activity == null) {
                return;
            }
            //DBから今日の日付分の回数を取得してテキスト
            countText.setText(String.valueOf(count));
        }
    }

    /**
     * 以下がRecognitionListenerの実装
     * onCreate内のsetRecognitionListenerの引数にlistenerを設定するために、新しくクラスを宣言してそのインスタンスを渡して上げる。
     */
    class listener implements RecognitionListener {
        private CountDatabase countDatabase;
        private WordDatabase wordDatabase;
        private TextView CountTextView;

        //コンストラクタ―
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
            }
        }

        //追加イベントを受信したら呼び出される。
        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent: eventType=" + i);

        }
    }

}