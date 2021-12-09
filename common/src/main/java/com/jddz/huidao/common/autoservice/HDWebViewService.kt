package com.jddz.huidao.common.autoservice

import android.content.Context
import androidx.fragment.app.Fragment
import java.util.*
import kotlin.reflect.KClass

interface HDWebViewService {

    fun startWebViewActivity(
        context: Context,
        url: String,
        title: String = "WebView页面",
        isShowActionBar: Boolean = true,
        isLandScape: Boolean = false,
        canNativeRefresh: Boolean = true,
        isExit: Boolean = false
    )

    fun getWebViewFragment(
        url: String,
        canNativeRefresh: Boolean = true
    ): Fragment

}