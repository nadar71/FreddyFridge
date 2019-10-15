package eu.indiewalkabout.fridgemanager.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

import eu.indiewalkabout.fridgemanager.R

/**
 * Custom view that is an extension of EditText.
 * It provides a Clear ("x") button within the text field
 * that, when tapped, clears the text from the field.
 */

class EditTextWithClear : android.support.v7.widget.AppCompatEditText {

    internal var mClearButtonImage: Drawable? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context,
                attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Initialize Drawable member variable.
        mClearButtonImage = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_clear_opaque_24dp, null)

        // If the X (clear) button is tapped, clear the text.
        setOnTouchListener(OnTouchListener { v, event ->
            // Use the getCompoundDrawables()[2] expression to check
            // if the drawable is on the "end" of text [2].
            if (compoundDrawablesRelative[2] != null) {
                val clearButtonStart: Float // Used for LTR languages
                val clearButtonEnd: Float  // Used for RTL languages
                var isClearButtonClicked = false
                // Detect the touch in RTL or LTR layout direction.
                if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                    // If RTL, get the end of the button on the left side.
                    clearButtonEnd = (mClearButtonImage!!
                            .intrinsicWidth + paddingStart).toFloat()
                    // If the touch occurred before the end of the button,
                    // set isClearButtonClicked to true.
                    if (event.x < clearButtonEnd) {
                        isClearButtonClicked = true
                    }
                } else {
                    // Layout is LTR.
                    // Get the start of the button on the right side.
                    clearButtonStart = (width - paddingEnd
                            - mClearButtonImage!!.intrinsicWidth).toFloat()
                    // If the touch occurred after the start of the button,
                    // set isClearButtonClicked to true.
                    if (event.x > clearButtonStart) {
                        isClearButtonClicked = true
                    }
                }
                // Check for actions if the button is tapped.
                if (isClearButtonClicked) {
                    // Check for ACTION_DOWN (always occurs before ACTION_UP).
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        // Switch to the black version of clear button.
                        mClearButtonImage = ResourcesCompat.getDrawable(resources,
                                R.drawable.ic_clear_black_24dp, null)
                        showClearButton()
                    }
                    // Check for ACTION_UP.
                    if (event.action == MotionEvent.ACTION_UP) {
                        // Switch to the opaque version of clear button.
                        mClearButtonImage = ResourcesCompat.getDrawable(resources,
                                R.drawable.ic_clear_opaque_24dp, null)
                        // Clear the text and hide the clear button.
                        text!!.clear()
                        hideClearButton()
                        return@OnTouchListener true
                    }
                } else {
                    return@OnTouchListener false
                }
            }
            false
        })

        // If the text changes, show or hide the X (clear) button.
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence,
                                           start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence,
                                       start: Int, before: Int, count: Int) {
                showClearButton()
            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }

    /**
     * Shows the clear (X) button.
     */

    private fun showClearButton() {
        // Sets the Drawables (if any) to appear to the left of,
        // above, to the right of, and below the text.
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, // Top of text.
                mClearButtonImage, null)// Start of text.
        // End of text.
        // Below text.
    }

    /**
     * Hides the clear button.
     */
    private fun hideClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)// Start of text.
        // Top of text.
        // End of text.
        // Below text.
    }
}

