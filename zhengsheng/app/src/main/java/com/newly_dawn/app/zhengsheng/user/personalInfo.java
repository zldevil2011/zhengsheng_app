package com.newly_dawn.app.zhengsheng.user;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.newly_dawn.app.zhengsheng.R;

public class personalInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("个人信息");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                personalInfo.this.setResult(1, intent);
                //关闭Activity
                personalInfo.this.finish();
            }
        });
        writeDate();
        initEvent();
    }
    public void writeDate(){
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
            String user_id = sharedPreferences.getString("user_id", null);
            if(user_id == null){
                Intent login_intent = new Intent(personalInfo.this, login.class);
                startActivityForResult(login_intent, 1);
            }else{

            }
        }catch (Exception e){
            Log.i("zhaolong_xp", String.valueOf(e));
        }
    }
    public void initEvent(){
        final EditText information_birthday = (EditText)findViewById(R.id.information_birthday);
        information_birthday.setFocusable(false);
        information_birthday.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String date = information_birthday.getText().toString();
                    String[] dateArr = date.split("-");
                    new DatePickerDialog(personalInfo.this, new DateListener(),
                            Integer.valueOf(dateArr[0]), Integer.valueOf(dateArr[1]) - 1, Integer.valueOf(dateArr[2])).show();
                }catch (Exception e){
                    new DatePickerDialog(personalInfo.this, new DateListener(), 1992, 12, 31).show();
                }
            }
        });
    }
    private class DateListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            arg2 += 1;
            String year = String.valueOf(arg1);
            String month = arg2 < 10 ? "0" + String.valueOf(arg2) : String.valueOf(arg2);
            String day = arg3 < 10 ? "0" + String.valueOf(arg3) : String.valueOf(arg3);
            String date = year + "-" + month + "-" + day;
            EditText information_birthday = (EditText)findViewById(R.id.information_birthday);
            information_birthday.setText(date);
        }
    }
}
