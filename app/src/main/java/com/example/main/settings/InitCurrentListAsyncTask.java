package com.example.main.settings;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InitCurrentListAsyncTask extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/
    //データベースの準備
    WordDatabase wordDatabase;

    private final WeakReference<Activity> weakReference;
    //受け取った文字列を格納する
    ArrayList<String> data;

    //アダプター
    ArrayAdapter<String> adapter;

    //コンストラクタ―
    public InitCurrentListAsyncTask(Activity activity, WordDatabase wordDatabase, ArrayList<String> data, ArrayAdapter<String> adapter) {
        weakReference = new WeakReference<>(activity);
        this.data = data;
        this.wordDatabase = wordDatabase;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();

        //単語DBからすべてのワードを取得してリストに代入する。
        List<WordTable> atList = wordTableDao.getAll();
        for (WordTable str : atList) {
            data.add(str.getWord());
        }
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
        adapter.notifyDataSetChanged();
    }
}