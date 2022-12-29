package de.r.gregat.graphhoppercoretest.screens.common

import java.util.*

abstract class BaseObservableViewMvc<ListenerType> : BaseViewMvc(),
    ObservableViewMvc<ListenerType> {

    private var listeners: MutableSet<ListenerType> = mutableSetOf()

    override fun registerListener(listener: ListenerType) {
        listeners.add(listener)
    }

    override fun unregisterListener(listener: ListenerType) {
        listeners.remove(listener)
    }

    fun getListeners(): Set<ListenerType> {
        return Collections.unmodifiableSet(listeners)
    }
}