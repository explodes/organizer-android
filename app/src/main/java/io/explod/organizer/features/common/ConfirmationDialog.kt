package io.explod.organizer.features.common

import android.app.AlertDialog
import android.content.Context
import android.support.annotation.StringRes

/**
 * ConfirmationDialog is a helper for easily building a simple confirmation dialog
 */
class ConfirmationDialog {
    companion object {

        inline fun show(context: Context, @StringRes titleRes: Int, @StringRes messageRes: Int, crossinline onConfirmed: () -> Unit, crossinline onCancel: () -> Unit): AlertDialog {
            return AlertDialog.Builder(context)
                    .setTitle(titleRes)
                    .setMessage(messageRes)
                    .setPositiveButton(android.R.string.ok, { _, _ -> onConfirmed() })
                    .setNegativeButton(android.R.string.cancel, { _, _ -> onCancel() })
                    .show()
        }

        inline fun show(context: Context, @StringRes titleRes: Int, @StringRes messageRes: Int, crossinline onConfirmed: () -> Unit): AlertDialog {
            return AlertDialog.Builder(context)
                    .setTitle(titleRes)
                    .setMessage(messageRes)
                    .setPositiveButton(android.R.string.ok, { _, _ -> onConfirmed() })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
        }

    }
}
