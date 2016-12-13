package com.newly_dawn.app.zhengsheng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
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
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.newly_dawn.app.zhengsheng.data.data_list;
import com.newly_dawn.app.zhengsheng.tools.Browser;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;
import com.newly_dawn.app.zhengsheng.user.Alarm;
import com.newly_dawn.app.zhengsheng.user.WorkOrder;
import com.newly_dawn.app.zhengsheng.user.feedback;
import com.newly_dawn.app.zhengsheng.user.login;
import com.newly_dawn.app.zhengsheng.user.Register;
import com.newly_dawn.app.zhengsheng.user.Contactus;
import com.newly_dawn.app.zhengsheng.user.personalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity {


    private BarChart mBarChart;
    private BarData mBarData;
    private BarCharts mBarCharts;

    private LineChart mLineChart;
    private LineData mLineData;
    private LineCharts mLineCharts;

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View index, data, mine;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout  = (TabLayout)findViewById(R.id.tab_FindFragment_title);
        mViewPager  = (ViewPager)findViewById(R.id.vp_FindFragment_pager);

        mInflater = getLayoutInflater();
        index = mInflater.inflate(R.layout.activity_index, null);
        data = mInflater.inflate(R.layout.activity_data, null);
        mine = mInflater.inflate(R.layout.activity_mine, null);

        //添加页卡视图
        mViewList.add(index);
        mViewList.add(data);
        mViewList.add(mine);

        //添加页卡标题
        mTitleList.add("首页");
        mTitleList.add("数据");
        mTitleList.add("我的");


        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));


        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        build_index();
        build_data();
        build_mine();
        reloadData();
    }
    public void reloadData(){
//        当再次打开App并保留有上次的登录信息的话，会读取信息并展示
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("user_id", null);
            String username = sharedPreferences.getString("username", null);
            String device_id = sharedPreferences.getString("device_id", null);
            if(user_id == null|| username==null||device_id==null){
                return;
            }
            TextView usernameTeV = (TextView) mine.findViewById(R.id.personal_username);
            TextView deviceIdTeV = (TextView) mine.findViewById(R.id.personal_device_id);
            usernameTeV.setText(username);
            deviceIdTeV.setText("ID:" + device_id);
            LinearLayout personal_info_linearlayout = (LinearLayout) mine.findViewById(R.id.personal_info_linearlayout);
            personal_info_linearlayout.setVisibility(View.VISIBLE);

            LinearLayout logoutLine = (LinearLayout) mine.findViewById(R.id.logoutLine);
            logoutLine.setVisibility(View.VISIBLE);
            String IP = getString(R.string.IP);
            String targetUrl = IP + "/api/v1/user/electricity/data/";
            Map<String, String> dataMp = new HashMap<>();
            dataMp.put("url", targetUrl);
            dataMp.put("user_id", user_id);
            new paintingToday().execute(dataMp);

            String targetUrl_month = IP + "/api/v1/user/electricity/data/";
            Map<String, String> dataMqp = new HashMap<>();
            dataMqp.put("url", targetUrl);
            dataMqp.put("user_id", user_id);
            new paintingMonth().execute(dataMqp);
        }catch (Exception e){
            Log.i("zhengsheng_bug", String.valueOf(e));
        }
    }
    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;
        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }
        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);//页卡标题
        }
    }
    private RollPagerView mRollViewPager;
    public void build_index(){
        mRollViewPager = (RollPagerView)index.findViewById(R.id.roll_view_pager);

        //设置播放时间间隔
        mRollViewPager.setPlayDelay(3000);
        //设置透明度
        mRollViewPager.setAnimationDurtion(500);
        //设置适配器
        mRollViewPager.setAdapter(new TestNormalAdapter());

        //设置指示器（顺序依次）
        //自定义指示器图片
        //设置圆点指示器颜色
        //设置文字指示器
        //隐藏指示器
        //mRollViewPager.setHintView(new IconHintView(this, R.drawable.point_focus, R.drawable.point_normal));
        mRollViewPager.setHintView(new ColorPointHintView(this, Color.YELLOW, Color.WHITE));
        //mRollViewPager.setHintView(new TextHintView(this));
        //mRollViewPager.setHintView(null);

        try {
            String IP = getString(R.string.IP);
            String targetUrl = IP + "/api/v1/newsList/";
            Map<String, String> dataMp = new HashMap<>();
            dataMp.put("url", targetUrl);
            new getNewsSync().execute(dataMp);
        }catch (Exception e){
            Log.i("zhengsheng_ap", String.valueOf(e));
        }
    }
    private int image_no = 0;
    private int news_total_num = 10;
    private Bitmap[] image_list = new Bitmap[100];
    private String[] news_title = new String[100];
    private String[] news_link = new String[100];
    private String[] news_image_link = new String[100];
    private ListView news_listview;
    public class getNewsSync extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected void onPreExecute() {}
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            try{
                HttpRequest httpRequest = new HttpRequest(url);
                httpRequest.get_connect();
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.i("zhengsheng_erro13", "ok_");
            return result;
        }

        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "获取新闻失败", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Log.i("zhengsheng_141z", result.get("text"));
                    String text = result.get("text");
                    JSONObject jsonObject = new JSONObject(text);
                    JSONArray news_list = jsonObject.getJSONArray("news_list");
                    Log.i("zhengsheng_141x", String.valueOf(news_list));
                    int len = news_list.length();
                    Log.i("zhengsheng_141y", String.valueOf(len));
                    news_total_num = 5;
                    for(int i = 0; i < len; ++i){
                        JSONObject tmp = news_list.getJSONObject(i);
                        news_title[i] = tmp.getString("name");
                        news_link[i] = tmp.getString("link").replace("\\/","/");
                        news_image_link[i] = tmp.getString("img").replace("\\/","/");
                        Log.i("zhengsheng_141w", String.valueOf(news_image_link[i]));
                    }
                    Map<String, String> dataMp = new HashMap<>();
                    dataMp.put("url", news_image_link[0]);
                    dataMp.put("idx", String.valueOf(0));
                    for(int j = 0; j < len; ++j) {
                        Log.i("zhaolong_title", String.valueOf(news_title[j]));
                    }
                    new downloadImageSync().execute(dataMp);
                    news_listview = (ListView)index.findViewById(R.id.news_list);
                    loadNewsList();
