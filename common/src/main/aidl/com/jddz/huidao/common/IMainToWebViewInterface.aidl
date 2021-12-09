// IMainToWebViewInterface.aidl
package com.jddz.huidao.common;

// Declare any non-default types here with import statements

interface IMainToWebViewInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    // 给网页端的回调
    void onResult(String callBackName,String response);
    // 给本地webview端的回调
    void onNativeResult(String type,String response);
}