package talaviassaf.swappit.models;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import talaviassaf.swappit.R;

@SuppressWarnings("EmptyMethod, unused")

public class CircularSeekBar extends View {

    protected static final float DEFAULT_CIRCLE_X_RADIUS = 30f, DEFAULT_CIRCLE_Y_RADIUS = 30f, DEFAULT_POINTER_RADIUS = 7f,
            DEFAULT_POINTER_HALO_WIDTH = 6f, DEFAULT_POINTER_HALO_BORDER_WIDTH = 2f,
            DEFAULT_CIRCLE_STROKE_WIDTH = 5f, DEFAULT_START_ANGLE = 270f, DEFAULT_END_ANGLE = 270f;

    protected static final int DEFAULT_MAX = 100, DEFAULT_PROGRESS = 0, DEFAULT_CIRCLE_COLOR = Color.DKGRAY,
            DEFAULT_CIRCLE_PROGRESS_COLOR = Color.argb(235, 74, 138, 255), DEFAULT_POINTER_COLOR = Color.argb(235, 74, 138, 255),
            DEFAULT_POINTER_HALO_COLOR = Color.argb(135, 74, 138, 255), DEFAULT_POINTER_HALO_COLOR_ONTOUCH = Color.argb(135, 74, 138, 255),
            DEFAULT_CIRCLE_FILL_COLOR = Color.TRANSPARENT, DEFAULT_POINTER_ALPHA = 135, DEFAULT_POINTER_ALPHA_ONTOUCH = 100;

    protected static final boolean DEFAULT_USE_CUSTOM_RADII = false, DEFAULT_MAINTAIN_EQUAL_CIRCLE = true,
            DEFAULT_MOVE_OUTSIDE_CIRCLE = false, DEFAULT_LOCK_ENABLED = true;

    protected final float DPTOPX_SCALE = getResources().getDisplayMetrics().density, MIN_TOUCH_TARGET_DP = 48;
    protected final RectF mCircleRectF = new RectF();
    protected final float[] mPointerPositionXY = new float[2];
    protected Paint mCirclePaint, mCircleFillPaint, mCircleProgressPaint, mCircleProgressGlowPaint,
            mPointerPaint, mPointerHaloPaint, mPointerHaloBorderPaint, line;
    protected float mCircleStrokeWidth, mCircleXRadius, mCircleYRadius, mPointerRadius, mPointerHaloWidth,
            mPointerHaloBorderWidth, mStartAngle, mEndAngle, mTotalCircleDegrees, mProgressDegrees,
            cwDistanceFromStart, ccwDistanceFromStart, cwDistanceFromEnd, ccwDistanceFromEnd, lastCWDistanceFromStart,
            cwDistanceFromPointer, ccwDistanceFromPointer, mCircleWidth, mCircleHeight, mPointerPosition;
    protected int mPointerColor = DEFAULT_POINTER_COLOR, mPointerHaloColor = DEFAULT_POINTER_HALO_COLOR,
            mPointerHaloColorOnTouch = DEFAULT_POINTER_HALO_COLOR_ONTOUCH, mCircleColor = DEFAULT_CIRCLE_COLOR,
            mCircleFillColor = DEFAULT_CIRCLE_FILL_COLOR, mCircleProgressColor = DEFAULT_CIRCLE_PROGRESS_COLOR,
            mPointerAlpha = DEFAULT_POINTER_ALPHA, mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH, mMax, mProgress;
    protected Path mCirclePath, mCircleProgressPath;
    protected boolean mCustomRadii, mMaintainEqualCircle, mMoveOutsideCircle, lockEnabled = true,
            lockAtStart = true, lockAtEnd = false, mUserIsMovingPointer = false, mIsMovingCW, isTouchEnabled = true;
    protected OnCircularSeekBarChangeListener mOnCircularSeekBarChangeListener;

