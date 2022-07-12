package com.example.main.settings.addword;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;

import java.lang.ref.WeakReference;

public class AddWordAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    //データベースの準備
    WordDatabase wordDatabase;

    private final WeakReference<Activity> weakReference;
    //受け取った文字列を格納する
    String addWord;

    //アダプター
    ArrayAdapter<String> adapter;

    //コンストラクタ―
    public AddWordAsyncTask(Activity activity, String addWord, WordDatabase wordDatabase, ArrayAdapter adapter) {
        weakReference = new WeakReference<>(activity);
        this.addWord = addWord;
        this.wordDatabase = wordDatabase;
        this.adapter = adapter;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();
        //新しいワードを追加する
        wordTableDao.insert(new WordTable(addWord));
        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    @Override
    protected void onPostExecute(Integer code) {
        //実行されるときに、呼び出し元のActivityが存在しなかったら何もしない
        //ずっとメインアクティビティ上でやってるからなくなるわけがない
        Activity activity = weakReference.get();
        if (activity == null) {
            return;
        }

        Log.d("非同期処理後の処理", "onPostExecute: " + "値を更新するよ");

        //新規追加した後にリストビューを更新する
        adapter.notifyDataSetChanged();
    }
}