//                    if(len > 0){
//                        String start_url = news_image_link[0];
//                        Map<String, String> dataMp = new HashMap<>();
//                        dataMp.put("url", start_url);
//                        new downloadImageSync().execute(dataMp);
//                    }
                    Log.i("zhengsheng_151", "load success");
                }catch (Exception e){
                    Log.i("zhengsheng_141", String.valueOf(e));
                }
            }
        }
    }
    public class downloadImageSync extends AsyncTask<Map<String,String>, Void, Map<String, Object>> {
        Map<String, Object> result = new HashMap<>();
        @Override
        protected void onPreExecute() {}
        @Override
        protected Map<String, Object> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            String idx = params[0].get("idx");
            Bitmap bitmap=null;
            URL myFileURL;
            try{
                Log.i("zhengsheng_URL", url);
                myFileURL = new URL(url);
                HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                conn.setConnectTimeout(6000);
                conn.setDoInput(true);
                conn.setUseCaches(true);
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);
                is.close();
            }catch(Exception e){
                e.printStackTrace();
                Log.i("zhengsheng_URL_EXC", String.valueOf(e));
            }
            result.put("img", bitmap);
            result.put("idx", idx);
            return result;
        }

        protected void onPostExecute(Map<String, Object> result) {
            if (result == null) {
                Toast.makeText(MainActivity.this, "获取图片失败", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    Bitmap img = (Bitmap) result.get("img");
                    int idx = Integer.parseInt(result.get("idx").toString());
                    List<Map<String,Object>> listItems = new ArrayList<>();
                    for(int j = 0; j < news_total_num; ++j) {
                        Log.i("zhaolong", "idx = " + String.valueOf(idx) + String.valueOf(news_title[j]));
                    }
                    for(int i = 0; i < news_total_num; ++i){
                        Log.i("zhengsheng_index", String.valueOf(i));
                        Map<String, Object> map = new HashMap<>();
                        if(i == idx){
                            map.put("news_img", img);
                            image_list[i] = img;
                        }else{
                            map.put("news_img", image_list[i]);
                        }
                        map.put("news_title", news_title[i]);
                        map.put("news_link", news_link[i]);
                        map.put("news_author", "中国电力新闻");
                        map.put("news_time","2015-10-10 1" + String.valueOf(i) + ":00:00");
                        listItems.add(map);
                    }
                    Log.i("zhengsheng_index_total", String.valueOf(listItems.size()));
                    Log.i("zhengsheng_index_list", String.valueOf(listItems));
                    SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, listItems, R.layout.news_list_item,
                            new String[]{"news_img", "news_title", "news_author", "news_time"}, new int[]{R.id.news_img, R.id.news_title, R.id.news_author, R.id.news_time});
                    adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

                        public boolean setViewValue(View view, Object data,
                                                    String textRepresentation) {
                            //判断是否为我们要处理的对象
                            if(view instanceof ImageView  && data instanceof Bitmap){
                                ImageView iv = (ImageView) view;

                                iv.setImageBitmap((Bitmap) data);
                                return true;
                            }else
                                return false;
                        }
                    });
                    news_listview.setAdapter(adapter);
                    setListViewHeightBasedOnChildren(news_listview);
                    Map<String, String> dataMp = new HashMap<>();
                    if(idx < news_total_num){
                        idx ++;
                        dataMp.put("url", news_image_link[idx]);
                        dataMp.put("idx", String.valueOf(idx));
                        new downloadImageSync().execute(dataMp);
                    }
                }catch (Exception e){
                    Log.i("zhengsheng_142", String.valueOf(e));
                }
            }
        }
    }
    public void loadNewsList(){

        List<Map<String,Object>> listItems = new ArrayList<>();
        for(int i = 0; i < news_total_num; ++i){
            Log.i("zhengsheng_index", String.valueOf(i));
            Map<String, Object> map = new HashMap<>();
            map.put("news_img", image_list[i]);
            map.put("news_title", news_title[i]);
            map.put("news_link", news_link[i]);
            map.put("news_author", "Author");
            map.put("news_time","2015-10-10 12:00:00");
            listItems.add(map);
        }
        Log.i("zhengsheng_index", String.valueOf(listItems.size()));
        Log.i("zhengsheng_index", String.valueOf(listItems));
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, listItems, R.layout.news_list_item,
                new String[]{"news_img", "news_title", "news_author", "news_time"}, new int[]{R.id.news_img, R.id.news_title, R.id.news_author, R.id.news_time});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                //判断是否为我们要处理的对象
                if(view instanceof ImageView  && data instanceof Bitmap){
                    ImageView iv = (ImageView) view;

                    iv.setImageBitmap((Bitmap) data);
                    return true;
                }else
                    return false;
            }
        });
        news_listview.setAdapter(adapter);
        setListViewHeightBasedOnChildren(news_listview);
        news_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = (Map<String, String>)parent.getItemAtPosition(position);
                Intent intent = new Intent();
                intent.putExtra("url", map.get("news_link"));
                intent.setClass(MainActivity.this, Browser.class);
                startActivity(intent);
            }
        });
    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    private class TestNormalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.drawable.zhengsheng,
                R.drawable.zhengsheng2,
        };
        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return view;
        }


        @Override
        public int getCount() {
            return imgs.length;
        }
    }
