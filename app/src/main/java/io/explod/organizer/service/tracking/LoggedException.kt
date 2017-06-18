package io.explod.organizer.service.tracking

/**
 * Wraps an exception with context of when an error occurred for more verbose logging
 * about failures that happen throughout the app.
 */
class LoggedException(message: String, cause: Throwable) : Exception(message, cause)
