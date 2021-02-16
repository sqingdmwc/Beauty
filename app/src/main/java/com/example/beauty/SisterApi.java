package com.example.beauty;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SisterApi {
    private static final String TAG = "Network";
    //private static final String BASE_URL = "http://gank.io/api/data/福利/";
    private static final String BASE_URL = "https://gank.io/api/v2/data/category/Girl/type/Girl/";

    /**
     * 查询妹子信息
     */
    public ArrayList<Sister> fetchSister(int count, int page) {
        String fetchUrl = BASE_URL + "page/" + page + "/count/" + count;
        //System.out.println(fetchUrl);
        ArrayList<Sister> sisters = new ArrayList<>();
        try {
            URL url = new URL(fetchUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            int code = conn.getResponseCode();
            Log.v(TAG, "Server response：" + code);
            if (code == 200) {
                InputStream in = conn.getInputStream();
                byte[] data = readFromStream(in);
                String result = new String(data, "UTF-8");
                sisters = parseSister(result);
                //System.out.println(result);
            } else {
                Log.e(TAG,"请求失败：" + code);
            }
        } catch (Exception e) {
            //System.out.println("hhhhhhhhhhhhhhhhhhhhhh");
            e.printStackTrace();
        }
        return sisters;
    }


    /**
     * 解析返回Json数据的方法
     */
    public ArrayList<Sister> parseSister(String content) throws Exception {
        ArrayList<Sister> sisters = new ArrayList<>();
        JSONObject object = new JSONObject(content);
        JSONArray array = object.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            JSONObject results = (JSONObject) array.get(i);
            Sister sister = new Sister();
            sister.set_id(results.getString("_id"));
            sister.setCreateAt(results.getString("createdAt"));
            sister.setDesc(results.getString("desc"));
            sister.setPublishedAt(results.getString("publishedAt"));

            sister.setType(results.getString("type"));
            sister.setUrl(results.getString("url"));

            sisters.add(sister);
        }
        return sisters;
    }

    /**
     * 读取流中数据的方法
     */
    public byte[] readFromStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len ;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return outputStream.toByteArray();
    }

}
