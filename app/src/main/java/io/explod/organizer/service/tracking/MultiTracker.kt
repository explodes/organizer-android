/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package io.explod.organizer.service.tracking

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

/**
 * A tracker that delegates to multiple trackers
 */
class MultiTracker(vararg trackers: Tracker) : Tracker {

    private val mTrackers: List<Tracker> = Collections.unmodifiableList(java.util.Arrays.asList(*trackers))

    override fun initialize(context: Context): Completable {
        return Observable.fromIterable(mTrackers)
                .flatMapCompletable { t -> t.initialize(context) }
    }

    override fun event(action: String, properties: Map<String, Any>?) {
        for (tracker in mTrackers) {
            tracker.event(action, properties)
        }
    }

    override fun recordException(@LogLevel level: Int, t: Throwable) {
        for (tracker in mTrackers) {
            tracker.recordException(level, t)
        }
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        for (tracker in mTrackers) {
            tracker.log(level, tag, message, t)
        }
    }

}
