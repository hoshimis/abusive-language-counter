package com.example.main.ui.bbs;

public class ReplyData {

    public String comment;
    public String replyKey;

    public ReplyData(String key, String comment) {
        this.replyKey = key;
        this.comment = comment;
    }

    public ReplyData() {

    }

    public String getReplyKey() {
        return replyKey;
    }

    public String getComment() {
        return comment;
    }

}
