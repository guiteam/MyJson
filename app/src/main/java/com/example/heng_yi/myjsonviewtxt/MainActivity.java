package com.example.heng_yi.myjsonviewtxt;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    private ConnectivityManager cmgr ;
    private TextView mesg_one;
    private TextView mesg_two;
    private TextView mesg_three;
    private TextView mesg_four;
    private String data ;
    private UIHandler handler ;
    private StringBuffer sb_one ;
    private StringBuffer sb_two ;
    private StringBuffer sb_three ;
    private StringBuffer sb_four ;



    private ImageView img;
    private Timer mTimer;
    private MyTread mt1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mesg_one =(TextView)findViewById(R.id.json_hh);
        mesg_two=(TextView)findViewById(R.id.json_co2);
        mesg_three=(TextView)findViewById(R.id.json_e);
        mesg_four=(TextView)findViewById(R.id.json_tm);
        cmgr =(ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cmgr.getActiveNetworkInfo();
        handler = new UIHandler();
        img =(ImageView)findViewById(R.id.img_bad);
        mTimer = new Timer();
        setTimerTask();






        if (info != null && info.isConnected()){
            try {
                Enumeration<NetworkInterface> ifs = NetworkInterface.getNetworkInterfaces();
                while (ifs.hasMoreElements()){
                    NetworkInterface ip = ifs.nextElement();
                    Enumeration<InetAddress> ips = ip.getInetAddresses();
                    while (ips.hasMoreElements()){
                        InetAddress ia = ips.nextElement();
                        Log.d("brad", ia.getHostAddress());
                    }
                }


            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else{
            Log.d("brad", "NOT Connect");
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // cancel timer
        mTimer.cancel();
    }
    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mt1 = new MyTread();
                mt1.start();
                Log.d("brad","Run as....");
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, 10000/* 表示1000毫秒之後，每隔1000毫秒執行一次 */);
    }

    //    public void upload(View v){
//        mesg.setText("");
//
//
//    }


    private class MyTread extends Thread {

        @Override
        public void run() {
            try {
                URL url = new URL("https://api.thingspeak.com/channels/173441/feed/last.json?api_key=BTNMKI5MV1A8CJMM");
                HttpsURLConnection conn =  (HttpsURLConnection) url.openConnection();
                conn.connect();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(conn.getInputStream()));
                data = reader.readLine();
                reader.close();
                parseJSONA();
            }catch(Exception ee){
                Log.d("brad", "error");
            }
        }
    }
    private void parseJSONA(){
        sb_one = new StringBuffer();
        sb_two = new StringBuffer();
        sb_three = new StringBuffer();
        sb_four = new StringBuffer();

        try {
            String name = new JSONObject(data).getString("created_at");
            String field1 = new JSONObject(data).getString("field1");
            String field2 = new JSONObject(data).getString("field2");
            String field3 = new JSONObject(data).getString("field3");
            String field4 = new JSONObject(data).getString("field4");

            Log.d("brad", name + " -> " + field1+ " -> "+field2+ " -> "+field3+ " -> "+field4);
            sb_one.append(field1);
            sb_two.append(field2);
            sb_three.append(field3);
            sb_four.append(field4);
            handler.sendEmptyMessage(1);

        }catch(Exception ee){
            Log.d("brad", ee.toString());
        }


    }
    private  void changeState(){
        float expend = (float) 20.0;
        float d = Float.valueOf(String.valueOf(sb_one)).floatValue();
        float f = Float.valueOf(String.valueOf(sb_two)).floatValue();
        float g = Float.valueOf(String.valueOf(sb_three)).floatValue();
        float h = Float.valueOf(String.valueOf(sb_four)).floatValue();

        Log.d("brad","float:"+d);
        if(d>expend){
            Log.d("brad","Hot Hot");
            img.setVisibility(View.VISIBLE);
        }
        if(f>expend){
            Log.d("brad","Hot Hot");
            img.setVisibility(View.VISIBLE);
        }
        if(g>expend){
            Log.d("brad","Hot Hot");
            img.setVisibility(View.VISIBLE);
        }
        if(h>expend){
            Log.d("brad","Hot Hot");
            img.setVisibility(View.VISIBLE);
        }

    }

    private class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mesg_one.setText(sb_one);
            mesg_two.setText(sb_two);
            mesg_three.setText(sb_three);
            mesg_four.setText(sb_four);
            if(sb_one!=null){
                changeState();
            }





        }
    }
}
