package com.example.app_nav.ui.recognition;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.app_nav.R;

import static android.Manifest.permission.RECORD_AUDIO;

public class RecognitionFragment extends Fragment {

    private RecognitionViewModel homeViewModel;

    /**
     * 以下音声認識に使う変数
     */

    private final int PERMISSIONS_RECORD_AUDIO = 1000;

    private SpeechRecognizer speechRecognizer;
    private TextView mText;
    private AlertDialog.Builder mAlert;
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

        final TextView textView = root.findViewById(R.id.text_Recognition);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        mText = (TextView) root.findViewById(R.id.recognize_text_view);
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
     * TODO:onRequestPermissionsResultについて調べる
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
     * スピーチtp中でも変換を受け取る（onPartialResultが呼ばれる）ためには、
     * intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULT, true)とする
     *
     * TODO：話者が話し始めたら自動的に認識が始まるようにする
     */

    public void startRecording() {
        if (speechRecognizer == null && checkSpeechRecognizer()) {
            mText.setText(getString(R.string.prepare_speech));
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
        if (speechRecognizer != null && checkSpeechRecognizer()) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
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


    /**
     * 以下がRecognitionListenerの実装
     * onCreate内のsetRecognitionListenerの引数にlistenerを設定するために、新しくクラスを宣言してそのインスタンスを渡して上げる。
     */
    class listener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech");
            mText.setText("");
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] aByte) {
            Log.d(TAG, "onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech");
            stopRecording();
        }

        @Override
        public void onError(int i) {
            Log.d(TAG, "onError=" + i);

            mText.setText(getString(R.string.speech_error) + "\nエラーコード：" + i);
            stopRecording();
        }

        @Override
        public void onResults(Bundle bundle) {
            Log.d(TAG, "onResults:");
        }

        /**
         *　ここに認識した結果がかえってくる
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

        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent: eventType=" + i);

        }
    }

}