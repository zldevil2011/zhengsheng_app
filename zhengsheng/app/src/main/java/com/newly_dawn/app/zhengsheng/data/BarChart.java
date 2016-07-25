package com.newly_dawn.app.zhengsheng.data;

/**
 * Created by dell on 2016/7/25.
 */
import java.util.Random;
import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.newly_dawn.app.zhengsheng.R;

public class BarChart {
    private Context context;
    public View execute(Context context) {
        this.context = context;
        return ChartFactory
                .getBarChartView(context, getBarDemoDataset(), getBarDemoRenderer(), Type.DEFAULT);
    }

    /**
     * XYMultipleSeriesDataset 类型的对象，用于提供图表需要表示的数据集，
     * 这里我们用 getBarDemoDataset 来得到它。
     */
    private XYMultipleSeriesDataset getBarDemoDataset() {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        final int nr = 12;
        Random r = new Random();
        for (int i = 0; i < 1; i++) {
            CategorySeries series = new CategorySeries("电量 " + (i + 1));
            for (int k = 0; k < nr; k++) {
                series.add(100 + r.nextInt() % 100);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    /**
     * XYMultipleSeriesRenderer 类型的对象，用于提供图表展现时的一些样式，
     * 这里我们用 getBarDemoRenderer 方法来得到它。
     * getBarDemoRenderer 方法构建了一个 XYMultipleSeriesRenderer 用来设置2个系列各自的颜色
     */
    public XYMultipleSeriesRenderer getBarDemoRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(Color.BLUE);
        renderer.addSeriesRenderer(r);

//        r = new SimpleSeriesRenderer();
//        r.setColor(Color.GREEN);
//        renderer.addSeriesRenderer(r);
//
//        r = new SimpleSeriesRenderer();
//        r.setColor(Color.RED);
//        renderer.addSeriesRenderer(r);
        renderer.setBarSpacing(1.0);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setMarginsColor(Color.WHITE);
        renderer.setZoomEnabled(false, false);
        renderer.setPanEnabled(false, false);
        renderer.setLabelsTextSize(25);
        renderer.setAxisTitleTextSize(20);
        renderer.setXLabels(12);
        renderer.setAxesColor(Color.BLACK);
        renderer.setLabelsColor(Color.BLACK);
        setChartSettings(renderer);
        return renderer;
    }

    /**
     * setChartSettings 方法设置了下坐标轴样式。
     */
    private void setChartSettings(XYMultipleSeriesRenderer renderer) {
        renderer.setChartTitle("");
        renderer.setXTitle("时间");
        renderer.setYTitle("用电量");
        renderer.setXAxisMin(0.5);
        renderer.setXAxisMax(12.5);
        renderer.setYAxisMin(0);
        renderer.setYAxisMax(210);
    }
}