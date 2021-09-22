package chom.arikui.waffle.digitalclockapp

import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView

class PopupSetting(private val activity: MainActivity) {

    var popupWindow: PopupWindow? = null
    lateinit var checkOverlayClock: CheckBox
    private lateinit var buttonBackgroundSetting: TextView
    private val fileIOWrapper = activity.fileIOWrapper

    companion object {
        private const val TAG = "POPUP_SETTING"
    }

    fun showPopup() {
        popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_popup_setting, null)
        checkOverlayClock = popupView.findViewById(R.id.check_permit_overlay_clock)
        checkOverlayClock.isChecked = ClockSettingDataHolder.validOverlayClock
        checkOverlayClock.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity)) {
                    val dialog = AttentionDialog.newInstance(activity.resources.getString(R.string.need_to_allow_display_over_other_apps_to_enable_overlay_clock), activity.getString(R.string.yes))
                    dialog.okListener = activity::gotoSettingOverlay
                    dialog.negListener = { view.isChecked = false }
                    dialog.show(activity.supportFragmentManager, TAG)
                } else {
                    processCheckOverlayChanging(true)
                }
            } else {
                processCheckOverlayChanging(false)
            }
        }

        buttonBackgroundSetting = popupView.findViewById(R.id.button_background_setting)
        buttonBackgroundSetting.setOnClickListener {
            activity.showPopupColor(activity.findViewById<FrameLayout>(R.id.activity_root))
        }

        popupWindow?.contentView = popupView
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true

        val d = activity.windowManager.defaultDisplay
        val p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow?.width = p2.x - CalculateUtil.convertDp2Px(120, activity).toInt()

        // 画面中央に表示
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    fun processCheckOverlayChanging(isChecked: Boolean) {
        ClockSettingDataHolder.validOverlayClock = isChecked
        fileIOWrapper.saveValidOverlayClock()
    }

}