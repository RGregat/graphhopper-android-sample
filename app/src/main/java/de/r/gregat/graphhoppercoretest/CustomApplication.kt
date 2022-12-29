package de.r.gregat.graphhoppercoretest

import android.app.Application
import de.r.gregat.graphhoppercoretest.di.CompositionRoot


class CustomApplication : Application() {
    private var compositionRoot: CompositionRoot? = null

    override fun onCreate() {
        super.onCreate()
        compositionRoot = CompositionRoot()
    }

    fun getCompositionRoot(): CompositionRoot? {
        return compositionRoot
    }
}