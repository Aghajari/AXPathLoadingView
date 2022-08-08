/*
 * Copyright (C) 2022 - Amir Hossein Aghajari
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.aghajari.axpathloading;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * @author Amir Hossein Aghajari
 * @version 1.0.1
 */
public class AXPathLoadingView extends View {

    private final Paint paint = new Paint();
    private final RectF bound = new RectF();
    private final Path subPath = new Path();

    private PathMeasure pathMeasure;
    private Path orgPath, path;
    private ValueAnimator animator;

    private float progressSize, currentPosition;
    private long duration = 1200, delay = 100;
    private int trackColor = Color.WHITE, progressColor = Color.LTGRAY;

    private boolean alphaAnimationEnabled = true;
    private boolean autoStart = true;

    private float alphaStart = 0.9f;

    public AXPathLoadingView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public AXPathLoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0, 0);
    }

    public AXPathLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AXPathLoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs, defStyleAttr, defStyleRes);
    }

    private void init(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        float strokeWidth = getResources().getDisplayMetrics().density * 6;
        float progressSize = 0.28f;

        if (attrs != null) {
            final TypedArray a = getContext().obtainStyledAttributes(attrs,
                    R.styleable.AXPathLoadingView, defStyleAttr, defStyleRes);
            trackColor = a.getColor(R.styleable.AXPathLoadingView_trackColor, trackColor);
            progressColor = a.getColor(R.styleable.AXPathLoadingView_progressColor, progressColor);

            strokeWidth = a.getDimension(R.styleable.AXPathLoadingView_thickness, strokeWidth);

            progressSize = a.getFloat(R.styleable.AXPathLoadingView_progressSize, progressSize);
            alphaStart = a.getFloat(R.styleable.AXPathLoadingView_alphaStart, alphaStart);

            delay = a.getInteger(R.styleable.AXPathLoadingView_delay, (int) delay);
            duration = a.getInteger(R.styleable.AXPathLoadingView_animationDuration, (int) duration);

            alphaAnimationEnabled = a.getBoolean(R.styleable.AXPathLoadingView_alphaAnimation, alphaAnimationEnabled);
            autoStart = a.getBoolean(R.styleable.AXPathLoadingView_autoStart, autoStart);

            createPath(getContext(), a.getString(R.styleable.AXPathLoadingView_path), attrs);
            a.recycle();
        }

        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(paint.getStrokeWidth() / 2f));
        setProgressSize(progressSize);
    }

    private void createPath(Context context, String name, AttributeSet attrs) {
        if (name == null || name.trim().isEmpty())
            return;

        int indexOfTag = name.indexOf('#');
        if (indexOfTag == -1)
            throw new IllegalStateException(attrs.getPositionDescription() +
                    ": Syntax is not valid for path: " + name);

        String className = indexOfTag == 0 ? context.getClass().getName()
                : name.substring(0, indexOfTag);
        String methodName = name.substring(indexOfTag + 1);

        if (className.charAt(0) == '.')
            className = context.getPackageName() + className;

        try {
            ClassLoader classLoader = isInEditMode()
                    ? this.getClass().getClassLoader()
                    : context.getClassLoader();

            Class<?> cls = Class.forName(className, false, classLoader);

            Method method = cls.getMethod(methodName, AXPathLoadingView.class);
            if (method.getReturnType() != Path.class)
                throw new IllegalStateException(attrs.getPositionDescription()
                        + ": Method must return an instance of android.graphics.Path: " + methodName);
            if (!Modifier.isStatic(method.getModifiers()))
                throw new IllegalStateException(attrs.getPositionDescription()
                        + ": Method must be static: " + methodName);

            method.setAccessible(true);
            setPath((Path) method.invoke(null, this));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(attrs.getPositionDescription()
                    + ": Unable to find class: " + className, e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(attrs.getPositionDescription()
                    + ": Cannot access non-public methods: " + methodName, e);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(attrs.getPositionDescription()
                    + ": Could not find the method: " + methodName, e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(attrs.getPositionDescription()
                    + ": Could not invoke the method: " + methodName, e);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode()) {
            paint.setColor(trackColor);

            if (path != null) {
                canvas.drawPath(path, paint);
                paint.setColor(progressColor);
                canvas.drawPath(getSubPath(0, progressSize), paint);
            } else {
                int w = getMeasuredWidth(), h = getMeasuredHeight();
                canvas.drawLine(10, h / 2f, w - 20, h / 2f, paint);
                paint.setColor(progressColor);
                canvas.drawLine(10, h / 2f, progressSize * (w - 20), h / 2f, paint);
            }
            return;
        }

        if (path != null) {
            int maxAlpha = paint.getAlpha();
            paint.setColor(trackColor);
            canvas.drawPath(path, paint);
            paint.setColor(progressColor);

            if (isAlphaAnimationEnabled() && animator != null) {
                float fraction = animator.getAnimatedFraction();
                if (fraction >= alphaStart) {
                    float max = calculateMaxAlphaFactor();
                    fraction -= alphaStart;

                    int alpha = (int) ((max - fraction) * 255 / max);
                    //paint.setColor((progressColor & 0x00ffffff) | (alpha << 24));
                    paint.setAlpha(alpha);
                }
            }

            canvas.drawPath(getSubPath(
                    Math.max(0f, currentPosition),
                    Math.min(1f, currentPosition + progressSize)
            ), paint);
            paint.setAlpha(maxAlpha);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        createPathToCenter();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (orgPath != null) {
            int xPadding = getPaddingLeft() + getPaddingRight() + (int) paint.getStrokeWidth();
            int yPadding = getPaddingTop() + getPaddingBottom() + (int) paint.getStrokeWidth();

            orgPath.computeBounds(bound, true);
            setMeasuredDimension((int) bound.width() + xPadding,
                    (int) bound.height() + yPadding);
        }
    }

    private Path getSubPath(float start, float end) {
        subPath.reset();
        if (pathMeasure != null)
            pathMeasure.getSegment(start * pathMeasure.getLength(),
                    end * pathMeasure.getLength(), subPath, true);
        return subPath;
    }

    public float getPathLength() {
        if (pathMeasure == null)
            return 0;

        return pathMeasure.getLength();
    }

    public Path getPath() {
        return orgPath;
    }

    public void setPath(Path path) {
        this.orgPath = path;
        createPathToCenter();
        if (autoStart)
            startAnimation();
    }

    private void createPathToCenter() {
        if (orgPath == null)
            return;

        path = new Path(orgPath);
        path.computeBounds(bound, true);
        path.offset(getPaddingLeft() + (getMeasuredWidth() - getPaddingLeft()
                        - getPaddingRight() - bound.right) / 2,
                getPaddingTop() + (getMeasuredHeight() - getPaddingTop()
                        - getPaddingBottom() - bound.bottom) / 2f);
        pathMeasure = new PathMeasure(this.path, false);
    }

    public float getProgressSize() {
        return progressSize;
    }

    public void setProgressSize(float progressSize) {
        this.progressSize = progressSize;
        if (animator != null) {
            cancelAnimation();
        } else {
            currentPosition = -progressSize;
        }

        animator = ValueAnimator.ofFloat(-progressSize, 1f);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.addUpdateListener(a -> {
            currentPosition = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // We need a delay before next start
                // So, We can't use inf repeat count!
                if (animator != null)
                    animator.start();
            }
        });

        // May we needed to customize this animator with inheritance.
        initAnimator(animator);

        if (path != null && autoStart)
            animator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (autoStart)
            startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (animator != null)
            cancelAnimation();
    }

    @Override
    public void setVisibility(int visibility) {
        int old = super.getVisibility();
        super.setVisibility(visibility);
        if (old != visibility) {
            if (visibility == VISIBLE) {
                if (autoStart)
                    startAnimation();
            } else if (animator != null) {
                cancelAnimation();
            }
        }
    }

    public void cancelAnimation() {
        currentPosition = -progressSize;
        if (animator != null)
            animator.cancel();
        animator = null;
        invalidate();
    }

    public void startAnimation() {
        if (animator != null)
            animator.start();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        if (animator != null)
            animator.setDuration(duration);
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
        if (animator != null)
            animator.setStartDelay(delay);
    }

    public int getTrackColor() {
        return trackColor;
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
        invalidate();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        invalidate();
    }

    public Paint getPaint() {
        return paint;
    }

    public boolean isAlphaAnimationEnabled() {
        return alphaAnimationEnabled;
    }

    public void setAlphaAnimationEnabled(boolean alphaAnimationEnabled) {
        this.alphaAnimationEnabled = alphaAnimationEnabled;
    }

    public float getAlphaStart() {
        return alphaStart;
    }

    public void setAlphaStart(float alphaStart) {
        this.alphaStart = alphaStart;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Calculate alpha factor
     * <ul>
     *  <li><code>(1 - alphaStart) * 1</code> : alpha will be 0 at the end</li>
     *  <li><code>(1 - alphaStart) * 2</code> : alpha will be 127 at the end</li>
     *  <li><code>(1 - alphaStart) * 3</code> : alpha will be 170 at the end</li>
     * </ul>
     */
    protected float calculateMaxAlphaFactor() {
        return (1f - alphaStart);//* (1f + progressSize * 3);
    }

    protected void initAnimator(ValueAnimator animator) {
    }
}
