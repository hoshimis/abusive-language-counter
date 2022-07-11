package com.example.main.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.main.R;
import com.example.main.db.wordtable.WordDatabase;
import com.example.main.db.wordtable.WordDatabaseSingleton;

import java.util.ArrayList;

public class AddWordDBFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_setting_notification, container, false);

        //データベースとの紐づけ
        //DBの宣言
        WordDatabase wordDatabase = WordDatabaseSingleton.getInstance(requireActivity().getApplicationContext());

        //リストビューを使う準備
        ListView currentWord = root.findViewById(R.id.current_word);
        ArrayList<String> data = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, data);
        currentWord.setAdapter(adapter);

        new InitCurrentListAsyncTask(requireActivity(), wordDatabase, data, adapter).execute();


        root.findViewById(R.id.submit_db_word).setOnClickListener(view -> {
            //XMLの紐づけと、edittextからの文字の取得
            EditText editDBWord = root.findViewById(R.id.edit_db_word);
            String addWord = editDBWord.getText().toString();

            new AddWordAsyncTask(requireActivity(), addWord, wordDatabase, adapter).execute();
        });

        return root;
    }
}