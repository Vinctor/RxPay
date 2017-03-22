# RxPay
支付宝 微信 支付 Rxjava

## Usage
>compile 'com.vinctor:rxpay:0.0.1'
#### 支付宝

官方强烈建议在服务器端进行签名,故```RxAliPay```只支持服务器端签名,然后本地客户端进行发起支付

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
在```OnNext```返回参数的说明可以
参照[支付宝](https://doc.open.alipay.com/docs/doc.htm?spm=a219a.7629140.0.0.kLWcOb&treeId=204&articleId=105302&docType=1)
最新官方文档的说明

#### 微信

因为微信支付官方要求用户创建```wxapi/WXPayEntryActivity```类,故分为以下几个步骤

 * 1 在你自定义的```Application```初始化
     >RxWxPay.init(Application application);
 * 2 对你任意一个```activity```类进行如下注解
 
        @WxPay(BuildConfig.APPLICATION_ID)
        public class MainActivity extends AppCompatActivity{
            //other codes
        }
        
     注:其中```BuildConfig.APPLICATION_ID```是系统变量,为获取你的应用的```applicationID```
     
 * 3  声明(**重要! ! ! ! !**)
    >```RxWxPay```不需要你自己编写``````wxapi/WXPayEntryActivity``````类以及声明微信要求的广播类```AppRegister```
          
    你只需要在当前app下下的```AndroidManifest```下声明```WXPayEntryActivity```,如下:
 
        <activity android:name="{你的applicationID}.wxapi.WXPayEntryActivity"
        android:exported="true">
         <intent-filter>
             <action android:name="android.intent.action.VIEW" />
             <category android:name="android.intent.category.DEFAULT" />
             <data android:scheme="{你的AppID}" />
         </intent-filter>
         </activity>
      注:
      你只需要自行填写的内容:
      
   >在```build.gradle```中声明的的```ApplicationID```(注意:不是```AndroidManifest```中声明的```package```)
      
   >在[微信开放平台](https://open.weixin.qq.com)中相应```app```的```AppID```
      
 * 4 完成以上3步,你就可以使用```RxWxPay```进行微信支付了:
 
        RxWxPay.getIntance()
                .withAppID("")//微信开放平台的AppID
                .withPartnerID("")//商户号partnerid
                .withPrepayID("")//预支付交易会话ID
                .withNoncestr("")//随机字符串
                .withTimestamp("")//时间戳
                .withSign("")//签名
                .requestPay()//发起支付请求
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
   你也可以自行构建```WXPayBean```,再进行支付:
       
        RxWxPay.WXPayBean payBean
            =new RxWxPay.WWXPayBean(appid, partnerid, noncestr, timestamp, prepayid, sign);
        RxWxPay.getIntance()
                        .withWxPayBean(payBean)
                        .requestPay()//发起支付请求
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
  在```OnNext```中返回的```WxPayResult```通过调用方法```WxPayResult.getErrCood()```查看支付结果
 
 如下:0成功  -1错误  -2取消
 
 #### END
