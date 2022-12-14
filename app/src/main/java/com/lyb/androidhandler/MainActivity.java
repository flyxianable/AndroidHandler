package com.lyb.androidhandler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements MyCallback {

    private final String TAG = "lybHandler";

    private TextView tvTips;
    private TreateTest treateTest = new TreateTest();

    private Handler handler = null;

    /**
     * 0:handleMessage
     * 1:post
     * 2：在子线程创建运行于主线程的handler
     * 3：使用子线程的looper创建handler，子线程跟子线程通讯；注：在子线程的handler中无法直接更新UI
     */
    private int type = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTips = findViewById(R.id.tvTips);

        treateHandler();
    }


    private void treateHandler() {
        //loading
        tvTips.setText("type = " +  type + " sleep...");

        treateTest.setMyCallback(this);

        //handler消息方式
        if (type == 0) {
            handler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    int result = (int) msg.obj;
                    updateTips(result);
                }
            };
            new Thread() {
                @Override
                public void run() {
                    //耗时操作、联网请求等
                    treateTest.treateSomthing();
                }
            }.start();
            //pos方式
        } else if (type == 1) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    //耗时操作、联网请求等
                    treateTest.treateSomthing();

                }
            });
            //在子线程创建运行于主线程的handler
        } else if (type == 2) {
            new Thread() {
                @Override
                public void run() {
                    handler = new MyHandler(Looper.getMainLooper());
                    treateTest.treateSomthing();
                }
            }.start();
        //使用子线程的looper创建handler
        //子线程跟子线程通讯,无法更新UI
        } else if (type == 3) {
            new Thread() {
                int a = 0;
                @Override
                public void run() {
                    Looper.prepare();//Looper初始化
                    //Handler初始化 需要注意, Handler初始化传入Looper对象是子线程中缓存的Looper对象
                    handler = new ThreadHandler(Looper.myLooper());
                    Looper.loop();//死循环
                    //注意: Looper.loop()之后的位置代码在Looper退出之前不会执行,(并非永远不执行)
                    a++;

                }
            }.start();

            new Thread(){
                @Override
                public void run() {
                    treateTest.treateSomthing();
                }
            }.start();

        }
    }


    @Override
    public void getData(int result) {
        Log.v("ThreadHandler", "getData");
        //通知更新UI
        if (type == 1) {
            updateTips(result);
        } else {
            Message msg = new Message();
            msg.obj = result;
            msg.what = 0;
            handler.sendMessage(msg);
        }

    }

    class MyHandler extends Handler {

        public MyHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int result = (int) msg.obj;
            updateTips(result);

        }
    }

    class ThreadHandler extends Handler {

        public ThreadHandler(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int result = (int) msg.obj;

            //创建主线程的handler，更新UI
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    updateTips(result);
                }
            });

            Log.v("ThreadHandler", "handler");
            handler.getLooper().quit();

        }
    }

    private void updateTips(int result){
        tvTips.setText("type = " +  type + " 醒了...你睡了 " + (result / 1000) + " 秒");
    }


}



/**
 * 接口：模拟接口请求或耗时操作，回调返回的情形数据
 */
interface MyCallback {
    void getData(int result);
}

