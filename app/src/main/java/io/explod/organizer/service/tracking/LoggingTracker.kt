/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package io.explod.organizer.service.tracking

import android.content.Context
import android.util.Log
import io.reactivex.Completable

/**
 * A tracker that reports to Crashlytics and Answers
 */
class LoggingTracker : Tracker {

    companion object {
        private const val TRACKER_TAG = "Tracker"
    }

    override fun initialize(context: Context): Completable = Completable.complete()

    override fun event(action: String, properties: Map<String, Any>?) {
        if (properties == null) {
            Log.d(TRACKER_TAG, action)
        } else {
            Log.d(TRACKER_TAG, "$action: $properties")
        }
    }

    override fun recordException(level: Int, t: Throwable) {
        logWithException(level, TRACKER_TAG, "Recorded Exception", t)
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        if (t == null) {
            logWithoutException(level, tag, message)
        } else {
            logWithException(level, tag, message, t)
        }
    }

    fun logWithException(priority: Int, tag: String, message: String, t: Throwable) {
        when (priority) {
            LevelV -> Log.v(tag, message, t)
            LevelD -> Log.d(tag, message, t)
            LevelI -> Log.i(tag, message, t)
            LevelW -> Log.w(tag, message, t)
            LevelE -> Log.e(tag, message, t)
            LevelA -> Log.wtf(tag, message, t)
        }
    }

    fun logWithoutException(priority: Int, tag: String, message: String) {
        when (priority) {
            LevelV -> Log.v(tag, message)
            LevelD -> Log.d(tag, message)
            LevelI -> Log.i(tag, message)
            LevelW -> Log.w(tag, message)
            LevelE -> Log.e(tag, message)
            LevelA -> Log.wtf(tag, message)
        }
    }

}
