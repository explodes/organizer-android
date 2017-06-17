/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package io.explod.organizer.service.tracking

import android.util.Log.getStackTraceString

/**
 * A tracker that reports to Crashlytics and Answers
 */
class FabricTracker : Tracker {

    private val initializedTracker: io.reactivex.subjects.BehaviorSubject<InitializedFabricTracker> = io.reactivex.subjects.BehaviorSubject.create()

    override fun initialize(context: android.content.Context): io.reactivex.Completable {
        return io.reactivex.Completable.fromCallable { io.fabric.sdk.android.Fabric.with(context, com.crashlytics.android.Crashlytics(), com.crashlytics.android.answers.Answers()) }
                .doOnComplete { initializedTracker.onNext(io.explod.organizer.service.tracking.InitializedFabricTracker()) }
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
    }

    override fun event(action: String, properties: Map<String, Any>?) {
        // defer execution until we have initialized
        initializedTracker
                .take(1)
                .subscribe(
                        { tracker -> tracker.event(action, properties) }
                )
    }

    override fun recordException(level: Int, t: Throwable) {
        // defer execution until we have initialized
        initializedTracker
                .take(1)
                .subscribe(
                        { tracker -> tracker.recordException(level, t) }
                )

    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        // defer execution until we have initialized
        initializedTracker
                .take(1)
                .subscribe(
                        { tracker -> tracker.log(level, tag, message, t) }
                )
    }

}

private class InitializedFabricTracker : Tracker {

    override fun initialize(context: android.content.Context): io.reactivex.Completable = io.reactivex.Completable.complete()

    override fun event(action: String, properties: Map<String, Any>?) {
        val event = com.crashlytics.android.answers.CustomEvent(action)
        properties?.forEach { x ->
            val key = x.key
            val value = x.value
            when (value) {
                is Number -> event.putCustomAttribute(key, value)
                else -> event.putCustomAttribute(key, value.toString())
            }
        }
        com.crashlytics.android.answers.Answers.getInstance().logCustom(event)
    }

    override fun recordException(level: Int, t: Throwable) {
        com.crashlytics.android.Crashlytics.logException(t)
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        com.crashlytics.android.Crashlytics.log(level, tag, message + if (t == null) "" else "\n" + getStackTraceString(t))
    }

}