package com.newly_dawn.app.zhengsheng.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.newly_dawn.app.zhengsheng.MainActivity;
import com.newly_dawn.app.zhengsheng.R;
import com.newly_dawn.app.zhengsheng.tools.HttpRequest;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("登录");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                login.this.setResult(1, intent);
                //关闭Activity
                login.this.finish();
            }
        });
        EditText usernameEDT = (EditText)findViewById(R.id.username);
        usernameEDT.setText("李四");
        EditText passwordEDT = (EditText)findViewById(R.id.password);
        passwordEDT.setText("123456");
        Button loginButton = (Button)findViewById(R.id.loginBtn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameEDT = (EditText)findViewById(R.id.username);
                String username = usernameEDT.getText().toString();
                EditText passwordEDT = (EditText)findViewById(R.id.password);
                String password = passwordEDT.getText().toString();
                String IP = getString(R.string.IP);
                String targetUrl = IP+"/api/v1/user/login/";
                Map<String, String> dataMp = new HashMap<>();
                dataMp.put("url", targetUrl);
                dataMp.put("username", username);
                dataMp.put("password", password);
                new LoginAsync().execute(dataMp);
            }
        });
    }

    public class LoginAsync extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                Map<String, String> dataMp = new HashMap<>();
                dataMp.put("username", params[0].get("username"));
                dataMp.put("password", params[0].get("password"));
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
                Toast.makeText(login.this, "登录失败", Toast.LENGTH_SHORT).show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.get("text"));
                        JSONObject user = new JSONObject(jsonObject.getString("user"));
                        String username = user.getString("username");
                        String id = user.getString("id");
                        String email = user.getString("email");
                        JSONObject device = new JSONObject(user.getString("device"));
                        String device_id = device.getString("device_id");
                        Log.i("zhengsheng_username", username);
                        Log.i("zhengsheng_email", email);
                        Log.i("zhengsheng_device_id", device_id);
                        Toast.makeText(login.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        //把返回数据存入Intent
                        intent.putExtra("username", username);
                        intent.putExtra("email", email);
                        intent.putExtra("device_id", device_id);
                        intent.putExtra("id", id);
                        SharedPreferences preferences=login.this.getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString("user_id", id);
                        editor.commit();
                        //设置返回数据
                        login.this.setResult(1, intent);
                        //关闭Activity
                        login.this.finish();
                    } catch (JSONException e) {
                        Log.i("zhengsheng_login_x2", String.valueOf(e));
                        e.printStackTrace();
                    }
                    Log.i("zhengsheng_login_x3", result.get("text"));
                } else {
                    Log.i("zhengsheng_login_x4", result.get("code"));
                    Toast.makeText(login.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


}
