package com.newly_dawn.app.zhengsheng.data;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.newly_dawn.app.zhengsheng.R;

public class data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        LinearLayout today = (LinearLayout)findViewById(R.id.today);
        LinearLayout month = (LinearLayout)findViewById(R.id.month);
        View picChart = new PieChart().execute(this);
        View barChart = new BarChart().execute(this);
        today.addView(picChart);
        month.addView(barChart);
    }
}
