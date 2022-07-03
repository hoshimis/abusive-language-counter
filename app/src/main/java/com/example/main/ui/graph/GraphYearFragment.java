package com.example.main.ui.graph;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphYearFragment extends Fragment {
    /*フィールド*/
    //maker Ryo Kamizato feat シュトゥーデューム

    //年間の回数を格納する配列を宣言
    static int[] yearCount = new int[12];
    //データの設定
    static List<Data> data = new ArrayList<>();
    //日付取得機能の準備
    GetDay gt = new GetDay();
    final String YEAR = gt.getDate(GetDay.TODAY, "yyyy");
    final int MONTH = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));
    int yearSumCount;
    int yearMaxCount = 0;
    int yearMinCount = 10000000;
    private BarChart mChart;
    private Typeface tfRegular;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_graph_year, container, false);

        //XMLとの紐づけ
        TextView dateTitle = root.findViewById(R.id.graph_year);
        TextView yearSum = root.findViewById(R.id.yearSumCount);
        TextView yearMax = root.findViewById(R.id.monthMinMax);
        TextView yearAverage = root.findViewById(R.id.yearAverage);
        mChart = root.findViewById(R.id.chart2);

        //グラフの描画
        setGraph(mChart);

        //グラフに表示するカウント数をここでDBに接続して挿入しておく
        CountDatabase countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        new GetCountAsyncTask(countDatabase, GetCountAsyncTask.GET_YEAR, 0).execute();

        //DBからデータを取得してくる前にグラフの描画が終わってしまうのですこしだけメインスレッドを止める
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //画面遷移するためのボタン
        root.findViewById(R.id.week).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.month).setOnClickListener(
                view -> {
                    Fragment toMonth = new GraphMonthFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonth);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        for (int tmp : yearCount) {
            yearSumCount += tmp;
            if (yearMaxCount < tmp) {
                yearMaxCount = tmp;
            }

            if (yearMinCount > tmp) {
                yearMinCount = tmp;
            }
        }

        //年間の情報と日付を表示する
        dateTitle.setText(getResources().getString(R.string.year_title, YEAR));
        yearSum.setText(getResources().getString(R.string.year_count_text, yearSumCount));
        yearMax.setText(getResources().getString(R.string.year_count_min_max_text, yearMaxCount, yearMinCount));
        yearAverage.setText(getResources().getString(R.string.year_count_text, (yearSumCount / 12)));

        setData(data);

        //年間合計の表情画像表示
        ImageView sum_year = root.findViewById(R.id.year_sum_face);
        if (yearSumCount == 0) {
            sum_year.setImageResource(R.drawable.level_0);
        } else if (yearSumCount <= 3650) {
            sum_year.setImageResource(R.drawable.level_15);
        } else if (yearSumCount <= 7300) {
            sum_year.setImageResource(R.drawable.level_510);
        } else {
            sum_year.setImageResource(R.drawable.level_max);
        }
        //最大最小の表情画像表示
        ImageView min_mux = root.findViewById(R.id.min_max_face);
        if (yearMaxCount == 0 || yearMinCount == 0) {
            min_mux.setImageResource(R.drawable.level_0);
        } else if (yearMaxCount <= 300 || yearMinCount <= 300) {
            min_mux.setImageResource(R.drawable.level_15);
        } else if (yearMaxCount <= 600 || yearMinCount <= 600) {
            min_mux.setImageResource(R.drawable.level_510);
        } else {
            min_mux.setImageResource(R.drawable.level_max);
        }
        //年間平均の表情画像表示
        ImageView ave_month = root.findViewById(R.id.year_ave_face);
        if ((yearSumCount / MONTH) == 0) {
            ave_month.setImageResource(R.drawable.level_0);
        } else if ((yearSumCount / MONTH) <= 120) {
            ave_month.setImageResource(R.drawable.level_15);
        } else if ((yearSumCount / MONTH) <= 240) {
            ave_month.setImageResource(R.drawable.level_510);
        } else {
            ave_month.setImageResource(R.drawable.level_max);
        }
        return root;
    }

    private void setData(List<Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        int origin = Color.rgb(184, 90, 78);
        int green = Color.rgb(110, 190, 102);
        int red = Color.rgb(211, 74, 88);

        for (int i = 0; i < dataList.size(); i++) {

            Data d = dataList.get(i);
            BarEntry entry = new BarEntry(d.xValue, d.yValue);
            values.add(entry);

            // specific colors
            if (d.yValue >= 0)
                colors.add(red);
            else
                colors.add(green);
        }

        BarDataSet set;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mChart.getData().getDataSetByIndex(0);
            set.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(origin);//棒グラフの色
            set.setValueTextColors(Collections.singletonList(origin));//グラフ上の文字の色

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new ValueFormatter());
            data.setBarWidth(0.8f);

            mChart.setData(data);
            mChart.invalidate();
        }
    }

    private void setGraph(BarChart mChart) {
        mChart.setBackgroundColor(-35);
        mChart.setExtraTopOffset(0);
        mChart.setExtraBottomOffset(5);//値を大きくするとx軸が上に行く
        mChart.setExtraLeftOffset(0);
        mChart.setExtraRightOffset(0);
        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);
        mChart.getDescription().setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setDrawGridBackground(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false); //グラフ上の縦線
        xAxis.setDrawAxisLine(true);//グラフの○○日のところの線
        xAxis.setTextColor(-16777216);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(12);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false); //格子の横線
        left.setSpaceTop(25f);
        left.setSpaceBottom(0);//値が０でもx軸から離れないようにするために０にする
        left.setDrawAxisLine(true);//y軸の数値のすぐ横の線
        left.setDrawGridLines(false); //なんか
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(-16777216);
        left.setZeroLineWidth(0.7f);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
    }

    private static class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }
}