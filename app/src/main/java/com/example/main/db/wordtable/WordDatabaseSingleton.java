package com.example.main.db.wordtable;

import android.content.Context;

import androidx.room.Room;

public class WordDatabaseSingleton {
    private static WordDatabase instance = null;

    public static WordDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        }

        instance = Room.databaseBuilder(context,
                WordDatabase.class, "words").build();
        return instance;
    }
}
