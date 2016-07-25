package com.newly_dawn.app.zhengsheng.tools;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 2016/7/25.
 */
public class HttpRequest {
    public String url;
    public String type;
    public Map<String, String> parameters= new HashMap<>();
    public String responseCode;
    public String responseText;
    public HttpRequest(String url){
        this.url = url;
    }
    public void post_connect(Map<String, String> parameters) throws Exception{
        this.parameters = parameters;
        URL url = new URL(this.url);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();	//创建一个HTTP连接
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setRequestProperty("Accept", "application/json");
        urlConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        urlConn.setRequestMethod("POST");
        urlConn.setDoOutput(true);

        JSONObject object = new JSONObject();
        for(String key : parameters.keySet()){
            String val = parameters.get(key);
            object.put(key, val);
        }
        byte[] data = object.toString().getBytes("UTF-8");
        OutputStream outputStream = urlConn.getOutputStream();
        outputStream.write(data);
        outputStream.flush();
        outputStream.close();

        InputStreamReader in = new InputStreamReader(urlConn.getInputStream()); // 获得读取的内容
        BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
        String inputLine = null;
        //通过循环逐行读取输入流中的内容
        String responseText = "";
        while ((inputLine = buffer.readLine()) != null) {
            responseText += inputLine + "\n";
        }
        String result_code = String.valueOf(urlConn.getResponseCode());
        String result_info = String.valueOf(urlConn.getResponseMessage());
        urlConn.disconnect();	//断开连接
        this.responseCode = result_code;
        this.responseText = responseText;
    }
    public void get_connect() throws Exception{
        URL url = new URL(this.url);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();	//创建一个HTTP连接
        InputStreamReader in = new InputStreamReader(urlConn.getInputStream()); // 获得读取的内容
        BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
        String inputLine = null;
        //通过循环逐行读取输入流中的内容
        String responseText = "";
        while ((inputLine = buffer.readLine()) != null) {
            responseText += inputLine + "\n";
        }
        in.close();	//关闭字符输入流对象
        urlConn.disconnect();	//断开连接
        String result_code = String.valueOf(urlConn.getResponseCode());
        String result_info = String.valueOf(urlConn.getResponseMessage());
        this.responseCode = result_code;
        this.responseText = responseText;
    }
    public String getResponseCode(){
        return this.responseCode;
    }
    public String getResponseText(){
        return this.responseText;
    }
}
