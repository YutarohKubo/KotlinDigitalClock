package chom.arikui.waffle.digitalclockapp

import android.content.Context
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import java.nio.ByteBuffer

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

    /**
     * bitmapをバイト配列に変換する
     *
     * @param bitmap ビットマップ
     * @return 変換後のバイト配列
     */
    fun bmp2byteArray(bitmap: Bitmap): ByteArray {
        val resultArr = ByteArray(1000000)
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(resultArr))
        return resultArr
    }

    /**
     * bitmapをバイト配列に変換する
     *
     * @param bitmap ビットマップ
     * @param format 圧縮フォーマット (デフォルト jpeg)
     * @param compressVal 圧縮率 (デフォルト 100(底圧縮・高画質))
     * @return 変換後のバイト配列
     */
    fun bmp2byteArray2(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, compressVal: Int = 100): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(format, compressVal, baos)
        return baos.toByteArray()
    }

}