package com.davidmiguel.multistateswitch

import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

/**
 * Defines the colors of one state.
 */
data class StateStyle(
        @ColorInt val textColor: Int?,
        @Dimension val textSize: Int?,
        val textTypeface: Typeface?,
        @ColorInt val selectedBackgroundColor: Int?,
        @ColorInt val selectedTextColor: Int?,
        @Dimension val selectedTextSize: Int?,
        val selectedTextTypeface: Typeface?,
        @ColorInt val disabledBackgroundColor: Int?,
        @ColorInt val disabledTextColor: Int?,
        @Dimension val disabledTextSize: Int?,
        val disabledTextTypeface: Typeface?
) {
    @Suppress("unused")
    class Builder {
        private var textColor : Int? = null
        private var textSize : Int? = null
        private var textTypeface: Typeface? = null
        private var selectedBackgroundColor : Int? = null
        private var selectedTextColor : Int? = null
        private var selectedTextSize : Int? = null
        private var selectedTextTypeface: Typeface? = null
        private var disabledBackgroundColor : Int? = null
        private var disabledTextColor : Int? = null
        private var disabledTextSize : Int? = null
        private var disabledTextTypeface: Typeface? = null

        /**
         * Defines state text color in normal state.
         */
        fun withTextColor(@ColorInt textColor: Int): Builder {
            this.textColor = textColor
            return this
        }

        /**
         * Defines state text size in normal state.
         */
        fun withTextSize(@Dimension textSize: Int): Builder {
            this.textSize = textSize
            return this
        }

        /**
         * Defines state text typeface in normal state.
         */
        fun withTextTypeface(textTypeface: Typeface): Builder {
            this.textTypeface = textTypeface
            return this
        }

        /**
         * Defines state background color in selected state.
         */
        fun withSelectedBackgroundColor(@ColorInt selectedBackgroundColor: Int): Builder {
            this.selectedBackgroundColor = selectedBackgroundColor
            return this
        }

        /**
         * Defines state text color in selected state.
         */
        fun withSelectedTextColor(@ColorInt selectedTextColor: Int): Builder {
            this.selectedTextColor = selectedTextColor
            return this
        }

        /**
         * Defines state text size in selected state.
         */
        fun withSelectedTextSize(@Dimension selectedTextSize: Int): Builder {
            this.selectedTextSize = selectedTextSize
            return this
        }

        /**
         * Defines state text typeface in selected state.
         */
        fun withSelectedTextTypeface(selectedTextTypeface: Typeface): Builder {
            this.selectedTextTypeface = selectedTextTypeface
            return this
        }

        /**
         * Defines state background color in disabled state.
         */
        fun withDisabledBackgroundColor(@ColorInt disabledBackgroundColor: Int): Builder {
            this.disabledBackgroundColor = disabledBackgroundColor
            return this
        }

        /**
         * Defines state text color in disabled state.
         */
        fun withDisabledTextColor(@ColorInt disabledTextColor: Int): Builder {
            this.disabledTextColor = disabledTextColor
            return this
        }

        /**
         * Defines state text size in disabled state.
         */
        fun withDisabledTextSize(@Dimension disabledTextSize: Int): Builder {
            this.disabledTextSize = disabledTextSize
            return this
        }

        /**
         * Defines state text typeface in disabled state.
         */
        fun withDisabledTextTypeface(disabledTextTypeface: Typeface): Builder {
            this.disabledTextTypeface = disabledTextTypeface
            return this
        }

        fun build(): StateStyle {
            return StateStyle(
                    textColor,
                    textSize,
                    textTypeface,
                    selectedBackgroundColor,
                    selectedTextColor,
                    selectedTextSize,
                    selectedTextTypeface,
                    disabledBackgroundColor,
                    disabledTextColor,
                    disabledTextSize,
                    disabledTextTypeface
            )
        }
    }
}
