package chom.arikui.waffle.digitalclockapp

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.InterstitialAd

open class AppCommonActivity : AppCompatActivity() {

    companion object {
        var terminalData: TerminalData? = null

        /**
         * プレミアム会員にアップグレード済かどうか判断する
         */
        fun isUpgradedPremium() = terminalData?.mPremiumState == true
    }

    // 広告インスタンス
    protected var mInterAd0: InterstitialAd? = null
    // 広告表示対象のキーの押下回数
    protected var countClickAdKey = 0

    /**
     * ナビゲーションバーを非表示にする
     */
    protected fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    /**
     * Viewの輝度半輝度を、切り替える
     */
    protected fun setViewEnable(view: View, isEnable: Boolean) {
        view.isEnabled = isEnable
        if (isEnable) {
            view.alpha = 1.0f
        } else {
            view.alpha = 0.3f
        }
    }

    /**
     * 端末データをロードする
     */
    protected fun loadTerminalData() {
        terminalData = TerminalData(this)
    }
}