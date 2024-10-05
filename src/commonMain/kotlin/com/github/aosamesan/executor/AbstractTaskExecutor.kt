package com.github.aosamesan.executor

import com.github.aosamesan.executor.constants.ExecutorTaskStatus
import com.github.aosamesan.executor.tasks.ExecutorTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

abstract class AbstractTaskExecutor(
    dispatcher: CoroutineDispatcher,
    maxConcurrentTasks: Int = 1
) {
    private val scope = CoroutineScope(dispatcher + SupervisorJob())
    private val semaphore = Semaphore(maxConcurrentTasks)
    private val fetchedTasks = MutableStateFlow<ArrayDeque<ExecutorTask<*, *>>>(ArrayDeque())
    private val _tasks = MutableStateFlow<List<ExecutorTask<*, *>>>(emptyList())
    val tasks = _tasks.asStateFlow()

    fun addTask(task: ExecutorTask<*, *>) {
        _tasks.value += task
        fetchedTasks.value += task
        processNextTask()
    }

    fun <T> addTask(data: T, converter: (T) -> ExecutorTask<*, *>) {
        addTask(converter(data))
    }

    fun removeTask(task: ExecutorTask<*, *>) {
        _tasks.value -= task
        if (task.status.value == ExecutorTaskStatus.Processing) {
            task.cancel()
            fetchedTasks.value -= task
        }
    }

    private fun fetchTask(): ExecutorTask<*, *>? {
        return fetchedTasks.value.removeFirstOrNull()
    }

    private fun processNextTask() {
        val task = fetchTask() ?: return
        process(task)
    }

    private fun process(task: ExecutorTask<*, *>) {
        scope.launch {
            semaphore.withPermit {
                task {
                    cancel()
                }
                task.updateProgressPercentage(100f)
            }
            processNextTask()
        }
    }
}