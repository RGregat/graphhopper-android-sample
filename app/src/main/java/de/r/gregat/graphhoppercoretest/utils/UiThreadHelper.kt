package de.r.gregat.graphhoppercoretest.utils

import android.os.Handler
import android.os.Looper




class UiThreadHelper {
    private val uiHandler: Handler = getMainHandler()

    fun post(runnable: Runnable?) {
        uiHandler.post(runnable!!)
    }

    private fun getMainHandler(): Handler {
        return Handler(Looper.getMainLooper())
    }
}