package com.vinctor.pay;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.support.annotation.Nullable;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.vinctor.pay.Wx.WxPayResult;
import com.vinctor.pay.utils.BusUtil;
import com.vinctor.pay.utils.RxPayUtils;

import java.lang.reflect.Constructor;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Vinctor on 2017/3/14.
 */

public class RxWxPay {
    private static RxWxPay singleton;

    public static RxWxPay getIntance() {
        if (singleton == null) {
            synchronized (RxAliPay.class) {
                if (singleton == null) {
                    singleton = new RxWxPay();
                    return singleton;
                }
            }
        }
        return singleton;
    }

    private static Application context;
    private static String packageName = "";

    public static void init(Application application) {
        context = application;
        packageName = context.getPackageName();

        //receive
        try {
            Class c = Class.forName(packageName + ".AppRegister");
            Constructor c0 = c.getDeclaredConstructor();
            BroadcastReceiver receiver = (BroadcastReceiver) c0.newInstance();
            context.registerReceiver(receiver,
                    new IntentFilter("com.tencent.mm.plugin.openapi.Intent.ACTION_REFRESH_WXAPP"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String partnerid;

    private String noncestr;

    private String timestamp;

    private String prepayid;

    private String sign;

    private String appID;

    private WXPayBean payBean;

    public RxWxPay withAppID(String Appid) {
        this.appID = Appid;
        return this;
    }

    public RxWxPay withPartnerID(String partnerid) {
        this.partnerid = partnerid;
        return this;
    }

    public RxWxPay withNoncestr(String noncestr) {
        this.noncestr = noncestr;
        return this;
    }

    public RxWxPay withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public RxWxPay withPrepayID(String prepayid) {
        this.prepayid = prepayid;
        return this;
    }

    public RxWxPay withSign(String sign) {
        this.sign = sign;
        return this;
    }

    public String getAppid() {
        return appID;
    }

    public RxWxPay withWxPayBean(WXPayBean payBean) {
        this.payBean = payBean;
        return this;
    }

    public Observable<WxPayResult> requestPay() {
        if (payBean == null) {
            payBean = new WXPayBean(appID, partnerid, noncestr, timestamp, prepayid, sign);
            appID = payBean.appid;
            partnerid = payBean.partnerid;
            noncestr = payBean.noncestr;
            timestamp = payBean.timestamp;
            prepayid = payBean.prepayid;
            sign = payBean.sign;
        }
        String checkResult = checkisEmpty();
        if (!isEmpty(checkResult)) {
            throw new IllegalArgumentException(checkResult + " cannot be null");
        }
        if (context == null) {
            throw new IllegalArgumentException("you have not init the WxPay in your Application!");
        }

        final IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        // 将该app注册到微信
        msgApi.registerApp(payBean.getAppid());
        return
                Observable
                        .create(new Observable.OnSubscribe<WxPayResult>() {
                            @Override
                            public void call(final Subscriber<? super WxPayResult> subscriber) {
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }
                                PayReq request = new PayReq();
                                request.appId = payBean.appid;
                                request.partnerId = payBean.partnerid;
                                request.prepayId = payBean.prepayid;
                                request.packageValue = "Sign=WXPay";
                                request.nonceStr = payBean.noncestr;
                                request.timeStamp = payBean.timestamp;
                                request.sign = payBean.sign;
                                boolean isSend = msgApi.sendReq(request);
                                if (!isSend) {
                                    subscriber.onNext(new WxPayResult(-1));
                                    subscriber.onCompleted();
                                } else {
                                    BusUtil.getDefault()
                                            .toObserverable(BaseResp.class)
                                            .subscribe(new Subscriber<BaseResp>() {
                                                @Override
                                                public void onCompleted() {

                                                }

                                                @Override
                                                public void onError(Throwable e) {

                                                }

                                                @Override
                                                public void onNext(BaseResp baseResp) {
                                                    subscriber.onNext(new WxPayResult(baseResp.errCode));
                                                    subscriber.onCompleted();
                                                }
                                            });

                                }
                            }
                        })
                        .compose(RxPayUtils.<WxPayResult>applySchedulers());
    }

    private String checkisEmpty() {
        return isEmpty(appID) ? "appid"
                : isEmpty(partnerid) ? "partnerid"
                : isEmpty(noncestr) ? "noncestr"
                : isEmpty(timestamp) ? "timestamp"
                : isEmpty(prepayid) ? "prepayid"
                : isEmpty(sign) ? "sign" : "";
    }


    public static class WXPayBean {

        private String appid;

        private String partnerid;


        private String noncestr;

        private String timestamp;

        private String prepayid;

        private String sign;

        public WXPayBean(String appid, String partnerid, String noncestr, String timestamp, String prepayid, String sign) {
            this.appid = appid;
            this.partnerid = partnerid;
            this.noncestr = noncestr;
            this.timestamp = timestamp;
            this.prepayid = prepayid;
            this.sign = sign;
        }

        public String getAppid() {
            return appid;
        }

        public String getPartnerid() {
            return partnerid;
        }


        public String getNoncestr() {
            return noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public String getSign() {
            return sign;
        }
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }
}
