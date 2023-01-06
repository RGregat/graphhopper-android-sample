package de.r.gregat.graphhoppercoretest.screens.main

import com.graphhopper.util.PointList
import de.r.gregat.graphhoppercoretest.screens.common.ObservableViewMvc

interface MainActivityMvcView: ObservableViewMvc<MainActivityMvcView.EventListener> {

    fun startCopyProcess()

    fun copyProcessDone()

    fun startCreateGraphhopperInstanceProcess()

    fun createGraphhopperInstanceDone()

    fun startRoutingProcess()

    fun startRoutingProcessDone()

    fun setRoutingResult(distance: Double, time: Long)

    fun setInstructionList(instructionList: List<String>)

    fun setGeoPoints(pointList: PointList)

    fun onResume()

    fun onPause()

    interface EventListener {
        fun selectPbf()

        fun createGraphhopperInstance()

        fun startRouting()
    }
}