    public CircularSeekBar(Context context) {

        super(context);

        init(null, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs) {

        super(context, attrs);

        init(attrs, 0);
    }

    public CircularSeekBar(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        init(attrs, defStyle);
    }

    protected void init(AttributeSet attrs, int defStyle) {

        final TypedArray attrArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircularSeekBar, defStyle, 0);

        initAttributes(attrArray);

        attrArray.recycle();

        initPaints();
    }

    protected void initAttributes(TypedArray attrArray) {

        mCircleXRadius = attrArray.getDimension(R.styleable.CircularSeekBar_circle_x_radius, DEFAULT_CIRCLE_X_RADIUS * DPTOPX_SCALE);
        mCircleYRadius = attrArray.getDimension(R.styleable.CircularSeekBar_circle_y_radius, DEFAULT_CIRCLE_Y_RADIUS * DPTOPX_SCALE);
        mPointerRadius = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_radius, DEFAULT_POINTER_RADIUS * DPTOPX_SCALE);
        mPointerHaloWidth = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_halo_width, DEFAULT_POINTER_HALO_WIDTH * DPTOPX_SCALE);
        mPointerHaloBorderWidth = attrArray.getDimension(R.styleable.CircularSeekBar_pointer_halo_border_width, DEFAULT_POINTER_HALO_BORDER_WIDTH * DPTOPX_SCALE);
        mCircleStrokeWidth = attrArray.getDimension(R.styleable.CircularSeekBar_circle_stroke_width, DEFAULT_CIRCLE_STROKE_WIDTH * DPTOPX_SCALE);

        mPointerColor = attrArray.getColor(R.styleable.CircularSeekBar_pointer_color, DEFAULT_POINTER_COLOR);
        mPointerHaloColor = attrArray.getColor(R.styleable.CircularSeekBar_pointer_halo_color, DEFAULT_POINTER_HALO_COLOR);
        mPointerHaloColorOnTouch = attrArray.getColor(R.styleable.CircularSeekBar_pointer_halo_color_on_touch, DEFAULT_POINTER_HALO_COLOR_ONTOUCH);
        mCircleColor = attrArray.getColor(R.styleable.CircularSeekBar_circle_color, DEFAULT_CIRCLE_COLOR);
        mCircleProgressColor = attrArray.getColor(R.styleable.CircularSeekBar_circle_progress_color, DEFAULT_CIRCLE_PROGRESS_COLOR);
        mCircleFillColor = attrArray.getColor(R.styleable.CircularSeekBar_circle_fill, DEFAULT_CIRCLE_FILL_COLOR);

        mPointerAlpha = Color.alpha(mPointerHaloColor);

        mPointerAlphaOnTouch = attrArray.getInt(R.styleable.CircularSeekBar_pointer_alpha_on_touch, DEFAULT_POINTER_ALPHA_ONTOUCH);

        if (mPointerAlphaOnTouch > 255 || mPointerAlphaOnTouch < 0)
            mPointerAlphaOnTouch = DEFAULT_POINTER_ALPHA_ONTOUCH;

        mMax = attrArray.getInt(R.styleable.CircularSeekBar_max, DEFAULT_MAX);
        mProgress = attrArray.getInt(R.styleable.CircularSeekBar_progress, DEFAULT_PROGRESS);
        mCustomRadii = attrArray.getBoolean(R.styleable.CircularSeekBar_use_custom_radii, DEFAULT_USE_CUSTOM_RADII);
        mMaintainEqualCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_maintain_equal_circle, DEFAULT_MAINTAIN_EQUAL_CIRCLE);
        mMoveOutsideCircle = attrArray.getBoolean(R.styleable.CircularSeekBar_move_outside_circle, DEFAULT_MOVE_OUTSIDE_CIRCLE);
        lockEnabled = attrArray.getBoolean(R.styleable.CircularSeekBar_lock_enabled, DEFAULT_LOCK_ENABLED);

        // Modulo 360 right now to avoid constant conversion
        mStartAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_start_angle), DEFAULT_START_ANGLE) % 360f)) % 360f);
        mEndAngle = ((360f + (attrArray.getFloat((R.styleable.CircularSeekBar_end_angle), DEFAULT_END_ANGLE) % 360f)) % 360f);

        if (mStartAngle == mEndAngle)
            //mStartAngle = mStartAngle + 1f;
            mEndAngle = mEndAngle - .1f;
    }

    protected void initPaints() {

        mCirclePaint = new Paint();

        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setDither(true);
        mCirclePaint.setColor(mCircleColor);
        mCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeJoin(Paint.Join.ROUND);
        mCirclePaint.setStrokeCap(Paint.Cap.ROUND);

        mCircleFillPaint = new Paint();

        mCircleFillPaint.setAntiAlias(true);
        mCircleFillPaint.setDither(true);
        mCircleFillPaint.setColor(mCircleFillColor);
        mCircleFillPaint.setStyle(Paint.Style.FILL);

        mCircleProgressPaint = new Paint();

        mCircleProgressPaint.setAntiAlias(true);
        mCircleProgressPaint.setDither(true);
        mCircleProgressPaint.setColor(mCircleProgressColor);
        mCircleProgressPaint.setStrokeWidth(mCircleStrokeWidth);
        mCircleProgressPaint.setStyle(Paint.Style.STROKE);
        mCircleProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mCircleProgressPaint.setStrokeCap(Paint.Cap.ROUND);

        mCircleProgressGlowPaint = new Paint();
        mCircleProgressGlowPaint.set(mCircleProgressPaint);
        mCircleProgressGlowPaint.setMaskFilter(new BlurMaskFilter((5f * DPTOPX_SCALE), BlurMaskFilter.Blur.NORMAL));

        mPointerPaint = new Paint();
        mPointerPaint.setAntiAlias(true);
        mPointerPaint.setDither(true);
        mPointerPaint.setStyle(Paint.Style.FILL);
        mPointerPaint.setColor(mPointerColor);
        mPointerPaint.setStrokeWidth(mPointerRadius);

        mPointerHaloPaint = new Paint();
        mPointerHaloPaint.set(mPointerPaint);
        mPointerHaloPaint.setColor(mPointerHaloColor);
        mPointerHaloPaint.setAlpha(mPointerAlpha);
        mPointerHaloPaint.setStrokeWidth(mPointerRadius + mPointerHaloWidth);

        mPointerHaloBorderPaint = new Paint();
        mPointerHaloBorderPaint.set(mPointerPaint);
        mPointerHaloBorderPaint.setStrokeWidth(mPointerHaloBorderWidth);
        mPointerHaloBorderPaint.setStyle(Paint.Style.STROKE);

        line = new Paint();

        line.set(mCirclePaint);
    }

    protected void calculateTotalDegrees() {

        mTotalCircleDegrees = (360f - (mStartAngle - mEndAngle)) % 360f; // Length of the entire circle/arc

        if (mTotalCircleDegrees <= 0f)
            mTotalCircleDegrees = 360f;
    }

    protected void calculateProgressDegrees() {

        mProgressDegrees = mPointerPosition - mStartAngle; // Verified
        mProgressDegrees = (mProgressDegrees < 0 ? 360f + mProgressDegrees : mProgressDegrees); // Verified
    }

    protected void calculatePointerAngle() {

        float progressPercent = ((float) mProgress / (float) mMax);

        mPointerPosition = (progressPercent * mTotalCircleDegrees) + mStartAngle;
        mPointerPosition = mPointerPosition % 360f;
    }

    protected void calculatePointerXYPosition() {

        PathMeasure pm = new PathMeasure(mCircleProgressPath, false);

        boolean returnValue = pm.getPosTan(pm.getLength(), mPointerPositionXY, null);

        if (!returnValue) {

            pm = new PathMeasure(mCirclePath, false);

            //noinspection UnusedAssignment
            returnValue = pm.getPosTan(0, mPointerPositionXY, null);
        }
    }

    protected void initPaths() {

        mCirclePath = new Path();

        mCirclePath.addArc(mCircleRectF, mStartAngle, mTotalCircleDegrees);

        mCircleProgressPath = new Path();

        mCircleProgressPath.addArc(mCircleRectF, mStartAngle, mProgressDegrees);
    }

    protected void initRects() {

        mCircleRectF.set(-mCircleWidth, -mCircleHeight, mCircleWidth, mCircleHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.translate(this.getWidth() / 2, this.getHeight() / 2);

        canvas.drawPath(mCirclePath, mCirclePaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressGlowPaint);
        canvas.drawPath(mCircleProgressPath, mCircleProgressPaint);

        canvas.drawPath(mCirclePath, mCircleFillPaint);

        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius + mPointerHaloWidth, mPointerHaloPaint);
        canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1], mPointerRadius, mPointerPaint);

        if (mUserIsMovingPointer)
            canvas.drawCircle(mPointerPositionXY[0], mPointerPositionXY[1],
                    mPointerRadius + mPointerHaloWidth + (mPointerHaloBorderWidth / 2f), mPointerHaloBorderPaint);

        canvas.drawLine(77.5f, -105, 95, -130, line);
    }

    public int getProgress() {

        return Math.round((float) mMax * mProgressDegrees / mTotalCircleDegrees);
    }

    public void setProgress(int progress) {

        if (mProgress != progress) {

            mProgress = progress;

            if (mOnCircularSeekBarChangeListener != null)
                mOnCircularSeekBarChangeListener.onProgressChanged(this, progress, false);

            recalculateAll();
            invalidate();
        }
    }

    protected void setProgressBasedOnAngle(float angle) {

        mPointerPosition = angle;
        calculateProgressDegrees();
        mProgress = Math.round((float) mMax * mProgressDegrees / mTotalCircleDegrees);
    }

    protected void recalculateAll() {

        calculateTotalDegrees();
        calculatePointerAngle();
        calculateProgressDegrees();

        initRects();

        initPaths();

        calculatePointerXYPosition();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        if (mMaintainEqualCircle) {

            int min = Math.min(width, height);

            setMeasuredDimension(min, min);
        } else
            setMeasuredDimension(width, height);

        // Set the circle width and height based toggle1 the view for the moment
        mCircleHeight = (float) height / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
        mCircleWidth = (float) width / 2f - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);

        // If it is not set to use custom
        if (mCustomRadii) {

            // Check to make sure the custom radii are not out of the view. If they are, just use the view values
            if ((mCircleYRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleHeight)
                mCircleHeight = mCircleYRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);

            if ((mCircleXRadius - mCircleStrokeWidth - mPointerRadius - mPointerHaloBorderWidth) < mCircleWidth)
                mCircleWidth = mCircleXRadius - mCircleStrokeWidth - mPointerRadius - (mPointerHaloBorderWidth * 1.5f);
        }

        if (mMaintainEqualCircle) { // Applies regardless of how the values were determined

            float min = Math.min(mCircleHeight, mCircleWidth);
            mCircleHeight = min;
            mCircleWidth = min;
        }

        recalculateAll();
    }

    public boolean isLockEnabled() {
        return lockEnabled;
    }

    public void setLockEnabled(boolean lockEnabled) {
        this.lockEnabled = lockEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isTouchEnabled)
            return false;

        // Convert coordinates to our internal coordinate system
        float x = event.getX() - getWidth() / 2;
        float y = event.getY() - getHeight() / 2;

        // Get the distance from the center of the circle in terms of x and y
        float distanceX = mCircleRectF.centerX() - x;
        float distanceY = mCircleRectF.centerY() - y;

        // Get the distance from the center of the circle in terms of a radius
        float touchEventRadius = (float) Math.sqrt((Math.pow(distanceX, 2) + Math.pow(distanceY, 2)));

        float minimumTouchTarget = MIN_TOUCH_TARGET_DP * DPTOPX_SCALE; // Convert minimum touch target into px

        float additionalRadius; // Either uses the minimumTouchTarget size or larger if the ring/pointer is larger

        // If the width is less than the minimumTouchTarget, use the minimumTouchTarget Otherwise use the width
        additionalRadius = mCircleStrokeWidth < minimumTouchTarget ? minimumTouchTarget / 2 : mCircleStrokeWidth / 2;

        float outerRadius = Math.max(mCircleHeight, mCircleWidth) + additionalRadius; // Max outer radius of the circle, including the minimumTouchTarget or wheel width
        float innerRadius = Math.min(mCircleHeight, mCircleWidth) - additionalRadius; // Min inner radius of the circle, including the minimumTouchTarget or wheel width

        // If the pointer radius is less than the minimumTouchTarget, use the minimumTouchTarget Otherwise use the radius

        //noinspection UnusedAssignment
        additionalRadius = mPointerRadius < (minimumTouchTarget / 2) ? minimumTouchTarget / 2 : mPointerRadius;

        float touchAngle;

        touchAngle = (float) ((Math.atan2(y, x) / Math.PI * 180) % 360); // Verified
        touchAngle = (touchAngle < 0 ? 360 + touchAngle : touchAngle); // Verified

        cwDistanceFromStart = touchAngle - mStartAngle; // Verified
        cwDistanceFromStart = (cwDistanceFromStart < 0 ? 360f + cwDistanceFromStart : cwDistanceFromStart); // Verified
        ccwDistanceFromStart = 360f - cwDistanceFromStart; // Verified

        cwDistanceFromEnd = touchAngle - mEndAngle; // Verified
        cwDistanceFromEnd = (cwDistanceFromEnd < 0 ? 360f + cwDistanceFromEnd : cwDistanceFromEnd); // Verified
        ccwDistanceFromEnd = 360f - cwDistanceFromEnd; // Verified

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                // These are only used for ACTION_DOWN for handling if the pointer was the part that was touched
                float pointerRadiusDegrees = (float) ((mPointerRadius * 180) / (Math.PI * Math.max(mCircleHeight, mCircleWidth)));
                cwDistanceFromPointer = touchAngle - mPointerPosition;
                cwDistanceFromPointer = (cwDistanceFromPointer < 0 ? 360f + cwDistanceFromPointer : cwDistanceFromPointer);
                ccwDistanceFromPointer = 360f - cwDistanceFromPointer;
                // This is for if the first touch is toggle1 the actual pointer.
                if (((touchEventRadius >= innerRadius) && (touchEventRadius <= outerRadius)) && ((cwDistanceFromPointer <= pointerRadiusDegrees)
                        || (ccwDistanceFromPointer <= pointerRadiusDegrees))) {

                    setProgressBasedOnAngle(mPointerPosition);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    mIsMovingCW = true;
                    mPointerHaloPaint.setAlpha(mPointerAlphaOnTouch);
                    mPointerHaloPaint.setColor(mPointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();

                    if (mOnCircularSeekBarChangeListener != null)
                        mOnCircularSeekBarChangeListener.onStartTrackingTouch(this);

                    mUserIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;

                } else if (cwDistanceFromStart > mTotalCircleDegrees) { // If the user is touching outside of the start AND end
                    mUserIsMovingPointer = false;
                    return false;
                } else if ((touchEventRadius >= innerRadius) && (touchEventRadius <= outerRadius)) { // If the user is touching near the circle
                    setProgressBasedOnAngle(touchAngle);
                    lastCWDistanceFromStart = cwDistanceFromStart;
                    mIsMovingCW = true;
                    mPointerHaloPaint.setAlpha(mPointerAlphaOnTouch);
                    mPointerHaloPaint.setColor(mPointerHaloColorOnTouch);
                    recalculateAll();
                    invalidate();
                    if (mOnCircularSeekBarChangeListener != null) {
                        mOnCircularSeekBarChangeListener.onStartTrackingTouch(this);
                        mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                    }
                    mUserIsMovingPointer = true;
                    lockAtEnd = false;
                    lockAtStart = false;
                } else { // If the user is not touching near the circle
                    mUserIsMovingPointer = false;
                    return false;
                }

                performClick();
                break;

            case MotionEvent.ACTION_MOVE:

                if (mUserIsMovingPointer) {
                    if (lastCWDistanceFromStart < cwDistanceFromStart) {
                        if ((cwDistanceFromStart - lastCWDistanceFromStart) > 180f && !mIsMovingCW) {
                            lockAtStart = true;
                            lockAtEnd = false;
                        } else {
                            mIsMovingCW = true;
                        }
                    } else {
                        if ((lastCWDistanceFromStart - cwDistanceFromStart) > 180f && mIsMovingCW) {
                            lockAtEnd = true;
                            lockAtStart = false;
                        } else
                            mIsMovingCW = false;
                    }

                    if (lockAtStart && mIsMovingCW)
                        lockAtStart = false;

                    if (lockAtEnd && !mIsMovingCW)
                        lockAtEnd = false;

                    if (lockAtStart && (ccwDistanceFromStart > 90))
                        lockAtStart = false;

                    if (lockAtEnd && (cwDistanceFromEnd > 90))
                        lockAtEnd = false;

                    // Fix for passing the end of a semi-circle quickly
                    if (!lockAtEnd && cwDistanceFromStart > mTotalCircleDegrees && mIsMovingCW && lastCWDistanceFromStart < mTotalCircleDegrees)
                        lockAtEnd = true;

                    if (lockAtStart && lockEnabled) {

                        // Add a check if mProgress is already 0, in which case don't call the listener
                        mProgress = 0;
                        recalculateAll();
                        invalidate();

                        if (mOnCircularSeekBarChangeListener != null)
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);

                    } else if (lockAtEnd && lockEnabled) {

                        mProgress = mMax;
                        recalculateAll();
                        invalidate();

                        if (mOnCircularSeekBarChangeListener != null)
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);

                    } else if ((mMoveOutsideCircle) || (touchEventRadius <= outerRadius)) {

                        if (!(cwDistanceFromStart > mTotalCircleDegrees))
                            setProgressBasedOnAngle(touchAngle);

                        recalculateAll();
                        invalidate();

                        if (mOnCircularSeekBarChangeListener != null)
                            mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, true);
                    } else
                        break;

                    lastCWDistanceFromStart = cwDistanceFromStart;

                } else
                    return false;

                break;

            case MotionEvent.ACTION_UP:

                mPointerHaloPaint.setAlpha(mPointerAlpha);
                mPointerHaloPaint.setColor(mPointerHaloColor);

                if (mUserIsMovingPointer) {

                    mUserIsMovingPointer = false;

                    invalidate();

                    if (mOnCircularSeekBarChangeListener != null)
                        mOnCircularSeekBarChangeListener.onStopTrackingTouch(this);
                } else
                    return false;

                break;

            case MotionEvent.ACTION_CANCEL: // Used when the parent view intercepts touches for things like scrolling

                mPointerHaloPaint.setAlpha(mPointerAlpha);
                mPointerHaloPaint.setColor(mPointerHaloColor);
                mUserIsMovingPointer = false;
                invalidate();
                break;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(true);

        return true;
    }

    @Override
    public boolean performClick() {

        return super.performClick();
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();

        Bundle state = new Bundle();
        state.putParcelable("PARENT", superState);
        state.putInt("MAX", mMax);
        state.putInt("PROGRESS", mProgress);
        state.putInt("mCircleColor", mCircleColor);
        state.putInt("mCircleProgressColor", mCircleProgressColor);
        state.putInt("mPointerColor", mPointerColor);
        state.putInt("mPointerHaloColor", mPointerHaloColor);
        state.putInt("mPointerHaloColorOnTouch", mPointerHaloColorOnTouch);
        state.putInt("mPointerAlpha", mPointerAlpha);
        state.putInt("mPointerAlphaOnTouch", mPointerAlphaOnTouch);
        state.putBoolean("lockEnabled", lockEnabled);
        state.putBoolean("isTouchEnabled", isTouchEnabled);

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        Bundle savedState = (Bundle) state;

        Parcelable superState = savedState.getParcelable("PARENT");

        super.onRestoreInstanceState(superState);

        mMax = savedState.getInt("MAX");
        mProgress = savedState.getInt("PROGRESS");
        mCircleColor = savedState.getInt("mCircleColor");
        mCircleProgressColor = savedState.getInt("mCircleProgressColor");
        mPointerColor = savedState.getInt("mPointerColor");
        mPointerHaloColor = savedState.getInt("mPointerHaloColor");
        mPointerHaloColorOnTouch = savedState.getInt("mPointerHaloColorOnTouch");
        mPointerAlpha = savedState.getInt("mPointerAlpha");
        mPointerAlphaOnTouch = savedState.getInt("mPointerAlphaOnTouch");
        lockEnabled = savedState.getBoolean("lockEnabled");
        isTouchEnabled = savedState.getBoolean("isTouchEnabled");

        initPaints();

        recalculateAll();
    }

    public void setOnSeekBarChangeListener(OnCircularSeekBarChangeListener l) {

        mOnCircularSeekBarChangeListener = l;
    }

    public int getCircleColor() {
        return mCircleColor;
    }

    public void setCircleColor(int color) {

        mCircleColor = color;
        mCirclePaint.setColor(mCircleColor);
        invalidate();
    }

    public int getCircleProgressColor() {
        return mCircleProgressColor;
    }

    public void setCircleProgressColor(int color) {

        mCircleProgressColor = color;
        mCircleProgressPaint.setColor(mCircleProgressColor);
        invalidate();
    }

    public int getPointerColor() {
        return mPointerColor;
    }

    public void setPointerColor(int color) {

        mPointerColor = color;
        mPointerPaint.setColor(mPointerColor);
        invalidate();
    }

    public int getPointerHaloColor() {
        return mPointerHaloColor;
    }

    public void setPointerHaloColor(int color) {

        mPointerHaloColor = color;
        mPointerHaloPaint.setColor(mPointerHaloColor);
        invalidate();
    }

    public int getPointerAlpha() {
        return mPointerAlpha;
    }

    public void setPointerAlpha(int alpha) {

        if (alpha >= 0 && alpha <= 255) {

            mPointerAlpha = alpha;
            mPointerHaloPaint.setAlpha(mPointerAlpha);
            invalidate();
        }
    }

    public int getPointerAlphaOnTouch() {
        return mPointerAlphaOnTouch;
    }

    public void setPointerAlphaOnTouch(int alpha) {

        if (alpha >= 0 && alpha <= 255)
            mPointerAlphaOnTouch = alpha;
    }

    public int getCircleFillColor() {
        return mCircleFillColor;
    }

    public void setCircleFillColor(int color) {

        mCircleFillColor = color;
        mCircleFillPaint.setColor(mCircleFillColor);
        invalidate();
    }

    public synchronized int getMax() {
        return mMax;
    }

    public void setMax(int max) {

        if (!(max <= 0)) { // Check to make sure it's greater than zero
            if (max <= mProgress) {
                mProgress = 0; // If the new max is less than current progress, set progress to zero
                if (mOnCircularSeekBarChangeListener != null)
                    mOnCircularSeekBarChangeListener.onProgressChanged(this, mProgress, false);
            }
            mMax = max;

            recalculateAll();
            invalidate();
        }
    }

    public boolean getIsTouchEnabled() {
        return isTouchEnabled;
    }

    public void setIsTouchEnabled(boolean isTouchEnabled) {
        this.isTouchEnabled = isTouchEnabled;
    }

    public interface OnCircularSeekBarChangeListener {

        void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser);

        void onStopTrackingTouch(CircularSeekBar seekBar);

        void onStartTrackingTouch(CircularSeekBar seekBar);
    }
}