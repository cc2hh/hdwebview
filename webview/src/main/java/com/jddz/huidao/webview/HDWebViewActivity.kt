package com.jddz.huidao.webview

import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.jddz.huidao.common.autoservice.eventbus.MsgEvent
import com.jddz.huidao.common.autoservice.utils.ToastUtils
import com.jddz.huidao.webview.bean.BackParam
import com.jddz.huidao.webview.databinding.ActivityHdWebViewBinding
import com.jddz.huidao.webview.service.IMWindowService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.*
import me.nereo.multi_image_selector.MultiImageSelector
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.AutoDispose
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import java.io.File


open class HDWebViewActivity : AppCompatActivity() {
    private val TAG = "HDWebViewActivity"
    private lateinit var bind: ActivityHdWebViewBinding
    private var mUrl: String? = null
    private lateinit var mTitle: String

    // 是否能本地刷新webview
    private var mCanNativeRefresh: Boolean = true

    // 是否显示Bar
    private var mIsShowActionBar: Boolean = true

    // 返回键是否直接退出webview
    private var mIsExit: Boolean = false

    // 双击退出webview标识
    private var m2Cilck: Boolean = false

    // 最小化窗体服务
    private var mIMWindservice: Intent? = null
    private var mFragment: Fragment? = null
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHdWebViewBinding.inflate(layoutInflater)
        setContentView(bind.root)
        // 注册EventBus
        EventBus.getDefault().register(this)

        intent.extras?.let {
            // url为空的话退出webview并弹出提示
            mUrl = it.getString(WEBVIEW_URL) ?: run {
                ToastUtils.showToast("url参数为空")
                finish()
                ""
            }
            mTitle = it.getString(WEBVIEW_ACTIVITY_TITLE) ?: ""
            mIsShowActionBar = it.getBoolean(WEBVIEW_ACTIVITY_IS_SHOW_ACTIONBAR)
            mIsExit = it.getBoolean(WEBVIEW_ACTIVITY_IS_EXIT)
            mCanNativeRefresh = it.getBoolean(WEBVIEW_FRAGMENT_CAN_NATIVE_REFRESH)
            val isLandScape = it.getBoolean(WEBVIEW_ACTIVITY_IS_LANDSCAPE)

            // 根据业务需求设置webview屏幕显示方向
            requestedOrientation = if (isLandScape) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        } ?: run {
            // 如果没有参数则直接退出webview并提示
            ToastUtils.showToast("webview参数为空")
            finish()
        }

        if (!mIsShowActionBar) {
            bind.flWebViewBar.visibility = View.GONE
            bind.tvWebViewBarTitle.text = mTitle
        }

        bind.ivWebViewBarBack.setOnClickListener { finish() }

        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        mFragment =
            HDWebViewFragment.newInstance(
                intent.getStringExtra(WEBVIEW_URL) ?: "",
                canNativeRefresh = mCanNativeRefresh
            )
        transaction.replace(R.id.fl_webView_bar_fragment, mFragment!!).commit()

