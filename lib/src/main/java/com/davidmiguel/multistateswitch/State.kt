package com.davidmiguel.multistateswitch

/**
 * Models a state of the switch.
 */
data class State @JvmOverloads constructor(
        val text: String,
        val selectedText: String = text,
        val disabledText: String = text
)
