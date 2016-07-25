package com.newly_dawn.app.zhengsheng.data;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

/**
 * Created by dell on 2016/7/25.
 */
public class PieChart {
    public View execute(Context context) {
        int[] colors = new int[] { Color.RED, Color.YELLOW, Color.BLUE };
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        CategorySeries categorySeries = new CategorySeries("Vehicles Chart");
        categorySeries.add("胜", 30);
        categorySeries.add("负", 20);
        categorySeries.add("平", 60);
        return ChartFactory
                .getPieChartView(context, categorySeries, renderer);
    }
    protected DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        //显示标签
        renderer.setShowLabels(true);
        //不显示底部说明
        renderer.setShowLegend(false);
        //设置标签字体大小
        renderer.setLabelsTextSize(15);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setZoomEnabled(false);
        renderer.setPanEnabled(false);
        return renderer;
    }
}
