package io.explod.organizer.features.common

import android.content.Context
import android.support.annotation.StringRes
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import io.explod.organizer.R
import java.lang.ref.WeakReference

/**
 * EditTextDialog is a helper for creating a dialog that asks the user for a string
 */
class EditTextDialog(private val context: Context) {

    interface OnTextChangedListener {
        fun onTextChanged(newText: String)
    }

    private var listenerRef: WeakReference<OnTextChangedListener>? = null

    private var initialText = ""

    @StringRes
    private var titleRes: Int = 0

    /**
     * Set the dialog's title
     */
    fun setTitle(@StringRes title: Int): EditTextDialog {
        titleRes = title
        return this
    }

    /**
     * Set the initial text in the dialog. Text will be selected when the dialog opens
     */
    fun setInitialText(initialText: String): EditTextDialog {
        this.initialText = initialText
        return this
    }

    /**
     * Sets the callback for when text is submitted, held by weak reference
     */
    fun setOnTextChangedListener(listener: OnTextChangedListener?): EditTextDialog {
        val listenerRef = this.listenerRef
        if (listenerRef != null) {
            listenerRef.clear()
            this.listenerRef = null
        }
        if (listener != null) {
            this.listenerRef = WeakReference(listener)
        }
        return this
    }

    /**
     * Show and return the dialog. The keyboard will be automatically opened and closed
     * for the user's convenience.
     */
    fun show(): AlertDialog {
        val input = makeEditText()

        val view = FrameLayout(context)
        val padding = context.resources.getDimensionPixelSize(R.dimen.activity_horizontal_margin)
        view.setPadding(padding, 0, padding, 0)
        view.addView(input)

        // show our dialog
        val dialog = AlertDialog.Builder(context)
                .setTitle(titleRes)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    notifyOnText(input.text.toString())
                    dismissKeyboardForView(input)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> dismissKeyboardForView(input) }
                .show()

        showKeyboardOnInputFocus(dialog, input)
        wireImeOptions(dialog, input)

        return dialog
    }

    private fun showKeyboardOnInputFocus(dialog: AlertDialog, input: EditText) {
        input.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val window = dialog.window
                window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            }
        }
    }

    private fun wireImeOptions(dialog: AlertDialog, input: EditText) {
        input.setOnEditorActionListener(TextView.OnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_GO) {
                notifyOnText(input.text.toString())
                dismissKeyboardForView(input)
                dialog.dismiss()
                return@OnEditorActionListener true
            }
            false
        })
    }


    private fun dismissKeyboardForView(view: View) {
        val context = view.context ?: return

        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun makeEditText(): EditText {
        // prepare our edit text
        // - show old group name and select it
        // - allow single-line only
        // - enable GO keyboard button
        // - enable auto-capitalization
        val input = EditText(context)
        input.id = R.id.text_input
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        input.layoutParams = lp
        input.setText(initialText)
        input.inputType = EditorInfo.TYPE_TEXT_FLAG_CAP_WORDS
        input.maxLines = 1
        input.setSelection(0, initialText.length)
        input.imeOptions = EditorInfo.IME_ACTION_GO
        return input
    }

    private fun notifyOnText(text: String) {
        val listener = listenerRef?.get() ?: return
        listener.onTextChanged(text)
    }

}
