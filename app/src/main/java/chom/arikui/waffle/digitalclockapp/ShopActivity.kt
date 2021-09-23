package chom.arikui.waffle.digitalclockapp

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import chom.arikui.waffle.digitalclockapp.databinding.ShopActivityBinding
import com.android.billingclient.api.*

class ShopActivity : AppCommonActivity() {

    companion object {
        private const val TAG = "SHOP_ACT"
        private const val ID_PREMIUM_MEMBERSHIP = "premium_membership"
    }

    // 課金サービスへのアクセス
    private lateinit var mBillingClient: BillingClient
    // プレミアム会員のSkuDetails
    private var mSkuDetails: SkuDetails? = null
    // アップグレードボタン
    private lateinit var buttonUpgrade: TextView
    private lateinit var mViewModel: ShopVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ShopActivityBinding>(
            this, R.layout.shop_activity
        )
        mViewModel = ViewModelProvider(this).get(ShopVM::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = mViewModel

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        )

        mBillingClient = BillingClient.newBuilder(this).setListener { billingResult, list ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Purchases updated is failed. response code : " + billingResult.responseCode)
                return@setListener
            }
            if (list?.isEmpty() == true) {
                Log.e(TAG, "onPurchasesUpdated() : Purchase list is invalid.")
                return@setListener
            }
            val purchase = list?.get(0)
            if (purchase?.skus?.get(0) == ID_PREMIUM_MEMBERSHIP) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // Todo プレミアム会員登録処理を実施する
                }
            } else {
                Log.e(TAG, "onConsumeResponse() : purchase sku is invalid.")
            }
        }.enablePendingPurchases().build()
        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                // Google Playとの接続に失敗した旨のダイアログ表示
                val dialog = AttentionDialog.newInstance(
                    getString(R.string.failed_to_access_google_play), getString(R.string.ok)
                )
                dialog.okListener = { finish() }
                dialog.show(supportFragmentManager, TAG)
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val skuList = arrayListOf<String>()
                    skuList.add(ID_PREMIUM_MEMBERSHIP)
                    val params = SkuDetailsParams.newBuilder()
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
                    mBillingClient.querySkuDetailsAsync(params.build()) { billingResult2, skuDetailsList ->
                        if (billingResult2.responseCode == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList?.isNotEmpty() == true) {
                                mSkuDetails = skuDetailsList[0]
                                Log.i(TAG, "aaaa")
                                updatePrice()
                                Log.i(TAG, "bbbb")
                                //setViewEnable(buttonUpgrade, true)
                                mViewModel.upgradeEnable.value = true
                                Log.i(TAG, "cccc")
                            }
                        } else {
                            Log.e(TAG, "onSkuDetailsResponse: ${billingResult2.responseCode}")
                            // Google Playとの接続に失敗した旨のダイアログ表示
                            val dialog = AttentionDialog.newInstance(
                                getString(R.string.failed_to_access_google_play),
                                getString(R.string.ok)
                            )
                            dialog.okListener = { finish() }
                            dialog.show(supportFragmentManager, TAG)
                        }
                    }
                } else {
                    Log.e(TAG, "onBillingSetupFinishedResponse: ${billingResult.responseCode}")
                    // Google Playとの接続に失敗した旨のダイアログ表示
                    val dialog = AttentionDialog.newInstance(
                        getString(R.string.failed_to_access_google_play), getString(R.string.ok)
                    )
                    dialog.okListener = { finish() }
                    dialog.show(supportFragmentManager, TAG)
                }
            }
        })
        // PENDING -> PURCHASEDに移行した購入が残っていないかを確認するため、フェッチする
        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, _ ->
            // Do nothing
        }

        buttonUpgrade = binding.buttonUpgrade
        mViewModel.premiumPriceLV.value = getString(R.string.upgrade)
        mViewModel.upgradeEnable.value = false
        buttonUpgrade.setOnClickListener {
            launchBillingFlow()
        }
    }

    /**
     * アップグレードボタンの表示価格を更新する
     */
    private fun updatePrice() {
        mViewModel.premiumPriceLV.value = mSkuDetails?.price
    }

    /**
     * プレミアム会員購入フローを開始する
     */
    private fun launchBillingFlow() {
        if (mSkuDetails == null) {
            Log.e(TAG, "Premium sku details is null.")
            return
        }
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(mSkuDetails!!).build()
        val resCode = mBillingClient.launchBillingFlow(this, billingFlowParams).responseCode
        if (resCode != BillingClient.BillingResponseCode.OK) {
            Log.e(TAG, "launchBillingFlow() : Response error code $resCode")
        }
    }
}