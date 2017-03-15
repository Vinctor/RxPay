package com.vinctor.pay.utils;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by Vinctor on 2016/5/4.
 */
public class BusUtil {
    private static volatile BusUtil defaultInstance;
    private final Subject bus;

    public BusUtil() {
        bus = new SerializedSubject<>(PublishSubject.create());
    }

    public static BusUtil getDefault() {
        BusUtil rxBus = defaultInstance;
        if (defaultInstance == null) {
            synchronized (BusUtil.class) {
                rxBus = defaultInstance;
                if (defaultInstance == null) {
                    rxBus = new BusUtil();
                    defaultInstance = rxBus;
                }
            }
        }
        return rxBus;
    }

    public void post(Object o) {
        bus.onNext(o);
    }

    public <T> Observable<T> toObserverable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}
