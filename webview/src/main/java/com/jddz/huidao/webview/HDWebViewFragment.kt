package com.jddz.huidao.webview

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import com.jddz.huidao.base.loadsir.ErrorCallback
import com.jddz.huidao.base.loadsir.LoadingCallback
import com.jddz.huidao.webview.databinding.FragmentHdWebViewBinding
import com.jddz.huidao.webview.process.webview.WebViewCallBack
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener

/**
 *  webviewFragment
 * @Author cc
 * @Date 2021/12/8 11:09
 * @version 1.0
 */
class HDWebViewFragment : Fragment(), OnRefreshListener, WebViewCallBack {

    private val TAG = "HDWebViewFragment"

    private var mUrl: String? = null
    private var mCanNativeRefresh = true
    private var mIsError = false
    private var mLoadService: LoadService<*>? = null

    private var mBind: FragmentHdWebViewBinding? = null
    val mBinding get() = mBind!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUrl = it.getString(WEBVIEW_URL)
            mCanNativeRefresh = it.getBoolean(WEBVIEW_FRAGMENT_CAN_NATIVE_REFRESH)
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBind = FragmentHdWebViewBinding.inflate(inflater, container, false)

        mBinding.webview.registerWebViewCallBack(this)
        mBinding.webview.loadUrl(mUrl!!)
        mLoadService = LoadSir.getDefault().register(mBinding.srlFragmentWebView) {
            mLoadService?.showCallback(LoadingCallback::class.java)
            mBinding.webview.reload()
        }
        mBinding.srlFragmentWebView.setOnRefreshListener(this)
        mBinding.srlFragmentWebView.isEnableRefresh = mCanNativeRefresh
        mBinding.srlFragmentWebView.isEnableLoadMore = false
        return mBinding.root
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mBinding.webview.reload()
    }

    override fun pageStarted(url: String?) {
        mLoadService?.showCallback(LoadingCallback::class.java)
    }

    override fun pageFinished(url: String?) {
        if (mIsError) {
            mBinding.srlFragmentWebView.isEnableRefresh = true
        } else {
            mBinding.srlFragmentWebView.isEnableRefresh = mCanNativeRefresh
        }
        Log.d(TAG, "pageFinished")
        mBinding.srlFragmentWebView.finishRefresh()
        if (mLoadService != null) {
            if (mIsError) {
                mLoadService?.showCallback(ErrorCallback::class.java)
            } else {
                mLoadService?.showSuccess()
            }
        }
        mIsError = false
    }

    override fun onError() {
        Log.e(TAG, "onError")
        mIsError = true
        mBinding.srlFragmentWebView.finishRefresh()
    }

    override fun updateTitle(title: String?) {
        activity?.let {
            if (it is HDWebViewActivity) {
                it.updateTitle(title)
            }
        }
    }

    override fun showFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        activity?.let {
            if (it is HDWebViewActivity) {
                it.showFileChooser(filePathCallback,fileChooserParams)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mBind = null
    }

    companion object {

        fun newInstance(url: String, canNativeRefresh: Boolean) =
            HDWebViewFragment().apply {
                arguments = Bundle().apply {
                    putString(WEBVIEW_URL, url)
                    putBoolean(WEBVIEW_FRAGMENT_CAN_NATIVE_REFRESH, canNativeRefresh)
                }
            }
    }
}