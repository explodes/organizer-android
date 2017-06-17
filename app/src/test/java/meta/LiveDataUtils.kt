package meta

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * Wait for LiveData to emit a single value
 */
@Throws(InterruptedException::class)
fun <T> LiveData<T>.first(timeoutMillis: Long = BaseRoboTest.DEFAULT_TIMEOUT, predicate: (t: T?) -> Boolean = { true }): T? {
    val success = arrayOf(false)
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(o: T?) {
            println("got $o")
            if (predicate(o)) {
                println("matches $o")
                success[0] = true
                data[0] = o
                latch.countDown()
                removeObserver(this)
            }
        }
    }
    observeForever(observer)
    BaseRoboTest.waitForMainLooper()
    latch.await(timeoutMillis, TimeUnit.MILLISECONDS)
    removeObserver(observer)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T?
}