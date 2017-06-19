/*
 * Copyright (c) 2017 SalesRabbit, Inc. All rights reserved.
 */

package io.explod.organizer.service.tracking

import android.content.Context
import android.support.annotation.IntDef
import android.util.Log
import io.reactivex.Completable

const val LevelV = Log.VERBOSE
const val LevelD = Log.DEBUG
const val LevelI = Log.INFO
const val LevelW = Log.WARN
const val LevelE = Log.ERROR
const val LevelA = Log.ASSERT

@IntDef(LevelV.toLong(), LevelD.toLong(), LevelI.toLong(), LevelW.toLong(), LevelE.toLong(), LevelA.toLong())
annotation class LogLevel


/**
 * A Tracker is responsible for logging events, crashes, and log info to some repository.
 */
interface Tracker {

    /**
     * Prepare the tracker with context
     */
    fun initialize(context: Context): Completable

    /**
     * Record a user event, such as clicking on a button or toggling a setting
     *
     * @param action name of the action to log, such as "settingsEnableAutomaticSync"
     * @param properties additional properties such as "enabled=true". Generally speaking,
     *      values will be converted to strings
     */
    fun event(action: String, properties: Map<String, Any>? = null)

    /**
     * Record an exception for later analysis
     *
     * @param level log level of this event, as some errors are more serious than others
     * @param t exception to record
     */
    fun recordException(@LogLevel level: Int, t: Throwable)

    /**
     * Log information about user flow, or an error.
     * This information is not meant for analytics, but is instead meant for debugging
     *
     * @param level log level of this event
     * @param tag tag of the log
     * @param message message to log
     * @param t optional exception to log
     */
    fun log(@LogLevel level: Int, tag: String, message: String, t: Throwable? = null)

}



