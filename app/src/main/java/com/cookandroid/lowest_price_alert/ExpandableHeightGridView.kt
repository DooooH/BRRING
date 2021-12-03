package com.cookandroid.lowest_price_alert

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.MEASURED_SIZE_MASK
import android.widget.GridView

class ExpandableHeightGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridView(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
            View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
        layoutParams.height = measuredHeight
    }
}
