package com.newly_dawn.app.zhengsheng;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.LinearLayout;

import com.newly_dawn.app.zhengsheng.data.BarChart;
import com.newly_dawn.app.zhengsheng.data.PieChart;
import com.newly_dawn.app.zhengsheng.data.TodayData;
import com.newly_dawn.app.zhengsheng.user.Alarm;
import com.newly_dawn.app.zhengsheng.user.Login;
import com.newly_dawn.app.zhengsheng.user.Register;

import java.util.ArrayList;
import java.util.List;


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
//    数据显示
    public void build_data(){
        LinearLayout today = (LinearLayout)data.findViewById(R.id.today);
        LinearLayout month = (LinearLayout)data.findViewById(R.id.month);
        View picChart = new TodayData().execute(this);
        View barChart = new BarChart().execute(this);
        today.addView(picChart);
        month.addView(barChart);

        LinearLayout linearLayout = (LinearLayout)data.findViewById(R.id.alarm_list_btn);
        linearLayout.setOnClickListener(new alarmListBtnClickListener());
    }
    public class alarmListBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent alarm_intent = new Intent(MainActivity.this, Alarm.class);
            startActivity(alarm_intent);
        }
    }
    public void build_mine(){
        Button loginBtn = (Button)mine.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new loginBtnClickListener());
        Button registerBtn = (Button)mine.findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new registerBtnClickListener());
    }
    public class loginBtnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Intent login_intent = new Intent(MainActivity.this, Login.class);
            startActivity(login_intent);
        }
    }
    public class registerBtnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent register_intent = new Intent(MainActivity.this, Register.class);
            startActivity(register_intent);
        }
    }
}
