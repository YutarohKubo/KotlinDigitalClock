package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import chom.arikui.waffle.digitalclockapp.databinding.ShopActivityBinding
import com.android.billingclient.api.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShopActivity : AppCommonActivity(), CoroutineScope {

    companion object {
        private const val TAG = "SHOP_ACT"
        private const val ID_PREMIUM_MEMBERSHIP = "premium_membership"
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

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
            if (list != null) {
                Log.i(TAG, "purchase update callback handlePurchase()")
                handlePurchase(billingResult, list)
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
                                launch {
                                    mSkuDetails = skuDetailsList[0]
                                    // 価格表示を更新
                                    updatePrice()
                                }
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
        mBillingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { billingResult, list ->
            // 残っている購入を処理する
            Log.i(TAG, "purchase fetch handlePurchase()")
            handlePurchase(billingResult, list)
        }

        buttonUpgrade = binding.buttonUpgrade
        mViewModel.premiumPriceLV.value = getString(R.string.upgrade)
        mViewModel.upgradeEnable.value = false
        buttonUpgrade.setOnClickListener {
            // 購入フローの開始
            launchBillingFlow()
        }

        // 価格表示を初期化
        updatePrice()
    }

    /**
     * アップグレードボタンの表示価格を更新する
     */
    private fun updatePrice() {
        val price = mSkuDetails?.price
        if (TextUtils.isEmpty(price)) {
            mViewModel.premiumPriceLV.value = getString(R.string.upgrade, "")
            mViewModel.upgradeEnable.value = false
        } else {
            mViewModel.premiumPriceLV.value = getString(R.string.upgrade, " (${price})")
            mViewModel.upgradeEnable.value = true
        }
        mViewModel.upgradeVisible.value = !(terminalData?.mPremiumState ?: true)
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

    /**
     * 購入を処理する
     */
    private fun handlePurchase(billingResult: BillingResult, list: List<Purchase>) {
        launch {
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(TAG, "Purchases updated is failed. response code : " + billingResult.responseCode)
                return@launch
            }
            if (list.isEmpty()) {
                Log.e(TAG, "onPurchasesUpdated() : Purchase list is invalid.")
                return@launch
            }
            val purchase = list[0]
            if (purchase.skus[0] == ID_PREMIUM_MEMBERSHIP) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    // プレミアム会員であることを、シェアプリへ保存する
                    savePremiumState()
                    // UIの更新
                    updatePrice()
                    // 購入を承認する
                    consumePurchase(purchase)
                } else {
                    val dialog = AttentionDialog.newInstance(getString(R.string.msg_update_premium_state_pending), getString(R.string.ok))
                    dialog.show(supportFragmentManager, TAG)
                }
            } else {
                Log.e(TAG, "onConsumeResponse() : purchase sku is invalid.")
            }
        }
    }

    /**
     * プレミアム会員として、シェアプリに保存する
     */
    private fun savePremiumState() {
        val preference = getSharedPreferences(SharedPreferenceManagement.NAME_SHOP_ITEM, Context.MODE_PRIVATE)
        val editor = preference.edit()
        editor.putBoolean(SharedPreferenceManagement.KeyData.KEY_PREMIUM_BOOLEAN.keyName, true)
        editor.apply()
        val tmData = terminalData
        tmData?.mPremiumState = true
    }

    /**
     * 1度限りの購入を承認する
     */
    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
            mBillingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {

            }
            Log.i(TAG, "finish acknowledge purchase.")
        }
    }

    /**
     * 消費可能アイテムの購入を承認する
     */
    private fun consumePurchase(purchase: Purchase) {
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
        mBillingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                Log.e(
                    TAG,
                    "Consume response is failed. response code : " + billingResult.responseCode
                )
                return@consumeAsync
            }
            Log.i(TAG, "purchaseToken = $purchaseToken")
        }
    }
}