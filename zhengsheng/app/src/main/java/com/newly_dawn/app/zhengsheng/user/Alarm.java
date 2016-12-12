package com.newly_dawn.app.zhengsheng.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.newly_dawn.app.zhengsheng.R;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Alarm extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("事件通知");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm.this.finish();
            }
        });
        initUI();
        loadData();
        loadEvent();
    }
    public void initUI(){

    }
    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id", null);
        Log.i("user_id", user_id);
        String IP = getString(R.string.IP);
        String targetUrl = IP+"/api/v1/user/eventList/?user_id=" + user_id;
        Map<String, String> dataMp = new HashMap<>();
        dataMp.put("url", targetUrl);
        new GetAlertListAsync().execute(dataMp);
    }
    public void loadEvent(){
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.ocher);
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        swipeRefreshLayout.setProgressBackgroundColor(R.color.white);
        swipeRefreshLayout.setProgressViewEndTarget(true, 200);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
    }
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    swipeRefreshLayout.setRefreshing(false);
                    loadData();
                    break;
                default:
                    break;
            }
        }
    };

    public class GetAlertListAsync extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                Map<String, String> dataMp = new HashMap<>();
                httpRequest.get_connect();
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
                Toast.makeText(Alarm.this, "获取信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        Log.i("zhaolong", String.valueOf(jsonObject));
                        JSONArray alertList = jsonObject.getJSONArray("event_list");
                        Log.i("zhaolong", String.valueOf(alertList));
                        ListView alarm_list = (ListView)findViewById(R.id.alarm_list);
                        List<Map<String,String>> listItems = new ArrayList<>();
                        int len = alertList.length();
                        for(int i = 0; i < len; ++i){
                            Map<String, String> map = new HashMap<>();
                            JSONObject event = alertList.getJSONObject(i);
                            String type = event.getString("name");
                            String time = event.getString("time");
                            map.put("type", type);
                            map.put("time", time);
                            map.put("description","请注意，您的电表箱在" + time + "发生了" + type + "事件。");
                            listItems.add(map);
                        }
                        SimpleAdapter adapter = new SimpleAdapter(Alarm.this, listItems, R.layout.alarm_list_item,
                                new String[]{"type", "time", "description"}, new int[]{R.id.type, R.id.time, R.id.description});
                        alarm_list.setAdapter(adapter);

                    } catch (JSONException e) {
                        Log.i("zhengsheng_login_x2", String.valueOf(e));
                        e.printStackTrace();
                    }
                    Log.i("zhengsheng_login_x3", result.get("text"));
                } else {
                    Log.i("zhengsheng_login_x4", result.get("code"));
                    Toast.makeText(Alarm.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}
