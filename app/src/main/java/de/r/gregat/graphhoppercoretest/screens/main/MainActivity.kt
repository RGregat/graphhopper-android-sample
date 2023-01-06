package de.r.gregat.graphhoppercoretest.screens.main

import android.os.Bundle
import android.preference.PreferenceManager
import de.r.gregat.graphhoppercoretest.screens.common.BaseActivity

class MainActivity : BaseActivity() {

    private var viewMvc: MainActivityMvcView? = null
    private var controller: MainActivityController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewMvc = getControllerCompositionRoot()
            ?.getViewMvcFactory()
            ?.newInstance(
                MainActivityMvcView::class.java,
                null
            )

        controller = getControllerCompositionRoot()
            ?.getMainActivityController()
        controller!!.bindViewMvc(viewMvc!!)

        lifecycle.addObserver(controller!!)

        setContentView(viewMvc?.getRootView())
    }
}