package com.example.main.ui.recognition;

import static android.Manifest.permission.RECORD_AUDIO;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;


import java.util.ArrayList;

public class RecognitionFragment extends Fragment {
    //以下フィールド
    //以下音声認識に使う変数
    private final int PERMISSIONS_RECORD_AUDIO = 1000;
    private SpeechRecognizer speechRecognizer;
    private TextView mText;
    //    private TextView titleView;
    private TextView countText;
    private ImageView jaggedImage;
    //log.dで使う文字列
    private final String TAG = "MainActivity";
    //DBの宣言
    private WordDatabase wordDatabase;
    private CountDatabase countDatabase;

    //今日の暴言をいった回数をカウントするための変数
    public static int count;

    //リスト構造を宣言
    ArrayList<String> data = new ArrayList<>();
    ArrayAdapter<String> adapter;

    /**
     * ActivityのようにFragmentにもライフサイクルがある
     * Viewを生成するたタイミングで呼ばれるのがonCreateView()
     * onCreateView()で渡されるLayoutInflaterにFragmentのレイアウトを挿入して返す
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recognition, container, false);


        //mText -> 認識した音声をテキスト化して表示するテキストビューを紐づけ
        //titleView -> 音声認識の状態を表示する部分のテキストビューを紐づけ
        //countText -> 単語DBと何回マッチしたかを表示するテキストビューの紐づけ
        //listView -> その日になんの言葉を話したか表示するListViewの紐づけ
        mText = root.findViewById(R.id.recognize_text_view);
//        titleView = root.findViewById(R.id.text_Recognition);
        countText = root.findViewById(R.id.count_text);
        ListView listView = root.findViewById(R.id.listView);
        jaggedImage = root.findViewById(R.id.count_image);


        //データベースとの紐づけ
        wordDatabase = WordDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());

        //speechRecognizerにnullを代入
        speechRecognizer = null;

        //音声機能についてパーミッションを明示的にユーザにリクエストする処理
        if (ContextCompat.checkSelfPermission(requireActivity().getApplicationContext(), RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{RECORD_AUDIO}, PERMISSIONS_RECORD_AUDIO);
        }

        //画面に正円を描画するための処理
//        CircleView circleView = root.findViewById(R.id.circle_view);
//        circleView.setColor(R.color.teal_200);


        //SpeechRecognizerを使用することができるか確認する
        checkSpeechRecognizer();
        //使用できるならば、レコーディングを開始する
        if (checkSpeechRecognizer()) {
            startRecording();
        }

        //adapterの設定
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        //暴言と認識された言葉を日時とともにリストビューに追加していく
        //画面作成時に、今日話した言葉を挿入するようにする
        new InsertListViewAsyncTask(getActivity(), countDatabase, data, adapter).execute();
        //ビュー作成時にデータベースから回数を取得してきて、画面に表示する
        new GetTodayCountAsyncTask(getActivity(), countDatabase, countText, jaggedImage).execute();

        root.findViewById(R.id.setting_go).setOnClickListener(
                view -> {
                    Fragment toMonths = new SettingFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonths);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        return root;
    }


//      checkSpeechRecognizer()
//      音声認識サポート端末であるかどうかを調べるメソッド
//      サポート端末ではなかったらその旨のテキストを表示(string.xmlから)
//      録音が許可されていなかったらユーザに許可を求めるシステムダイアログを表示する。

    public Boolean checkSpeechRecognizer() {
        //音声認識が可能かチェックする
        if (!SpeechRecognizer.isRecognitionAvailable(requireActivity().getApplicationContext())) {
            mText.setText(getString(R.string.speech_not_available));
            return false;
        }

        //録音機能のパーミッションが許可されていなかったら許可するようにアラートを出す
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            mText.setText(getString(R.string.speech_not_granted));
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    },
                    PERMISSIONS_RECORD_AUDIO);
            return false;
        }
        return true;
    }


    // 許可ダイアログの承認結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permission, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permission, grantResults);
        Log.d(TAG, "onRequestPermissionResult");

        if (grantResults.length <= 0) {
            return;
        }

        if (requestCode == PERMISSIONS_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mText.setText("");
            }
        }
    }

    /**
     * SpeechRecognizerを生成して開始する。
     * スピーチ中でも変換を受け取る（onPartialResultが呼ばれる）ためには、
     * intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULT, true)とする
     */
    public void startRecording() {
        if (speechRecognizer == null && checkSpeechRecognizer()) {
//            titleView.setText(getString(R.string.prepare_speech));

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            speechRecognizer.setRecognitionListener(new listener(countDatabase, wordDatabase, countText));
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().getPackageName());
            //以下指定で途中の認識を拾う
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

            speechRecognizer.startListening(intent);
        }
    }

    //SpeechRecognizerをキャンセルして破棄
    public void stopRecording() {
//        titleView.setText("停止中");
        if (speechRecognizer != null && checkSpeechRecognizer()) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }

    // 音声認識を再開する
    public void restartListeningService() {
        stopRecording();
        startRecording();
    }

    //必要がなくなった SpeechRecognizer は破棄する
    //SpeechRecognizer の破棄は destroy で行う
    //使い終わったらdestroyで破棄する
    public void onDestroyView() {
        super.onDestroyView();
        speechRecognizer.destroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 以下がRecognitionListenerの実装
     * onCreate内のsetRecognitionListenerの引数にlistenerを設定するために、新しくクラスを宣言してそのインスタンスを渡して上げる。
     */
    class listener implements RecognitionListener {
        private final CountDatabase countDatabase;
        private final WordDatabase wordDatabase;
        private final TextView CountTextView;

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
            mText.setText("開始");
//            mText.setText("");
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
        //一度入力が終わったら停止したのちに音声入力を再開する
        @Override
        public void onEndOfSpeech() {
//            titleView.setText("停止");
            Log.d(TAG, "onEndOfSpeech");
        }

        //ネットワークエラー、音声入力に関するエラーが発生したら呼び出される
        //一定時間無入力、ネットワークもタイムアウトをしてしまうとエラーで止まってしまうので、
        //そのエラーが来ても、音声入力を再開するようにする
        @Override
        public void onError(int error) {
            String reason = "";
            switch (error) {
                // Audio recording error
                case SpeechRecognizer.ERROR_AUDIO:
                    reason = "ERROR_AUDIO";
                    break;
                // Other client side errors
                case SpeechRecognizer.ERROR_CLIENT:
                    reason = "ERROR_CLIENT";
                    break;
                // Insufficient permissions
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    reason = "ERROR_INSUFFICIENT_PERMISSIONS";
                    break;
                // 	Other network related errors
                case SpeechRecognizer.ERROR_NETWORK:
                    reason = "ERROR_NETWORK";
                    /* ネットワーク接続をチェックする処理をここに入れる */
                    break;
                // Network operation timed out
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    reason = "ERROR_NETWORK_TIMEOUT";
                    break;
                // No recognition result matched
                case SpeechRecognizer.ERROR_NO_MATCH:
                    reason = "ERROR_NO_MATCH";
                    break;
                // RecognitionService busy
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    reason = "ERROR_RECOGNIZER_BUSY";
                    break;
                // Server sends error status
                case SpeechRecognizer.ERROR_SERVER:
                    reason = "ERROR_SERVER";
                    /* ネットワーク接続をチェックをする処理をここに入れる */
                    break;
                // No speech input
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    reason = "ERROR_SPEECH_TIMEOUT";
                    break;
            }
            Log.d("開発用のログを出してるよ！！", "onError=" + error + reason);

            if ((error == SpeechRecognizer.ERROR_NO_MATCH) || (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) || (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT)) {
                //ネットワークエラーと、無音の時にエラーが帰ってきやすいので一度止めてから再スタートして挙げる
                restartListeningService();
                Log.d("開発用のログを出してるよ！！", "onError: 音声認識を再開させました。");
            }
        }

        //音声入力が終わり、結果が準備できたら呼び出される
        @Override
        public void onResults(Bundle bundle) {
            Log.d(TAG, "onResults:");
            restartListeningService();
        }

        //ここに認識した結果がかえってくる部分的な認識結果が利用可能な時に呼び出される。
        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d(TAG, "onPartialResults");
            String str = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
            if (str.length() > 0) {
                mText.setText(str);
                //ここで、認識した言葉を非同期処理に渡して、マッチするかを確認する
                new DataStoreAsyncTask(getActivity(), countDatabase, wordDatabase, str, CountTextView, jaggedImage).execute();
                new InsertListViewAsyncTask(getActivity(), countDatabase, data, adapter).execute();
            }
        }

        //追加イベントを受信したら呼び出される。
        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent: eventType=" + i);
        }
    }
}