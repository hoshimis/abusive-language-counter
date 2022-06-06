package com.example.app_nav.db;

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

    //入力した単語と既存のデータベースの単語を比較する
    //TODO:引数と一致した単語を検出する
    @Query("")

    //
    @Insert
    void insertAll(WordTable... wordTable);

    @Insert
    void insert(WordTable wordTable);

    @Delete
    void delete(WordTable wordTable);
}
