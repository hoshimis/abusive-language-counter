package com.example.main.db.wordtable;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * データベースの作成
 * RoomDatabaseを継承したabstractクラス
 *・wordTable　→　単語データベース
 */

@Database(entities = {WordTable.class}, version = 1, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {
    public abstract WordTableDao wordTableDao();
}