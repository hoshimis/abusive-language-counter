package com.example.main.ui.graph;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.main.R;
import com.example.main.db.dayscount.CountDatabase;
import com.example.main.db.dayscount.CountDatabaseSingleton;
import com.example.main.util.GetDay;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment {
    /*フィールド*/
    private GraphViewModel dashboardViewModel;
    private BarChart mchart;
    private Typeface tfRegular;
    private LineChart mChart;
    //DB接続用に宣言
    private CountDatabase countDatabase;
    //週間の回数を格納する配列を宣言
    public static int[] weekCount = new int[7];
    //日付取得機能の準備
    GetDay gt = new GetDay();

    //xmlとの紐づけ
    TextView todaySum;
    TextView comparedYesterday;
    TextView weekSum;

    int weekSumCount;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);

        //グラフに表示するカウント数をここで挿入しておく
        countDatabase = CountDatabaseSingleton.getInstance(getActivity().getApplicationContext());
        new GetCountAsyncTask(getActivity(), countDatabase, GetCountAsyncTask.GET_WEEK).execute();

        root.findViewById(R.id.gekkan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toMonth = new Intent(getActivity(), GraphMonthActivity.class);
                toMonth.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(toMonth);
            }
        });
        root.findViewById(R.id.nenkan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toYear = new Intent(getActivity(), GraphYearActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(toYear);
                    }
                }
        );
        final TextView hiduke = (TextView) root.findViewById(R.id.gurahusyuukan);//結びつけ
        hiduke.setText(gt.getDate(GetDay.SIX_DAYS_AGO, "MM/ dd") + "～" + gt.getDate(GetDay.TODAY, "MM/ dd"));

        //今日の回数を表示する
        todaySum = root.findViewById(R.id.todaySum);
        comparedYesterday = root.findViewById(R.id.comparedYesterday);
        weekSum = root.findViewById(R.id.weekSum);

        mchart = root.findViewById(R.id.chart1);
        mchart.setBackgroundColor(-35);
        mchart.setExtraTopOffset(0);
        mchart.setExtraBottomOffset(5);//値を大きくするとx軸が上に行く
        mchart.setExtraLeftOffset(0);
        mchart.setExtraRightOffset(0);
        mchart.setDrawBarShadow(false);
        mchart.setDrawBorders(false);
        mchart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mchart.setPinchZoom(true);
        mchart.setDrawGridBackground(false);
        XAxis xAxis = mchart.getXAxis();

        //横線の値を設定する
        //一番右端を今日として、左に向かって一日前ずつ表示していく
        String label[] = new String[]{
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

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        //データの設定
        final List<Data> data = new ArrayList<>();

        //xValue -> 6が右端 右端に今日のデータが入っている
        data.add(new Data(0, weekCount[6], "1"));
        data.add(new Data(1, weekCount[5], "1"));
        data.add(new Data(2, weekCount[4], "1"));
        data.add(new Data(3, weekCount[3], "1"));
        data.add(new Data(4, weekCount[2], "1"));
        data.add(new Data(5, weekCount[1], "1"));
        data.add(new Data(6, weekCount[0], "1"));

        for (int tmp : weekCount) {
            weekSumCount += tmp;
        }

        //今日の回数を表示する
        todaySum.setText(String.valueOf(weekCount[0]) + "回");
        comparedYesterday.setText(String.valueOf(weekCount[1] - weekCount[0]) + "回");
        weekSum.setText(String.valueOf(weekSumCount) + "回");


        setData(data);
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

        if (mchart.getData() != null &&
                mchart.getData().getDataSetCount() > 0) {
            set = (BarDataSet) mchart.getData().getDataSetByIndex(0);
            set.setValues(values);
            mchart.getData().notifyDataChanged();
            mchart.notifyDataSetChanged();
        } else {
            set = new BarDataSet(values, "Values");
            set.setColors(colors);
            set.setValueTextColors(colors);

            BarData data = new BarData(set);
            data.setValueTextSize(13f);
            data.setValueTypeface(tfRegular);
            data.setValueFormatter(new ValueFormatter());
            data.setBarWidth(0.8f);

            mchart.setData(data);
            mchart.invalidate();
        }
    }

    //Demo class representing data.
    private class Data {

        final String xAxisValue;
        final float yValue;
        final float xValue;

        Data(float xValue, float yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    private class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }
}
