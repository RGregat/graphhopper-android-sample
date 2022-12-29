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

        binding.button.setOnClickListener {
            getListeners().forEach {
                it.selectPbf()
            }
        }
    }
}