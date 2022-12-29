package de.r.gregat.graphhoppercoretest.utils

import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class BackgroundThreadHelper {

    companion object {
        const val CORE_THREADS = 3
        const val KEEP_ALIVE_SECONDS = 60L
    }

    private val threadPoolExecutor: ThreadPoolExecutor = newThreadPoolExecutor()

    fun post(runnable: Runnable?) {
        threadPoolExecutor.execute(runnable)
    }

    private fun newThreadPoolExecutor(): ThreadPoolExecutor {
        return ThreadPoolExecutor(
            CORE_THREADS,
            Int.MAX_VALUE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            SynchronousQueue<Runnable>()
        )
    }
}