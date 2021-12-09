package com.jddz.huidao.common.autoservice.hdwebview

import com.jddz.huidao.common.IMainToWebViewInterface

interface Command {
    fun name(): String
    fun execute(
        parameters: Map<*, *>?,
        callback: IMainToWebViewInterface?
    )
}