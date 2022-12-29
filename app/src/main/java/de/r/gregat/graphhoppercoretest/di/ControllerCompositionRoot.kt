package de.r.gregat.graphhoppercoretest.di

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import de.r.gregat.graphhoppercoretest.screens.common.ViewMvcFactory





class ControllerCompositionRoot(private val activityCompositionRoot: ActivityCompositionRoot) {

    fun getFragmentActivity(): FragmentActivity {
        return activityCompositionRoot.fragmentActivity
    }

    private fun getContext(): Context {
        return getFragmentActivity()
    }

    private fun getLayoutInflater(): LayoutInflater {
        return LayoutInflater.from(getContext())
    }

    fun getViewMvcFactory(): ViewMvcFactory {
        return ViewMvcFactory(getLayoutInflater())
    }
}