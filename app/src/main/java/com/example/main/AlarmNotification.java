package com.example.main;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

//PendingIntentからのIntentを受け取るクラス。
//アラームを受けてトーストを行う
//前日の回数を通知するクラス
//追加で当日の分も追加することができればいい
public class AlarmNotification extends BroadcastReceiver {

    @Override   // データを受信した
    public void onReceive(Context context, Intent intent) {

        //ここで、RequestCodeを受け取ってるから、分岐をいい感じにできそう
        int requestCode = intent.getIntExtra("RequestCode", 0);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT |
                                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "default";
        // app name
        String title = context.getString(R.string.app_name);

        // メッセージ
        String message = "前日の回数:「ここに前日の回数を入れたい」";

        // Notification　Channel 設定
        NotificationChannel channel =
                new NotificationChannel(channelId, title,
                        NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(message);

        NotificationManager notificationManager =
//                (NotificationManager)context.getSystemService(NotificationManager.class);
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        //ステータスバーに表示されるアイコンを指定する
                        .setSmallIcon(android.R.drawable.btn_star)
                        //通知のタイトルを設定する
                        .setContentTitle("前日の回数")
                        //通知の本文を設定する
                        .setContentText(message + "11時です")
                        //通知のプライオリティを設定する
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        //通知の
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat
                = NotificationManagerCompat.from(context);

        // 通知を最終的に表示させる設定
        notificationManagerCompat.notify(R.string.app_name, builder.build());

    }
}