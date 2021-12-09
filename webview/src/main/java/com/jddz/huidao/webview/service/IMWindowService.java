package com.jddz.huidao.webview.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.jddz.huidao.base.serviceloader.HDServiceLoader;
import com.jddz.huidao.common.autoservice.HDWebViewService;
import com.jddz.huidao.webview.ConstantKt;
import com.jddz.huidao.webview.R;

/**
*  悬浮窗，最小化服务
* @Author cc
* @Date 2021/12/8 11:13
* @version 1.0
*/
public class IMWindowService extends Service {

    private View view;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;

    public IMWindowService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.x = 0;
        layoutParams.y = 300;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        showUI(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showUI(Intent intent) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        view = layoutInflater.inflate(R.layout.pop_im_window_service, null);
        view.setOnTouchListener(new View.OnTouchListener() {

            //刚按下是起始位置的坐标
            float startDownX, startDownY;
            float downX, downY;
            float moveX, moveY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startDownX = downX = event.getRawX();
                        startDownY = downY = event.getRawY();
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        moveX = event.getRawX();
                        moveY = event.getRawY();
                        layoutParams.x += (int) (moveX - downX);
                        layoutParams.y += (int) (moveY - downY);

                        windowManager.updateViewLayout(view, layoutParams);
                        downX = moveX;
                        downY = moveY;
                        return true;
                    case MotionEvent.ACTION_UP:
                        float upX = event.getRawX();
                        float upY = event.getRawY();
                        if (Math.abs(upX - startDownX) < 2 && Math.abs(upY - startDownY) < 2) {

                            HDWebViewService load = HDServiceLoader.INSTANCE.load(HDWebViewService.class);
                            load.startWebViewActivity(getApplicationContext(),
                                    intent.getStringExtra(ConstantKt.WEBVIEW_URL),
                                    intent.getStringExtra(ConstantKt.WEBVIEW_ACTIVITY_TITLE),
                                    intent.getBooleanExtra(ConstantKt.WEBVIEW_ACTIVITY_IS_SHOW_ACTIONBAR,false),
                                    intent.getBooleanExtra(ConstantKt.WEBVIEW_ACTIVITY_IS_LANDSCAPE,false),
                                    intent.getBooleanExtra(ConstantKt.WEBVIEW_FRAGMENT_CAN_NATIVE_REFRESH,false),
                                    intent.getBooleanExtra(ConstantKt.WEBVIEW_ACTIVITY_IS_EXIT,false));
                            stopSelf();
                            return false;
                        }
                }
                return true;
            }
        });
        windowManager.addView(view, layoutParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 移除悬浮窗
        if (view != null) {
            windowManager.removeView(view);
        }
    }
}
