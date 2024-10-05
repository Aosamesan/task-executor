import com.github.aosamesan.executor.AbstractTaskExecutor
import com.github.aosamesan.executor.tasks.ExecutorTask
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import java.util.concurrent.Executors
import kotlin.test.Test

class TaskExecutorJVMTests {
    companion object TaskExecutor : AbstractTaskExecutor(Executors.newCachedThreadPool().asCoroutineDispatcher(), 2)

    @Test
    fun executorTest() = runBlocking {
        val job = launch {
            TaskExecutor.tasks.collect {
                println("Tasks Changed : ${it.map { task -> task.parameter }}")
            }
        }

        fun convertToTask(num: Int): ExecutorTask<Int, Unit> = ExecutorTask(num) {
            println("[${Clock.System.now()}] Parameter : $num and wait 1 second...")
            delay(1000L)
            println("[${Clock.System.now()}] Done! $num")
        }

        (1..10).map(::convertToTask).forEach {
            TaskExecutor.addTask(it)
            delay(100L)
        }

        delay(12000L)
        job.cancel()
    }
}