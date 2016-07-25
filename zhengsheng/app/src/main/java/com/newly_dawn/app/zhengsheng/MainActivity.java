package com.newly_dawn.app.zhengsheng;

import android.app.Activity;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private View index, attention, mine;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout  = (TabLayout)findViewById(R.id.tab_FindFragment_title);
        mViewPager  = (ViewPager)findViewById(R.id.vp_FindFragment_pager);

        mInflater = getLayoutInflater();
        index = mInflater.inflate(R.layout.activity_index, null);
        attention = mInflater.inflate(R.layout.activity_welcome, null);
        mine = mInflater.inflate(R.layout.activity_mine, null);

        //添加页卡视图
        mViewList.add(index);
        mViewList.add(attention);
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
}
