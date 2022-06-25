package com.example.main.ui.graph;

//データを表現するデモクラス
 class Data {

    final String xAxisValue;
    final float yValue;
    final float xValue;

    Data(float xValue, float yValue, String xAxisValue) {
        this.xAxisValue = xAxisValue;
        this.yValue = yValue;
        this.xValue = xValue;
    }
}