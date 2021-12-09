package com.jddz.huidao.base

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import com.jddz.huidao.base.loadsir.*
import com.kingja.loadsir.core.LoadSir

/**
 * 基础Application
 * @Author cc
 * @Date 2021/11/9-15:59
 * @version 1.0
 */
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        sApplication = this

        LoadSir.beginBuilder()
            .addCallback(ErrorCallback())
            .addCallback(EmptyCallback())
            .addCallback(LoadingCallback())
            .addCallback(TimeoutCallback())
            .addCallback(CustomCallback())
            .setDefaultCallback(LoadingCallback::class.java)
            .commit()
    }

    companion object {
        lateinit var sApplication: Application
    }
}