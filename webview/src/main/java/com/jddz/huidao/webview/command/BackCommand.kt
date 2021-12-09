package com.jddz.huidao.webview.command

import com.google.auto.service.AutoService
import com.google.gson.Gson
import com.jddz.huidao.common.IMainToWebViewInterface
import com.jddz.huidao.common.autoservice.hdwebview.Command
import com.jddz.huidao.webview.TYPE_BACK

/**
 * 退出应用
 * @Author cc
 * @Date 2021/12/2-14:52
 * @version 1.0
 */
@AutoService(Command::class)
open class BackCommand : Command {

    override fun name() = TYPE_BACK

    override fun execute(parameters: Map<*, *>?, callback: IMainToWebViewInterface?) {
        callback?.onNativeResult(name(), Gson().toJson(parameters))
    }
}