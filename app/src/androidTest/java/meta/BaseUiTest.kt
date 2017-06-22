package meta

import android.support.test.InstrumentationRegistry


/**
 * Explicitly wait for the UI to catch up
 */
fun syncUi() {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    instrumentation.waitForIdleSync()
}

/**
 * Run a function on the UI and wait for the UI to catch up
 */
fun awaitUi(f: () -> Unit) {
    val instrumentation = InstrumentationRegistry.getInstrumentation()
    instrumentation.runOnMainSync({ f() })
    syncUi()
}
