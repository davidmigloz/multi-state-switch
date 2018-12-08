package com.davidmiguel.multistateswitch;

import android.graphics.Typeface;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Defines the colors of one state.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class StateStyle {

    @ColorInt
    private int textColor;
    @Dimension
    private int textSize;
    private Typeface textTypeface;
    @ColorInt
    private int selectedBackgroundColor;
    @ColorInt
    private int selectedTextColor;
    @Dimension
    private int selectedTextSize;
    private Typeface selectedTextTypeface;
    @ColorInt
    private int disabledBackgroundColor;
    @ColorInt
    private int disabledTextColor;
    @Dimension
    private int disabledTextSize;
    private Typeface disabledTextTypeface;

    private StateStyle(@NonNull Builder builder) {
        textColor = builder.textColor;
        textSize = builder.textSize;
        textTypeface = builder.textTypeface;
        selectedBackgroundColor = builder.selectedBackgroundColor;
        selectedTextColor = builder.selectedTextColor;
        selectedTextSize = builder.selectedTextSize;
        selectedTextTypeface = builder.selectedTextTypeface;
        disabledBackgroundColor = builder.disabledBackgroundColor;
        disabledTextColor = builder.disabledTextColor;
        disabledTextSize = builder.disabledTextSize;
        disabledTextTypeface = builder.disabledTextTypeface;
    }

    public int getTextColor(@ColorInt int defaultTextColor) {
        return textColor != 0 ? textColor : defaultTextColor;
    }

    public int getTextSize(@Dimension int defaultTextSize) {
        return textSize != 0 ? textSize : defaultTextSize;
    }

    public Typeface getTextTypeface(@Nullable Typeface defaultTextTypeface) {
        return textTypeface != null ? textTypeface : defaultTextTypeface;
    }

    public int getSelectedBackgroundColor(@ColorInt int defaultSelectedBackgroundColor) {
        return selectedBackgroundColor != 0 ? selectedBackgroundColor : defaultSelectedBackgroundColor;
    }

    public int getSelectedTextColor(@ColorInt int defaultSelectedTextColor) {
        return selectedTextColor != 0 ? selectedTextColor : defaultSelectedTextColor;
    }

    public int getSelectedTextSize(@Dimension int defaultSelectedTextSize) {
        return selectedTextSize != 0 ? selectedTextSize : defaultSelectedTextSize;
    }

    public Typeface getSelectedTextTypeface(@Nullable Typeface defaultSelectedTextTypeface) {
        return selectedTextTypeface != null ? selectedTextTypeface : defaultSelectedTextTypeface;
    }

    public int getDisabledBackgroundColor(@ColorInt int defaultDisabledBackgroundColor) {
        return disabledBackgroundColor != 0 ? disabledBackgroundColor : defaultDisabledBackgroundColor;
    }

    public int getDisabledTextColor(@ColorInt int defaultDisabledTextColor) {
        return disabledTextColor != 0 ? disabledTextColor : defaultDisabledTextColor;
    }

    public int getDisabledTextSize(@Dimension int defaultDisabledTextSize) {
        return disabledTextSize != 0 ? disabledTextSize : defaultDisabledTextSize;
    }

    public Typeface getDisabledTextTypeface(@Nullable Typeface defaultDisabledTextTypeface) {
        return disabledTextTypeface != null ? disabledTextTypeface : defaultDisabledTextTypeface;
    }

    public static final class Builder {

        private int textColor = 0;
        private int textSize = 0;
        private Typeface textTypeface;
        private int selectedBackgroundColor = 0;
        private int selectedTextColor = 0;
        private int selectedTextSize = 0;
        private Typeface selectedTextTypeface;
        private int disabledBackgroundColor = 0;
        private int disabledTextColor = 0;
        private int disabledTextSize = 0;
        private Typeface disabledTextTypeface;

        /**
         * Defines state text color in normal state.
         */
        public Builder withTextColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Defines state text size in normal state.
         */
        public Builder withTextSize(@Dimension int textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * Defines state text typeface in normal state.
         */
        public Builder withTextTypeface(@NonNull Typeface textTypeface) {
            this.textTypeface = textTypeface;
            return this;
        }

        /**
         * Defines state background color in selected state.
         */
        public Builder withSelectedBackgroundColor(@ColorInt int selectedBackgroundColor) {
            this.selectedBackgroundColor = selectedBackgroundColor;
            return this;
        }

        /**
         * Defines state text color in selected state.
         */
        public Builder withSelectedTextColor(@ColorInt int selectedTextColor) {
            this.selectedTextColor = selectedTextColor;
            return this;
        }

        /**
         * Defines state text size in selected state.
         */
        public Builder withSelectedTextSize(@Dimension int selectedTextSize) {
            this.selectedTextSize = selectedTextSize;
            return this;
        }

        /**
         * Defines state text typeface in selected state.
         */
        public Builder withSelectedTextTypeface(@NonNull Typeface selectedTextTypeface) {
            this.selectedTextTypeface = selectedTextTypeface;
            return this;
        }

        /**
         * Defines state background color in disabled state.
         */
        public Builder withDisabledBackgroundColor(@ColorInt int disabledBackgroundColor) {
            this.disabledBackgroundColor = disabledBackgroundColor;
            return this;
        }

        /**
         * Defines state text color in disabled state.
         */
        public Builder withDisabledTextColor(@ColorInt int disabledTextColor) {
            this.disabledTextColor = disabledTextColor;
            return this;
        }

        /**
         * Defines state text size in disabled state.
         */
        public Builder withDisabledTextSize(@Dimension int disabledTextSize) {
            this.disabledTextSize = disabledTextSize;
            return this;
        }

        /**
         * Defines state text typeface in disabled state.
         */
        public Builder withDisabledTextTypeface(@NonNull Typeface disabledTextTypeface) {
            this.disabledTextTypeface = disabledTextTypeface;
            return this;
        }

        public StateStyle build() {
            return new StateStyle(this);
        }
    }
}
