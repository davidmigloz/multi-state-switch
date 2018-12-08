package com.davidmiguel.multistateswitch;

import androidx.annotation.NonNull;

/**
 * Listener of state changes.
 */
@SuppressWarnings("unused")
public interface StateListener {

    /**
     * Invoked when a state is selected.
     *
     * @param stateIndex index of the state.
     * @param state      state instance.
     */
    void onStateSelected(int stateIndex, @NonNull State state);

}
