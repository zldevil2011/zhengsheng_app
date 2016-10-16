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
        String targetUrl = "http://192.168.1.60:8000/api/v1/user_info/";
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
//    连接网络获取JSON数据
    public class getElectricityAsyncTask extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute() {}
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                Map<String, String> dataMp = new HashMap<>();
                dataMp.put("user_id", params[0].get("user_id"));
                httpRequest.post_connect(dataMp);
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("zhengsheng_exp", String.valueOf(e));
                result = null;
            }
            return result;
        }

        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                Toast.makeText(context, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        Log.i("zhengsheng_exp_json", String.valueOf(jsonObject));
                        JSONObject today_data = new JSONObject(jsonObject.getString("today_data"));
                        JSONArray today_hour = new JSONArray(today_data.getString("today_hour"));

                        JSONObject month_data = new JSONObject(jsonObject.getString("month_data"));
                        JSONArray month_day = new JSONArray(month_data.getString("year_month"));
                        Log.i("zhengsheng_exp_today", String.valueOf(today_hour));
                        Log.i("zhengsheng_exp_month", String.valueOf(month_day));
                        int len = month_day.length();
                        double[] month_day_arr = new double[31];
                        for(int i = 0; i < len; ++i){
                            Log.i("zhengsheng_exp_day", month_day.getString(i));
                            month_day_arr[i] = month_day.getDouble(i);
                        }
                        Log.i("zhengsheng_exp_day_arr", String.valueOf(month_day_arr));
                        todayX = new ArrayList();
                        todayY = new ArrayList();
                        todayX.add(month_day_arr);
                        todayY.add(month_day_arr);

                        String[] titles = new String[] { "电压"};
                        XYMultipleSeriesDataset dataset = buildDataset(titles, todayX, todayY);

                        int[] colors = new int[] { Color.BLUE};
                        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
                        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);
                        int xmin = 0;
                        int xmax = 30;
                        int ymin = 0;
                        int ymax = 50;
                        setChartSettings(renderer, "", "时间","数值", xmin, xmax, ymin, ymax , Color.BLACK, Color.BLACK);
                        ret_view =  ChartFactory.getCubeLineChartView(context, dataset, renderer, 0.3F);

                    } catch (JSONException e) {
                        Log.i("zhengsheng_exp2", String.valueOf(e));
                        e.printStackTrace();
                    }
                    Log.i("zhengsheng_exp3", result.get("text"));
                } else {
                    Log.i("zhengsheng_exp4", result.get("code"));
                }
            }
        }
    }

//    Next Function
}