//    数据显示
    public void build_data(){
        LinearLayout today = (LinearLayout)data.findViewById(R.id.today);
        LinearLayout month = (LinearLayout)data.findViewById(R.id.month);
//        View picChart = new TodayData().execute(this);
//        View barChart = new BarChart().execute(this);
//        today.addView(picChart);
//        month.addView(barChart);


        LinearLayout linearLayout = (LinearLayout)data.findViewById(R.id.alarm_list_btn);
        linearLayout.setOnClickListener(new alarmListBtnClickListener());
        LinearLayout workOrder_list_btn = (LinearLayout)data.findViewById(R.id.workOrder_list_btn);
        workOrder_list_btn.setOnClickListener(new workOrderListBtnClickListener());

        Button dataDetailsBtn = (Button)data.findViewById(R.id.dataDetailsBtn);
        dataDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent datadetail_intent = new Intent(MainActivity.this, data_list.class);
                startActivity(datadetail_intent);
            }
        });
    }
    public class alarmListBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                String user_id = sharedPreferences.getString("user_id", null);
//                Log.i("user_id", user_id);
                if(user_id == null){
                    Intent login_intent = new Intent(MainActivity.this, login.class);
                    startActivityForResult(login_intent, 1);
                }else{
                    Intent alarm_intent = new Intent(MainActivity.this, Alarm.class);
                    startActivity(alarm_intent);
                }
            }catch (Exception e){
                Log.i("zhaolong_xp", String.valueOf(e));
            }
        }
    }
    public class workOrderListBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            try {
                SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                String user_id = sharedPreferences.getString("user_id", null);
                if(user_id == null){
                    Intent login_intent = new Intent(MainActivity.this, login.class);
                    startActivityForResult(login_intent, 1);
                }else{
                    Intent workorder_intent = new Intent(MainActivity.this, WorkOrder.class);
                    startActivity(workorder_intent);
                }
            }catch (Exception e){
            }
        }
    }
    public void build_mine(){
        Button loginBtn = (Button)mine.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new loginBtnClickListener());
        Button registerBtn = (Button)mine.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new registerBtnClickListener());

        LinearLayout logoutLine = (LinearLayout)mine.findViewById(R.id.logoutLine);
        logoutLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确认登出吗？")
                        .setCancelText("取消")
                        .setConfirmText("确认")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener(){
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();

                                LinearLayout personal_info_linearlayout = (LinearLayout)mine.findViewById(R.id.personal_info_linearlayout);
                                personal_info_linearlayout.setVisibility(View.GONE);

                                LinearLayout logoutLine = (LinearLayout)mine.findViewById(R.id.logoutLine);
                                logoutLine.setVisibility(View.GONE);

                                LinearLayout today = (LinearLayout)data.findViewById(R.id.today);
                                today.removeAllViews();
                                LinearLayout month = (LinearLayout)data.findViewById(R.id.month);
                                month.removeAllViews();
                                TextView workOrderLenTex = (TextView)data.findViewById(R.id.workOrderLen);
                                workOrderLenTex.setText("0");
                                TextView tempAlertLenTex = (TextView)data.findViewById(R.id.tempAlertLen);
                                tempAlertLenTex.setText("0");
                                try {
                                    SharedPreferences preferences=getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=preferences.edit();
                                    editor.putString("user_id", null);
                                    editor.putString("username", null);
                                    editor.putString("device_id", null);
                                    editor.apply();
                                }catch (Exception e){
                                    Log.i("zhaolong_xp_null", String.valueOf(e));
                                }
                            }
                        })
                        .show();
            }
        });

        TextView contactUsTex = (TextView)mine.findViewById(R.id.contactUs);
        contactUsTex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent contactUs_intent = new Intent(MainActivity.this, Contactus.class);
                    startActivity(contactUs_intent);
                }catch (Exception e){
                    Log.i("zhengsheng_error5", String.valueOf(e));
                }
            }
        });
        TextView myInformation = (TextView)mine.findViewById(R.id.myInformation);
        TextView feedback_btn = (TextView)mine.findViewById(R.id.feedback);
        myInformation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                    String user_id = sharedPreferences.getString("user_id", null);
                    if(user_id == null){
                        Intent login_intent = new Intent(MainActivity.this, login.class);
                        startActivityForResult(login_intent, 1);
                    }else{
                        Intent personalInfo_intent = new Intent(MainActivity.this, personalInfo.class);
                        startActivity(personalInfo_intent);
                    }
                }catch (Exception e){
                    Log.i("zhaolong_xp", String.valueOf(e));
                }
            }
        });
        feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedback_intent = new Intent(MainActivity.this, feedback.class);
                startActivity(feedback_intent);
            }
        });
    }
    public class loginBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent login_intent = new Intent(MainActivity.this, login.class);
            startActivityForResult(login_intent, 1);
