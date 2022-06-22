package com.example.main.ui.graph;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.main.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphYearActivity extends AppCompatActivity {
    //maker Ryo Kamizato feat シュトゥーデューム
    //TODO:エラーなくす,メモ帳みくらべる


    private BarChart mchart;
    private Typeface tfRegular;
    private LineChart mChart;
    final long DAY = -86400000;
    //private DashboardViewModel dashboardViewModel;
    protected String getYear() {
        DateFormat df = new SimpleDateFormat("yyyy");
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        findViewById(R.id.syuukan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GraphYearActivity.this, GraphFragment.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        findViewById(R.id.gekkan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GraphYearActivity.this, GraphMonthActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        final TextView hiduke =(TextView) findViewById(R.id.gurahunenkan);//結びつけ
        hiduke.setText(getYear()+"年");



        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("BarChartPositiveNegative");

        mchart = findViewById(R.id.chart2);
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
        //TODO*数値を整数にする　https://www.web-dev-qa-db-ja.com/ja/android/%E6%A3%92%E3%82%B0%E3%83%A9%E3%83%95%E3%81%AEy%E8%BB%B8%E3%83%A9%E3%83%99%E3%83%AB%E3%82%92%E6%95%B4%E6%95%B0%E3%81%AB%E5%BC%B7%E5%88%B6%E3%81%97%E3%81%BE%E3%81%99%E3%81%8B%EF%BC%9F/1050831784/


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

        data.add(new Data(1,1 , "12-29"));
        data.add(new Data(2,1 , "12-30"));
        data.add(new Data(3, 4, "12-31"));
        data.add(new Data(4, 5, "01-01"));
        data.add(new Data(5, 1, "01-02"));
        data.add(new Data(6, 4, "01-02"));
        data.add(new Data(7, 4, "01-02"));
        data.add(new Data(8, 5, "01-02"));
        data.add(new Data(9, 4, "01-02"));
        data.add(new Data(10, 5, "01-02"));
        data.add(new Data(11, 8, "01-02"));
        data.add(new Data(12, 1, "01-02"));

//        xAxis.setValueFormatter(new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(int value, AxisBase axis) {
//                return data.get(Math.min(Math.max((int) value, 0), data.size()-1)).xAxisValue;
//            }
//        });


        setData(data);
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
        final int yValue;
        final int xValue;

        Data(int xValue, int yValue, String xAxisValue) {
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.only_github, menu);
//        return true;
//    }
//    //メニュー関連のこと？だから一応消した
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.viewGithub: {
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/BarChartPositiveNegative.java"));
//                startActivity(i);
//                break;
//            }
//        }
//
//        return true;
//    }
//    //https://developer.android.com/guide/topics/ui/menus?hl=ja#java ここのサイト参照　なんとなくいらなそう
//
//
//    @Override
//    public void saveToGallery() { /* Intentionally left empty */ }
//
//}