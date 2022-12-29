package de.r.gregat.graphhoppercoretest.screens.main

import android.view.LayoutInflater
import android.view.ViewGroup
import de.r.gregat.graphhoppercoretest.databinding.ActivityMainBinding
import de.r.gregat.graphhoppercoretest.screens.common.BaseObservableViewMvc


class MainActivityMvcViewImpl(
    private val layoutInflater: LayoutInflater,
    private val container: ViewGroup?
) :
    BaseObservableViewMvc<MainActivityMvcView.EventListener>(), MainActivityMvcView {

    private val binding: ActivityMainBinding = ActivityMainBinding
        .inflate(
            layoutInflater,
            container,
            false
        )


    init {
        setRootView(binding.root)
    }

    init {
        binding.tvStartLocation.text = "52.506532501639114, 13.416267775403348"
        binding.tvDestinationLocation.text = "52.544940065357245, 13.354310290455304"
    }

    init {
        binding.btnSelectPbf.setOnClickListener {
            getListeners().forEach {
                it.selectPbf()
            }
        }

        binding.btnCreateGraphhopperInstance.setOnClickListener {
            getListeners().forEach {
                it.createGraphhopperInstance()
            }
        }

        binding.btnStartRouting.setOnClickListener {
            getListeners().forEach {
                it.startRouting()
            }
        }
    }

    override fun startCopyProcess() {
        binding.tvFileCopyProcessAnnotation.text =
            "Copy selected PBF File to external App Storage..."
    }

    override fun copyProcessDone() {
        binding.tvFileCopyProcessAnnotation.text =
            "PBF File successfully copied to external App Storage."
    }

    override fun startCreateGraphhopperInstanceProcess() {
        binding.tvCreateGraphhopperInstanceAnnotation.text =
            "Creating a new Graphhopper instance..."
    }

    override fun createGraphhopperInstanceDone() {
        binding.tvCreateGraphhopperInstanceAnnotation.text =
            "Successfully created a new Graphhopper instance."
    }

    override fun startRoutingProcess() {
        binding.tvStartRoutingAnnotation.text =
            "Find for the given start and destination location a routing..."
    }

    override fun startRoutingProcessDone() {
        binding.tvStartRoutingAnnotation.text = "Successfully calculated a routing."
    }

    override fun setRoutingResult(distance: Double, time: Long) {
        binding.tvDistanceInM.text = String.format("%f m", distance)
        binding.tvTimeInMS.text = String.format("%d ms", time)
    }
}