        resetIMWindService()
    }

    override fun onBackPressed() {
        // 直接退出webview
        if (mIsExit) {
            exitBy2Click()
        } else {
            mFragment?.let {
                val webview = (it as HDWebViewFragment).mBinding.webview
                webview.post {
                    // 网页允许后退流程
                    if (webview.canGoBack()) {
                        webview.goBack()
                    } else {
                        exitBy2Click()
                    }
                }
            }
        }
    }

    /**
     * 双击退出函数
     */
    private fun exitBy2Click() {
        if (!m2Cilck) {
            m2Cilck = true // 准备退出
            ToastUtils.showToast("再按一次退出程序")
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    m2Cilck = false // 取消退出
                }
            }, 1500) // 如果1.5秒钟内没有按下返回键,则重置退出标志
        } else {
            resetIMWindService()
            finish()
        }
    }

    /**
     *   重置最小化服务
     */
    private fun resetIMWindService() {
        mIMWindservice?.let {
            stopService(it)
            mIMWindservice = null
        }
    }


    /**
     * 设置标题
     *
     */
    fun updateTitle(title: String?) {
        bind.tvWebViewBarTitle.text = title
    }


    /**
     * 网页端选择文件
     *
     */
    fun showFileChooser(
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ) {
        mFilePathCallback = filePathCallback

        RxPermissions(this).request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .`as`(
                AutoDispose.autoDisposable(
                    AndroidLifecycleScopeProvider.from(
                        this,
                        Lifecycle.Event.ON_DESTROY
                    )
                )
            )
            .subscribe({
                if (it) {
                    dealFile(fileChooserParams)
                } else {
                    mFilePathCallback?.onReceiveValue(null)
                    ToastUtils.showToast("需要授权才能使用")
                }
            }, {
                ToastUtils.showToast("权限：$it")
            })
    }

    private fun dealFile(fileChooserParams: WebChromeClient.FileChooserParams?) {
        // 检查是否是文件操作
        if (fileChooserParams!!.acceptTypes.contains(".txt")) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
//            // 设置类型
//            intent.putExtra(
//                Intent.EXTRA_MIME_TYPES,
//                arrayListOf(
//                    FILE_TYPE_TXT, FILE_TYPE_PDF, FILE_TYPE_DOC, FILE_TYPE_DOCX,
//                    FILE_TYPE_XLS, FILE_TYPE_XLSX, FILE_TYPE_PPT, FILE_TYPE_PPTX
//                )
//            )

            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, CODE_FILE)
        } else {
            MultiImageSelector.create()
                .showCamera(true)
                .single()
                .start(this, CODE_IMAGE)
        }
    }

    /**
     * 事件处理
     *
     */
    @Subscribe
    fun dealMsgEvent(msgEvent: MsgEvent) {
        when (msgEvent.type) {
            TYPE_BACK -> {
                // 获取网页端后退命令是否带有退出参数
                msgEvent.param?.let {
                    val param = Gson().fromJson(it, BackParam::class.java)
                    mIsExit = param.isExit
                }
                onBackPressed()
            }
            TYPE_BACK_WINDOW -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        startWindow()
                    } else {
                        ToastUtils.showToast("需要取得权限以使用悬浮窗")
                        //若没有权限，提示获取.
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data = Uri.parse("package:$packageName")
                        startActivityForResult(intent, CODE_WIND)
                    }
                } else {
                    startWindow()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            // 处理获取到悬浮窗权限
            when (requestCode) {
                CODE_WIND -> if (Build.VERSION.SDK_INT >= 23) {
                    if (Settings.canDrawOverlays(this)) {
                        ToastUtils.showToast("授权成功")
                        startWindow()
                    } else {
                        ToastUtils.showToast("授权失败")
                    }
                }
                CODE_IMAGE -> {
                    val path: String? =
                        data?.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT)?.get(0)
                    if (path == null) {
                        ToastUtils.showToast("没有获取到图片路径")
                        mFilePathCallback?.onReceiveValue(null)
                    } else {
                        val uri = FileProvider.getUriForFile(
                            this,
                            "$packageName.fileprovider",
                            File(path)
                        )
                        mFilePathCallback?.onReceiveValue(arrayOf(uri))
                    }
                }
                CODE_FILE -> {
                    data?.data?.let {
//                        val s = FileUtil.getPath(this, it)

//                        if ("txt,pdf,doc,docx,mp4,xlsx,ppt,pptx".contains(File(s).extension)) {
                        mFilePathCallback?.onReceiveValue(arrayOf(it))
//                        } else {
//                            ToastUtils.showToast("不支持该文件类型")
//                            mFilePathCallback?.onReceiveValue(null)
//                        }
                    } ?: run {
                        mFilePathCallback?.onReceiveValue(null)
                    }
                }
            }
        } else {
            if (mFilePathCallback == null) {
                ToastUtils.showToast("没有权限")
            } else {
                mFilePathCallback?.onReceiveValue(null)
                ToastUtils.showToast("没有选择文件")
            }
        }
    }

    /**
     * 最小化窗体
     *
     */
    private fun startWindow() {
        mIMWindservice = Intent(this, IMWindowService::class.java)
        // onCreate里面已经判断了intent.extras必定不为空
        mIMWindservice?.putExtras(intent.extras!!)
        startService(mIMWindservice)
        finish()
    }

    companion object {
        // 悬浮窗获取权限
        const val CODE_WIND = 0

        // 选择文件
        const val CODE_FILE = 1

        // 选择图片
        const val CODE_IMAGE = 2
    }
}