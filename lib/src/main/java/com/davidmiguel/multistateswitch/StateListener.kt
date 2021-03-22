package com.davidmiguel.multistateswitch

/**
 * Listener of state changes.
 */
fun interface StateListener {
    /**
     * Invoked when a state is selected.
     *
     * @param stateIndex index of the state.
     * @param state      state instance.
     */
    fun onStateSelected(stateIndex: Int, state: State)
}
