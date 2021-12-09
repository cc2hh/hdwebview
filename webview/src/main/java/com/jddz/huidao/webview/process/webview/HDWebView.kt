package com.jddz.huidao.webview.process.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.jddz.huidao.webview.process.webview.settings.WebViewDefaultSettings
import android.text.TextUtils
import android.util.Log

import com.google.gson.Gson

import com.jddz.huidao.webview.bean.JsParam

import android.webkit.JavascriptInterface
import com.jddz.huidao.webview.process.webview.WebViewProcessCommandsDispatcher.executeCommand
import com.jddz.huidao.webview.process.webview.webchromeclient.HDWebChromeClient
import com.jddz.huidao.webview.process.webview.webviewclient.HDWebViewClient


/**

 * @Author cc
 * @Date 2021/11/9-17:55
 * @version 1.0
 */
class HDWebView : WebView {

    val TAG = "HDWebView"

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    @SuppressLint("JavascriptInterface")
    private fun init() {
        WebViewProcessCommandsDispatcher.initAidlConnection()
        addJavascriptInterface(this, "hdwebview")
        WebViewDefaultSettings.getInstance().setSettings(this)
    }

    fun registerWebViewCallBack(webViewCallBack: WebViewCallBack) {
        webViewClient = HDWebViewClient(webViewCallBack)
        webChromeClient = HDWebChromeClient(webViewCallBack)
    }

    @JavascriptInterface
    fun takeNativeAction(jsParam: String) {
        Log.e(TAG, "this is a call from html javascript.$jsParam")
        if (jsParam.isNotEmpty()) {
            val jsParamObject = Gson().fromJson(jsParam, JsParam::class.java)
            if (jsParamObject != null) {
                executeCommand(jsParamObject.name, Gson().toJson(jsParamObject.param), this)
            }
        }
    }

    /**
    * 处理发送给网页端的回调
    *
    */
    fun handleCallback(callbackname: String?, response: String?) {
        if (!TextUtils.isEmpty(callbackname) && !TextUtils.isEmpty(response)) {
            post {
                val jscode =
                    "javascript:hdphone.callback('$callbackname',$response)"
                evaluateJavascript(jscode, null)
            }
        }
    }

}