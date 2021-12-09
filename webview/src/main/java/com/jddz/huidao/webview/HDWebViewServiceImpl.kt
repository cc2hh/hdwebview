package com.jddz.huidao.webview

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.auto.service.AutoService
import com.jddz.huidao.common.autoservice.HDWebViewService
import com.jddz.huidao.webview.HDWebViewFragment.Companion.newInstance

@AutoService(HDWebViewService::class)
class HDWebViewServiceImpl : HDWebViewService {
    override fun startWebViewActivity(
        context: Context,
        url: String,
        title: String,
        isShowActionBar: Boolean,
        isLandScape: Boolean,
        canNativeRefresh: Boolean,
        isExit: Boolean
    ) {
        var intent = Intent(context, HDWebViewActivity::class.java)
        intent.putExtra(WEBVIEW_URL, url)
        intent.putExtra(WEBVIEW_ACTIVITY_TITLE, title)
        intent.putExtra(WEBVIEW_ACTIVITY_IS_SHOW_ACTIONBAR, isShowActionBar)
        intent.putExtra(WEBVIEW_ACTIVITY_IS_LANDSCAPE, isLandScape)
        intent.putExtra(WEBVIEW_FRAGMENT_CAN_NATIVE_REFRESH, canNativeRefresh)
        intent.putExtra(WEBVIEW_ACTIVITY_IS_EXIT, isExit)
        context.startActivity(intent)
    }

    override fun getWebViewFragment(
        url: String,
        canNativeRefresh: Boolean
    ): Fragment {
        return newInstance(url, canNativeRefresh)
    }

}