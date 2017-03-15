package com.Vinctor;

/**
 * Created by Vinctor on 2017/3/12.
 */

public class VLog {
    private static Boolean isDebug = false;

    public static void setDebug(Boolean isDebug) {
        VLog.isDebug = isDebug;
    }

    public static Boolean getDebug() {
        return isDebug;
    }
}
