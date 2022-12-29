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
    }

    override fun startCopyProcess() {
        binding.tvFileCopyProcessAnnotation.text = "Copy selected PBF File to external App Storage..."
    }

    override fun copyProcessDone() {
        binding.tvFileCopyProcessAnnotation.text = "PBF File successfully copied to external App Storage."
    }

    override fun startCreateGraphhopperInstanceProcess() {
        binding.tvCreateGraphhopperInstanceAnnotation.text = "Creating a new Graphhopper instance..."
    }

    override fun createGraphhopperInstanceDone() {
        binding.tvCreateGraphhopperInstanceAnnotation.text = "Successfully created a new Graphhopper instance."
    }
}