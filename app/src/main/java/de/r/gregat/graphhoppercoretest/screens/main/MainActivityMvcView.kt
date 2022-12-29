package de.r.gregat.graphhoppercoretest.screens.main

import de.r.gregat.graphhoppercoretest.screens.common.ObservableViewMvc

interface MainActivityMvcView: ObservableViewMvc<MainActivityMvcView.EventListener> {

    interface EventListener {
        fun selectPbf()
    }
}