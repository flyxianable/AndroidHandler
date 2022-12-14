package com.lyb.androidhandler;

import android.os.SystemClock;

/**
 * 模拟处理网络请求或耗时操作的类
 */
public class TreateTest {


    public void setMyCallback(MyCallback myCallback) {
        this.myCallback = myCallback;
    }

    MyCallback myCallback;

    public void treateSomthing(){
        SystemClock.sleep(3000);
        if(myCallback != null){
            myCallback.getData(3000);
        }
    }

}
