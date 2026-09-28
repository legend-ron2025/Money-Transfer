package com.moneytracker.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.scheduling.Task
import java.util.concurrent.*

class AppExecutors private constructor(
    private val diskIO: ExecutorCoroutineDispatcher,
    private val networkIO: ExecutorCoroutineDispatcher,
    private val cpuIO: ExecutorCoroutineDispatcher
) {

    companion object {
        @Volatile
        private var INSTANCE: AppExecutors? = null

        fun initialize() {
            if (INSTANCE == null) {
                synchronized(AppExecutors::class) {
                    if (INSTANCE == null) {
                        INSTANCE = AppExecutors(
                            diskIO = Dispatchers.IO,
                            networkIO = Dispatchers.IO,
                            cpuIO = Dispatchers.Default
                        )
                    }
                }
            }
        }

        fun getInstance(): AppExecutors {
            return INSTANCE ?: throw IllegalStateException("AppExecutors not initialized. Call initialize() first.")
        }

        val diskIO: ExecutorCoroutineDispatcher
            get() = getInstance().diskIO

        val networkIO: ExecutorCoroutineDispatcher
            get() = getInstance().networkIO

        val cpuIO: ExecutorCoroutineDispatcher
            get() = getInstance().cpuIO

        val mainThread: ExecutorCoroutineDispatcher
            get() = Dispatchers.Main
    }
}