package com.github.aosamesan.executor.tasks

import com.github.aosamesan.executor.constants.ExecutorTaskStatus
import kotlinx.coroutines.flow.StateFlow

sealed interface ExecutorProcess {
    val progressPercentage: StateFlow<Float>
    val status: StateFlow<ExecutorTaskStatus>
    fun updateProgressPercentage(value: Float)
}