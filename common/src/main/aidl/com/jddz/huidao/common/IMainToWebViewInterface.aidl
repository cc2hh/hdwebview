// IMainToWebViewInterface.aidl
package com.jddz.huidao.common;

// Declare any non-default types here with import statements

interface IMainToWebViewInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */

    void onResult(String callBackName,String response);
    void onNativeResult(String type,String response);
}