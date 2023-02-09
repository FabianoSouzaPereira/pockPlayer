/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */

package com.kevalpatel2106.rulerpicker;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.CheckResult;
import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Keval Patel on 28 Mar 2018.
 * <p>
 * This is custom {@link View} which will draw a ruler with indicators.
 * There are two types of indicators:
 * <li><b>Long Indicators:</b> These indicators marks specific important value after some periodic interval.
 * e.g. Long indicator represents evert 10th (10, 20, 30...) value.</li>
 * <li><b>Short Indicators:</b> There indicators represents single value.</li>
 */

final class RulerView extends View {

    private int mOneDayMinutes = 1441;
    private int mOneHourMinutes = 60;
    public float mShowScaleMinutes = 0;
    public ArrayList<Timeline> mTimelineData = new ArrayList<>();
    private onFinishLoadTimeline mFinishListener;

    public RulerView(Context context, onFinishLoadTimeline finishListener) {
        super(context);
        this.mFinishListener = finishListener;
    }

    interface onFinishLoadTimeline {
        void onFinishedLoadTimeline();
    }

    /**
     * Height of the view. This view height is measured in {@link #onMeasure(int, int)}.
     *
     * @see #onMeasure(int, int)
     */
    private int mViewHeight;

    /**
     * {@link Paint} for the line in the ruler view.
     *
     * @see #refreshPaint()
     */
    private Paint mIndicatorPaint;
    private Paint mTimeLineIndicatorPaint;

    /**
     * {@link Paint} to display the text on the ruler view.
     *
     * @see #refreshPaint()
     */
    private Paint mTextPaint;

    /**
     * Distance interval between two subsequent indicators on the ruler.
     *
     * @see #setIndicatorIntervalDistance(int)
     * @see #getIndicatorIntervalWidth()
     */
    private int mIndicatorInterval = 2/* Default value */;
    private int mSmallIndicatorInterval = 1 /* Default value */;

    /**
     * Minimum value. This value will be displayed at the left-most end of the ruler. This value
     * must be less than {@link #mMaxValue}.
     *
     * @see #setValueRange(int, int)
     * @see #getMinValue()
     */
    private int mMinValue = 0 /* Default value */;

    /**
     * Maximum value. This value will be displayed at the right-most end of the ruler. This value
     * must be greater than {@link #mMinValue}.
     *
     * @see #setValueRange(int, int)
     * @see #getMaxValue()
     */
    private int mMaxValue = mOneDayMinutes /* Default maximum value */;

    /**
     * Ratio of long indicator height to the ruler height. This value must be between 0 to 1. The
     * value should greater than {@link #mShortIndicatorHeight}. Default value is 0.6 (i.e. 60%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see #setIndicatorHeight(float, float)
     * @see #getLongIndicatorHeightRatio()
     */
    private float mLongIndicatorHeightRatio = 0.6f /* Default value */;

    /**
     * Ratio of short indicator height to the ruler height. This value must be between 0 to 1. The
     * value should less than {@link #mLongIndicatorHeight}. Default value is 0.4 (i.e. 40%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see #setIndicatorHeight(float, float)
     * @see #getShortIndicatorHeightRatio()
     */
    private float mShortIndicatorHeightRatio = 0.4f /* Default value */;

    /**
     * Actual height of the long indicator in pixels. This height is derived from
     * {@link #mLongIndicatorHeightRatio}.
     *
     * @see #updateIndicatorHeight(float, float)
     */
    private int mLongIndicatorStartHeight = 18;
    private int mLongIndicatorHeight = 55;
    private int mBottomLineTimelineHeight = 50;

    private int mSmallIndicatorY = 30;
    private int mTimelineIndicatorY = 47;

    /**
     * Actual height of the short indicator in pixels. This height is derived from
     * {@link #mShortIndicatorHeightRatio}.
     *
     * @see #updateIndicatorHeight(float, float)
     */
    private int mShortIndicatorHeight = 0;

    /**
     * Integer color of the text, that is displayed on the ruler.
     *
     * @see #setTextColor(int)
     * @see #getTextColor()
     */
    @ColorInt
    private int mTextColor = Color.WHITE;

    /**
     * Integer color of the indicators.
     *
     * @see #setIndicatorColor(int)
     * @see #getIndicatorColor()
     */
    @ColorInt
    private int mIndicatorColor = Color.LTGRAY;
    private int mTimeLineIndicatorColor = Color.parseColor("#d50000");

