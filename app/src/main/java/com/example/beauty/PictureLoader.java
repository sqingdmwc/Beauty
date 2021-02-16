package com.example.beauty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class PictureLoader {
    private ImageView loadImg;
    private String imgUrl;
    private byte[] picByte;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x123) {
                if (picByte != null) {

                    //System.out.println("ggggggggggggggggg");
                    Bitmap bitmap = BitmapFactory.decodeByteArray(picByte, 0, picByte.length);
                    loadImg.setImageBitmap(bitmap);
                }
                else{
                    System.out.println("sssssssssssssss");
                }
            }
        }
    };

    public void load(ImageView loadImg, String imgUrl) {
        //Log.d("MainActivity", "here");
        this.loadImg = loadImg;
        this.imgUrl = imgUrl;
        Drawable drawable = loadImg.getDrawable();
        if(drawable != null && drawable instanceof BitmapDrawable) {

            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if(bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                //System.out.println("11111111111111111:");
                URL url = new URL(imgUrl);

/**
                URLConnection con = url.openConnection();
                System.out.println( "orignal url: " + con.getURL() );
                con.connect();
                System.out.println( "connected url: " + con.getURL() );
                InputStream is = con.getInputStream();
                System.out.println( "redirected url: " + con.getURL() );
                is.close();



                HttpURLConnection con = (HttpURLConnection)(url.openConnection());
                con.setInstanceFollowRedirects( false );
                con.connect();
                int responseCode = con.getResponseCode();
                System.out.println( responseCode );
                String location = con.getHeaderField( "Location" );
                System.out.println( location );
                 **/


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                //System.out.println(conn.getResponseCode());
                if (conn.getResponseCode() == 301 || conn.getResponseCode() == 200) {

                    String urls = conn.getHeaderField("Location");
                    conn = (HttpURLConnection) new URL(urls).openConnection();

                    InputStream in = conn.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] bytes = new byte[1024];
                    int length = -1;
                    while ((length = in.read(bytes)) != -1) {
                        out.write(bytes, 0, length);
                    }

                    picByte = out.toByteArray();
                    //System.out.println(picByte);
                    in.close();
                    out.close();
                    handler.sendEmptyMessage(0x123);
                }
                else{
                    System.out.println("222222222222222222:");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}