package com.example.main.db.dayscount;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DaysCount {
    //プライマリーキー
    @PrimaryKey(autoGenerate = true)
    private int id;

    //カラム　日付
    @ColumnInfo(name = "date")
    private String date;

    //カラム　回数
    @ColumnInfo(name = "count")
    private int count;

    //コンストラクタ―
    //インスタンス化するときに回数を引数として渡す
    @Ignore
    public DaysCount(int count) {
        this.count = count;
    }

    //コンストラクタ―のオーバーロード
    public DaysCount(String date) {
        this.date = date;
    }


    /***
     *以下セッター、ゲッターメソッド
     */
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

}