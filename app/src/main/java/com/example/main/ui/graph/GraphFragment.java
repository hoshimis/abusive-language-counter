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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphFragment extends Fragment {

    private GraphViewModel dashboardViewModel;
    private BarChart mchart;
    private Typeface tfRegular;
    private LineChart mChart;
    final long DAY = -86400000;

    //今日の年月日を取得する
    protected String getToday(int days) {
        DateFormat df = new SimpleDateFormat("yyyy/ MM/ dd");
        Date date = new Date(System.currentTimeMillis() + DAY * days);
        return df.format(date);
    }

    protected String getDay(int days) {
        DateFormat df3 = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis() + DAY * days);
        return df3.format(date);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(GraphViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        //final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_graph);
        root.findViewById(R.id.gekkan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GraphMonthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        root.findViewById(R.id.nenkan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), GraphYearActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
        );
        final TextView hiduke = (TextView) root.findViewById(R.id.gurahusyuukan);//結びつけ
        hiduke.setText(getToday(6) + "～" + getToday(0));
        //*日付計算方法
        // まず、一日の秒数を求める
        // oncreate（プログラムが始まる）の外に日付関係のメソッド(関数)を作る
        // その中で日にちの形を作るやつと日にちを求める計算をする
        // 計算は、コンピュータの人生の秒数から24時間の秒数を引いた数
        //　それがdf.format(data),つまり、dataを○○月○○日って形にフォーマットにしたデータを
        // settextでやっている*//


//        //勉強時間を共有プリファレンスから取ってくる。
//        private int getStudyTimeToday() {
//            //整形00:00にする
//            final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss", Locale.JAPANESE);
//            //今日の日付の共有プリファレンスを取ってきて、時間を切り取り数字に変換する。
//            long tmp = pref.getLong(getToday(), RESET_TIME);
//            String test = dataFormat.format(tmp);
//            int extractionTime = Integer.parseInt(test.substring(0, 2));
//
//
//            return checkExceed30minutes() ? extractionTime + 1 : extractionTime;
//        }

//        private boolean checkExceed30minutes(){
//            //整形00:00にする
//            final SimpleDateFormat dataFormat = new SimpleDateFormat("HH:mm:ss", Locale.JAPANESE);
//            //今日の日付の共有プリファレンスを取ってきて、時間を切り取り数字に変換する。
//           // long tmp = pref.getLong(getToday(), RESET_TIME);
//           // String test = dataFormat.format(tmp);
//           // int checkExceed = Integer.parseInt(test.substring(3, 4));
//
//            return checkExceed >= 30;


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setTitle("BarChartPositiveNegative");

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
        String label[] = new String[]{
                getDay(6),
                getDay(5),
                getDay(4),
                getDay(3),
                getDay(2),
                getDay(1),
                getDay(0),
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
//        String label[] = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
//                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
//                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31"};
//        xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));


//TODO 日付設定
        data.add(new Data(0, 0, "1"));
        data.add(new Data(1, 2, "1"));
        data.add(new Data(2, 1, "1"));
        data.add(new Data(3, 4, "1"));
        data.add(new Data(4, 2, "1"));
        data.add(new Data(5, 2, "1"));
        data.add(new Data(6, 5, "1"));

//        xAxis.setValueFormatter(new IAxisValueFormatter(){
//            @Override
//            public String getFormattedValue(int value, AxisBase axis) {
//                return data.get(Math.min(Math.max((int) value, 0), data.size()-1)).xAxisValue;
//            }
//        });


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


        //return root;　謎のエラーにより消去
    }
}
