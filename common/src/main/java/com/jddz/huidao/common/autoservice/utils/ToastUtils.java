package com.jddz.huidao.common.autoservice.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import com.jddz.huidao.base.BaseApplication;

/**
 * Created by cc on 2018/6/4.
 * <p>
 * function :
 */

public class ToastUtils {

    public static void showToast(String msg) {
        runOnUiThread(() -> {
            Log.d("ToastUtils", msg);
            System.out.println(msg);
            Toast.makeText(BaseApplication.sApplication, msg, Toast.LENGTH_SHORT).show();
        });
    }

    public static void runOnUiThread(final Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            new Handler(Looper.getMainLooper()).post(runnable);
        }
    }
}
