package com.gmail.risingvoiceindicator

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.gmail.risingvoiceindicator.Utils.Companion.getRandomNumber

class RisingVoiceIndicator : RelativeLayout {

    private var upperIndicator: VoiceIndicator? = null
    private var underIndicator: VoiceIndicator? = null
    private var mType = 0
    private var thread: Thread? = null
    private var mRadius = 20f

    constructor(context: Context?) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(
        context,
        attrs
    ) {
        getAttrs(attrs)
        init(context, attrs)

    }

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        getAttrs(attrs, defStyleAttr)
        init(context, attrs)

    }

    private fun getAttrs(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.RisingVoiceIndicator)
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet?, defStyle: Int) {
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RisingVoiceIndicator,
            defStyle,
            0
        )
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        mRadius = typedArray.getDimension(R.styleable.RisingVoiceIndicator_radius, mRadius)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val view =
            LayoutInflater.from(context).inflate(R.layout.view_voice_indicator, this, true)
        upperIndicator = view.findViewById(R.id.upper_indicator)
        underIndicator = view.findViewById(R.id.under_indicator)
    }

    @JvmOverloads
    fun startAnimation(type: Int = VoiceIndicator.START_USER) {
        mType = type
        makeSystemDecibel()
        upperIndicator!!.startAnimation(type)
        underIndicator!!.startAnimation(type)
    }

    fun stopAnimation() {
        stopSystemDecibel()
        upperIndicator!!.stopAnimation()
        underIndicator!!.stopAnimation()
    }

    fun setDb(dB: Float) {
        upperIndicator!!.db = dB
        underIndicator!!.db = dB
    }

    /**
     * 임의의 데시벨 데이터 생성하기 위한 쓰레드 생성
     */
    private fun makeSystemDecibel() {
        if (mType == VoiceIndicator.START_SYSTEM
            && thread == null
        ) {
            thread = Thread(Runnable {
                try {
                    while (!thread!!.isInterrupted) {
                        systemDecibel = getRandomNumber(45, 75).toFloat()
                        Thread.sleep(150)
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            })
            thread!!.start()
        }
    }

    /**
     * 쓰레드 중지 및 해제
     */
    private fun stopSystemDecibel() {
        if (thread != null) {
            thread!!.interrupt()
            thread = null
        }
    }

    companion object {
        var systemDecibel = 0f
    }
}