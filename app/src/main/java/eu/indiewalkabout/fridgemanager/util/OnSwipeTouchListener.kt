package eu.indiewalkabout.fridgemanager.util

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View


class OnSwipeTouchListener(ctx: Context?, mainView: View): View.OnTouchListener {

    companion object {
        val TAG = OnSwipeTouchListener::class.java.name
    }

    private var gestureDetector: GestureDetector? = null
    var context: Context? = null

    init {
        gestureDetector = GestureDetector(ctx, MyGestureListener())
        mainView.setOnTouchListener(this)
        context = ctx
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return gestureDetector!!.onTouchEvent(event)
    }

    open class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(event: MotionEvent): Boolean {
            Log.d(TAG, "onDown: $event")
            return true
        }

        override fun onFling(
                event1: MotionEvent,
                event2: MotionEvent,
                velocityX: Float,
                velocityY: Float
        ): Boolean {
            Log.d(TAG, "onFling: $event1 $event2")
            return true
        }


        open fun onSwipeLeft() {}

        open fun onSwipeRight() {}

        open fun onSwipeBottom() {}

        open fun onSwipeTop() {}

        open fun onDownTouch() {}

    }
}