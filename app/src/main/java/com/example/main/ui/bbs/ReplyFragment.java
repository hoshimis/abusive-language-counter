package com.example.main.ui.bbs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class ReplyFragment extends Fragment {

    ArrayList<String> data = new ArrayList<>();


    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference();
    DatabaseReference replyReference = db.getReference();

    //xmlとの紐づけ用の変数
    private EditText commentEditText;
    private BbsCustomAdapter mBbsCustomAdapter;
    private ReplyCustomAdapter mReplyCustomAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_reply, container, false);

        String firebaseKey = getArguments().getString("FirebaseKey");
        String title = getArguments().getString("Title");
        //読込先の場所を参照する
        replyReference = db.getReference("Threads").child("Thread").child(firebaseKey).child("reply");

        //遷移元のフラグメントから情報を受け取る処理
//        Intent intent = getIntent();
//        String firebaseKey = intent.getStringExtra("FirebaseKey");
//        String title = intent.getStringExtra("Title");

        TextView textView3 = root.findViewById(R.id.reply_text_view);
        textView3.setText(title);

        //リストビューとの紐づけ
        ListView mListView = root.findViewById(R.id.reply_list_view);

        //CustomAdapterをセットする
        mReplyCustomAdapter = new ReplyCustomAdapter(requireActivity().getApplicationContext(), R.layout.card_reply_view, new ArrayList<ReplyData>());
        mListView.setAdapter(mReplyCustomAdapter);

        //もどるボタンの処理
        root.findViewById(R.id.back_button).setOnClickListener(view -> {
            Fragment back = new BbsFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, back);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        //返信ボタンの処理
        commentEditText = root.findViewById(R.id.comment);

        root.findViewById(R.id.reply_button).setOnClickListener(view -> {
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

            reference = db.getReference();
            commentEditText.setText("");
        });


        //firebaseと同期するリスナー
        replyReference.addChildEventListener(new ChildEventListener() {

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

        return root;
    }
}
