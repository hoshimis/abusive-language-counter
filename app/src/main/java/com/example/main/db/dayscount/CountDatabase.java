package com.example.main.db.dayscount;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * データベース　テーブル作成
 * RoomDatabaseを継承したabstractクラス
 * ・daysCount　→　回数カウントするデータベース
 */

@Database(entities = {DaysCount.class}, version = 1, exportSchema = false)
public abstract class CountDatabase extends RoomDatabase {
    public abstract DaysCountDao daysCountDao();
}
