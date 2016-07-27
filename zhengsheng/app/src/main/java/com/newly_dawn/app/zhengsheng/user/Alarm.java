package com.newly_dawn.app.zhengsheng.user;

import android.graphics.Color;
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

import com.newly_dawn.app.zhengsheng.R;

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
        toolbar.setTitle("预警");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alarm.this.finish();
            }
        });
        initUI();
    }
    public void initUI(){
        ListView alarm_list = (ListView)findViewById(R.id.alarm_list);
        List<Map<String,String>> listItems = new ArrayList<>();
        for(int i = 0; i < 20; ++i){
            Map<String, String> map = new HashMap<>();
            map.put("type", "温度预警");
            map.put("time","2016-07-26 15:17:22");
            map.put("description","您的电表箱温度过高，请注意检修");
            listItems.add(map);
        }
        Log.i("zhaolong", String.valueOf(listItems));
        Log.i("zhaolong", listItems.size() + "");
        SimpleAdapter adapter = new SimpleAdapter(Alarm.this, listItems, R.layout.alarm_list_item,
                new String[]{"type", "time", "description"}, new int[]{R.id.type, R.id.time, R.id.description});
        alarm_list.setAdapter(adapter);

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
                            Thread.sleep(3000);
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
//                    listviewadapter = new ListViewAdapter(getApplication(),key);
//                    ListView_Id.setAdapter(listviewadapter);
                    break;
                default:
                    break;
            }
        }
    };
}
