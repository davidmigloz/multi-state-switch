package com.davidmiguel.multistateswitch

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

// Num of states used in editor preview
private const val DEFAULT_NUM_STATES = 3

// Overflow of the shadow
private const val SHADOW_TOP_OVERFLOW_DP = 2f
private const val SHADOW_BOTTOM_OVERFLOW_DP = 10f

@Suppress("MemberVisibilityCanBePrivate", "unused")
class MultiStateSwitch @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = R.attr.multistateswitch_MultiStateSwitchStyle,
        defStyleRes: Int = R.style.multistateswitch_MultiStateSwitch,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    // START Styleable properties
    @ColorInt
    private var switchBackgroundColor: Int = 0

    @ColorInt
    private var textColor: Int = 0

    @Dimension
    private var textSize: Int = 0
    private var textTypeface: Typeface? = null

    @ColorInt
    private var selectedBackgroundColor: Int = 0

    @ColorInt
    private var selectedTextColor: Int = 0

    @Dimension
    private var selectedTextSize: Int = 0
    private var disabledStateEnabled: Boolean = false
    private var disableStateIndex: Int = 0

    @ColorInt
    private var disabledBackgroundColor: Int = 0

    @ColorInt
    private var disabledTextColor: Int = 0

    @Dimension
    private var disabledTextSize: Int = 0
    private var maxNumberStates: Int = -1
    // END Styleable properties

    private val drawingArea = Rect()

    private lateinit var background: GradientDrawable
    private var shadowTopOverflowPx: Int = 0
    private var shadowBottomOverflowPx: Int = 0
    private var shadowStartEndOverflowPx: Int = 0
    private var shadowHeight: Int = 0


    private val states: MutableList<State> = ArrayList(DEFAULT_NUM_STATES)
    private val statesStyles: SparseArray<StateStyle> = SparseArray(DEFAULT_NUM_STATES)
    private val statesSelectors: MutableList<StateSelector> = ArrayList(DEFAULT_NUM_STATES)
    private val statesCenters: MutableList<Point> = ArrayList(DEFAULT_NUM_STATES)
    private val stateSize = Point()
    private val stateRadius = Point()

    private var initialized: Boolean = false
    private var currentStateIndex: Int = 0
    private val currentStateCenter = Point()
    private var stateIsPressed: Boolean = false
    private var touchDownX: Float = 0f
    private val scaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private var stateListeners: MutableList<StateListener> = ArrayList(1)

    init {
        processAttributes(context, attrs, defStyleAttr, defStyleRes)
        configView()
        configEditMode()
    }

    /**
     * Reads the xml attributes and configures the view based on them.
     */
    private fun processAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateSwitch, defStyleAttr, defStyleRes)
        try {
            switchBackgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_background_color, 0)
            textColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_text_color, 0)
            textSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_text_size, 0)
            currentStateIndex = a.getInt(R.styleable.MultiStateSwitch_multistateswitch_selected_state_index, 0)
            selectedBackgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_selected_background_color, 0)
            selectedTextColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_selected_text_color, 0)
            selectedTextSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_selected_text_size, 0)
            disabledStateEnabled = a.getBoolean(R.styleable.MultiStateSwitch_multistateswitch_disabled_state_enabled, false)
            disableStateIndex = a.getInt(R.styleable.MultiStateSwitch_multistateswitch_disabled_state_index, 0)
            disabledBackgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_disabled_background_color, 0)
            disabledTextColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_disabled_text_color, 0)
            disabledTextSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_disabled_text_size, 0)
            maxNumberStates = a.getInt(R.styleable.MultiStateSwitch_multistateswitch_max_number_states, -1)
        } finally {
            a.recycle()
        }
    }

    private fun configView() {
        // Measures
        shadowTopOverflowPx = SHADOW_TOP_OVERFLOW_DP.dp2px.toInt()
        shadowBottomOverflowPx = SHADOW_BOTTOM_OVERFLOW_DP.dp2px.toInt()
        // Background
        background = ContextCompat.getDrawable(context, R.drawable.multistateswitch_background)!!.mutate() as GradientDrawable
        background.setColor(switchBackgroundColor)
    }

    /**
     * Configures the mock preview displayed in the layout editor.
     */
    private fun configEditMode() {
        if (!isInEditMode) return
        val numMockStates = if (maxNumberStates > 0) maxNumberStates else 3
        for (i in 1..numMockStates) {
            addStateFromString(i.toString())
        }
    }

    /**
     * Adds state to the switch.
     */
    @JvmOverloads
    fun addState(state: State, stateStyle: StateStyle? = null) {
        if (hasMaxNumberStates() && getNumberStates() >= getMaxNumberStates()) return
        states.add(state)
        stateStyle?.run { statesStyles.put(getNumberStates() - 1, stateStyle) }
        if (isAttachedToWindow) {
            populateView()
            invalidate()
        }
    }

    /**
     * Adds states to the switch and the displaying styles.
     * If you provide styles, you have to provide them for every state.
     */
    @JvmOverloads
    fun addStates(states: List<State>, stateStyles: List<StateStyle>? = null) {
        require(stateStyles == null || states.size == stateStyles.size) { "Number of states and styles must be the same" }
        states.forEachIndexed { index, state ->
            addState(state, stateStyles?.get(index))
        }
    }

    /**
     * Adds state to the switch directly from a string and the displaying style.
     * The text will be used for normal, selected and disabled states.
     */
    @JvmOverloads
    fun addStateFromString(stateText: String, stateStyle: StateStyle? = null) {
        addState(State(stateText), stateStyle)
    }

    /**
     * Adds states to the switch directly from a string and the displaying styles.
     * If you provide styles, you have to provide them for every state.
     * The texts will be used for normal, selected and disabled states.
     */
    @JvmOverloads
    fun addStatesFromStrings(statesTexts: List<String>, stateStyles: List<StateStyle>? = null) {
        statesTexts.forEachIndexed { index, stateText ->
            addStateFromString(stateText, stateStyles?.get(index))
        }
    }

    /**
     * Replaces state.
     */
    @JvmOverloads
    fun replaceState(stateIndex: Int, state: State, stateStyle: StateStyle? = null) {
        require(stateIndex < getNumberStates()) { "State index doesn't exist" }
        states[stateIndex] = state
        statesStyles.put(stateIndex, stateStyle)
        if (hasDrawingArea()) {
            statesSelectors[stateIndex] = createStateSelector(stateIndex)
        }
        invalidate()
    }

    /**
     * Replaces state directly from a string.
     * The text will be used for normal, selected and disabled states.
     */
    fun replaceStateFromString(stateIndex: Int, stateText: String) {
        replaceState(stateIndex, State(stateText))
    }

    /**
     * Removes an state.
     */
    fun removeState(stateIndex: Int) {
        require(stateIndex < getNumberStates()) { "State index doesn't exist" }
        states.removeAt(stateIndex)
        statesStyles.remove(stateIndex)
        if (stateIndex == currentStateIndex) {
            currentStateIndex = if (stateIndex == 0) 0 else stateIndex - 1
        }
        if (isAttachedToWindow) {
            populateView()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Called when the view is first assigned a size, and again if the size changes for any reason
        // All calculations related to positions, dimensions, and any other values must be done here (not in onDraw)
        super.onSizeChanged(w, h, oldw, oldh)
        populateView()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        populateView()
    }

    /**
     * Prepares all data needed to draw the view.
     */
    private fun populateView() {
        if (getNumberStates() == 0 || !hasDrawingArea()) return
        try {
            calculateDrawingSizes()
            populateStates()
            calculateBounds()
            determineCenterPositions()
            initialized = true
        } catch (e: Exception) { // Ignored
        }
    }

    /**
     * Checks that the drawing area is not 0.
     */
    private fun hasDrawingArea(): Boolean = width != 0 && height != 0

    /**
     * Calculates the area where we can draw (essentially the bounding rect minus any padding).
     * The area is represented in a rectangle.
     */
    private fun calculateDrawingSizes() {
        drawingArea[paddingStart, paddingTop, width - paddingEnd] = height - paddingBottom
        setStateHeight(drawingArea.height())
        setStateWidth(drawingArea.width() / getNumberStates())
    }

    /**
     * Sets the height available for a state.
     */
    private fun setStateHeight(height: Int) {
        stateSize.y = height
        stateRadius.y = height / 2
    }

    /**
     * Sets the width available for a state.
     */
    private fun setStateWidth(width: Int) {
        stateSize.x = width
        stateRadius.x = width / 2
    }

    /**
     * Populates the states that have to be drawn.
     */
    private fun populateStates() {
        statesSelectors.clear()
        for (i in states.indices) {
            try {
                val selector = createStateSelector(i)
                statesSelectors.add(selector)
            } catch (e: Exception) {
                Log.e("asdf", "populateStates()", e)
            }
        }
    }

    /**
     * Creates the two possible representations of the state (normal and selected, or disabled in
     * case disabled state is supported).
     */
    private fun createStateSelector(stateIndex: Int): StateSelector {
        val (text, selectedText, disabledText) = states[stateIndex]
        val stateStyle = statesStyles[stateIndex] ?: StateStyle.Builder().build()
        // Create the drawable representing the state (normal, selected or disabled)
        val bitmapNormal = createStateDrawable(
                stateText = text,
                textWidth = stateSize.x,
                textHeight = stateSize.y,
                backgroundColor = 0,
                shadow = false,
                textColor = stateStyle.textColor ?: textColor,
                textSize = stateStyle.textSize ?: textSize,
                textTypeface = stateStyle.textTypeface ?: textTypeface
        )
        val bitmapSelected = if (disabledStateEnabled && stateIndex == disableStateIndex) createStateDrawable(
                stateText = disabledText,
                textWidth = stateSize.x,
                textHeight = stateSize.y,
                backgroundColor = stateStyle.disabledBackgroundColor ?: disabledBackgroundColor,
                shadow = true,
                textColor = stateStyle.disabledTextColor ?: disabledTextColor,
                textSize = stateStyle.disabledTextSize ?: disabledTextSize,
                textTypeface = stateStyle.disabledTextTypeface ?: textTypeface
        )
        else createStateDrawable(selectedText,
                textWidth = stateSize.x,
                textHeight = stateSize.y,
                backgroundColor = stateStyle.selectedBackgroundColor ?: selectedBackgroundColor,
                shadow = true,
                textColor = stateStyle.selectedTextColor ?: selectedTextColor,
                textSize = stateStyle.selectedTextSize ?: selectedTextSize,
                textTypeface = stateStyle.selectedTextTypeface ?: textTypeface
        )
        return StateSelector(bitmapNormal, bitmapSelected)
    }

    /**
     * Creates a drawable that represents the state with given styles.
     */
    private fun createStateDrawable(
            stateText: String,
            @Dimension textWidth: Int,
            @Dimension textHeight: Int,
            @ColorInt backgroundColor: Int,
            shadow: Boolean,
            @ColorInt textColor: Int,
            @Dimension textSize: Int,
            textTypeface: Typeface?
    ): BitmapDrawable {
        // Create text view
        val stateTV = TextView(context).apply {
            width = textWidth
            height = textHeight - shadowBottomOverflowPx - shadowTopOverflowPx
            gravity = Gravity.CENTER
            text = stateText
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            setTextColor(textColor)
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            textTypeface?.run { typeface = this }
            if (shadow) {
                background = (this@MultiStateSwitch.background.constantState!!.newDrawable().mutate() as GradientDrawable).apply {
                    setColor(backgroundColor)
                }
            }
            isDrawingCacheEnabled = true
            // Draw it in cache
            measure(MeasureSpec.makeMeasureSpec(0 /* any */, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0 /* any */, MeasureSpec.UNSPECIFIED))
            layout(0, 0, measuredWidth, measuredHeight)
        }
        // Convert text view to bitmap
        val stateBm = createBitmapFromView(stateTV)
        // Create shadow
        val shadowBm = createShadow(stateBm, shadowBottomOverflowPx / 2)
        return BitmapDrawable(resources, combineBitmaps(shadowBm, if (shadow) 50 else 0, stateBm, shadowTopOverflowPx.toFloat()))
    }

    /**
     * Creates a bitmap from a view.
     */
    private fun createBitmapFromView(view: View): Bitmap {
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        view.background?.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }

    /**
     * Creates a bitmap representing the shadow of given bitmap.
     */
    private fun createShadow(bitmap: Bitmap, blurRadius: Int): Bitmap {
        val shadow = Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)
        val shadowPaint = Paint().apply {
            maskFilter = BlurMaskFilter(blurRadius.toFloat(), BlurMaskFilter.Blur.NORMAL)
        }
        val offsetXY = IntArray(2)
        return shadow.extractAlpha(shadowPaint, offsetXY)
    }

    /**
     * Combines two bitmaps into one.
     */
    private fun combineBitmaps(shadow: Bitmap, shadowAlpha: Int, foreground: Bitmap, topOverflowPx: Float): Bitmap {
        val shadowWidth = shadow.width
        shadowHeight = shadow.height
        val fgWidth = foreground.width
        val bgPaint = Paint()
        bgPaint.alpha = shadowAlpha // 20% opacity
        val result = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        // Draw background
        canvas.drawBitmap(shadow, 0f, 0f, bgPaint)
        // Draw foreground centered horizontally in the background and with given top margin
        shadowStartEndOverflowPx = (shadowWidth - fgWidth) / 2
        canvas.drawBitmap(foreground, shadowStartEndOverflowPx.toFloat(), topOverflowPx, null)
        canvas.save()
        canvas.restore()
        return result
    }

    /**
     * Selects state in given index.
     *
     * @param index                index of the state to select.
     * @param notifyStateListeners if true all the listeners will be notified about the new selected state.
     */
    @JvmOverloads
    fun selectState(index: Int, notifyStateListeners: Boolean = true) {
        if (!initialized) {
            currentStateIndex = index
            return
        }
        val validIndex = when {
            index < 0 -> 0
            index >= states.size -> states.size
            else -> index
        }
        setCurrentState(validIndex, notifyStateListeners)
        invalidate()
    }

    /**
     * Sets current state and notifies the listeners.
     */
    private fun setCurrentState(
            currentStateIndex: Int,
            notifyStateListeners: Boolean
    ) {
        if (notifyStateListeners) {
            notifyListeners(currentStateIndex)
        }
        this.currentStateIndex = currentStateIndex
        currentStateCenter.x = statesCenters[currentStateIndex].x
    }

    /**
     * Notifies listeners of the new position.
     */
    private fun notifyListeners(currentStateIndex: Int = this.currentStateIndex) {
        stateListeners.forEach { listener ->
            listener.onStateSelected(currentStateIndex, states[currentStateIndex])
        }
    }

    /**
     * Calculate the bounds of the view and the centers of the states.
     */
    private fun calculateBounds() {
        statesCenters.clear()
        // Calculate background bounds
        val backgroundBounds = Rect().apply {
            left = drawingArea.left + shadowStartEndOverflowPx
            top = drawingArea.top + shadowTopOverflowPx
            right = drawingArea.right - shadowStartEndOverflowPx
            bottom = drawingArea.bottom - shadowBottomOverflowPx
        }
        background.bounds = backgroundBounds
        // Calculate states centers
        currentStateCenter.y = height / 2
        val stateWidth = backgroundBounds.width() / getNumberStates()
        for (i in 1..getNumberStates()) {
            statesCenters.add(Point(backgroundBounds.left + stateWidth * i - stateWidth / 2, currentStateCenter.y))
        }
        currentStateCenter.x = statesCenters[currentStateIndex].x
    }

    /**
     * Determines the center of each state.
     */
    private fun determineCenterPositions() {
        // Determine current item
        var spaceBetween: Int
        var newCurrentItemIndex = 0
        var minSpace = Int.MAX_VALUE
        for (i in 0 until getNumberStates()) {
            spaceBetween = abs(statesCenters[i].x - currentStateCenter.x)
            if (minSpace > spaceBetween) {
                newCurrentItemIndex = i
                minSpace = spaceBetween
            }
        }
        this.currentStateIndex = newCurrentItemIndex
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled || states.isEmpty()) return false
        val rawX = event.x
        val normalizedX = getNormalizedX(event)
        currentStateCenter.x = when {
            // Before first item -> cast to start of first item
            normalizedX < statesCenters.first().x -> statesCenters.first().x
            // After last item -> cast to end of last item
            normalizedX > statesCenters.last().x -> statesCenters.last().x
            // Else just follow finger
            else -> normalizedX
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchDownX = rawX
                // If we're inside a vertical scrolling container, we should start dragging in ACTION_MOVE
                if (!isInVerticalScrollingContainer()) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    requestFocus()
                    stateIsPressed = true
                    determineCenterPositions()
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!stateIsPressed) { // Check if we're trying to scroll vertically instead of dragging this Slider
                    if (isInVerticalScrollingContainer() && abs(rawX - touchDownX) < scaledTouchSlop) return false
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                stateIsPressed = true
                determineCenterPositions()
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (!stateIsPressed) { // Single touch and we are inside an scrolling container
                    determineCenterPositions()
                }
                notifyListeners()
                stateIsPressed = false
                currentStateCenter.x = statesCenters[currentStateIndex].x // Snap to selected state
                invalidate()
            }
        }
        isPressed = stateIsPressed
        return true
    }

    private fun getNormalizedX(event: MotionEvent): Int {
        return event.x.toInt().coerceAtLeast(stateRadius.x).coerceAtMost(width - stateRadius.y)
    }

    /**
     * If this returns true, we can't start dragging the Slider immediately when we receive a MotionEvent.ACTION_DOWN.
     * Instead, we must wait for a MotionEvent.ACTION_MOVE.
     * @return true if any of this View's parents is a scrolling View and can scroll vertically.
     */
    private fun isInVerticalScrollingContainer(): Boolean {
        var p = parent
        while (p is ViewGroup) {
            val parent = p
            val canScrollVertically = parent.canScrollVertically(1) ||
                    parent.canScrollVertically(-1) ||
                    (parent is NestedScrollView && parent.maxScrollAmount > 0)
            if (canScrollVertically && parent.shouldDelayChildPressedState()) return true
            p = p.getParent()
        }
        return false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (getNumberStates() == 0 || !hasDrawingArea()) return
        background.draw(canvas)
        // Paint normal states
        for (i in 0 until getNumberStates()) {
            val normalStateDrawable = statesSelectors[i].normal
            drawState(canvas, normalStateDrawable, statesCenters[i])
        }
        // Paint selected state
        val selectedStateDrawable = statesSelectors[currentStateIndex].selected
        drawState(canvas, selectedStateDrawable, currentStateCenter)
    }

    /**
     * Draws a state on the canvas.
     */
    private fun drawState(canvas: Canvas, stateDrawable: Drawable, stateCenter: Point) {
        stateDrawable.setBounds(
                stateCenter.x - stateRadius.x - shadowStartEndOverflowPx,
                stateCenter.y - stateRadius.y,
                stateCenter.x + stateRadius.x + shadowStartEndOverflowPx, shadowHeight)
        stateDrawable.draw(canvas)
    }

    /**
     * Returns number of states of the switch.
     */
    fun getNumberStates(): Int = states.size

    /**
     * Sets the max number of states. If you try to add a new state but the number of states is
     * already maxNumberStates the state will be ignored. By default is -1 which means that there
     * is no restriction.
     * This parameter is also used to determine how many states to show in the editor preview. If
     * it is set to no limit, 3 will be rendered by default, if not the number of states drawn will
     * match maxNumberStates.
     */
    fun setMaxNumberStates(maxNumberStates: Int) {
        require(maxNumberStates != 0) { "Max number of states cannot be zero!" }
        this.maxNumberStates = maxNumberStates
    }

    /**
     * Returns max number of states. By default is -1 which means that there is no restriction.
     */
    fun getMaxNumberStates(): Int = maxNumberStates

    /**
     * Checks whether there is a limit in the number of states or not.
     */
    fun hasMaxNumberStates(): Boolean = getMaxNumberStates() > 0

    /**
     * Sets typeface.
     */
    fun setTextTypeface(textTypeface: Typeface) {
        this.textTypeface = textTypeface
    }

    /**
     * Adds a state listener to receive state selection changes.
     */
    fun addStateListener(stateListener: StateListener) {
        stateListeners.add(stateListener)
    }
}