package com.example.main.ui.graph;

import static android.content.ContentValues.TAG;

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

    //月間画面
    private BarChart mchart;
    private Typeface tfRegular;

    //日付取得機能の準備
    GetDay gt = new GetDay();
    //年、月をそれぞれ定義しておく
    final String YEAR = gt.getDate(GetDay.TODAY, "yyyy");
    final String MONTH = gt.getDate(GetDay.TODAY, "MM");
    int thisMonth = Integer.parseInt(gt.getDate(GetDay.TODAY, "MM"));

    //1ヶ月分の回数を格納する配列を宣言
    public static int[] monthCount = new int[31];

    TextView monthSum;
    TextView comparedBeforeMonth;
    TextView monthAverage;


    int monthSumCount;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_graph_month, container, false);

        //ここで月間の回数を挿入する
        //DB接続用に宣言
        CountDatabase countDatabase = CountDatabaseSingleton.getInstance(requireActivity().getApplicationContext());
        new GetCountAsyncTask(countDatabase, GetCountAsyncTask.GET_MONTH).execute();

        root.findViewById(R.id.syuukan).setOnClickListener(
                view -> {
                    Fragment toWeek = new GraphWeekFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toWeek);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );
        root.findViewById(R.id.nenkan).setOnClickListener(
                view -> {
                    Fragment toYear = new GraphYearFragment();
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.nav_host_fragment, toYear);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
        );

        final TextView dateTitle = root.findViewById(R.id.gurahugekkan);//結びつけ
        dateTitle.setText(getResources().getString(R.string.month_title, YEAR, MONTH));

        monthSum = root.findViewById(R.id.monthSum);
        comparedBeforeMonth = root.findViewById(R.id.comparedBeforeMonth);
        monthAverage = root.findViewById(R.id.monthAverage);


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


        //y軸の値を格納する　→　
        int y = Integer.parseInt(MONTH);
        switch (y) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
                for (int i = 0; i <= 31; i++) {
                    //0 -> 月の1日から格納していく
                    data.add(new Data(i, monthCount[i], "12-30"));
                }
                break;
            case 2:
                for (int i = 0; i <= 28; i++) {
                    data.add(new Data(i, monthCount[i], "12-30"));
                }
                break;
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
                for (int i = 0; i < 30; i++) {
                    data.add(new Data(i, monthCount[i], "12-30"));
                }
                break;
        }

        for (int tmp : monthCount) {
            monthSumCount += tmp;
        }

        //先月比を計算する
        int diff = GraphYearFragment.yearCount[thisMonth - 2] - GraphYearFragment.yearCount[thisMonth - 1];
        monthSum.setText(getResources().getString(R.string.month_count_text, monthSumCount));
        comparedBeforeMonth.setText(getResources().getString(R.string.month_count_text, diff));
        monthAverage.setText(getResources().getString(R.string.month_count_text, monthSumCount / getLastDay(thisMonth)));
        setData(data);
        Log.d(TAG, "onCreate: ここでデータをセットしているよ！！");

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

    private int getLastDay(int month) {
        if (month == 2 || month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            return 31;
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