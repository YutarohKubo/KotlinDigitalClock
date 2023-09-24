package chom.arikui.waffle.digitalclockapp

import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*

class PopupSetting(private val activity: MainActivity) {

    var popupWindow: PopupWindow? = null
    lateinit var checkOverlayClock: CheckBox
    private lateinit var buttonBackgroundSetting: TextView
    private lateinit var buttonUpgradeSetting: TextView
    private val fileIOWrapper = activity.fileIOWrapper

    companion object {
        private const val TAG = "POPUP_SETTING"
    }

    fun showPopup() {
        popupWindow = PopupWindow(activity)
        popupWindow!!.setBackgroundDrawable(null)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_popup_setting, null)
        // 表示時のアニメーション設定
        val animShowPopup = AnimationUtils.loadAnimation(activity, R.anim.popup_window_show_effect)
        popupView.animation = animShowPopup
        checkOverlayClock = popupView.findViewById(R.id.check_permit_overlay_clock)
        checkOverlayClock.isChecked = ClockSettingDataHolder.validOverlayClock
        checkOverlayClock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkOverlayClockPermission()
            } else {
                processCheckOverlayChanging(false)
            }
        }

        buttonBackgroundSetting = popupView.findViewById(R.id.button_background_setting)
        buttonBackgroundSetting.setOnClickListener {
            activity.showPopupColor(activity.findViewById<FrameLayout>(R.id.activity_root))
        }
        buttonUpgradeSetting = popupView.findViewById(R.id.button_upgrade_setting)
        buttonUpgradeSetting.setOnClickListener {
            //ショップのアクティビティへのインテント発行
            val intent = Intent(activity, ShopActivity::class.java)
            activity.startActivity(intent)
        }

        // バツボタン
        val buttonClose = popupView.findViewById<ImageView>(R.id.button_popup_setting_close)
        buttonClose.setOnClickListener {
            popupWindow?.dismiss()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // KITKAT以前の端末は、背景画像の設定をサポートしないため、アップグレードもサポートしない
            buttonUpgradeSetting.visibility = View.GONE
            val textUpgradeSetting = popupView.findViewById<TextView>(R.id.text_title_upgrade)
            textUpgradeSetting.visibility = View.GONE
        }

        popupWindow?.contentView = popupView
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true

        val d = activity.windowManager.defaultDisplay
        val p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow?.width = (p2.x * 1.0).toInt()

        // 画面中央に表示
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    fun processCheckOverlayChanging(isChecked: Boolean) {
        ClockSettingDataHolder.validOverlayClock = isChecked
        fileIOWrapper.saveValidOverlayClock()
    }

    /**
     * 常駐時計の権限チェック
     */
    fun checkOverlayClockPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API33以上の場合は、通知権限チェック
            if (!activity.isPostNotificationPermissionGranted()) {
                showPostNotificationDialog()
                return
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity)) {
            val dialog = AttentionDialog.newInstance(
                activity.resources.getString(R.string.need_to_allow_display_over_other_apps_to_enable_overlay_clock),
                activity.getString(R.string.yes),
                activity.getString(R.string.no)
            )
            dialog.okListener = activity::gotoSettingOverlay
            dialog.negListener = { checkOverlayClock.isChecked = false }
            dialog.show(activity.supportFragmentManager, TAG)
        } else {
            processCheckOverlayChanging(true)
        }
    }

    private fun showPostNotificationDialog() {
        val notificationDialog = AttentionDialog.newInstance(
            activity.getString(R.string.msg_grant_post_notification_permission),
            activity.getString(R.string.yes), activity.getString(R.string.no)
        )
        notificationDialog.okListener = {
            activity.requestPostNotificationPermission()
        }
        notificationDialog.negListener = {
            // 通知権限設定ダイアログで、いいえが押下された場合は、チェックをOFFに戻す
            checkOverlayClock.isChecked = false
        }
        notificationDialog.show(activity.supportFragmentManager, TAG)
    }

}