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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphMonthActivity extends AppCompatActivity {
    //maker Ryo Kamizato feat シュトゥーデューム


    //月間画面
    private BarChart mchart;
    private Typeface tfRegular;
    private LineChart mChart;
    final long DAY = -86400000;

    //TODO getDayメソッドが冗長なので簡潔にしたい　→　インターフェースしたい
    protected String getYear() {
        DateFormat df = new SimpleDateFormat("yyyy");
        //DateFormat df2 = new SimpleDateFormat("MM");
        Date date = new Date(System.currentTimeMillis());
        return df.format(date);
    }

    protected String getMonth() {
        DateFormat df2 = new SimpleDateFormat("MM");
        Date date = new Date(System.currentTimeMillis());
        return df2.format(date);
    }

    protected Integer getMonths() {
        DateFormat df4 = new SimpleDateFormat("MM");
        Date date2 = new Date(System.currentTimeMillis());
        return Integer.valueOf(df4.format(date2));
    }

    protected String getDay(int days) {
        DateFormat df3 = new SimpleDateFormat("dd");
        Date date = new Date(System.currentTimeMillis() + DAY * days);
        return df3.format(date);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.syuukan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
        findViewById(R.id.nenkan).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(GraphMonthActivity.this, GraphYearActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
        );
        final TextView hiduke = (TextView) findViewById(R.id.gurahugekkan);//結びつけ
        hiduke.setText(getYear() + "年" + getMonth() + "月");


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
        Integer x = getMonths();
        String label[] = new String[40];
        switch (x) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
                for (int i = 31; i >= 0; i--) {
                    label[31 - i] = getDay(i);
                }
                xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));
                break;
            case 2:
                for (int i = 0; i <= 28; i++) {
                    label[28 - i] = getDay(i);
                }
                xAxis.setValueFormatter(new IndexAxisValueFormatter((label)));
                break;
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
                for (int i = 1; i <= 30; i++) {
                    label[i-1] = String.valueOf(i);
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



        Integer y = getMonths();
        data.add(new Data(y, 2, "12-30"));
        switch (y) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
                for (int i = 1; i <= 31; i++) {
                    data.add(new Data(y,2,"12-30"));
                }
                break;
            case 2:
                for (int i = 0; i <= 28; i++) {
                    data.add(new Data(i,2,"12-30"));
                }
                break;
            case 4:
            case 6:
            case 8:
            case 10:
            case 12:
                for (int i = 0; i <30; i++) {
                    data.add(new Data(i,2,"12-30"));
                }
                break;
        }




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