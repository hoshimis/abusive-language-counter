package com.example.main.db.dayscount;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DaysCount {
    //プライマリーキー
    @PrimaryKey(autoGenerate = true)
    private int id;

    //カラム　日付
    @ColumnInfo(name = "date")
    private final String date;
    //カラム　日付
    @ColumnInfo(name = "word")
    private final String word;

    //コンストラクタ―
    public DaysCount(String date, String word) {
        this.date = date;
        this.word = word;
    }

    /***
     *以下セッター、ゲッターメソッド
     */
    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public int getId() {
        return id;
    }
    public String getWord() {
        return word;
    }
}