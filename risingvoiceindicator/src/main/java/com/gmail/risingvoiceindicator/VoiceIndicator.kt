package com.gmail.risingvoiceindicator

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.gmail.risingvoiceindicator.RisingVoiceIndicator
import java.util.*

/**
 * Created by cor.park on 2020-03-31.
 */
class VoiceIndicator : View {
    private val delays = intArrayOf(70, 140, 210, 280, 350)
    private val translateYFloats = FloatArray(4)
    private val translateCopy = FloatArray(4)
    private val translateTemp = FloatArray(4)

    private val colorsStop = intArrayOf(
        R.color.color_voice_indicator_circle_0,
        R.color.color_voice_indicator_circle_1,
        R.color.color_voice_indicator_circle_2,
        R.color.color_voice_indicator_circle_3
    )
    private val colorsUser = intArrayOf(
        R.color.color_voice_indicator_circle_0,
        R.color.color_voice_indicator_circle_1,
        R.color.color_voice_indicator_circle_2,
        R.color.color_voice_indicator_circle_3
    )
    private val colorsSystem = intArrayOf(
        R.color.color_voice_indicator_circle_system_0,
        R.color.color_voice_indicator_circle_system_1,
        R.color.color_voice_indicator_circle_system_2,
        R.color.color_voice_indicator_circle_system_3
    )
    private val animators = ArrayList<ValueAnimator>()
    private var isFirst = true
    private var mDecibel = 30.0f
    private var isRunning = false
    private var isStopping = false
    private var isInterceptor = false
    private var mRadius = 20f
    private var mType = 0
    private var mTempType = 0
    private var ballSize = colorsStop.size -1

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        getAttrs(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        getAttrs(attrs, defStyleAttr)
    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.VoiceIndicator)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.VoiceIndicator,
            defStyle,
            0
        )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        mRadius = typedArray.getDimension(R.styleable.VoiceIndicator_voice_indicator_radius, mRadius)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val circleSpacing = 30f
        val x = width / 2 - (mRadius * 5 + circleSpacing)
        for (i in 0..ballSize) {
            canvas.save()
            val translateX = x + mRadius * 2 * i + circleSpacing * i
            val paintUpperBall = Paint()
            val paintUpperLine = Paint()
            if (isFirst) {
                val yPos = height / 2
                translateYFloats[i] = yPos.toFloat()
                translateCopy[i] = yPos.toFloat()
                translateTemp[i] = yPos.toFloat()
                if (i == ballSize) {
                    isFirst = false
                }
            }
            when (mType) {
                STOP -> {
                    paintUpperBall.color = ContextCompat.getColor(context, colorsStop[i])
                    paintUpperLine.color = ContextCompat.getColor(context, colorsStop[i])
                }
                START_USER -> {
                    paintUpperBall.color = ContextCompat.getColor(context, colorsUser[i])
                    paintUpperLine.color = ContextCompat.getColor(context, colorsUser[i])
                }
                START_SYSTEM -> {
                    paintUpperBall.color = ContextCompat.getColor(
                        context,
                        colorsSystem[i]
                    )
                    paintUpperLine.color = ContextCompat.getColor(
                        context,
                        colorsSystem[i]
                    )
                }
            }
            paintUpperLine.strokeWidth = mRadius * 2
            canvas.drawLine(
                translateX,
                translateCopy[i],
                translateX,
                translateYFloats[i],
                paintUpperLine
            )
            canvas.translate(translateX, translateYFloats[i])
            canvas.drawCircle(0f, 0f, mRadius, paintUpperBall)
            canvas.restore()
        }
    }

    /**
     * 애니메이션 시작
     * 유저 발화와 시스템 발화 컬러 변경
     * @param type START_USER,START_SYSTEM  발화
     */
    fun startAnimation(type: Int) {
        Log.d("corpark", "[startAnimation]$type")
        mTempType = type
        mType = type
        val dB = db
        oneCycleAnimation(dB)
    }

    private fun oneCycleAnimation(decibel: Float) {
        mType = mTempType
        if (isRunning) return
        isRunning = true
        clearAnim()
        var boundHeight: Float
        for (i in 0..ballSize) {
            boundHeight = if (i == 1 || i == 3) {
                decibel * 0.7f
            } else {
                decibel
            }
            val scaleAnim = ValueAnimator.ofFloat(translateTemp[i], height / 2 - boundHeight)
            scaleAnim.duration = 200
            scaleAnim.repeatCount = 0
            scaleAnim.addUpdateListener(AnimatorUpdateListener { animation ->
                if (isInterceptor) return@AnimatorUpdateListener
                val height = animation.animatedValue as Float
                translateYFloats[i] = height
                postInvalidate()
            })
            scaleAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    translateTemp[i] = translateYFloats[i]
                    if (i == ballSize) {
                        isRunning = false
                        if (isInterceptor) return
                        oneCycleAnimation(db)
                    }
                }
            })
            animators.add(scaleAnim)

        }
        for (animator in animators) {
            animator.start()
        }
    }

    /**
     * 애니메이션 스탑
     */
    fun stopAnimation() {
        isInterceptor = true
        if (isStopping) return
        isStopping = true
        clearAnim()
        // 기존 자리로 이동
        for (i in 0..ballSize) {
            val scaleAnim =
                ValueAnimator.ofFloat(translateYFloats[i], translateCopy[i])
            scaleAnim.duration = 400
            scaleAnim.repeatCount = 0
            scaleAnim.startDelay = delays[i].toLong()
            scaleAnim.addUpdateListener { animation ->
                val height = animation.animatedValue as Float
                translateYFloats[i] = height
                postInvalidate()
            }
            scaleAnim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    Log.d("cor.park", "onAnimationEnd$mType")
                    if (i == ballSize) {
                        isStopping = false
                        isInterceptor = false
                        mType = STOP
                        invalidate()
                    }
                }
            })
            animators.add(scaleAnim)
        }
        for (animator in animators) {
            animator.start()
        }
    }

    /**
     * 애니메이션 제거
     */
    private fun clearAnim() {
        for (animator in animators) {
            animator.cancel()
            animator.addUpdateListener(null)
        }
        animators.clear()
    }// 일반적인 상황에서 발화 시 50이상 임.

    /**
     * 100이상의 뻥튀기 데시벨이 처음에 들어와 100이상은 막음.
     * @param dB
     */
    var db: Float
        get() {
            if (mType == START_SYSTEM) {
                mDecibel = RisingVoiceIndicator.systemDecibel
            }
            return mDecibel
        }
        set(dB) {
            if (0 < dB && dB < 100) mDecibel = if (dB < 50) { // 일반적인 상황에서 발화 시 50이상 임.
                dB * 0.4f
            } else {
                dB * 0.8f
            }
        }

    companion object {
        const val STOP = 0
        const val START_USER = 1
        const val START_SYSTEM = 2
    }
}