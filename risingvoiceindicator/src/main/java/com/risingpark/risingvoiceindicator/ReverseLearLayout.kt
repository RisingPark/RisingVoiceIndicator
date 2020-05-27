package com.risingpark.risingvoiceindicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout

class ReverseLearLayout : LinearLayout {
    var isReverse = true

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {}

    constructor(
        context: Context?, attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    override fun dispatchDraw(arg0: Canvas) {
        if (isReverse) {
            arg0.scale(1f, -1f, arg0.width / 2.toFloat(), arg0.height / 2.toFloat())
        }
        super.dispatchDraw(arg0)
    }
}