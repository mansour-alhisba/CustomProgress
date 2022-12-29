package com.example.demod.customview

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.annotation.ColorInt
import com.example.demod.R
import kotlin.math.roundToLong


class CustomProgress(context: Context, attributes: AttributeSet) : View(context, attributes) {

    private val outerArc = Paint()
    private val innerArc = Paint()

    private val textMaxWidth: Float
    private val progressWidth: Float

    private val progressRect = RectF()

    private var progressMaxscale = DEFAULT_MAX_SCALE
    private var progressbarTouchEnable = DEFAULT_TOUCH_ENABLED

    private var progress = DEFAULT_PROGRESS
    private val textPaint = Paint()
    private var width = 0f
    private var height = 0f

    @ColorInt
    private var defaultForegroundProgressColor  = Color.parseColor("#71CC75")//Todo

    @ColorInt
    private var defaultBackgroundProgressColor = Color.parseColor("#F9F9FA")//Todo

    private var foregroundProgressbarWidth = DEFAULT_FOREGROUND_PROGRESSBAR_WIDTH

    @ColorInt
    private var foregroundProgressColor = defaultForegroundProgressColor

    @ColorInt
    private var backgroundProgressColor = defaultBackgroundProgressColor


    init {


        val array = context.obtainStyledAttributes(attributes, R.styleable.ArcProgress)
        val textSize = array.getDimension(R.styleable.ArcProgress_android_textSize, 0f)

        val textView = TextView(context)
        textView.textSize = textSize

        textMaxWidth = textView.paint.measureText("100%")

        progressWidth = array.getDimension(R.styleable.ArcProgress_progressWidth, 1f)

        initTypeArray(array)
        progressValidation(progress)
        outerArc.strokeWidth = foregroundProgressbarWidth
        innerArc.strokeWidth = foregroundProgressbarWidth
        outerArc.style = Paint.Style.STROKE
        innerArc.color = backgroundProgressColor
        outerArc.color = foregroundProgressColor
        innerArc.style = Paint.Style.STROKE
        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5f
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 40f

    }


    constructor(context: Context, attributes: AttributeSet, defStyle: Int) : this(
        context,
        attributes
    ) {
        val typedArray =
            context.obtainStyledAttributes(attributes, R.styleable.ArcProgress, defStyle, 0)
        initTypeArray(typedArray)
        init()
        progressValidation(progress)
    }

    private fun initTypeArray(typedArray: TypedArray) {
        foregroundProgressbarWidth = typedArray.getFloat(
            R.styleable.ArcProgress_ap_foreground_progressbar_width,
            DEFAULT_FOREGROUND_PROGRESSBAR_WIDTH
        )

        foregroundProgressColor =
            typedArray.getColor(
                R.styleable.ArcProgress_ap_progress_color,
                defaultForegroundProgressColor
            )
        backgroundProgressColor = typedArray.getColor(
            R.styleable.ArcProgress_ap_progress_background_color,
            defaultBackgroundProgressColor
        )
        progress = typedArray.getFloat(R.styleable.ArcProgress_ap_progress, DEFAULT_PROGRESS)

        setProgressWithAnimation(typedArray.getFloat(R.styleable.ArcProgress_ap_progress_with_animation, progress))

        progressMaxscale =
            typedArray.getFloat(R.styleable.ArcProgress_ap_progress_maxscale, DEFAULT_MAX_SCALE)

        progressbarTouchEnable = typedArray.getBoolean(
            R.styleable.ArcProgress_ap_progress_touchEnabled,
            DEFAULT_TOUCH_ENABLED
        )

        typedArray.recycle()
    }


    private fun init() {
        outerArc.strokeWidth = foregroundProgressbarWidth
        outerArc.style = Paint.Style.STROKE
        innerArc.color = backgroundProgressColor
        outerArc.color = foregroundProgressColor
        innerArc.strokeWidth = foregroundProgressbarWidth
        innerArc.style = Paint.Style.STROKE
        textPaint.color = Color.BLACK
        textPaint.strokeWidth = 5f
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 40f

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        width = if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            paddingLeft + paddingRight + textMaxWidth + progressWidth * 2
        } else {
            MeasureSpec.getSize(widthMeasureSpec).toFloat()
        }
        height = if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            paddingTop + paddingBottom + textMaxWidth + progressWidth * 2
        } else {
            MeasureSpec.getSize(heightMeasureSpec).toFloat()
        }

        setMeasuredDimension(width.toInt(), height.toInt())

        progressRect.bottom = height / 0.5f
        progressRect.left = paddingLeft + progressWidth / 2f
        progressRect.top = paddingTop + progressWidth / 2f
        progressRect.right = width - paddingRight - progressWidth / 2f
        progressRect.bottom = height - paddingBottom - progressWidth / 2f
        setMeasuredDimension(width.toInt(), height.toInt())


    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        progressValidation(progress)

        outerArc.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
        innerArc.pathEffect = DashPathEffect(floatArrayOf(20f, 20f), 0f)
        canvas.drawArc(progressRect, START_ANGLE, SWIPE_ANGLE, false, innerArc)
        canvas.drawArc(progressRect, START_ANGLE, progress, false, outerArc)
        invalidate()

    }

    fun setForegroundProgressColor(color: Int) {
        foregroundProgressColor = color
        outerArc.color = foregroundProgressColor
        invalidate()
    }

    fun setBackgroundProgressColor(color: Int) {
        backgroundProgressColor = color
        innerArc.color = backgroundProgressColor
        invalidate()
    }

    fun setForeGroundProgressThickness(thickness: Float) {
        outerArc.strokeWidth = thickness
        invalidate()
    }

    fun setBackGroundProgressWidth(thickness: Float) {
        innerArc.strokeWidth = thickness
        invalidate()
    }

    fun setProgressThickness(thickness: Float) {
        innerArc.strokeWidth = thickness
        invalidate()
    }

    fun getprogress(): Float {//Todo
        return progress
    }

    fun setProgress(progress: Float) {
        progressValidation(progress)

    }

    fun getProgress(): Float {
        val percentage = (progress / 180) * 100
        val progressValue = (percentage / 100) * progressMaxscale
        return progressValue
    }


    private fun progressValidation(progress: Float) {
        if (this.progress >= SWIPE_ANGLE) {
            this.progress = SWIPE_ANGLE

        } else {
            this.progress = progress
        }
        invalidate()
    }

    fun setProgressbarMaxscale(maxScale: Float) {
        progressMaxscale = maxScale
    }

    fun getProgressbarMaxscale(): Float {
        return progressMaxscale
    }

    fun setTouchEnable(touchEnable: Boolean) {
        progressbarTouchEnable = touchEnable
    }

    fun getTouchEnable(): Boolean {
        return progressbarTouchEnable
    }


    /*Adding the animation to progressbar*/
    fun setProgressWithAnimation(progress: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", this.progress, progress)
        objectAnimator.duration = DEFAULT_ANIMATION_DURATION.roundToLong()
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }

    fun setProgressWithAnimation(progress: Float, duration: Int) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress)
        objectAnimator.duration = duration.toLong()
        objectAnimator.interpolator = DecelerateInterpolator()
        objectAnimator.start()
    }

    companion object {
        private const val DEFAULT_PROGRESS = 0f
        private const val DEFAULT_MAX_SCALE = 100f
        private const val DEFAULT_TOUCH_ENABLED = false
        private const val DEFAULT_FOREGROUND_PROGRESSBAR_WIDTH = 30f
        private const val DEFAULT_ANIMATION_DURATION = 1500f

        private const val START_ANGLE = -250f
        private const val SWIPE_ANGLE = 320f


    }
}