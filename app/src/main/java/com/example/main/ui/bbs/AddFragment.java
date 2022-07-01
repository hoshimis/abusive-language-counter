package com.example.main.ui.bbs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.main.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class AddFragment extends Fragment {

    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference reference = db.getReference();

    //xmlとの紐づけ用の変数
    private EditText titleEditText, contentEditText;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        titleEditText = root.findViewById(R.id.title);
        contentEditText = root.findViewById(R.id.content);

        root.findViewById(R.id.add_button).setOnClickListener(view -> {
            String title = titleEditText.getText().toString();
            String content = contentEditText.getText().toString();
            String key = reference.push().getKey();

            //入力欄どちらかがからだったら何もしない
            if (title.isEmpty() || content.isEmpty()) {
                return;
            }

            BbsData bbsData = new BbsData(key, title, content);

            //Threadsに一意のIDをつけて投稿内容を保存する
            reference.child("Threads").child("Thread").child(key).setValue(bbsData).addOnSuccessListener(unused -> {
                Fragment toBack = new BbsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment, toBack);
                transaction.addToBackStack(null);
                transaction.commit();
            });
        });

        return root;
    }


}