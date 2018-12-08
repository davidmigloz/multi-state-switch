package com.davidmiguel.multistateswitch;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

/**
 * Stores the two representations of the state.
 */
@SuppressWarnings("unused")
class StateSelector {

    private Drawable normal;
    private Drawable selected;

    StateSelector(@NonNull Drawable normal, @NonNull Drawable selected) {
        this.normal = normal;
        this.selected = selected;
    }

    Drawable getNormal() {
        return normal;
    }

    Drawable getSelected() {
        return selected;
    }
}
