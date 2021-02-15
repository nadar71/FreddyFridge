package eu.indiewalkabout.fridgemanager.util.extensions

import android.view.View

// Views extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}