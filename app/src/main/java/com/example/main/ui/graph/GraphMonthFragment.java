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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphMonthFragment extends Fragment {
    /*フィールド*/
    //maker Ryo Kamizato feat シュトゥーデューム
    private BarChart mChart;
    private Typeface tfRegular;

    //日付取得機能の準備
    GetDay gt = new GetDay();
    //年、月をそれぞれ定義しておく
    final String YEAR = gt.getDate(GetDay.TODAY, "yyyy");
    final String MONTH = gt.getDate(GetDay.TODAY, "MM");
    int thisMonth = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));

    //1ヶ月分の回数を格納する配列を宣言
    public static int[] monthCount = new int[31];

    //データの設定
    static List<Data> data = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_graph_month, container, false);

        //XMLとの紐づけ
        TextView dateTitle = root.findViewById(R.id.graph_month);
        TextView monthSum = root.findViewById(R.id.monthSum);
        TextView comparedBeforeMonth = root.findViewById(R.id.comparedBeforeMonth);
        TextView monthAverage = root.findViewById(R.id.monthAverage);
        mChart = root.findViewById(R.id.chart2);

        //今月の合計値を格納する為の変数
        int monthSumCount = 0;

        //グラフの描画
        setGraph(mChart);

        //グラフに表示するカウント数をここでDBに接続して挿入しておく
        CountDatabase countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        new GetCountAsyncTask(countDatabase, GetCountAsyncTask.GET_MONTH, Integer.parseInt(MONTH)).execute();

        //DBからデータを取得してくる前にグラフの描画が終わってしまうのですこしだけメインスレッドを止める
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        root.findViewById(R.id.week).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
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

        for (int tmp : monthCount) {
            monthSumCount += tmp;
        }

        //前月比と日付を表示する
        //diffは前月比のやつ
        int diff = GraphYearFragment.yearCount[thisMonth - 1] - GraphYearFragment.yearCount[thisMonth - 2];
        dateTitle.setText(getResources().getString(R.string.month_title, YEAR, MONTH));
        monthSum.setText(getResources().getString(R.string.month_count_text, monthSumCount));
        comparedBeforeMonth.setText(getResources().getString(R.string.month_count_text, diff));
        monthAverage.setText(getResources().getString(R.string.month_count_text, monthSumCount / getLastDay(thisMonth)));
        setData(data);
        //月間合計の表情画像表示
        ImageView sum_month= root.findViewById(R.id.month_sum_face);
        if(monthSumCount==0){
            sum_month.setImageResource(R.drawable.level_0);
        }else if(monthSumCount<=10){
            sum_month.setImageResource(R.drawable.level_15);
        }else if(monthSumCount<=20){
            sum_month.setImageResource(R.drawable.level_510);
        }else{
            sum_month.setImageResource(R.drawable.level_max);
        }
        //前月比の表情画像表示
        ImageView  last_month= root.findViewById(R.id.last_month_face);
        if(diff==0){
            last_month.setImageResource(R.drawable.level_0);
        }else if(diff<=10){
            last_month.setImageResource(R.drawable.level_15);
        }else if(diff<=20){
            last_month.setImageResource(R.drawable.level_510);
        }else{
            last_month.setImageResource(R.drawable.level_max);
        }
        //月間平均の表情画像表示
        ImageView  ave_month= root.findViewById(R.id.month_ave_face);
        if(monthSumCount / getLastDay(thisMonth)==0){
            ave_month.setImageResource(R.drawable.level_0);
        }else if(monthSumCount / getLastDay(thisMonth)<=10){
            ave_month.setImageResource(R.drawable.level_15);
        }else if(monthSumCount / getLastDay(thisMonth)<=20){
            ave_month.setImageResource(R.drawable.level_510);
        }else{
            ave_month.setImageResource(R.drawable.level_max);
        }



        return root;
    }




    private void setData(List<Data> dataList) {

        ArrayList<BarEntry> values = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

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
            set.setColors(colors);
            set.setValueTextColors(colors);

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

        //x軸の値を格納する　→　日付
        int x = Integer.parseInt(MONTH);
        String[] label = new String[31];
        switch (x) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                for (int i = 0; i < 32; i++) {
                    label[i] = String.valueOf(i + 1);
                }
                xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));
                break;
            case 2:
                for (int i = 0; i < 28; i++) {
                    label[i] = String.valueOf(i + 1);
                }
                xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                for (int i = 0; i < 31; i++) {
                    label[i] = String.valueOf(i + 1);
                }
                xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));
                break;
        }


        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false); //グラフ上の縦線
        xAxis.setDrawAxisLine(true);//グラフの○○日のところの線
        xAxis.setTextColor(-16777216);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(31);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1);

        YAxis left = mChart.getAxisLeft();
        left.setDrawLabels(false); //格子の横線
        left.setSpaceTop(25f);
        left.setSpaceBottom(0);//値が０でもx軸から離れないようにするために０にする
        left.setDrawAxisLine(true);//y軸の数値のすぐ横の線
        left.setDrawGridLines(false); //なんか
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(Color.GRAY);
        left.setZeroLineWidth(0.7f);
        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
    }

    private int getLastDay(int month) {
        if (month == 2 || month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 31;
        }
    }


    private static class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {
        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }
}