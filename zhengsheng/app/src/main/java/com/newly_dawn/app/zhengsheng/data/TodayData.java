package com.newly_dawn.app.zhengsheng.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.chart.*;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by dell on 2016/7/25.
 */
public class TodayData {
    private Context context;
    public View execute(Context context) {
        this.context = context;
        String[] titles = new String[] { "First"};

        List x = new ArrayList();
        List y = new ArrayList();
        final int nr = 60;
        Random r = new Random();
        double[] XX = new double[120];
        double[] YY = new double[120];
        double ymin = 100000, ymax = -1, xmin = Integer.MAX_VALUE, xmax = -1;
        for(int i = 0; i < nr; ++i){
            XX[i] = i + 1;
            YY[i] = (100 + r.nextInt() % 100);
            xmin = i < xmin ? i : xmin;
            xmax = i > xmax ? i + 1 : xmax;
            if(YY[i] <= ymin){
                ymin = YY[i];
            }
            if(YY[i] >= ymax){
                ymax = YY[i];
            }
        }
        x.add(XX);
        y.add(YY);
//        x.add(new double[] { 1, 2, 3, 4, 5, 6, 7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24} );
//        x.add(new double[] { 0, 2, 4, 6, 8, 10} );

//        y.add(new double[] { 13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25});
//        y.add(new double[] { 18, 9, 21, 15, 10, 6});

        XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);

        int[] colors = new int[] { Color.BLUE};
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

        setChartSettings(renderer, "Line Chart Demo", "X", "Y", xmin, xmax, ymin, ymax , Color.WHITE, Color.WHITE);

//        View chart = ChartFactory.getLineChartView(context, dataset, renderer);

        return ChartFactory
                .getCubeLineChartView(context, dataset, renderer, 0.3F);
    }
    protected XYMultipleSeriesDataset buildDataset(String[] titles, List xValues, List yValues) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;                  //有几条线
        for (int i = 0; i < length; i++)
        {
            XYSeries series = new XYSeries(titles[i]);    //根据每条线的名称创建
            double[] xV = (double[]) xValues.get(i);                 //获取第i条线的数据
            double[] yV = (double[]) yValues.get(i);
            int seriesLength = xV.length;                 //有几个点

            for (int k = 0; k < seriesLength; k++)        //每条线里有几个点
            {
                series.add(xV[k], yV[k]);
            }

            dataset.addSeries(series);
        }

        return dataset;
    }

    protected XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles, boolean fill)
    {

        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);
        renderer.setLabelsTextSize(15);
        renderer.setLabelsColor(Color.BLACK);
        int length = colors.length;
        for (int i = 0; i < length; i++)
        {
            XYSeriesRenderer r = new XYSeriesRenderer();
            r.setColor(colors[i]);
//            r.setPointStyle(styles[i]);
            r.setFillPoints(fill);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,String yTitle, double xMin,
                                    double xMax, double yMin, double yMax, int axesColor,int labelsColor) {
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setLabelsColor(labelsColor);
    }
}
