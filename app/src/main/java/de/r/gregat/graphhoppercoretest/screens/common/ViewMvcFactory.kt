package de.r.gregat.graphhoppercoretest.screens.common

import android.view.LayoutInflater
import android.view.ViewGroup
import de.r.gregat.graphhoppercoretest.screens.main.MainActivityMvcView
import de.r.gregat.graphhoppercoretest.screens.main.MainActivityMvcViewImpl

class ViewMvcFactory(private val layoutInflater: LayoutInflater) {

    fun <T : ViewMvc?> newInstance(mvcViewClass: Class<T>, container: ViewGroup): T? {
        var viewMvc: ViewMvc? = null

        if (mvcViewClass == MainActivityMvcView::class.java) {
            viewMvc = MainActivityMvcViewImpl(layoutInflater, container)
        }

        return viewMvc as T?
    }
}