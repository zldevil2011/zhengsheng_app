package com.newly_dawn.app.zhengsheng.user;

import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.newly_dawn.app.zhengsheng.R;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("工单");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkOrder.this.finish();
            }
        });
        loadData();
    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);
        String IP = getString(R.string.IP);
        String targetUrl = IP + "/api/v1/user/workorderList/";
        Map<String, String> dataMp = new HashMap<>();
        dataMp.put("url", targetUrl);
        dataMp.put("user_id", user_id);
        new getUserWorkOrders().execute(dataMp);
    }
    public class getUserWorkOrders extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute() {}
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            url += "?user_id=" + params[0].get("user_id");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                Toast.makeText(WorkOrder.this, "获取工单信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        ListView workOrderList = (ListView)findViewById(R.id.workOrderList);
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        Log.i("zhaolong", String.valueOf(jsonObject));
                        JSONArray alertList = jsonObject.getJSONArray("workOrderList");
                        Log.i("zhaolong", String.valueOf(alertList));
                        List<Map<String,String>> listItems = new ArrayList<>();
                        int len = alertList.length();
                        for(int i = 0; i < len; ++i){
                            Map<String, String> map = new HashMap<>();
                            String num = alertList.getJSONObject(i).getString("num");
                            String time = alertList.getJSONObject(i).getString("time");
                            String content = alertList.getJSONObject(i).getString("content");
                            String status = alertList.getJSONObject(i).getString("status");
                            map.put("work_order_content", content);
                            map.put("work_order_status", "状态:" + status + ",  ");
                            map.put("work_order_number", "编号:" +num + ",  ");
                            map.put("work_order_time", time);
                            listItems.add(map);
                        }
                        SimpleAdapter adapter = new SimpleAdapter(WorkOrder.this, listItems, R.layout.workorder_list_item,
                                new String[]{"work_order_content", "work_order_status", "work_order_number","work_order_time"},
                                new int[]{R.id.work_order_content, R.id.work_order_status, R.id.work_order_number, R.id.work_order_time});
                        workOrderList.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.i("zhengsheng_exp2", String.valueOf(e));
                        e.printStackTrace();
                    }
//                    Log.i("zhengsheng_exp3", result.get("text"));
                } else {
                    Log.i("zhengsheng_exp4", result.get("code"));
                }
            }
        }
    }
}
