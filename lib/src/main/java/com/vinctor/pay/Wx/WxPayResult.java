package com.vinctor.pay.Wx;

/**
 * Created by Vinctor on 2017/3/14.
 */

public class WxPayResult {
    int errCood;

    public WxPayResult(int errCood) {
        this.errCood = errCood;
    }

    /**
     * 获取微信支付结果状态码
     *
     * @return 0成功  -1错误  -2取消
     */
    public int getErrCood() {
        return errCood;
    }

    public void setErrCood(int errCood) {
        this.errCood = errCood;
    }
}
