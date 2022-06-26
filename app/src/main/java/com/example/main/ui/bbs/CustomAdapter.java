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

public class CustomAdapter extends ArrayAdapter<BbsData> {
    private List<BbsData> mCards;

    public CustomAdapter(Context context, int layoutResourceId, List<BbsData> BBSData) {
        super(context, layoutResourceId, BBSData);

        this.mCards = BBSData;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Nullable
    @Override
    public BbsData getItem(int position) {
        return mCards.get(position);
    }

    @NonNull
    @Override
    public android.view.View getView(final int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView != null) {
            viewHolder = (ViewHolder) convertView.getTag();

        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_view, null);
            viewHolder = new ViewHolder();

            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.content_text_view);
            convertView.setTag(viewHolder);

        }

        BbsData BBSData = mCards.get(position);

        viewHolder.titleTextView.setText(BBSData.getTitle());
        viewHolder.contentTextView.setText(BBSData.getContent());

        return convertView;
    }


    public BbsData getBBSDataKey(String key) {
        for (BbsData BBSData : mCards) {
            if (BBSData.getFirebaseKey().equals(key)) {
                return BBSData;
            }
        }

        return null;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView contentTextView;
    }

}