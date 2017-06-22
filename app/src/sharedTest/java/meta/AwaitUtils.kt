package meta

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


private const val DEFAULT_TIMEOUT = 5_000L

private const val OFFLOAD_NAME = "await-"
private var sOffloadCount = 0L
private val nextOffloadName: String
    get() = "$OFFLOAD_NAME-${++sOffloadCount}"

/**
 * Offload work to a new thread, but block until complete.
 *
 * A great use-case is when you are testing Room stuff, and
 * are not allowed to query on the main thread. Adding
 * [android.arch.persistence.room.RoomDatabase.Builder.allowMainThreadQueries]
 * will let the tests run without crashing, but the app may be misusing
 * Room and the tests will not reflect that.
 */
@Throws(Exception::class)
fun <T> await(timeoutMillis: Long = DEFAULT_TIMEOUT, work: () -> T): T? {

    class AwaitResult<T>(var result: T? = null, var finished: Boolean = false, var error: Throwable? = null)

    // results and wait latch
    val results = AwaitResult<T>()
    val latch = CountDownLatch(1)

    // The worker thread
    val thread = Thread({
        try {
            results.result = work()
            results.finished = true
        } catch (ex: Throwable) {
            results.error = ex
        } finally {
            latch.countDown()
        }
    }, nextOffloadName)

    // Execute and wait
    thread.start()
    latch.await(timeoutMillis, TimeUnit.MILLISECONDS)

    // Handle the results
    if (results.error != null) {
        throw RuntimeException("Offloading was interrupted because of a failure", results.error)
    }
    if (!results.finished) {
        throw TimeoutException("Unable to perform work in a reasonable time frame")
    }

    // Return the result of our work
    @Suppress("UNCHECKED_CAST")
    return results.result as T
}
