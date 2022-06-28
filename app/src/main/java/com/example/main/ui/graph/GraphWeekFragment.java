package com.example.main.ui.graph;

import static android.content.ContentValues.TAG;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.util.GetDay;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphWeekFragment extends Fragment {
    private BarChart mchart;
    private Typeface tfRegular;

    //週間の回数を格納する配列を宣言
    static int[] weekCount = new int[7];

    //データの設定
    static final List<Data> data = new ArrayList<>();
    //日付取得機能の準備
    GetDay gt = new GetDay();
    CountDatabase countDatabase;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GraphViewModel dashboardViewModel = new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph_week, container, false);

        //XMLとの紐づけ
        TextView dateTitle = root.findViewById(R.id.graph_week);
        TextView todaySum = root.findViewById(R.id.todaySum);
        TextView comparedYesterday = root.findViewById(R.id.comparedYesterday);
        TextView weekSum = root.findViewById(R.id.weekSum);
        mchart = root.findViewById(R.id.chart1);

        //今週の合計値を格納する為の変数
        int weekSumCount = 0;

        //グラフに表示するカウント数をここでDBに接続して挿入しておく
        countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        new GetCountAsyncTask(countDatabase, GetCountAsyncTask.GET_WEEK, 0).execute();

        //DBからデータを取得してくる前にグラフの描画が終わってしまうのですこしだけメインスレッドを止める
        //もっと格好いいやり方なかったかなぁ
        //→await, promiseみたいな感じで、非同期処理が終了したらメインスレッドを始めるみたいな
        //逐次処理的なことをしたかった
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //グラフの描画
        setGraph(mchart);

        root.findViewById(R.id.month).setOnClickListener(
                view -> {
                    Fragment toMonth = new GraphMonthFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonth);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.year).setOnClickListener(
                view -> {
                    Fragment toYear = new GraphYearFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toYear);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        for (int tmp : weekCount) {
            weekSumCount += tmp;
        }

        //今日の回数と日付を表示する
        dateTitle.setText(getResources().getString(R.string.week_title, gt.getDate(GetDay.SIX_DAYS_AGO, "MM/ dd"), gt.getDate(GetDay.TODAY, "MM/ dd")));
        todaySum.setText(getResources().getString(R.string.week_count_text, weekCount[0]));
        comparedYesterday.setText(getResources().getString(R.string.week_count_text, weekCount[0] - weekCount[1]));
        weekSum.setText(getResources().getString(R.string.week_count_text, weekSumCount));

        setData(data);

        //合計の表情画像表示
        ImageView sum_today= root.findViewById(R.id.today_sum_face);
        if(weekCount[0]==0){
            sum_today.setImageResource(R.drawable.level_0);
        }else if(weekCount[0]<=10){
            sum_today.setImageResource(R.drawable.level_15);
        }else if(weekCount[0]<=20){
            sum_today.setImageResource(R.drawable.level_510);
        }else{
            sum_today.setImageResource(R.drawable.level_max);
        }
        //前日比の表情画像表示
        ImageView  compared_yesterday= root.findViewById(R.id.comparedYesterday_face);
        if(weekCount[0] - weekCount[1]==0){
            compared_yesterday.setImageResource(R.drawable.level_0);
        }else if(weekCount[0] - weekCount[1]<=10){
            compared_yesterday.setImageResource(R.drawable.level_15);
        }else if(weekCount[0] - weekCount[1]<=20){
            compared_yesterday.setImageResource(R.drawable.level_510);
        }else{
            compared_yesterday.setImageResource(R.drawable.level_max);
        }
        //週間合計の表情画像表示
        ImageView  sum_week= root.findViewById(R.id.week_sum_face);
        if(weekSumCount==0){
            sum_week.setImageResource(R.drawable.level_0);
        }else if(weekSumCount<=10){
            sum_week.setImageResource(R.drawable.level_15);
        }else if(weekSumCount<=20){
            sum_week.setImageResource(R.drawable.level_510);
        }else{
            sum_week.setImageResource(R.drawable.level_max);
        }

        return root;
    }

    //グラフを描画する処理
    private void setGraph(BarChart mchart) {
        mchart.setBackgroundColor(-35);
        mchart.setExtraTopOffset(0);
        mchart.setExtraBottomOffset(5);//値を大きくするとx軸が上に行く
        mchart.setExtraLeftOffset(0);
        mchart.setExtraRightOffset(0);
        mchart.setDrawBarShadow(false);
        mchart.setDrawBorders(false);
        mchart.getDescription().setEnabled(false);

        mchart.setPinchZoom(true);
        mchart.setDrawGridBackground(false);
        XAxis xAxis = mchart.getXAxis();

        //横線の値を設定する
        //一番右端を今日として、左に向かって一日前ずつ表示していく
        String[] label = new String[]{
                gt.getDate(GetDay.SIX_DAYS_AGO, "dd"),
                gt.getDate(GetDay.FIVE_DAYS_AGO, "dd"),
                gt.getDate(GetDay.FOUR_DAYS_AGO, "dd"),
                gt.getDate(GetDay.THREE_DAYS_AGO, "dd"),
                gt.getDate(GetDay.TWO_DAYS_AGO, "dd"),
                gt.getDate(GetDay.YESTERDAY, "dd"),
                gt.getDate(GetDay.TODAY, "dd"),
        };
        xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));

        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false); //グラフの縦線
        xAxis.setDrawAxisLine(true);//グラフの○○日のところの線
        xAxis.setTextColor(-16777216);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(7);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1);

        YAxis left = mchart.getAxisLeft();
        left.setDrawLabels(false); //格子の横線
        left.setSpaceTop(25f);
        left.setSpaceBottom(0);//値が０でもx軸から離れないようにするために０にする
        left.setDrawAxisLine(true);//y軸の数値のすぐ横の線
        left.setDrawGridLines(false); //なんか
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        mchart.getAxisRight().setEnabled(false);
        mchart.getLegend().setEnabled(false);
    }

    public void setData(List<Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int origin=Color.rgb(184,90,78);
        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // 具体的な色
            if (d.yValue >= 0)
                colors.add(red);
            else
                colors.add(green);
        }

        BarDataSet set;

        if (mchart.getData() != null &&
                mchart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mchart.getData().getDataSetByIndex(0);
            set.setValues(values);
            mchart.getData().notifyDataChanged();
            mchart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(origin);//棒グラフの色
            set.setValueTextColors(Collections.singletonList(origin));//グラフ上の文字の色

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new ValueFormatter());
            data.setBarWidth(0.8f);

            mchart.setData(data);
            mchart.invalidate();
        }
    }

    private static class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }

}
