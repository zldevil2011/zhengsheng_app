package com.newly_dawn.app.zhengsheng.data;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.newly_dawn.app.zhengsheng.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class data_list extends AppCompatActivity {
    private BarChart mBarChart;
    private BarData mBarData;
    private BarCharts mBarCharts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);
        mBarCharts = new BarCharts();
        mBarChart = (BarChart) findViewById(R.id.chart1);
        mBarData = mBarCharts.getBarData(14, 500);
        mBarCharts.showBarChart(mBarChart, mBarData);
    }

    public class BarCharts {

        private String[] color = {"#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD","#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD"};

        public void showBarChart(BarChart barChart, BarData barData) {
            // 数据描述
            // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
            barChart.setNoDataText("You need to provide data for the chart.");
            // 是否显示表格颜色
            barChart.setDrawGridBackground(false);
            // 设置是否可以触摸
            barChart.setTouchEnabled(true);
            // 是否可以拖拽
            barChart.setDragEnabled(false);
            // 是否可以缩放
            barChart.setScaleEnabled(false);
            // 集双指缩放
            barChart.setPinchZoom(false);
            // 设置背景
            barChart.setBackgroundColor(Color.parseColor("#01000000"));
            // 如果打开，背景矩形将出现在已经画好的绘图区域的后边。
            barChart.setDrawGridBackground(false);
            // 集拉杆阴影
            barChart.setDrawBarShadow(false);
            // 图例
            barChart.getLegend().setEnabled(false);
            // 设置数据
            barChart.setData(barData);

            // 隐藏右边的坐标轴 (就是右边的0 - 100 - 200 - 300 ... 和图表中横线)
            barChart.getAxisRight().setEnabled(false);
            // 隐藏左边的左边轴 (同上)
//        barChart.getAxisLeft().setEnabled(false);

            // 网格背景颜色
            barChart.setGridBackgroundColor(Color.parseColor("#00000000"));
            // 是否显示表格颜色
            barChart.setDrawGridBackground(false);
            // 设置边框颜色
            barChart.setBorderColor(Color.parseColor("#00000000"));
            // 说明颜色
            // 拉杆阴影
            barChart.setDrawBarShadow(false);
            // 打开或关闭绘制的图表边框。（环绕图表的线）
            barChart.setDrawBorders(false);


            Legend mLegend = barChart.getLegend(); // 设置比例图标示
            // 设置窗体样式
            mLegend.setForm(Legend.LegendForm.CIRCLE);
            // 字体
            mLegend.setFormSize(4f);
            // 字体颜色
            mLegend.setTextColor(Color.parseColor("#00000000"));


            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);


            barChart.animateY(1000); // 立即执行的动画,Y轴
        }
        public BarData getBarData(int count, float range) {
            ArrayList<String> xValues = new ArrayList<String>();
            for (int i = 0; i < count; i++) {
                xValues.add(""+(i+1)+"周");// 设置每个壮图的文字描述
            }
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            for (int i = 0; i < count; i++) {
                float value = (float) (Math.random() * range/*100以内的随机数*/) + 3;
                yValues.add(new BarEntry(i, value, "测试饼状图"));
            }
            // y轴的数据集合
            BarDataSet barDataSet = new BarDataSet(yValues, "测试饼状图");
            ArrayList<Integer> colors = new ArrayList<Integer>();
            for(int i = 0;i < count ;i++){
                colors.add(Color.parseColor(color[i]));
            }
            barDataSet.setColors(colors);
            // 设置栏阴影颜色
            barDataSet.setBarShadowColor(Color.parseColor("#01000000"));
            barDataSet.setValueTextColor(Color.parseColor("#000000"));
            // 绘制值
            barDataSet.setDrawValues(true);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(barDataSet);

            BarData data1 = new BarData(dataSets);

            return data1;
        }
    }
}
