package cn.vinctor.example.user;

import android.app.Application;

import com.vinctor.pay.RxWxPay;

/**
 * Created by Vinctor on 2017/3/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxWxPay.init(this);
    }
}
