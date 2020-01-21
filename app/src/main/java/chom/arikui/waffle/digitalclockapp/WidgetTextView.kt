package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView

class WidgetTextView(context: Context?) : TextView(context) {

    init {
        initTextFont(context)
    }

    constructor(context: Context?, attributeSet: AttributeSet) : this(context){
        initTextFont(context)
    }

    constructor(context: Context?, attributeSet: AttributeSet, defStyleAttr: Int): this(context, attributeSet) {
        initTextFont(context)
    }

    private fun initTextFont(context: Context?) {
        val typeface = Typeface.createFromAsset(context?.assets, "fonts/7barSPBd.ttf")
        this.typeface = typeface
    }

}