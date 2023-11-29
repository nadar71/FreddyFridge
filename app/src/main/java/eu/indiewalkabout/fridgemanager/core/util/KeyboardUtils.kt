package eu.indiewalkabout.fridgemanager.core.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager


// Based on the following Stackoverflow answer:
// http://stackoverflow.com/questions/2150078/how-to-check-visibility-of-software-keyboard-in-android
class KeyboardUtils private constructor(act: Activity, private var mCallback: SoftKeyboardToggleListener?) : ViewTreeObserver.OnGlobalLayoutListener {
    private val mRootView: View
    private var prevValue: Boolean? = null
    private val mScreenDensity: Float

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }


    override fun onGlobalLayout() {
        val r = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = mRootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible
            mCallback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        mCallback = null

        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    init {

        mRootView = (act.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)

        mScreenDensity = act.resources.displayMetrics.density
    }

    companion object {
        private val MAGIC_NUMBER = 200
        private val sListenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtils>()

        // Add a new keyboard listener
        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)

            sListenerMap[listener] = KeyboardUtils(act, listener)
        }

        // Remove a registered listener
        fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k = sListenerMap[listener]
                k!!.removeListener()

                sListenerMap.remove(listener)
            }
        }

        // Remove all registered keyboard listeners
        fun removeAllKeyboardToggleListeners() {
            for (l in sListenerMap.keys)
                sListenerMap[l]!!.removeListener()

            sListenerMap.clear()
        }

        // Manually toggle soft keyboard visibility
        fun toggleKeyboardVisibility(context: Context) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        // Force closes the soft keyboard
        fun forceCloseKeyboard(activeView: View) {
            val inputMethodManager = activeView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activeView.windowToken, 0)
        }
    }

}
