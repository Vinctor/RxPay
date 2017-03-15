package com.vinctor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Vinctor on 2017/3/14.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface WxPay {
    String value();
}
