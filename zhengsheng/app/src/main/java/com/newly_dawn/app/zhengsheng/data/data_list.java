package com.newly_dawn.app.zhengsheng.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.newly_dawn.app.zhengsheng.R;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

public class data_list extends AppCompatActivity {
    private LineChart line1, line2, line3, line4, line5;
    private LineData line_data1, line_data2, line_data3, line_data4, line_data5;
    private LineCharts mLineCharts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_data_list);
            loadData();
        }catch (Exception e){
            Log.i("zhengsheng_error2", String.valueOf(e));
        }

    }
    public void loadData(){
        try {
            mLineCharts = new LineCharts();
            line1 = (LineChart) findViewById(R.id.detail_chart1);
            line2 = (LineChart) findViewById(R.id.detail_chart2);
            line3 = (LineChart) findViewById(R.id.detail_chart3);
            line4 = (LineChart) findViewById(R.id.detail_chart4);
            line5 = (LineChart) findViewById(R.id.detail_chart5);
            SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("user_id", null);
            String IP = getString(R.string.IP);
            String targetUrl = IP + "/api/v1/user/electricity/details/";
//            String targetUrl = IP + "/api/v1/user/electricity/data/";
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("url", targetUrl);
            dataMap.put("user_id", user_id);

            new getDetailSync().execute(dataMap);
        }catch (Exception e){
            Log.i("zhengsheng_error1", String.valueOf(e));
        }
    }


    public class getDetailSync extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
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
                Log.i("zhengsheng_error6", String.valueOf(responseText));
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("zhengsheng_error4", String.valueOf(e));
                result = null;
            }
            return result;
        }

        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                Toast.makeText(data_list.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        JSONObject reactive_power_data = new JSONObject(jsonObject.getString("reactive_power_data"));
                        JSONObject power_factor_data = new JSONObject(jsonObject.getString("power_factor_data"));
                        JSONObject voltage_data = new JSONObject(jsonObject.getString("voltage_data"));
                        JSONObject active_power_data = new JSONObject(jsonObject.getString("active_power_data"));
                        JSONObject electric_current_data = new JSONObject(jsonObject.getString("electric_current_data"));
                        JSONArray reactive_power_x = new JSONArray(reactive_power_data.getString("reactive_power_x"));
                        JSONArray reactive_power_y = new JSONArray(reactive_power_data.getString("reactive_power_y"));
                        long[] reactive_power_x_arr = new long[reactive_power_x.length()];
                        double[] reactive_power_y_arr = new double[reactive_power_y.length()];
                        for(int i = 0; i < reactive_power_x.length(); ++i){
                            reactive_power_x_arr[i] = reactive_power_x.getLong(i) * 1000;
                            reactive_power_y_arr[i] = (double)reactive_power_y.getDouble(i);
                        }
                        JSONArray power_factor_x = new JSONArray(power_factor_data.getString("power_factor_x"));
                        JSONArray power_factor_y = new JSONArray(power_factor_data.getString("power_factor_x"));
                        long[] power_factor_x_arr = new long[power_factor_x.length()];
                        double[] power_factor_y_arr = new double[power_factor_x.length()];
                        for(int i = 0; i < power_factor_x.length(); ++i){
                            power_factor_x_arr[i] = power_factor_x.getLong(i) * 1000;
                            power_factor_y_arr[i] = (double)power_factor_y.getDouble(i);
                        }
                        JSONArray voltage_x = new JSONArray(voltage_data.getString("voltage_x"));
                        JSONArray voltage_y = new JSONArray(voltage_data.getString("voltage_y"));
                        long[] voltage_x_arr = new long[voltage_x.length()];
                        double[] voltage_y_arr = new double[voltage_x.length()];
                        for(int i = 0; i < voltage_x.length(); ++i){
                            voltage_x_arr[i] = voltage_x.getLong(i) * 1000;
                            voltage_y_arr[i] = (double)voltage_y.getDouble(i);
                        }
                        JSONArray active_power_x = new JSONArray(active_power_data.getString("active_power_x"));
                        JSONArray active_power_y = new JSONArray(active_power_data.getString("active_power_y"));
                        long[] active_power_x_arr = new long[active_power_x.length()];
                        double[] active_power_y_arr = new double[active_power_x.length()];
                        for(int i = 0; i < active_power_x.length(); ++i){
                            active_power_x_arr[i] = active_power_x.getLong(i) * 1000;
                            active_power_y_arr[i] = (double)active_power_y.getDouble(i);
                        }
                        JSONArray electric_current_x = new JSONArray(electric_current_data.getString("electric_current_x"));
                        JSONArray electric_current_y = new JSONArray(electric_current_data.getString("electric_current_y"));
                        long[] electric_current_x_arr = new long[electric_current_x.length()];
                        double[] electric_current_y_arr = new double[electric_current_x.length()];
                        for(int i = 0; i < electric_current_x.length(); ++i){
                            electric_current_x_arr[i] = electric_current_x.getLong(i) * 1000;
                            electric_current_y_arr[i] = (double)electric_current_y.getDouble(i);
                        }
                        Log.i("zhengsheng_error7", String.valueOf(reactive_power_data));
                        try {
                            line_data5 = mLineCharts.getLineData(reactive_power_x_arr, reactive_power_y_arr);
                            line_data3 = mLineCharts.getLineData(power_factor_x_arr, power_factor_y_arr);
                            line_data1 = mLineCharts.getLineData(voltage_x_arr, voltage_y_arr);
                            line_data4 = mLineCharts.getLineData(active_power_x_arr, active_power_y_arr);
                            line_data2 = mLineCharts.getLineData(electric_current_x_arr, electric_current_y_arr);
                            mLineCharts.showLineChart(line1, line_data1);
                            mLineCharts.showLineChart(line2, line_data2);
                            mLineCharts.showLineChart(line3, line_data3);
                            mLineCharts.showLineChart(line4, line_data4);
                            mLineCharts.showLineChart(line5, line_data5);
                        }catch (Exception e){
                            Log.i("zhengsheng_error8", String.valueOf(e));
                        }
                    } catch (Exception e) {
                        Log.i("zhengsheng_error3", String.valueOf(e));
                    }
                } else {
                    Log.i("result", result.get("code"));
                }
            }
        }
    }
    public class LineCharts {
        public void showLineChart(LineChart lineChart, LineData lineData) {
            // 数据描述
            // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
            lineChart.setNoDataText("未能获取监控数据");
            Description ap = new Description();
            ap.setText("");
            lineChart.setDescription(ap);
            // 是否显示表格颜色
            lineChart.setDrawGridBackground(false);
            // 设置是否可以触摸
            lineChart.setTouchEnabled(true);
            // 是否可以拖拽
            lineChart.setDragEnabled(false);
            // 是否可以缩放
            lineChart.setScaleEnabled(false);
            // 集双指缩放
            lineChart.setPinchZoom(false);
            // 设置背景
            lineChart.setBackgroundColor(Color.parseColor("#01000000"));
            // 如果打开，背景矩形将出现在已经画好的绘图区域的后边。
            lineChart.setDrawGridBackground(false);
            // 图例
            lineChart.getLegend().setEnabled(false);
            // 设置数据
            lineChart.setData(lineData);

            // 隐藏右边的坐标轴 (就是右边的0 - 100 - 200 - 300 ... 和图表中横线)
            lineChart.getAxisRight().setEnabled(false);
            // 隐藏左边的左边轴 (同上)

            // 网格背景颜色
            lineChart.setGridBackgroundColor(Color.parseColor("#00000000"));
            // 是否显示表格颜色
            lineChart.setDrawGridBackground(false);
            // 设置边框颜色
            lineChart.setBorderColor(Color.parseColor("#00000000"));
            // 打开或关闭绘制的图表边框。（环绕图表的线）
            lineChart.setDrawBorders(false);


            Legend mLegend = lineChart.getLegend(); // 设置比例图标示
            // 设置窗体样式
            mLegend.setForm(Legend.LegendForm.CIRCLE);
            // 字体
            mLegend.setFormSize(4f);
            // 字体颜色
            mLegend.setTextColor(Color.parseColor("#00000000"));


            XAxis xAxis = lineChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            Log.i("zhengsheng_error9", "ok");
            try {
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(1f); // one hour
                xAxis.setValueFormatter(new IAxisValueFormatter() {

                    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm");

                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        long millis = TimeUnit.HOURS.toMillis((long) value);
                        return mFormat.format(new Date(millis));
                    }
                });
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        if (value > 10000) {
                            return (value / 10000) + "w";
                        } else if (value > 1000) {
                            return (value / 1000) + "k";
                        } else {
                            return String.valueOf(value);
                        }
                    }
                });
                lineChart.animateY(1000); // 立即执行的动画,Y轴
                Log.i("zhengsheng_error11", "fine");
            }catch (Exception e){
                Log.i("zhengsheng_error10", String.valueOf(e));
            }
        }
        public LineData getLineData(long count[], double YV[]) {
            ArrayList<Entry> values = new ArrayList<Entry>();
            int id = 0;
            long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());
            Log.i("zhengsheng_now", String.valueOf(now));
            Log.i("zhengsheng_now", String.valueOf(System.currentTimeMillis()));
            for (int i = 0; i < count.length; i++) {
                float y = (float) YV[i];
                float x = (float) TimeUnit.MILLISECONDS.toHours(count[i]);
                Log.i("zhengsheng_now_x", String.valueOf(x));
                Log.i("zhengsheng_now_x", String.valueOf(count[i]));
                values.add(new Entry(x, y)); // add one entry per hour
            }
            LineDataSet set1 = new LineDataSet(values, "DataSet 1");
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.parseColor("#ff0000"));
            set1.setLineWidth(2.5f);
            set1.setDrawCircles(true);
            set1.setFillAlpha(65);
            set1.setFillColor(ColorTemplate.getHoloBlue());
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
//            set1.setDrawValues(true);
            LineData data = new LineData(set1);
//            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(5f);
            return data;
        }
    }



}
