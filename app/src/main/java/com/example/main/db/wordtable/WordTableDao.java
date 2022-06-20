package com.example.main.db.wordtable;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/**
 * DAO
 * →クエリ、更新、挿入、削除の処理を作る
 * インターフェースとして作成して、メソッドを定義しておく
 */

@Dao
public interface WordTableDao {

    //wordtableを全選択
    @Query("select * from wordtable")
    List<WordTable> getAll();

    //wordtableからidを指定して選択
    @Query("select * from wordtable where id in (:ids)")
    List<WordTable> loadAllByIds(int[] ids);

    //オートインクリメントのリセット
    @Query("delete from sqlite_sequence where name='wordtable'")
    void autoIncrementReset();

    //デフォルト：データを挿入
    @Insert
    void insertAll(WordTable... wordTable);

    //デフォルト：データを挿入
    @Insert
    void insert(WordTable wordTable);

    //デフォルト:データを削除
    @Delete
    void delete(WordTable wordTable);
}
