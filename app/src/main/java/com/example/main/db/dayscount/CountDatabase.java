package com.example.main.db.dayscount;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * データベース　テーブル作成
 * RoomDatabaseを継承したabstractクラス
 * <p>
 * ここでは、二つのテーブルを定義している
 * ・wordTable　→　単語データベース
 * ・daysCount　→　回数カウントするデータベース
 */

@Database(entities = {DaysCount.class}, version = 1, exportSchema = false)
public abstract class CountDatabase extends RoomDatabase {
    public abstract DaysCountDao daysCountDao();
}
