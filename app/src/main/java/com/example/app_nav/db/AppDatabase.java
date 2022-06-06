package com.example.app_nav.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * データベースの作成
 * RoomDatabaseを継承したabstractクラス
 */

@Database(entities = {WordTable.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WordTableDao wordTableDao();
}