package com.byk.pandora.rexhttp.sample.sp;

import android.content.Context;

import com.baoyz.treasure.Treasure;

/**
 * Created by Byk on 2018/1/3.
 *
 * @author Byk
 */
public class SpMgr {

    public static <T> T get(Context context, Class<T> interfaceClass) {
        return Treasure.get(context, interfaceClass);
    }

    public static <T> T get(Context context, Class<T> interfaceClass, String id) {
        return Treasure.get(context, interfaceClass, id);
    }
}
