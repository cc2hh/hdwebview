// IWebViewToMainInterface.aidl
package com.jddz.huidao.common;

import com.jddz.huidao.common.IMainToWebViewInterface;
// Declare any non-default types here with import statements

interface IWebViewToMainInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */


    void handleWebCommand(String commandName,String jsonParam,IMainToWebViewInterface callback);
}