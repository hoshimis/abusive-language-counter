package com.example.main.db.wordtable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


/**
 * Entity
 * 主キーやカラムを示すためのアノテーションをつける
 */

/**
 * 単語データベース
 * 暴言とか言い換えの言葉とかのテーブルを定義する
 */
@Entity
public class WordTable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    //カラム　認識する言葉
    @ColumnInfo(name = "word")
    private String word;

    //実際に動かせてはいないけど、認識する言葉に対応した言い換えの言葉とかを入れれたらいいなぁ
    @ColumnInfo(name = "other")
    private String other;

    public WordTable(String word) {
        this.word = word;
    }

    //以下getter,setterメソッド
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getWord() {
        return word;
    }

    public String getOther() {
        return other;
    }
}