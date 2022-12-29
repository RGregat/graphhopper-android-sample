package de.r.gregat.graphhoppercoretest.screens.common

import android.content.Context
import android.view.View

abstract class BaseViewMvc : ViewMvc {
    private lateinit var rootView: View

    override fun getRootView(): View {
        return rootView
    }

    fun setRootView(rootView: View) {
        this.rootView = rootView
    }

    fun getContext(): Context {
        return rootView.context
    }

    fun getString(id: Int): String {
        return getContext().getString(id)
    }

    open fun <T : View?> findViewById(id: Int): T {
        return getRootView().findViewById<T>(id)
    }
}