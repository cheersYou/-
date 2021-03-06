package com.example.administrator.diaokes.Fujin;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.diaokes.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Administrator on 2018/8/6.
 */

public class test extends AppCompatActivity {
    private Button btn;
    private EditText et;
    private TextView tv;
    //    private WifiManager w = null;
    private Socket socket;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        final Handler handler = new MyHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    socket = new Socket("192.168.1.103", 10086);
                    // 接收
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[102400];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String s = new String(buffer, 0, len);
                        //
                        Message message = Message.obtain();
                        message.what = 0;
                        message.obj = s;
                        handler.sendMessage(message);
                    }

                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String string = et.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            OutputStream outputStream = null;
                            outputStream = socket.getOutputStream();
                            outputStream.write(("IP:" + getHostIp() + " " + string).getBytes("utf-8"));
                            outputStream.flush();// 清空缓存

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
    private String getHostIp() {

        WifiManager mg = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mg == null){
            return "";
        }

        WifiInfo wifiInfo = mg.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        return ((ip & 0xff) + "." + (ip >> 8 & 0xff) + "."
                + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff));
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String s = (String) msg.obj;
                tv.setText(s);
            }
        }

    }
}
