var xiangxuejs = {};
xiangxuejs.os = {};
xiangxuejs.os.isIOS = /iOS|iPhone|iPad|iPod/i.test(navigator.userAgent);
xiangxuejs.os.isAndroid = !xiangxuejs.os.isIOS;
xiangxuejs.callbacks = {}

xiangxuejs.takeNativeAction = function(commandname, parameters){
    console.log("xiangxuejs takenativeaction")
    var request = {};
    // 为了保持ios和android的一致性，所以将请求封装成一个String
    request.name = commandname;
    request.param = parameters;
    if(window.xiangxuejs.os.isAndroid){
        console.log("android take native action" + JSON.stringify(request));
        window.hdwebview.takeNativeAction(JSON.stringify(request));
    } else {
        window.webkit.messageHandlers.hdwebview.postMessage(JSON.stringify(request))
    }
}

xiangxuejs.takeNativeActionWithCallback = function(commandname, parameters, callback) {
    var callbackname = "nativetojs_callback_" +  (new Date()).getTime() + "_" + Math.floor(Math.random() * 10000);
    xiangxuejs.callbacks[callbackname] = callback;

    var request = {};
    request.name = commandname;
    request.param = parameters;
    request.param.callbackname = callbackname;
    if(window.xiangxuejs.os.isAndroid){
        window.hdwebview.takeNativeAction(JSON.stringify(request));
    } else {
        window.webkit.messageHandlers.hdwebview.postMessage(JSON.stringify(request))
    }
}

xiangxuejs.callback = function (callbackname, response) {
   var callbackobject = xiangxuejs.callbacks[callbackname];
   if (callbackobject !== undefined){
       var ret = callbackobject(response);
       if(ret === false){
           return
       }
       delete xiangxuejs.callbacks[callbackname];
   }
}