//            startActivity(login_intent);
        }
    }
    public class registerBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent register_intent = new Intent(MainActivity.this, Register.class);
            startActivity(register_intent);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1:
                try {

                    String username = data.getExtras().getString("username");//得到新Activity 关闭后返回的数据
                    String id = data.getExtras().getString("id");//得到新Activity 关闭后返回的数据
                    String email = data.getExtras().getString("email");//得到新Activity 关闭后返回的数据
                    String device_id = data.getExtras().getString("device_id");//得到新Activity 关闭后返回的数据
                    TextView usernameTeV = (TextView) mine.findViewById(R.id.personal_username);
                    TextView deviceIdTeV = (TextView) mine.findViewById(R.id.personal_device_id);
                    usernameTeV.setText(username);
                    deviceIdTeV.setText("ID:" + device_id);
                    LinearLayout personal_info_linearlayout = (LinearLayout) mine.findViewById(R.id.personal_info_linearlayout);
                    personal_info_linearlayout.setVisibility(View.VISIBLE);

                    LinearLayout logoutLine = (LinearLayout) mine.findViewById(R.id.logoutLine);
                    logoutLine.setVisibility(View.VISIBLE);
                    String IP = getString(R.string.IP);
                    String targetUrl = IP + "/api/v1/user/electricity/data/";
                    Map<String, String> dataMp = new HashMap<>();
                    dataMp.put("url", targetUrl);
                    dataMp.put("user_id", id);
                    new paintingToday().execute(dataMp);

                    String targetUrl_month = IP + "/api/v1/user/electricity/data/";
                    Map<String, String> dataMqp = new HashMap<>();
                    dataMqp.put("url", targetUrl);
                    dataMqp.put("user_id", id);
                    new paintingMonth().execute(dataMqp);
                }
                catch (Exception e){
                    Log.i("zhengsheng_exp_noret", String.valueOf(e));
                }
            case 2:
                //来自按钮2的请求，作相应业务处理
        }
    }
    public class paintingToday extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
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
                Toast.makeText(MainActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    Log.i("zhengsheng_error", String.valueOf(result.get("text")));
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
//                        显示工单数量
                        String workOrderLen = jsonObject.getString("workorder_len");
                        TextView workOrderLenTex = (TextView)data.findViewById(R.id.workOrderLen);
                        workOrderLenTex.setText(workOrderLen);
//                        显示温度报警数量
                        String tempAlertLen = jsonObject.getString("tempalert_len");
                        TextView tempAlertLenTex = (TextView)data.findViewById(R.id.tempAlertLen);
                        tempAlertLenTex.setText(tempAlertLen);
                        JSONObject today_data = new JSONObject(jsonObject.getString("today_data"));
                        JSONArray today_hour = new JSONArray(today_data.getString("today_hour"));
                        JSONArray today_power = new JSONArray(today_data.getString("today_power"));




                        int len = today_hour.length();
                        double[] today_hour_arr = new double[len];
                        double[] today_power_arr = new double[len];
                        int ret_data_index = 0;
                        for(int i = 0; i < len; ++i){
                            today_hour_arr[i] = i;
                            try{
                                if(today_hour.getInt(ret_data_index) == i){
                                    today_power_arr[i] = today_power.getDouble(ret_data_index);
                                    ret_data_index++;
                                }else{
                                    today_power_arr[i] = 0;
                                }
                            }catch (Exception e){
                                today_power_arr[i] = 0;
                            }
                        }
                        if(len == 0){
                            return;
                        }
                        mLineCharts = new LineCharts();
                        mLineChart = (LineChart) data.findViewById(R.id.todayhElectricityData);
                        double[] ap = new double[10];
                        mLineData = mLineCharts.getLineData(len, today_power_arr);

                        mLineCharts.showLineChart(mLineChart, mLineData);
                    } catch (Exception e) {
                        Log.i("zhengsheng_exp2", String.valueOf(e));
                        e.printStackTrace();
                    }
                } else {
                    Log.i("zhengsheng_exp4", result.get("code"));
                }
            }
        }
    }

