/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package io.explod.organizer.service.tracking

import android.content.Context
import android.util.Log.getStackTraceString
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import io.fabric.sdk.android.Fabric
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

/**
 * A tracker that reports to Crashlytics and Answers
 */
class FabricTracker : Tracker {

    private val initializedTracker: BehaviorSubject<Tracker> = BehaviorSubject.create()

    override fun initialize(context: Context): Completable {
        return Completable.fromCallable { Fabric.with(context, Crashlytics(), Answers()) }
                .doOnComplete { initializedTracker.onNext(InitializedFabricTracker()) }
                .subscribeOn(Schedulers.io())
    }

    /**
     * Defer execution of actual tracking until the tracker has actually been initialized
     */
    private inline fun whenTrackerIsReady(crossinline action: (tracker: Tracker) -> Unit) {
        initializedTracker
                .take(1)
                .subscribeBy(onNext = { action(it) })
    }

    override fun event(action: String, properties: Map<String, Any>?) {
        whenTrackerIsReady { it.event(action, properties) }
    }

    override fun recordException(level: Int, t: Throwable) {
        whenTrackerIsReady { it.recordException(level, t) }
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        whenTrackerIsReady { it.log(level, tag, message, t) }
    }

}

private class InitializedFabricTracker : Tracker {

    override fun initialize(context: Context): Completable = Completable.complete()

    override fun event(action: String, properties: Map<String, Any>?) {
        val event = CustomEvent(action)
        properties?.forEach { x ->
            val key = x.key
            val value = x.value
            when (value) {
                is Number -> event.putCustomAttribute(key, value)
                else -> event.putCustomAttribute(key, value.toString())
            }
        }
        Answers.getInstance().logCustom(event)
    }

    override fun recordException(level: Int, t: Throwable) {
        Crashlytics.logException(t)
    }

    override fun log(level: Int, tag: String, message: String, t: Throwable?) {
        Crashlytics.log(level, tag, message + if (t == null) "" else "\n" + getStackTraceString(t))
    }

}