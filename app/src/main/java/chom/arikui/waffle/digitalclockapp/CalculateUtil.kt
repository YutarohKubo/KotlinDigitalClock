package chom.arikui.waffle.digitalclockapp

import android.content.Context

object CalculateUtil {

    /**
     * dpからpixelへの変換
     * @param dp
     * @param context
     * @return float pixel
     */
    fun convertDp2Px(dp: Int, context: Context): Float{
        val metrics = context.resources.displayMetrics
        return dp * metrics.density
    }

    /**
     * pixelからdpへの変換
     * @param px
     * @param context
     * @return float dp
     */
    fun convertPx2Dp(px: Int, context: Context): Float{
        val metrics = context.resources.displayMetrics
        return px / metrics.density;
    }

}