package com.jddz.huidao.webview.bean

import com.google.gson.JsonObject

/**
 * 网页相关命令和参数
 *
 * @version 1.0
 * @Author cc
 * @Date 2021/12/8 11:14
 */
data class JsParam(var name: String, var param: JsonObject? = null)