    /**
     * Height of the text, that is displayed on ruler in pixels.
     *
     * @see #setTextSize(int)
     * @see #getTextSize()
     */
    @Dimension
    private int mTextSize = 36;

    /**
     * Width of the indicator in pixels.
     *
     * @see #setIndicatorWidth(int)
     * @see #getIndicatorWidth()
     */
    @Dimension
    private float mIndicatorWidthPx = 4f;
    private float mIndicatorWidthPxLine = 9f;

    public RulerView(@NonNull final Context context) {
        super(context);
        parseAttr(null);
    }

    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs) {
        super(context, attrs);
        parseAttr(attrs);
    }

    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs,
                     final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttr(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RulerView(@NonNull final Context context,
                     @Nullable final AttributeSet attrs,
                     int defStyleAttr,
                     int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttr(attrs);
    }

    private void parseAttr(@Nullable AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet,
                    R.styleable.RulerView,
                    0,
                    0);

            try { //Parse params
                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
                    mTextColor = a.getColor(R.styleable.RulerView_ruler_text_color, Color.WHITE);
                }

                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
                    mTextSize = a.getDimensionPixelSize(R.styleable.RulerView_ruler_text_size, 14);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
                    mIndicatorColor = a.getColor(R.styleable.RulerView_indicator_color, Color.WHITE);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_width)) {
                    mIndicatorWidthPx = a.getDimensionPixelSize(R.styleable.RulerView_indicator_width,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
                    mIndicatorInterval = a.getDimensionPixelSize(R.styleable.RulerView_indicator_interval,
                            4);
                }

                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
                    mLongIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_long_height_height_ratio,
                            1, 1, 0.6f);
                }
                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
                    mShortIndicatorHeightRatio = a.getFraction(R.styleable.RulerView_short_height_height_ratio,
                            1, 1, 0.4f);
                }
                setIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

                if (a.hasValue(R.styleable.RulerView_min_value)) {
                    mMinValue = a.getInteger(R.styleable.RulerView_min_value, 0);
                }
                if (a.hasValue(R.styleable.RulerView_max_value)) {
                    mMaxValue = a.getInteger(R.styleable.RulerView_max_value, 100);
                }
                setValueRange(mMinValue, mMaxValue);
            } finally {
                a.recycle();
            }
        }
        refreshPaint();
    }

    /**
     * Create the indicator paint and value text color.
     */
    void refreshPaint() {
        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(mIndicatorColor);
        mIndicatorPaint.setStrokeWidth(mIndicatorWidthPx);
        mIndicatorPaint.setStyle(Paint.Style.STROKE);

        mTimeLineIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTimeLineIndicatorPaint.setColor(mTimeLineIndicatorColor);
        mTimeLineIndicatorPaint.setStrokeWidth(mIndicatorWidthPxLine);
        mTimeLineIndicatorPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        invalidate();
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int mThirtyMinutes = 30;
        int mFifteenMinutes = 15;

        for (int value = 1; value < mMaxValue - mMinValue; value++) {
            if (value % mOneHourMinutes == 0) {
                drawLongIndicator(canvas, value);
                drawValueText(canvas, value);
            } else if ((mShowScaleMinutes != 0) &&
                    (mShowScaleMinutes > 5 && (value % mThirtyMinutes == 0)) ||
                    (mShowScaleMinutes > 10 && (value % mFifteenMinutes == 0))) {
                drawSmallIndicator(canvas, value);
            }
        }

        //Draw the first indicator.
        drawSmallIndicator(canvas, 0);
        //Draw the last indicator.
        drawSmallIndicator(canvas, getWidth());
        //Draw line bottom indicator
        drawBottomLineTimeline(canvas, getWidth());

        for(int i = 0; i < mTimelineData.size(); i++) {
            canvas.drawLine(mTimelineData.get(i).getStart() * (mIndicatorInterval), //startX
                    convertToDp(mTimelineIndicatorY), //startY
                    (mTimelineData.get(i).getDuration() + mTimelineData.get(i).getStart()) * (mIndicatorInterval),//stopX
                    convertToDp(mTimelineIndicatorY),//stopY
                    mTimeLineIndicatorPaint);
        }


        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Measure dimensions
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int viewWidth = ((mMaxValue - mMinValue - 1) * (mIndicatorInterval));

        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

        this.setMeasuredDimension(viewWidth, mViewHeight);
    }

    /**
     * Calculate and update the height of the long and the short indicators based on new ratios.
     *
     * @param longIndicatorHeightRatio  Ratio of long indicator height to the ruler height.
     * @param shortIndicatorHeightRatio Ratio of short indicator height to the ruler height.
     */
    private void updateIndicatorHeight(final float longIndicatorHeightRatio,
                                       final float shortIndicatorHeightRatio) {
        mLongIndicatorHeight = (int) (mViewHeight * longIndicatorHeightRatio);
        mShortIndicatorHeight = (int) (mViewHeight * shortIndicatorHeightRatio);

    }

    public float convertToDp(float pixels) {
        return pixels * getDensity();
    }

    private float getDensity() {
        return Resources.getSystem().getDisplayMetrics().density;
    }

    /**
     * Draw the vertical short line at every value.
     *
     * @param canvas {@link Canvas} on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private void drawSmallIndicator(@NonNull final Canvas canvas,
                                    final int value) {
        canvas.drawLine(mIndicatorInterval * value,
                convertToDp(mSmallIndicatorY),
                mIndicatorInterval * value,
                convertToDp(mLongIndicatorHeight),
                mIndicatorPaint);
    }

    /**
     * Draw the vertical long line.
     *
     * @param canvas {@link Canvas} on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private void drawLongIndicator(@NonNull final Canvas canvas,
                                   final int value) {

        canvas.drawLine(mIndicatorInterval * value,
                convertToDp(mLongIndicatorStartHeight),
                (mIndicatorInterval * value),
                convertToDp(mLongIndicatorHeight),
                mIndicatorPaint);
    }


    private void drawBottomLineTimeline(@NonNull final Canvas canvas,
                                        final int value) {
        canvas.drawLine(0,
                convertToDp(mBottomLineTimelineHeight),
                value,
                convertToDp(mBottomLineTimelineHeight),
                mIndicatorPaint);
    }

    /**
     * Draw the value number below the longer indicator. This will use {@link #mTextPaint} to draw
     * the text.
     *
     * @param canvas {@link Canvas} on which the text will be drawn.
     * @param value  Value to draw.
     */
    private void drawValueText(@NonNull final Canvas canvas,
                               final int value) {
        int hour = (value / mOneHourMinutes);

        if(hour < 24){
            String hourText = String.format(hour + "h");
            canvas.drawText(hourText,
                    (mIndicatorInterval * value),
                    mTextPaint.getTextSize(),
                    mTextPaint);
        }
    }

    /////////////////////// Properties getter/setter ///////////////////////

    /**
     * @return Color integer value of the ruler text color.
     * @see #setTextColor(int)
     */
    @CheckResult
    @ColorInt
    int getTextColor() {
        return mIndicatorColor;
    }

    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     */
    void setTextColor(@ColorInt final int color) {
        mTextColor = color;
        refreshPaint();
    }

    /**
     * @return Size of the text of ruler in pixels.
     * @see #setTextSize(int)
     */
    @CheckResult
    float getTextSize() {
        return mTextSize;
    }

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param textSizeSp Text size dimension in dp.
     */
    void setTextSize(final int textSizeSp) {
        mTextSize = RulerViewUtils.sp2px(getContext(), textSizeSp);
        refreshPaint();
    }


    /**
     * @return Color integer value of the indicator color.
     * @see #setIndicatorColor(int)
     */
    @CheckResult
    @ColorInt
    int getIndicatorColor() {
        return mIndicatorColor;
    }

    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     */
    void setIndicatorColor(@ColorInt final int color) {
        mIndicatorColor = color;
        refreshPaint();
    }

    /**
     * @return Width of the indicator in pixels.
     * @see #setIndicatorWidth(int)
     */
    @CheckResult
    float getIndicatorWidth() {
        return mIndicatorWidthPx;
    }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     */
    void setIndicatorWidth(final int widthPx) {
        mIndicatorWidthPx = widthPx;
        refreshPaint();
    }


    /**
     * @return Get the minimum value displayed on the ruler.
     * @see #setValueRange(int, int)
     */
    @CheckResult
    int getMinValue() {
        return mMinValue;
    }

    /**
     * @return Get the maximum value displayed on the ruler.
     * @see #setValueRange(int, int)
     */
    @CheckResult
    int getMaxValue() {
        return mMaxValue;
    }

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     *                 or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     *                 or zero.This value must be greater than min value. Default minimum value is 100.
     */
    void setValueRange(final int minValue, final int maxValue) {
        mMinValue = minValue;
        mMaxValue = maxValue;
        invalidate();
    }

    /**
     * @return Get distance between two indicator in pixels.
     * @see #setIndicatorIntervalDistance(int)
     */
    @CheckResult
    int getIndicatorIntervalWidth() {
        return mIndicatorInterval;
    }

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @throws IllegalArgumentException if interval is negative or zero.
     */
    void setIndicatorIntervalDistance(final int indicatorIntervalPx) {
        if (indicatorIntervalPx <= 0)
            throw new IllegalArgumentException("Interval cannot be negative or zero.");
        mIndicatorInterval = indicatorIntervalPx;

        invalidate();
    }

    /**
     * @return Ratio of long indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     */
    @CheckResult
    float getLongIndicatorHeightRatio() {
        return mLongIndicatorHeightRatio;
    }

    /**
     * @return Ratio of short indicator height to the ruler height.
     * @see #setIndicatorHeight(float, float)
     */
    @CheckResult
    float getShortIndicatorHeightRatio() {
        return mShortIndicatorHeightRatio;
    }

    /**
     * Set the height of the long and short indicators.
     *
     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should greater than {@link #mShortIndicatorHeight}.
     *                         Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as the
     *                         ruler height.
     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
     *                         be between 0 to 1. The value should less than {@link #mLongIndicatorHeight}.
     *                         Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
     *                         be displayed. If the value is 1, indicator height will be same as
     *                         the ruler height.
     * @throws IllegalArgumentException if any of the parameter is invalid.
     */
    void setIndicatorHeight(final float longHeightRatio,
                            final float shortHeightRatio) {

        if (shortHeightRatio < 0 || shortHeightRatio > 1) {
            throw new IllegalArgumentException("Sort indicator height must be between 0 to 1.");
        }

        if (longHeightRatio < 0 || longHeightRatio > 1) {
            throw new IllegalArgumentException("Long indicator height must be between 0 to 1.");
        }

        if (shortHeightRatio > longHeightRatio) {
            throw new IllegalArgumentException("Long indicator height cannot be less than sort indicator height.");
        }

        mLongIndicatorHeightRatio = longHeightRatio;
        mShortIndicatorHeightRatio = shortHeightRatio;

        updateIndicatorHeight(mLongIndicatorHeightRatio, mShortIndicatorHeightRatio);

        invalidate();
    }

    public class Timeline {
        long start;
        long duration;

        private Timeline(long start, long duration) {
            this.start = start;
            this.duration = duration;
        }

        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration / 60;
        }
    }

    public void setDataTimeline(HashMap<Long, Long> hash) {
        final List<Map.Entry<Long, Long>> entries = new ArrayList<>(hash.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<Long, Long>>() {
            @Override
            public int compare(Map.Entry<Long, Long> longLongEntry, Map.Entry<Long, Long> t1) {
                return (int) (longLongEntry.getKey() - t1.getKey());
            }
        });

        mTimelineData.clear();

        if (entries.size() == 0) return;

        long timeInitial = aroundDate(entries.get(entries.size() - 1).getKey(), true);

        for(int i = 0; i < entries.size(); i++){
            mTimelineData.add(new Timeline(entries.get(i).getKey(), entries.get(i).getValue()));
        }

        for(int i = 0; i < mTimelineData.size(); i++) {
            mTimelineData.get(i).setStart((mTimelineData.get(i).getStart() - timeInitial) /60);
            mTimelineData.get(i).setDuration(mTimelineData.get(i).getDuration());
        }

        mFinishListener.onFinishedLoadTimeline();

        invalidate();
    }

    public long aroundDate(Long  epoch, Boolean isDown) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(epoch * 1000);

        if(isDown){
            date.set(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH),
                    0, 0, 0);
        } else {
            date.set(date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH),
                    23, 59, 59);
        }
        return date.getTimeInMillis()/ 1000;
    }

}
