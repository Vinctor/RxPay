package com.vinctor.pay;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.vinctor.pay.Ali.PayResult;
import com.vinctor.pay.utils.RxPayUtils;

import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by Vinctor on 2017/3/14.
 */

public class RxAliPay {
    private static RxAliPay singleton;
    private Activity activity;
    private String paySign;

    public static RxAliPay getIntance() {
        if (singleton == null) {
            synchronized (RxAliPay.class) {
                if (singleton == null) {
                    singleton = new RxAliPay();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    public RxAliPay with(Activity activity, String paySign) {
        this.activity = activity;
        this.paySign = paySign;
        return this;
    }

    public RxAliPay with(Activity activity) {
        this.activity = activity;
        return this;
    }


    public Observable<PayResult> requestPay() {
        if (activity == null) {
            throw new IllegalArgumentException("activity cannot be null");
        }
        if (paySign == null || "".equals(paySign)) {
            throw new IllegalArgumentException("paySign cannot be null");
        }
        return Observable
                .create(new Observable.OnSubscribe<PayTask>() {
                    @Override
                    public void call(Subscriber<? super PayTask> subscriber) {
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        PayTask alipay = new PayTask(activity);
                        subscriber.onNext(alipay);
                        subscriber.onCompleted();
                    }
                })
                .map(new Func1<PayTask, PayResult>() {
                    @Override
                    public PayResult call(PayTask payTask) {
                        Map<String, String> result = payTask.payV2(paySign, true);
                        PayResult payResult = new PayResult(result);
                        return payResult;
                    }
                })
                .compose(RxPayUtils.<PayResult>applySchedulers());
    }

    public static void main(String[] args) {
        RxAliPay.getIntance()
                .with(null, "")
                .requestPay()
                .subscribe(new Subscriber<PayResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(PayResult payResult) {

                    }
                });
    }

}
