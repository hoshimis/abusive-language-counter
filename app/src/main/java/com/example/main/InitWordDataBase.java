package com.example.main;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordTable;
import com.example.main.db.wordtable.WordTableDao;
import com.example.main.util.GetDay;

import java.lang.ref.WeakReference;

public class InitWordDataBase extends AsyncTask<Void, Void, Integer> {
    /*フィールド*/

    //データベースの準備
    WordDatabase wordDatabase;

    //日付取得機能の準備
    GetDay gt = new GetDay();

    private final WeakReference<Activity> weakReference;
    //受け取った文字列を格納する
    String initWord;

    //受け取った文字列をSplitで分割して格納する
    String[] word;



    //コンストラクタ―
    public InitWordDataBase(Activity activity, String initWord, WordDatabase wordDatabase) {
        weakReference = new WeakReference<>(activity);
        this.initWord = initWord;
        this.wordDatabase = wordDatabase;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        word = initWord.split(",");

    }

    //AsyncTaskの実装
    //非同期処理(Activityの処理を止めることなく実行したい処理)
    //Activityに何も書いてないけどどうなんでしょうね？？
    //このフラグメントにいろんな処理書いてあるからそれでいいのかもしれないですね
    @Override
    protected Integer doInBackground(Void... params) {
        WordTableDao wordTableDao = wordDatabase.wordTableDao();

        //データベースに値を入れる処理

//            wordTableDao.insert(new WordTable("バカ"));
//            wordTableDao.insert(new WordTable("アホ"));
//            wordTableDao.insert(new WordTable("死ね"));
//            wordTableDao.insert(new WordTable("消えろ"));
//            wordTableDao.insert(new WordTable("ハゲ"));
//            wordTableDao.insert(new WordTable("おはよう"));
//            wordTableDao.insert(new WordTable("ばーか"));
//            wordTableDao.insert(new WordTable("今日は"));


        for (String at : word) {
            wordTableDao.insert(new WordTable(at));
        }
        return 0;
    }

    //非同期処理が実行し終わった後に実行されるメソッド
    @Override
    protected void onPostExecute(Integer code) {
        //実行されるときに、呼び出し元のActivityが存在しなかったら何もしない
        //Activity activity = weakReference.get();
//        if (activity == null) {
//            return;
//        }


    }
}