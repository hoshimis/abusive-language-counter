package com.example.app_nav.ui.bbs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BbsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BbsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("掲示板がめんだよー");
    }

    public LiveData<String> getText() {
        return mText;
    }
}