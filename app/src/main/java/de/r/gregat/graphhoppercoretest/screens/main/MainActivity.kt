package de.r.gregat.graphhoppercoretest.screens.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.graphhopper.GraphHopper
import de.r.gregat.graphhoppercoretest.R
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
                null)

        controller = getControllerCompositionRoot()
            ?.getMainActivityController()

        setContentView(viewMvc?.getRootView())

    }
}