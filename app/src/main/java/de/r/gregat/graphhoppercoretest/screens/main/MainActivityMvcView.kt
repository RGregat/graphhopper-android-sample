package de.r.gregat.graphhoppercoretest.screens.main

import de.r.gregat.graphhoppercoretest.screens.common.ObservableViewMvc

interface MainActivityMvcView: ObservableViewMvc<MainActivityMvcView.EventListener> {

    fun startCopyProcess()

    fun copyProcessDone()

    fun startCreateGraphhopperInstanceProcess()

    fun createGraphhopperInstanceDone()

    interface EventListener {
        fun selectPbf()

        fun createGraphhopperInstance()
    }
}