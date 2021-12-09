package com.jddz.huidao.webview.command

import com.google.auto.service.AutoService
import com.jddz.huidao.common.IMainToWebViewInterface
import com.jddz.huidao.common.autoservice.hdwebview.Command
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.jddz.huidao.base.BaseApplication
import com.jddz.huidao.webview.TYPE_BACK_WINDOW
import com.jddz.huidao.webview.WEBVIEW_ACTIVITY_TITLE
import com.jddz.huidao.webview.WEBVIEW_URL


/**
 * 最小化应用
 * @Author cc
 * @Date 2021/12/2-14:52
 * @version 1.0
 */
@AutoService(Command::class)
class BackWindowCommand : Command {

    override fun name() = TYPE_BACK_WINDOW

    override fun execute(parameters: Map<*, *>?, callback: IMainToWebViewInterface?) {
        callback?.onNativeResult(name(), null)
    }


}