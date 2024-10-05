package com.github.aosamesan.executor.tasks

import com.github.aosamesan.executor.constants.ExecutorTaskStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.cancellation.CancellationException

typealias CancelJob = () -> Unit

data class ExecutorTask<TData, TResult>(
    val parameter: TData,
    val execute: suspend ExecutorProcess.(TData) -> TResult
) : ExecutorProcess {
    private val _progressPercentage = MutableStateFlow(0f)
    private val _status = MutableStateFlow(ExecutorTaskStatus.Ready)
    private val _result = MutableSharedFlow<TResult>()
    private val _exception = MutableSharedFlow<Throwable>()
    private var cancelJob: CancelJob? = null

    override val progressPercentage = _progressPercentage.asStateFlow()
    override val status = _status.asStateFlow()
    val result = _result.asSharedFlow()
    val exception = _exception.asSharedFlow()

    override fun updateProgressPercentage(value: Float) {
        check(value in 0f .. 100f) {
            "Progress percentage must be in range 0 .. 100, but was $value"
        }
        _progressPercentage.value = value
    }

    suspend operator fun invoke() {
        try {
            _status.value = ExecutorTaskStatus.Processing
            val result = execute(parameter)
            _status.value = ExecutorTaskStatus.Completed
            _result.emit(result)
        } catch (e: CancellationException) {
            _status.value = ExecutorTaskStatus.Canceled
        } catch (e: Throwable) {
            _exception.emit(e)
            _status.value = ExecutorTaskStatus.Error
        }
    }

    suspend operator fun invoke(cancelJob: CancelJob) {
        this.cancelJob = cancelJob
        invoke()
    }

    fun cancel() {
        cancelJob?.invoke()
    }
}