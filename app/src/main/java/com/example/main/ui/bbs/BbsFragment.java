package com.example.main.ui.bbs;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class BbsFragment extends Fragment implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    //Realtime Databaseを使った掲示板の作成
    //最初は、FireStoreを使おうとしたけど分かりずらかったからRealtimeDatabaseを使うことにします。

    private DatabaseReference reference;
    private BbsCustomAdapter mBbsCustomAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_bbs, container, false);
        //RealtimeDatabaseのインスタンスを取得
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference pushedThreadsRef = db.getReference().push();
        String threadsId = pushedThreadsRef.getKey();

        Log.d(TAG, "onCreateView: " + threadsId);


        //書き込み先の場所を参照する
        reference = db.getReference("Threads").child("Thread");

        //リストビューとの紐づけ
        ListView mListView = root.findViewById(R.id.list_view);

        //CustomAdapterをセットする
        mBbsCustomAdapter = new BbsCustomAdapter(requireContext().getApplicationContext(), R.layout.card_view, new ArrayList<>());
        mListView.setAdapter(mBbsCustomAdapter);

        //LongListenerを設定
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemClickListener(this);

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
                BbsData bbsData = snapshot.getValue(BbsData.class);
                //上の行で得た値を、CustomAdapterに挿入する
                mBbsCustomAdapter.add(bbsData);
                //CustomAdapterの変更を画面に反映させる
                mBbsCustomAdapter.notifyDataSetChanged();
            }

            //リスト内にアイテムに対する変更がないかを監視する
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            //リストから削除されるアイテムがないかを監視する
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                BbsData result = snapshot.getValue(BbsData.class);
                //resultがnullだったら何もしない
                if (result == null) return;

                //FirebaseKeyを取得する
                BbsData item = mBbsCustomAdapter.getBBSDataKey(result.getFirebaseKey());

                //上の行に該当するリストの要素を削除
                mBbsCustomAdapter.remove(item);
                //CustomAdapterの変更を画面に反映させる
                mBbsCustomAdapter.notifyDataSetChanged();
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

        //スレッド作成画面に遷移
        root.findViewById(R.id.add_button).setOnClickListener(view -> {
            Fragment toAdd = new AddFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, toAdd);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return root;
    }

    //リストを長押しでその要素を削除するようにする。
    //本当は別画面に遷移するようにしたい。現時点では、削除するだけにしておく
    //別画面に遷移したときに、なんのデータをどうやって持っていくか不明瞭なため
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        BbsData bbsData = mBbsCustomAdapter.getItem(position);

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete?")
                .setMessage("このスレッドを削除しますか？")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // OK button pressed
                    reference.child(bbsData.getFirebaseKey()).removeValue();
                })
                .setNegativeButton("No", null)
                .show();
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        BbsData bbsData = mBbsCustomAdapter.getItem(position);
        String firebaseKey = bbsData.getFirebaseKey();
        String title = bbsData.getTitle();

        //遷移先のフラグメントに情報を渡す処理
        Fragment toReply = new ReplyFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("FirebaseKey", firebaseKey);
        bundle.putString("Title", title);
        toReply.setArguments(bundle);
        transaction.replace(R.id.nav_host_fragment, toReply);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}