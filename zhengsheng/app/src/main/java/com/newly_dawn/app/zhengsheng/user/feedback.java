package com.newly_dawn.app.zhengsheng.user;

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
import android.widget.Button;
import android.widget.EditText;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class feedback extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                feedback.this.setResult(1, intent);
                //关闭Activity
                feedback.this.finish();
            }
        });
        loadEvent();
    }
    public void loadEvent(){
        Button submit = (Button)findViewById(R.id.submit_feedback);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText add_content = (EditText)findViewById(R.id.add_content);
                String content = add_content.getText().toString();
                content = content.trim();
                if(content.length() == 0){
                    Toast.makeText(feedback.this,"请填写反馈信息",  Toast.LENGTH_SHORT).show();
                    return;
                }
                final String finalContent = content;
                new SweetAlertDialog(feedback.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("确认提交吗")
                        .setCancelText("取消")
                        .setConfirmText("提交")
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
                                try {
                                    SharedPreferences sharedPreferences = getSharedPreferences("zhengsheng", Context.MODE_PRIVATE);
                                    String user_id = sharedPreferences.getString("user_id", null);
                                    if(user_id == null){
                                        user_id = "0";
                                    }
                                    String IP = getString(R.string.IP);
                                    String targetUrl = IP+"/api/v1/feedback_add/";
                                    Map<String, String> dataMp = new HashMap<>();
                                    dataMp.put("url", targetUrl);
                                    dataMp.put("user_id", user_id);
                                    dataMp.put("content", finalContent);
                                    new UploadFeedbackAsync().execute(dataMp);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        })
                        .show();
            }
        });
    }

    public class UploadFeedbackAsync extends AsyncTask<Map<String,String>, Void, Map<String, String>> {
        Map<String, String> result = new HashMap<>();
        @Override
        protected Map<String, String> doInBackground(Map<String, String>... params) {
            String url = params[0].get("url");
            HttpRequest httpRequest = new HttpRequest(url);
            try {
                Map<String, String> dataMp = new HashMap<>();
                dataMp.put("user_id",params[0].get("user_id"));
                dataMp.put("content", params[0].get("content"));
                httpRequest.post_connect(dataMp);
                String responseCode = httpRequest.getResponseCode();
                String responseText = httpRequest.getResponseText();
                result.put("code", responseCode);
                result.put("text", responseText);
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
            return result;
        }
        protected void onPostExecute(Map<String, String> result) {
            if (result == null) {
                new SweetAlertDialog(feedback.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("提交失败")
                        .setContentText("请检查网络连接")
                        .show();
            } else {
                if (result.get("code").equals("200")) {
                    try {
                        new SweetAlertDialog(feedback.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("提交成功!")
                                .setContentText("我们会重视您的意见，感谢您的提交!")
                                .show();
                        Intent intent = new Intent();
                        feedback.this.setResult(1, intent);
                        feedback.this.finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    new SweetAlertDialog(feedback.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("提交失败")
                            .show();
                }
            }
        }
    }

}
