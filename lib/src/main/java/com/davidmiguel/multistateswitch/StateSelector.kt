package com.davidmiguel.multistateswitch

import android.graphics.drawable.Drawable

/**
 * Stores the two representations of the state.
 */
data class StateSelector(
        val normal: Drawable,
        val selected: Drawable
)
