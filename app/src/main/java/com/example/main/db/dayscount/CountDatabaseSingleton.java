package com.example.main.db.dayscount;

import android.content.Context;

import androidx.room.Room;


/**
 * DataBase呼び出し用のクラス
 * シングルプロセスで実行するアプリの場合は、AppDatabaseオブジェクトをインスタンス化する際にシングルトン設計パターンに従う
 * インスタンスは非常に高コストであり、単一のプロセス内でインスタンスにアクセスする必要はない
 * 要は、生成されるインスタンスを1つに制限するデザイン設計？？
 */
public class CountDatabaseSingleton {
    private static CountDatabase instance = null;

    public static CountDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        }

        instance = Room.databaseBuilder(context,
                CountDatabase.class, "count").build();
        return instance;
    }
}