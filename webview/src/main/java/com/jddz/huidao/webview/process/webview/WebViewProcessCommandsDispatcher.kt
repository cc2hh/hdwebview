package com.jddz.huidao.webview.process.webview

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.jddz.huidao.base.BaseApplication
import com.jddz.huidao.common.IMainToWebViewInterface
import com.jddz.huidao.common.IWebViewToMainInterface
import com.jddz.huidao.common.autoservice.eventbus.MsgEvent
import com.jddz.huidao.webview.process.main.MainProcessCommandService
import org.greenrobot.eventbus.EventBus

/**
 *
 * @Author cc
 * @Date 2021/12/7 17:57
 * @version 1.0
 */
object WebViewProcessCommandsDispatcher : ServiceConnection {
    private var iWebviewToMainInterface: IWebViewToMainInterface? = null

    fun initAidlConnection() {
        val intent = Intent(BaseApplication.sApplication, MainProcessCommandService::class.java)
        BaseApplication.sApplication.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onServiceConnected(componentName: ComponentName?, iBinder: IBinder?) {
        iWebviewToMainInterface =
            IWebViewToMainInterface.Stub.asInterface(iBinder)
    }

    override fun onServiceDisconnected(componentName: ComponentName?) {
        iWebviewToMainInterface = null
        initAidlConnection()
    }

    /**
     * 分发网页端命令
     *
     */
    fun executeCommand(commandName: String, parameters: String, hdWebView: HDWebView) {
        iWebviewToMainInterface?.handleWebCommand(
            commandName,
            parameters,
            object : IMainToWebViewInterface.Stub() {
                override fun onResult(callbackname: String?, response: String?) {
                    hdWebView.handleCallback(callbackname, response)
                }

                override fun onNativeResult(type: String, response: String?) {
                    EventBus.getDefault().post(MsgEvent(type, response))
                }
            })
    }
}