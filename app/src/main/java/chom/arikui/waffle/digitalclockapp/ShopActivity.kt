package chom.arikui.waffle.digitalclockapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import chom.arikui.waffle.digitalclockapp.databinding.ShopActivityBinding
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult

class ShopActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SHOP_ACT"
    }

    // 課金サービスへのアクセス
    private lateinit var mBillingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ShopActivityBinding>(
            this, R.layout.shop_activity
        )

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        mBillingClient = BillingClient.newBuilder(this).setListener { billingResult, list ->

        }.enablePendingPurchases().build()
        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Google Playとの接続に失敗した旨のダイアログ表示
                val dialog = AttentionDialog.newInstance(
                        getString(R.string.failed_to_access_google_play), getString(R.string.ok))
                dialog.okListener = { finish() }
                dialog.show(supportFragmentManager, TAG)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                } else {
                    Log.e(TAG, "onBillingSetupFinishedResponse: ${billingResult.responseCode}")
                    // Google Playとの接続に失敗した旨のダイアログ表示
                    val dialog = AttentionDialog.newInstance(
                        getString(R.string.failed_to_access_google_play), getString(R.string.ok))
                    dialog.okListener = { finish() }
                    dialog.show(supportFragmentManager, TAG)
                }
            }
        })
        // PENDING -> PURCHASEDに移行した購入が残っていないかを確認するため、フェッチする
        mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);

        binding.buttonUpgrade.setOnClickListener {

        }
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}