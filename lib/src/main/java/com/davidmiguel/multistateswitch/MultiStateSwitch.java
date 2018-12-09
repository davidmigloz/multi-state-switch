package com.davidmiguel.multistateswitch;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class MultiStateSwitch extends View {

    // Num of states used in editor preview
    private static final int DEFAULT_NUM_STATES = 3;
    // Overflow of the shadow
    private static final int SHADOW_TOP_OVERFLOW_DP = 2;
    private static final int SHADOW_BOTTOM_OVERFLOW_DP = 10;

    // Styleable properties
    @ColorInt
    private int backgroundColor;
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
    private boolean disabledStateEnabled;
    private int disableStateIndex;
    @ColorInt
    private int disabledBackgroundColor;
    @ColorInt
    private int disabledTextColor;
    @Dimension
    private int disabledTextSize;

    private final Rect drawingArea = new Rect();

    private GradientDrawable background;
    private int shadowHeight;
    private int shadowTopOverflowPx;
    private int shadowBottomOverflowPx;
    private int shadowStartEndOverflowPx;

    private List<State> states;
    private SparseArray<StateStyle> statesStyles;
    private List<StateSelector> statesSelectors;
    private List<Point> statesCenters;
    private Point stateSize = new Point();
    private Point stateRadius = new Point();

    private boolean initialized = false;
    private int currentStateIndex;
    private Point currentStateCenter = new Point();

    private List<StateListener> stateListeners;

    public MultiStateSwitch(Context context) {
        super(context);
        init(context, null, R.attr.multistateswitch_MultiStateSwitchStyle, R.style.multistateswitch_MultiStateSwitch);
    }

    public MultiStateSwitch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.multistateswitch_MultiStateSwitchStyle, R.style.multistateswitch_MultiStateSwitch);
    }

    public MultiStateSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, R.style.multistateswitch_MultiStateSwitch);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MultiStateSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Initialises the view.
     */
    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        processAttributes(context, attrs, defStyleAttr, defStyleRes);
        configView(context);
        configEditMode();
    }

    /**
     * Reads the xml attributes and configures the view based on them.
     */
    private void processAttributes(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiStateSwitch, defStyleAttr, defStyleRes);
        try {
            backgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_background_color, 0);
            textColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_text_color, 0);
            textSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_text_size, 0);
            currentStateIndex = a.getInt(R.styleable.MultiStateSwitch_multistateswitch_selected_state_index, 0);
            selectedBackgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_selected_background_color, 0);
            selectedTextColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_selected_text_color, 0);
            selectedTextSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_selected_text_size, 0);
            disabledStateEnabled = a.getBoolean(R.styleable.MultiStateSwitch_multistateswitch_disabled_state_enabled, false);
            disableStateIndex = a.getInt(R.styleable.MultiStateSwitch_multistateswitch_disabled_state_index, 0);
            disabledBackgroundColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_disabled_background_color, 0);
            disabledTextColor = a.getColor(R.styleable.MultiStateSwitch_multistateswitch_disabled_text_color, 0);
            disabledTextSize = a.getDimensionPixelSize(R.styleable.MultiStateSwitch_multistateswitch_disabled_text_size, 0);
        } finally {
            a.recycle();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void configView(@NonNull Context context) {
        // Measures
        shadowTopOverflowPx = (int) Utils.dpToPx(context, SHADOW_TOP_OVERFLOW_DP);
        shadowBottomOverflowPx = (int) Utils.dpToPx(context, SHADOW_BOTTOM_OVERFLOW_DP);

        // Background
        background = (GradientDrawable) ContextCompat.getDrawable(getContext(), R.drawable.multistateswitch_background).mutate();
        background.setColor(backgroundColor);
    }

    /**
     * Configures the mock preview displayed in the layout editor.
     */
    private void configEditMode() {
        if (!isInEditMode()) {
            return;
        }
        addState(new State("ONE"));
        addState(new State("TWO"));
        addState(new State("THREE"));
    }

    /**
     * Adds state to the switch.
     * The text will be used for normal, selected and disabled states.
     */
    public void addState(@NonNull String stateText) {
        addState(new State(stateText), null);
    }

    /**
     * Adds state to the switch.
     */
    public void addState(@NonNull State state) {
        addState(state, null);
    }

    /**
     * Adds state to the switch and the displaying style.
     */
    public void addState(@NonNull State state, @Nullable StateStyle stateStyle) {
        Objects.requireNonNull(state);
        if (states == null) {
            createDataStructures(DEFAULT_NUM_STATES);
        }
        states.add(state);
        if (stateStyle != null) {
            statesStyles.put(states.size() - 1, stateStyle);
        }
    }

    /**
     * Adds states to the switch.
     */
    public void addStates(@NonNull List<State> states) {
        addStates(states, null);
    }

    /**
     * Adds states to the switch and the displaying styles.
     * If you provide styles, you have to provide them for every state.
     */
    public void addStates(@NonNull List<State> states, @Nullable List<StateStyle> stateStyles) {
        Objects.requireNonNull(states);
        if (stateStyles != null && states.size() != stateStyles.size()) {
            throw new IllegalArgumentException("Number of states and styles must be the same");
        }
        if (this.states == null) {
            createDataStructures(states.size());
        }
        for (int i = 0; i < states.size(); i++) {
            this.states.add(states.get(i));
            if (stateStyles != null) {
                statesStyles.put(states.size() - 1, stateStyles.get(i));
            }
        }
    }

    /**
     * Replaces state.
     * The text will be used for normal, selected and disabled states.
     */
    public void replaceState(int stateIndex, @NonNull String stateText) {
        replaceState(stateIndex, new State(stateText), null);
    }

    /**
     * Replaces state.
     */
    public void replaceState(int stateIndex, @NonNull State state) {
        replaceState(stateIndex, state, null);
    }

    /**
     * Replaces state.
     */
    public void replaceState(int stateIndex, @NonNull State state, @Nullable StateStyle stateStyle) {
        Objects.requireNonNull(state);
        if (stateIndex >= getNumberStates()) {
            throw new IllegalArgumentException("State index doesn't exist");
        }
        states.set(stateIndex, state);
        statesStyles.put(stateIndex, stateStyle);
        statesSelectors.set(stateIndex, createStateSelector(stateIndex));
        invalidate();
    }

    /**
     * Creates data structures to store the states and related info.
     */
    private void createDataStructures(int size) {
        states = new ArrayList<>(size);
        statesStyles = new SparseArray<>(size);
        statesSelectors = new ArrayList<>(size);
        statesCenters = new ArrayList<>(size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // Called when the view is first assigned a size, and again if the size changes for any reason
        // All calculations related to positions, dimensions, and any other values must be done here (not in onDraw)
        super.onSizeChanged(w, h, oldw, oldh);
        populateView();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        populateView();
    }

    /**
     * Prepares all data needed to draw the view.
     */
    private void populateView() {
        if (states == null || states.isEmpty() || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        calculateDrawingSizes();
        populateStates();
        calculateBounds();
        determineCenterPositions(false);
        initialized = true;
    }

    /**
     * Calculates the area where we can draw (essentially the bounding rect minus any padding).
     * The area is represented in a rectangle.
     */
    private void calculateDrawingSizes() {
        drawingArea.set(getPaddingStart(), getPaddingTop(), getWidth() - getPaddingEnd(),
                getHeight() - getPaddingBottom());
        setStateHeight(drawingArea.height());
        setStateWidth((drawingArea.width() / states.size()));
    }

    /**
     * Sets the height available for a state.
     */
    private void setStateHeight(int height) {
        stateSize.y = height;
        stateRadius.y = height / 2;
    }

    /**
     * Sets the width available for a state.
     */
    private void setStateWidth(int width) {
        stateSize.x = width;
        stateRadius.x = width / 2;
    }

    /**
     * Populates the states that have to be drawn.
     */
    private void populateStates() {
        for (int i = 0; i < states.size(); i++) {
            statesSelectors.add(createStateSelector(i));
        }
    }

    /**
     * Creates the two possible representations of the state (normal and selected, or disabled in
     * case disabled state is supported).
     */
    private StateSelector createStateSelector(int stateIndex) {
        State state = states.get(stateIndex);
        StateStyle stateStyle = statesStyles.get(stateIndex);
        if (stateStyle == null) {
            stateStyle = new StateStyle.Builder().build();
        }
        // Create the drawable representing the state (normal, selected or disabled)
        BitmapDrawable bitmapNormal = createStateDrawable(state.getText(), stateSize.x, stateSize.y, 0, false,
                stateStyle.getTextColor(textColor), stateStyle.getTextSize(textSize), stateStyle.getTextTypeface(textTypeface));
        BitmapDrawable bitmapSelected = disabledStateEnabled && stateIndex == disableStateIndex ?
                createStateDrawable(state.getDisabledText(), stateSize.x, stateSize.y, stateStyle.getDisabledBackgroundColor(disabledBackgroundColor), true,
                        stateStyle.getDisabledTextColor(disabledTextColor), stateStyle.getDisabledTextSize(disabledTextSize), stateStyle.getDisabledTextTypeface(textTypeface)) :
                createStateDrawable(state.getSelectedText(), stateSize.x, stateSize.y, stateStyle.getSelectedBackgroundColor(selectedBackgroundColor), true,
                        stateStyle.getSelectedTextColor(selectedTextColor), stateStyle.getSelectedTextSize(selectedTextSize), stateStyle.getSelectedTextTypeface(textTypeface));
        return new StateSelector(bitmapNormal, bitmapSelected);
    }

    /**
     * Creates a drawable that represents the state with given styles.
     */
    @SuppressWarnings("ConstantConditions")
    private BitmapDrawable createStateDrawable(@NonNull String text,
                                               @Dimension int width, @Dimension int height,
                                               @ColorInt int backgroundColor, boolean shadow,
                                               @ColorInt int textColor, @Dimension int textSize,
                                               @Nullable Typeface textTypeface) {
        // Create text view
        TextView stateTV = new TextView(getContext());
        stateTV.setWidth(width);
        stateTV.setHeight((height - shadowBottomOverflowPx - shadowTopOverflowPx));
        stateTV.setGravity(Gravity.CENTER);
        stateTV.setText(text);
        stateTV.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        stateTV.setTextColor(textColor);
        stateTV.setMaxLines(1);
        stateTV.setEllipsize(TextUtils.TruncateAt.END);
        if (textTypeface != null) {
            stateTV.setTypeface(textTypeface);
        }
        if (shadow) {
            GradientDrawable textViewBg = (GradientDrawable) background.getConstantState().newDrawable().mutate();
            textViewBg.setColor(backgroundColor);
            stateTV.setBackground(textViewBg);
        }
        stateTV.setDrawingCacheEnabled(true);
        // Draw it in cache
        stateTV.measure(MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0 /* any */, View.MeasureSpec.UNSPECIFIED));
        stateTV.layout(0, 0, stateTV.getMeasuredWidth(), stateTV.getMeasuredHeight());
        stateTV.buildDrawingCache(true);
        // Convert text view to bitmap
        Bitmap stateBm = Bitmap.createBitmap(stateTV.getDrawingCache());
        stateTV.setDrawingCacheEnabled(false);
        // Create shadow
        Bitmap shadowBm = createShadow(stateBm, shadowBottomOverflowPx / 2);
        return new BitmapDrawable(getResources(), combineBitmaps(shadowBm, shadow ? 50 : 0, stateBm, shadowTopOverflowPx));
    }

    /**
     * Creates a bitmap representing the shadow of given bitmap.
     */
    private Bitmap createShadow(@NonNull Bitmap bitmap, int blurRadius) {
        Bitmap shadow = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL));
        int[] offsetXY = new int[2];
        return shadow.extractAlpha(shadowPaint, offsetXY);
    }

    /**
     * Combines two bitmaps into one.
     */
    private Bitmap combineBitmaps(@NonNull Bitmap shadow, int shadowAlpha, @NonNull Bitmap foreground, float topOverflowPx) {
        int shadowWidth = shadow.getWidth();
        shadowHeight = shadow.getHeight();
        int fgWidth = foreground.getWidth();
        Paint bgPaint = new Paint();
        bgPaint.setAlpha(shadowAlpha); // 20% opacity
        Bitmap result = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        // Draw background
        canvas.drawBitmap(shadow, 0, 0, bgPaint);
        // Draw foreground centered horizontally in the background and with given top margin
        shadowStartEndOverflowPx = (shadowWidth - fgWidth) / 2;
        canvas.drawBitmap(foreground, shadowStartEndOverflowPx, topOverflowPx, null);
        canvas.save();
        canvas.restore();
        return result;
    }

    /**
     * Selects state in given index.
     * The listeners will be notified about the new selected state.
     *
     * @param index index of the state to select.
     */
    public void selectState(int index) {
        selectState(index, true);
    }

    /**
     * Selects state in given index.
     *
     * @param index                index of the state to select.
     * @param notifyStateListeners if true all the listeners will be notified about the new selected state.
     */
    public void selectState(int index, boolean notifyStateListeners) {
        if(!initialized) {
            currentStateIndex = index;
            return;
        }
        int validIndex;
        if (index < 0) {
            validIndex = 0;
        } else if (index >= states.size()) {
            validIndex = states.size();
        } else {
            validIndex = index;
        }
        setCurrentState(validIndex, true, notifyStateListeners);
        invalidate();
    }

    /**
     * Sets current state and notifies the listeners.
     */
    private void setCurrentState(int currentStateIndex, boolean overwriteCurrentPosition, boolean notifyStateListeners) {
        if (this.currentStateIndex == currentStateIndex) {
            return;
        }
        if (notifyStateListeners && stateListeners != null) {
            for (StateListener listener : stateListeners) {
                listener.onStateSelected(currentStateIndex, states.get(currentStateIndex));
            }
        }
        this.currentStateIndex = currentStateIndex;
        if (overwriteCurrentPosition) {
            currentStateCenter.x = statesCenters.get(currentStateIndex).x;
        }
    }

    /**
     * Calculate the bounds of the view and the centers of the states.
     */
    private void calculateBounds() {
        // Calculate background bounds
        Rect backgroundBounds = new Rect();
        backgroundBounds.left = drawingArea.left + shadowStartEndOverflowPx;
        backgroundBounds.top = drawingArea.top + shadowTopOverflowPx;
        backgroundBounds.right = drawingArea.right - shadowStartEndOverflowPx;
        backgroundBounds.bottom = drawingArea.bottom - shadowBottomOverflowPx;
        background.setBounds(backgroundBounds);
        // Calculate states centers
        currentStateCenter.y = getHeight() / 2;
        int size = getNumberStates();
        int stateWidth = backgroundBounds.width() / size;
        for (int i = 1; i <= size; i++) {
            statesCenters.add(new Point(backgroundBounds.left + stateWidth * i - stateWidth / 2, currentStateCenter.y));
        }
        currentStateCenter.x = statesCenters.get(currentStateIndex).x;
    }

    /**
     * Determines the center of each state.
     */
    private void determineCenterPositions(boolean notifyStateListeners) {
        // Determine current item
        int spaceBetween;
        int newCurrentItemIndex = 0;
        int minSpace = Integer.MAX_VALUE;
        for (int i = 0; i < getNumberStates(); i++) {
            spaceBetween = Math.abs(statesCenters.get(i).x - currentStateCenter.x);
            if (minSpace > spaceBetween) {
                newCurrentItemIndex = i;
                minSpace = spaceBetween;
            }
        }
        setCurrentState(newCurrentItemIndex, false, notifyStateListeners);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isEnabled()) {
            int normalizedX = getNormalizedX(event);
            if (normalizedX < statesCenters.get(0).x) {  // not below x of lowest item
                currentStateCenter.x = statesCenters.get(0).x;
            } else if (normalizedX > statesCenters.get(getNumberStates() - 1).x) { // not beyond x of highest item
                currentStateCenter.x = statesCenters.get(getNumberStates() - 1).x;
            } else { // else just follow finger
                currentStateCenter.x = normalizedX;
            }
            int action = event.getAction();
            if (action != MotionEvent.ACTION_UP) { // User finishes swiping
                determineCenterPositions(true);
            } else {
                currentStateCenter.x = statesCenters.get(currentStateIndex).x;
            }
            invalidate();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_UP) {
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private int getNormalizedX(@NonNull MotionEvent event) {
        return Math.min(Math.max((int) event.getX(), stateRadius.x), getWidth() - stateRadius.y);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (states == null || states.isEmpty()) {
            return;
        }
        background.draw(canvas);
        // Paint normal states
        for (int i = 0; i < getNumberStates(); i++) {
            Drawable normalStateDrawable = statesSelectors.get(i).getNormal();
            drawState(canvas, normalStateDrawable, statesCenters.get(i));
        }
        // Paint selected state
        Drawable selectedStateDrawable = statesSelectors.get(currentStateIndex).getSelected();
        drawState(canvas, selectedStateDrawable, currentStateCenter);
    }

    /**
     * Draws a state on the canvas.
     */
    private void drawState(Canvas canvas, Drawable stateDrawable, Point stateCenter) {
        stateDrawable.setBounds(
                stateCenter.x - stateRadius.x - shadowStartEndOverflowPx,
                stateCenter.y - stateRadius.y,
                stateCenter.x + stateRadius.x + shadowStartEndOverflowPx, shadowHeight);
        stateDrawable.draw(canvas);
    }


    /**
     * Returns number of states of the switch.
     */
    public int getNumberStates() {
        return states != null ? states.size() : 0;
    }

    /**
     * Sets typeface.
     */
    public void setTextTypeface(@NonNull Typeface textTypeface) {
        this.textTypeface = textTypeface;
    }

    /**
     * Adds a state listener to receive state selection changes.
     */
    public void addStateListener(@NonNull StateListener stateListener) {
        Objects.requireNonNull(stateListener);
        if (stateListeners == null) {
            stateListeners = new ArrayList<>(1);
        }
        stateListeners.add(stateListener);
    }
}
