package com.example.main.ui.bbs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.main.R;

import java.util.List;

public class ReplyCustomAdapter extends ArrayAdapter<ReplyData> {
    private final List<ReplyData> mCards;

    public ReplyCustomAdapter(Context context, int layoutResourceId, List<ReplyData> ReplyData) {
        super(context, layoutResourceId, ReplyData);

        this.mCards = ReplyData;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Nullable
    @Override
    public ReplyData getItem(int position) {
        return mCards.get(position);
    }

    @NonNull
    @Override
    public android.view.View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final ReplyCustomAdapter.ViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ReplyCustomAdapter.ViewHolder) convertView.getTag();

        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_reply_view, null);
            viewHolder = new ReplyCustomAdapter.ViewHolder();

            viewHolder.titleTextView = convertView.findViewById(R.id.title_text_view);
            convertView.setTag(viewHolder);

        }

        ReplyData replyData = mCards.get(position);

        viewHolder.titleTextView.setText(replyData.getComment());

        return convertView;
    }

    static class ViewHolder {
        TextView titleTextView;
    }

}
