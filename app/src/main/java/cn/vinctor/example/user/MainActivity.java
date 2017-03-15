package cn.vinctor.example.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.vinctor.annotation.WxPay;
import com.vinctor.pay.Ali.PayResult;
import com.vinctor.pay.RxAliPay;
import com.vinctor.pay.RxWxPay;
import com.vinctor.pay.Wx.WxPayResult;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

@WxPay(BuildConfig.APPLICATION_ID)
public class MainActivity extends AppCompatActivity {
    Activity thisActivity;

    View text;
    View text2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;
        text = findViewById(R.id.text);
        text2 = findViewById(R.id.text2);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    RxAliPay.getIntance()
        .with(MainActivity.this, "signString")
        .requestPay()
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<PayResult>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(thisActivity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(PayResult payResult) {
                Toast.makeText(thisActivity, payResult.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    });
        text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxWxPay.WXPayBean payBean=new RxWxPay.WXPayBean("", "", "", "", "", "");
    RxWxPay.getIntance()
            .withAppID("")
            .withPartnerID("")
            .withPrepayID("")
            .withNoncestr("")
            .withTimestamp("")
            .withSign("")
            //.withWxPayBean(new RxWxPay.WXPayBean("", "", "", "", "", ""))
            .requestPay()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Subscriber<WxPayResult>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(thisActivity, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNext(WxPayResult wxPayResult) {
                    Toast.makeText(thisActivity, wxPayResult.getErrCood() + "", Toast.LENGTH_SHORT).show();
                }
            });
            }
        });
    }
}
