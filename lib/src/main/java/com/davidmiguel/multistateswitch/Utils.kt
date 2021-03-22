package com.davidmiguel.multistateswitch

import android.content.res.Resources

/**
 * Converts dp (density-independent pixel) to px.
 * E.g. 8 dp to px: 8.px
 */
val Float.dp2px: Float
    get() = this * Resources.getSystem().displayMetrics.density
