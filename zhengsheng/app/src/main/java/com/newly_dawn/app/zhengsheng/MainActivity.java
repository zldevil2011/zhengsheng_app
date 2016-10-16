package com.newly_dawn.app.zhengsheng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.jude.rollviewpager.hintview.ColorPointHintView;
import com.newly_dawn.app.zhengsheng.data.BarChart;
import com.newly_dawn.app.zhengsheng.data.TodayData;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;
import com.newly_dawn.app.zhengsheng.user.Alarm;
import com.newly_dawn.app.zhengsheng.user.login;
import com.newly_dawn.app.zhengsheng.user.Register;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
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


public class MainActivity extends AppCompatActivity {
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
    }
    public class alarmListBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("user_id", null);
            if(user_id == null){
                Intent login_intent = new Intent(MainActivity.this, login.class);
                startActivityForResult(login_intent, 1);
            }else{
                Intent alarm_intent = new Intent(MainActivity.this, Alarm.class);
                startActivity(alarm_intent);
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
                    editor.commit();
                }catch (Exception e){
                    Log.i("zhengsheng_exp_null", String.valueOf(e));
                }

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

                    String targetUrl = "http://192.168.1.60:8000/api/v1/user_info/";
                    Map<String, String> dataMp = new HashMap<>();
                    dataMp.put("url", targetUrl);
                    dataMp.put("user_id", id);
                    new paintingToday().execute(dataMp);

                    String targetUrl_month = "http://192.168.1.60:8000/api/v1/user_info/";
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
                Toast.makeText(MainActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        Log.i("zhengsheng_exp_json", String.valueOf(jsonObject));
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


                        int xmin = 0;
                        int xmax = 30;
                        int ymin = 0;
                        int ymax = 31;

                        int len = today_hour.length();
                        double[] month_day_arr = new double[31];
                        double[] month_power_arr = new double[31];
                        for(int i = 0; i < len; ++i){
                            month_day_arr[i] = today_hour.getDouble(i);
                            xmax = i + 1;
                            month_power_arr[i] = today_power.getDouble(i);
                            if(ymax < month_power_arr[i]){
                                ymax = today_power.getInt(i) + 2;
                            }
                        }
                        List todayX = new ArrayList();
                        List todayY = new ArrayList();
                        todayX.add(month_day_arr);
                        todayY.add(month_power_arr);

                        String[] titles = new String[] { "电能"};
                        XYMultipleSeriesDataset dataset = buildDataset(titles, todayX, todayY);

                        int[] colors = new int[] { Color.BLUE};
                        PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE};
                        XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, true);

                        setChartSettings(renderer, "", "时间","电能(Kwh)", xmin, xmax, ymin, ymax , Color.BLACK, Color.BLACK);
                        View newView = ChartFactory.getCubeLineChartView(MainActivity.this, dataset, renderer, 0.3F);
                        LinearLayout today = (LinearLayout)data.findViewById(R.id.today);
                        today.removeAllViews();
                        today.addView(newView);
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
                        Log.i("zhengsheng_exp_json", String.valueOf(jsonObject));

                        JSONObject month_data = new JSONObject(jsonObject.getString("month_data"));
                        JSONArray month_day = new JSONArray(month_data.getString("month_day"));
                        JSONArray month_power = new JSONArray(month_data.getString("month_power"));
                        Log.i("zhengsheng_exp_month", String.valueOf(month_day));

                        double xmin = 0;
                        double xmax = -1;
                        double ymin = 0;
                        double ymax = -1;
                        int len = month_day.length();
                        double[] month_day_arr = new double[31];
                        double[] month_power_arr = new double[31];
                        for(int i = 0; i < len; ++i){
//                            Log.i("zhengsheng_exp_day", month_day.getString(i));
                            month_day_arr[i] = month_day.getDouble(i);
                            xmax = i + 1;

                            month_power_arr[i] = month_power.getDouble(i);
                            if(month_power.getDouble(i) > ymax){
                                ymax = month_power.getDouble(i);
                            }
                        }

                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                        int nr = len;
                        for (int i = 0; i < 1; i++) {
                            CategorySeries series = new CategorySeries("电量 " + (i + 1));
                            for (int k = 0; k < nr; k++) {
                                series.add(month_power_arr[k]);
                            }
                            dataset.addSeries(series.toXYSeries());
                        }
                        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

                        SimpleSeriesRenderer render = new SimpleSeriesRenderer();
                        render.setColor(Color.BLUE);
                        renderer.addSeriesRenderer(render);
                        renderer.setBarSpacing(1.0);
                        renderer.setApplyBackgroundColor(true);
                        renderer.setBackgroundColor(Color.WHITE);
                        renderer.setMarginsColor(Color.WHITE);
                        renderer.setZoomEnabled(false, false);
                        renderer.setPanEnabled(false, false);
                        renderer.setLabelsTextSize(25);
                        renderer.setAxisTitleTextSize(20);
                        renderer.setXLabels(nr);
                        renderer.setAxesColor(Color.BLACK);
                        renderer.setLabelsColor(Color.BLACK);
                        setChartSettingsPie(renderer, xmin, xmax, ymin, ymax);

                        View newView = ChartFactory.getBarChartView(MainActivity.this, dataset, renderer, org.achartengine.chart.BarChart.Type.DEFAULT);

                        LinearLayout month = (LinearLayout)data.findViewById(R.id.month);
                        month.removeAllViews();
                        month.addView(newView);
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

    /**
     * setChartSettings 方法设置了下坐标轴样式。
     */
    private void setChartSettingsPie(XYMultipleSeriesRenderer renderer, double xmin, double xmax, double ymin, double ymax) {
        renderer.setChartTitle("");
        renderer.setXTitle("时间");
        renderer.setYTitle("用电量");
        renderer.setXAxisMin(xmin);
        renderer.setXAxisMax(xmax);
        renderer.setYAxisMin(ymin);
        renderer.setYAxisMax(ymax);
    }
}
