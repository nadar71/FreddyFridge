package eu.indiewalkabout.fridgemanager.core.util

import android.os.Handler
import android.os.Looper

import java.util.concurrent.Executor
import java.util.concurrent.Executors

// Global executor for all the app request; all tasks are grouped here.
// Used to run threads outside main thread.
class AppExecutors// singleton constructor
private constructor(private val diskIO: Executor, private val mainThread: Executor, private val networkIO: Executor) {


    fun diskIO(): Executor {
        return diskIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    fun networkIO(): Executor {
        return networkIO
    }


    private class MainThreadExecutor : Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    companion object {
        // singleton instantiation
        private val LOCK = Any()
        private var singleInstance: AppExecutors? = null

        val instance: AppExecutors?
            get() {
                if (singleInstance == null) {
                    synchronized(LOCK) {
                        singleInstance = AppExecutors(
                                Executors.newSingleThreadExecutor(),
                                Executors.newFixedThreadPool(3),
                                MainThreadExecutor()
                        )
                    }
                }
                return singleInstance
            }
    }


}
