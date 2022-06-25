package com.example.main.ui.graph;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class GraphYearFragment extends Fragment {
    /*フィールド*/
    //maker Ryo Kamizato feat シュトゥーデューム

    private BarChart mchart;
    private Typeface tfRegular;
    private LineChart mChart;
    //年間の回数を格納する配列を宣言
    public static int[] yearCount = new int[12];

    //日付取得機能の準備
    GetDay gt = new GetDay();
    final String YEAR = gt.getDate(GetDay.TODAY, "yyyy");

    int yearSumCount;
    int yearMaxCount = 0;
    int yearMinCount = 10000000;

    TextView yearSum;
    TextView yearMax;
    TextView yearAverage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_graph_year, container, false);

        yearSum = root.findViewById(R.id.yearSumCount);
        yearMax = root.findViewById(R.id.yearMax);
        yearAverage = root.findViewById(R.id.yearAverage);

        //ここで年間の回数を挿入する
        //DB接続用に宣言
        CountDatabase countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        Log.d(TAG, "onCreate: ここで年間の値を挿入するよ！！");
        new GetCountAsyncTask(countDatabase, GetCountAsyncTask.GET_YEAR).execute();

        root.findViewById(R.id.syuukan).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.gekkan).setOnClickListener(
                view -> {
                    Fragment toMonth = new GraphMonthFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toMonth);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        final TextView dateTitle = root.findViewById(R.id.gurahunenkan);//結びつけ
        dateTitle.setText(getResources().getString(R.string.year_title, YEAR));


        mchart = root.findViewById(R.id.chart2);
        mchart.setBackgroundColor(-35);
        mchart.setExtraTopOffset(0);
        mchart.setExtraBottomOffset(5);//値を大きくするとx軸が上に行く
        mchart.setExtraLeftOffset(0);
        mchart.setExtraRightOffset(0);
        mchart.setDrawBarShadow(false);
        mchart.setDrawValueAboveBar(true);
        mchart.getDescription().setEnabled(false);
        // scaling can now only be done on x- and y-axis separately
        mchart.setPinchZoom(true);
        mchart.setDrawGridBackground(false);

        XAxis xAxis = mchart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false); //グラフ上の縦線
        xAxis.setDrawAxisLine(true);//グラフの○○日のところの線
        xAxis.setTextColor(-16777216);
        xAxis.setTextSize(13f);
        xAxis.setLabelCount(12);
        xAxis.setCenterAxisLabels(false);
        xAxis.setGranularity(1);

        YAxis left = mchart.getAxisLeft();
        left.setDrawLabels(false); //格子の横線
        left.setSpaceTop(25f);
        left.setSpaceBottom(0);//値が０でもx軸から離れないようにするために０にする
        left.setDrawAxisLine(true);//y軸の数値のすぐ横の線
        left.setDrawGridLines(false); //なんか
        left.setDrawZeroLine(true); // draw a zero line
        left.setZeroLineColor(-16777216);
        left.setZeroLineWidth(0.7f);
        mchart.getAxisRight().setEnabled(false);
        mchart.getLegend().setEnabled(false);

        // THIS IS THE ORIGINAL DATA YOU WANT TO PLOT
        //データの設定
        final List<Data> data = new ArrayList<>();

        data.add(new Data(1, yearCount[0], "12-29"));
        data.add(new Data(2, yearCount[1], "12-30"));
        data.add(new Data(3, yearCount[2], "12-31"));
        data.add(new Data(4, yearCount[3], "01-01"));
        data.add(new Data(5, yearCount[4], "01-02"));
        data.add(new Data(6, yearCount[5], "01-02"));
        data.add(new Data(7, yearCount[6], "01-02"));
        data.add(new Data(8, yearCount[7], "01-02"));
        data.add(new Data(9, yearCount[8], "01-02"));
        data.add(new Data(10, yearCount[9], "01-02"));
        data.add(new Data(11, yearCount[10], "01-02"));
        data.add(new Data(12, yearCount[11], "01-02"));

        for (int tmp : yearCount) {
            yearSumCount += tmp;
            if (yearMaxCount < tmp) {
                yearMaxCount = tmp;
            }
        }

        yearSum.setText(getResources().getString(R.string.year_count_text, yearSumCount));
        yearSum.setText(getResources().getString(R.string.year_count_text, yearMaxCount));
        yearSum.setText(getResources().getString(R.string.year_count_text, yearSumCount/12));

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

    /**
     * Demo class representing data.
     */
    private static class Data {

        final String xAxisValue;
        final int yValue;
        final int xValue;

        Data(int xValue, int yValue, String xAxisValue) {
            this.xAxisValue = xAxisValue;
            this.yValue = yValue;
            this.xValue = xValue;
        }
    }

    private static class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            return String.valueOf((int) value);
        }
    }
}