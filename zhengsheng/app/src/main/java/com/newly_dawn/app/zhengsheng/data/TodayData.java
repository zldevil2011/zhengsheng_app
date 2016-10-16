package com.newly_dawn.app.zhengsheng.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.zhengsheng.MainActivity;
import com.newly_dawn.app.zhengsheng.R;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;

import org.achartengine.ChartFactory;
import org.achartengine.chart.*;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * Created by dell on 2016/7/25.
 */
public class TodayData {
    private Context context;
    private List todayX, monthX;
    private List todayY, monthY;
    public View ret_view;
    public View execute(Context context) {
        this.context = context;

        SharedPreferences preferences=this.context.getSharedPreferences("zhengsheng",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        String user_id="3";
        editor.putString("user_id", user_id);
        editor.commit();

        SharedPreferences sharedPreferences = this.context.getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("user_id", null);
        Log.i("zhengsheng_exp_share", token);
        String IP = context.getString(R.string.IP);
        String targetUrl = IP + "/api/v1/user_info/";
        Map<String, String> dataMp = new HashMap<>();
        dataMp.put("url", targetUrl);
        dataMp.put("user_id", token);
//        new getElectricityAsyncTask().execute(dataMp);

        List x = new ArrayList();
        List y = new ArrayList();
        x.add(new double[] { 1, 2, 3, 4, 5, 6, 7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24} );
        x.add(new double[] { 0, 2, 4, 6, 8, 10} );

        y.add(new double[] { 13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25,13, 14, 15, 30, 20, 25});
        y.add(new double[] { 18, 9, 21, 15, 10, 6});

        String[] titles = new String[] { "电压"};
        XYMultipleSeriesDataset dataset = buildDataset(titles, x, y);

        int[] colors = new int[] { Color.BLUE};
        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);
        int xmin = 0;
        int xmax = 24;
        int ymin = 0;
        int ymax = 50;
        setChartSettings(renderer, "", "时间","数值", xmin, xmax, ymin, ymax , Color.BLACK, Color.BLACK);
        return  ChartFactory.getCubeLineChartView(context, dataset, renderer, 0.3F);
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
        renderer.setXLabels(20);
        renderer.setLabelsTextSize(25);
        renderer.setAxisTitleTextSize(20);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setApplyBackgroundColor(true);
        renderer.setBackgroundColor(Color.WHITE);
        renderer.setLabelsColor(labelsColor);
    }

//    Next Function
}
