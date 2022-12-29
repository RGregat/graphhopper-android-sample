package de.r.gregat.graphhoppercoretest.screens.common

import androidx.appcompat.app.AppCompatActivity
import de.r.gregat.graphhoppercoretest.CustomApplication
import de.r.gregat.graphhoppercoretest.di.ActivityCompositionRoot
import de.r.gregat.graphhoppercoretest.di.ControllerCompositionRoot


open class BaseActivity: AppCompatActivity() {
    private var activityCompositionRoot: ActivityCompositionRoot? = null
    private var controllerCompositionRoot: ControllerCompositionRoot? = null

    fun getActivityCompositionRoot(): ActivityCompositionRoot? {
        if (activityCompositionRoot == null) {
            activityCompositionRoot = ActivityCompositionRoot(
                (application as CustomApplication).getCompositionRoot()!!,
                this
            )
        }
        return activityCompositionRoot
    }

    protected fun getCompositionRoot(): ControllerCompositionRoot? {
        if (controllerCompositionRoot == null) {
            controllerCompositionRoot = ControllerCompositionRoot(getActivityCompositionRoot()!!)
        }
        return controllerCompositionRoot
    }
}