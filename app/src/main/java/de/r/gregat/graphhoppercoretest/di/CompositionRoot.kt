package de.r.gregat.graphhoppercoretest.di

import de.r.gregat.graphhoppercoretest.utils.BackgroundThreadHelper
import de.r.gregat.graphhoppercoretest.utils.UiThreadHelper

class CompositionRoot {
    private var backgroundThreadHelper: BackgroundThreadHelper? = null
    private var uiThreadHelper: UiThreadHelper? = null

    fun getBackgroundThreadHelper(): BackgroundThreadHelper {
        if(backgroundThreadHelper == null) {
            backgroundThreadHelper = BackgroundThreadHelper()
        }
        return backgroundThreadHelper!!
    }

    fun getUiThreadHelper(): UiThreadHelper {
        if(uiThreadHelper == null) {
            uiThreadHelper = UiThreadHelper()
        }
        return uiThreadHelper!!
    }
}