package com.newly_dawn.app.zhengsheng.user;

import android.app.DatePickerDialog;
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
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.HashMap;
import java.util.Map;

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
                String IP = getString(R.string.IP);
                String targetUrl = IP + "/api/v1/user_info/data/";
                Map<String, String> dataMqp = new HashMap<>();
                dataMqp.put("url", targetUrl);
                dataMqp.put("user_id", user_id);
                new getPersonalInfo().execute(dataMqp);
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
    public class getPersonalInfo  extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
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
                Log.i("zhengsheng_exp", String.valueOf(e));
                result = null;
            }
            return result;
        }

        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                Toast.makeText(personalInfo.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        Log.i("zhengsheng_exp_json", String.valueOf(jsonObject));

                        JSONObject user_info = new JSONObject(jsonObject.getString("user"));

                        Log.i("zhengsheng_exp_user", String.valueOf(user_info));
                        EditText username = (EditText)findViewById(R.id.information_nickname);
                        EditText telephone = (EditText)findViewById(R.id.information_telephone);
                        EditText email = (EditText)findViewById(R.id.information_email);
                        EditText location = (EditText)findViewById(R.id.information_location);
                        username.setText(user_info.getString("username"));
                        telephone.setText(user_info.getString("telephone"));
                        email.setText(user_info.getString("email"));
                        location.setText(user_info.getString("address"));
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
}
