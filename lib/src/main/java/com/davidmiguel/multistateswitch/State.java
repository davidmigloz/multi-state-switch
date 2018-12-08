package com.davidmiguel.multistateswitch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Models a state of the switch.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class State {

    private String text;
    private String selectedText;
    private String disabledText;

    /**
     * Creates state with given text.
     * The same text will be used for normal, selected and disabled state.
     */
    public State(@NonNull String text) {
        this(text, null, null);
    }

    /**
     * Creates state with given text and selected text.
     * Text will be used for disabled state.
     */
    public State(String text, String selectedText) {
        this(text, selectedText, null);
    }

    /**
     * Creates state with given text, selected text and disabled text.
     */
    public State(@NonNull String text, @Nullable String selectedText, @Nullable String disabledText) {
        Objects.requireNonNull(text);
        this.text = text;
        this.selectedText = selectedText;
        this.disabledText = disabledText;
    }

    public String getText() {
        return text;
    }

    public String getSelectedText() {
        return selectedText != null ? selectedText : text;
    }

    public String getDisabledText() {
        return disabledText != null ? disabledText : text;
    }
}
