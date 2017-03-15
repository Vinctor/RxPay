package com.vinctor.pay.utils;

import android.text.TextUtils;

import com.vinctor.pay.Ali.PayResult;
import com.vinctor.pay.Exception.PayFailedException;

import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Vinctor on 2017/3/14.
 */

public class RxPayUtils {

    public static Observable.Transformer<PayResult, PayResult> checkAliPayResult() {
        return new Observable.Transformer<PayResult, PayResult>() {
            @Override
            public Observable<PayResult> call(Observable<PayResult> payResultObservable) {
                return payResultObservable.map(new Func1<PayResult, PayResult>() {
                    @Override
                    public PayResult call(PayResult payResult) {
                        String resultStatus = payResult.getResultStatus();
                        // 判断resultStatus 为9000则代表支付成功
                        if (!TextUtils.equals(resultStatus, "9000")) {
                            throw new PayFailedException(resultStatus);
                        }
                        return payResult;
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return
                new Observable.Transformer<T, T>() {
                    @Override
                    public Observable<T> call(Observable<T> observable) {
                        return observable
                                .subscribeOn(rx.schedulers.Schedulers.io())
                                .unsubscribeOn(Schedulers.io());

                    }
                };
    }
}
