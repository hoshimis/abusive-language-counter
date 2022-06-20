package com.example.main.db.dayscount;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

//カウントした値を挿入するデータベースを操作するメソッドを定義する

@Dao
public interface DaysCountDao {
    //デーブルの要素をすべて取得する
    @Query("select * from dayscount")
    List<DaysCount> getAll();

    //idを指定して特定の要素を取得する
    @Query("select * from dayscount where id in (:ids)")
    List<DaysCount> loadAllByIds(int[] ids);

    //指定した日付の文字を全抽出
    @Query("select * from dayscount where date like :date")
    List<DaysCount> getDayAll(String date);

    //指定した日付が何行あるかをカウントする（ココが何回話したかをカウントする機能）
    @Query("select count(*) from dayscount where date like :date")
    int getCount(String date);

    //デフォルト：要素を挿入
    @Insert
    void insertAll(DaysCount... daysCount);

    //デフォルト：要素を挿入
    @Insert
    void insert(DaysCount daysCount);

    //デフォルト：値を削除する
    @Delete
    void delete(DaysCount daysCount);
}