//    打开之后联网获取当前用户当月的用电数据
    public class paintingMonth extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
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
                Toast.makeText(MainActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        JSONObject month_data = new JSONObject(jsonObject.getString("month_data"));
                        JSONArray month_day = new JSONArray(month_data.getString("month_day"));
                        JSONArray month_power = new JSONArray(month_data.getString("month_power"));
                        int len = month_day.length();
                        double[] month_day_arr = new double[len];
                        double[] month_power_arr = new double[len];
                        for(int i = 0; i < len; ++i){
                            month_day_arr[i] = month_day.getDouble(i);
                            month_power_arr[i] = month_power.getDouble(i);
                        }
                        if(month_day_arr.length == 0){
                            return;
                        }
                        mBarCharts = new BarCharts();
                        mBarChart = (BarChart)data.findViewById(R.id.monthElectricityData);
                        mBarData = mBarCharts.getBarData(month_day.length(), month_power_arr);
                        mBarCharts.showBarChart(mBarChart, mBarData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i("zhengsheng_exp4", result.get("code"));
                }
            }
        }
    }
    public class BarCharts {

        private String[] color = {
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD"};

        public void showBarChart(BarChart barChart, BarData barData) {
            // 数据描述
            // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
            barChart.setNoDataText("You need to provide data for the chart.");
            Description ap = new Description();
            ap.setText("");
            barChart.setDescription(ap);
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
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return String.valueOf((int)value) + "月";
                }
            });
            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    if(value > 10000){
                        return (value / 10000) + "w";
                    }
                    else if(value > 1000){
                        return (value / 1000) + "k";
                    }else if(value < -10000){
                        return (value / 10000) + "w";
                    }else if(value < -1000){
                        return (value / 1000) + "k";
                    }else{
                        return String.valueOf(value);
                    }
                }
            });

            barChart.animateY(1000); // 立即执行的动画,Y轴
        }
        public BarData getBarData(int count, double YV[]) {
            ArrayList<String> xValues = new ArrayList<String>();
            for (int i = 0; i < count; i++) {
                xValues.add(""+(i+1)+"号");
            }
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            for (int i = 0; i < count; i++) {
                float value = (float) YV[i];
                yValues.add(new BarEntry((i+1), value));
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

    public class LineCharts {

        private String[] color = {
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD",
                "#C4FF8E","#FFF88D","#FFD38C","#8CEBFF","#FF8F9D","#6BF3AD"};

        public void showLineChart(LineChart lineChart, LineData lineData) {
            // 数据描述
            // 如果没有数据的时候，会显示这个，类似ListView的EmptyView
            lineChart.setNoDataText("You need to provide data for the chart.");
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
                    if(value > 10000){
                        return (value / 10000) + "w";
                    }
                    else if(value > 1000){
                        return (value / 1000) + "k";
                    }else{
                        return String.valueOf(value);
                    }
                }
            });
            lineChart.animateY(1000); // 立即执行的动画,Y轴
        }
        public LineData getLineData(int count, double YV[]) {
            Calendar calendar = Calendar.getInstance();
            int hours = calendar.get(Calendar.HOUR_OF_DAY); // 时
            int minutes = calendar.get(Calendar.MINUTE);    // 分
            int seconds = calendar.get(Calendar.SECOND);    // 秒
            long sub = hours * 3600 + minutes * 60  + seconds;
            long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis()) - hours;
            Log.i("zhengsheng_hour", String.valueOf(now));
            ArrayList<Entry> values = new ArrayList<Entry>();
            float from = now;
//            int to = from + count;
            float to = now + count;
            int id = 0;
            for (float x = from; x < to; x++) {
                float y = (float) YV[id++];
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
            set1.setDrawValues(true);
            LineData data = new LineData(set1);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(5f);
            return data;
        }
    }

}
