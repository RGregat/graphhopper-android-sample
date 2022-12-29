package de.r.gregat.graphhoppercoretest.screens.main

import de.r.gregat.graphhoppercoretest.screens.common.ObservableViewMvc

interface MainActivityMvcView: ObservableViewMvc<MainActivityMvcView.EventListener> {

    fun startCopyProcess()

    fun copyProcessDone()

    fun startCreateGraphhopperInstanceProcess()

    fun createGraphhopperInstanceDone()

    fun startRoutingProcess()

    fun startRoutingProcessDone()

    fun setRoutingResult(distance: Double, time: Long)

    interface EventListener {
        fun selectPbf()

        fun createGraphhopperInstance()

        fun startRouting()
    }
}