package com.example.main.ui.bbs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.main.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ReplyActivity extends AppCompatActivity  {

    ArrayList<String> data = new ArrayList<>();



    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference();

    //xmlとの紐づけ用の変数
    private EditText commentEditText;
    private BbsCustomAdapter mBbsCustomAdapter;
    private ReplyCustomAdapter mReplyCustomAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Intent intent = getIntent();
        String firebaseKey = intent.getStringExtra("FirebaseKey");
        String title = intent.getStringExtra("Title");

        TextView textView3 = findViewById(R.id.textVIew3);
        textView3.setText(title);

//もどるボタンの処理
        findViewById(R.id.back_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

//返信ボタンの処理
        commentEditText = findViewById(R.id.comment);

        findViewById(R.id.add_button2).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String comment = commentEditText.getText().toString();
                        String key = reference.push().getKey();

                        //入力欄どちらかがからだったら何もしない
                        if (comment.isEmpty()) {
                            return;
                        }
                        ReplyData replyData = new ReplyData(key, comment);

                        //Threadsに一意のIDをつけて投稿内容を保存する
                        reference.child("Threads").child("Thread").child(firebaseKey).child("reply").child(key).setValue(replyData);

                        //RealtimeDatabaseのインスタンスを取得
                        db = FirebaseDatabase.getInstance();

                        DatabaseReference pushedThreadsRef = db.getReference().push();
                        String threadsId = pushedThreadsRef.getKey();

                        Log.d(TAG, "onCreateView: " + threadsId);


                        //書き込み先の場所を参照する
                        reference = db.getReference("Threads").child("Thread").child(firebaseKey).child("reply");

                        //リストビューとの紐づけ
                       ListView mListView = findViewById(R.id.list_view2);

                        //CustomAdapterをセットする
                        mReplyCustomAdapter = new ReplyCustomAdapter(getApplicationContext(), R.layout.card_view, new ArrayList<ReplyData>());
                        mListView.setAdapter(mReplyCustomAdapter);

                        //firebaseと同期するリスナー
                        reference.addChildEventListener(new ChildEventListener() {
                            //アイテムのリストを取得するか、アイテムのリストへの追加がないかを監視する
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                //コールバック関数はDataSnapshot
                                //スナップショットとは、ある時点における特定のデータベースにあるデータの全体像を写し取ったもの
                                //スナップショットのgetValue()を呼び出すと、データの言語固有のオブジェクト表現が返される
                                //参照先の場所にデータが存在しない場合は、値がnullになる

                                Log.d(TAG, "onChildAdded: " + snapshot.getValue().getClass());

                                //BBSDataクラスで指定した形式にする
                                ReplyData replyData = snapshot.getValue(ReplyData.class);
                                //上の行で得た値を、CustomAdapterに挿入する
                                mReplyCustomAdapter.add(replyData);
                                //CustomAdapterの変更を画面に反映させる
                                mReplyCustomAdapter.notifyDataSetChanged();
                            }

                            //リスト内にアイテムに対する変更がないかを監視する
                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            //リストから削除されるアイテムがないかを監視する
                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            //並べ替えリストの項目順に変更がないかを監視する
                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            //ログを記録するなどError時の処理を記載する
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

    }
}