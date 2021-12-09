package com.jddz.huidao.webview.process.main

import android.util.Log
import com.google.gson.Gson
import com.jddz.huidao.common.IMainToWebViewInterface
import com.jddz.huidao.common.IWebViewToMainInterface
import com.jddz.huidao.common.autoservice.hdwebview.Command
import java.util.*

object MainProcessCommandsManager : IWebViewToMainInterface.Stub() {
    private val mCommands: HashMap<String, Command> = HashMap<String, Command>()

    init {
        val serviceLoader = ServiceLoader.load(
            Command::class.java
        )
        for (command in serviceLoader) {
            if (!mCommands.containsKey(command.name())) {
                mCommands[command.name()] = command
            }
        }
    }

    const val TAG = "CommandsManager"

    override fun handleWebCommand(
        commandName: String?,
        jsonParams: String?,
        callback: IMainToWebViewInterface?
    ) {
        Log.i(TAG, "Main process commands manager handle web command")
        mCommands[commandName]?.execute(Gson().fromJson(jsonParams, Map::class.java), callback)
    }
}