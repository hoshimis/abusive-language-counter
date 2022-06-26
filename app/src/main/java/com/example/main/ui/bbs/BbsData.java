package com.example.main.ui.bbs;

public class BbsData {
    public String title;
    public String content;
    public String firebaseKey;


    public BbsData(String key, String title, String content) {
        this.firebaseKey = key;
        this.title = title;
        this.content = content;
    }

    public BbsData() {
    }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String context) {
        this.content = context;
    }

}