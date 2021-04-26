package chom.arikui.waffle.digitalclockapp

import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.widget.CheckBox
import android.widget.PopupWindow

class PopupSetting(private val activity: MainActivity) {

    var popupWindow: PopupWindow? = null
    lateinit var checkOverlayClock: CheckBox
    private val settingDataHolder = activity.settingDataHolder

    companion object {
        private const val TAG = "POPUP_SETTING"
    }

    // Todo 実装これから
    fun showPopup() {
        popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_popup_setting, null)
        checkOverlayClock = popupView.findViewById(R.id.check_permit_overlay_clock)
        checkOverlayClock.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(activity)) {
                    val dialog = AttentionDialog.newInstance(activity.resources.getString(R.string.need_to_allow_display_over_other_apps_to_enable_overlay_clock))
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

        popupWindow?.contentView = popupView
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true

        val d = activity.windowManager.defaultDisplay
        val p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow?.width = p2.x - 200

        // 画面中央に表示
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    fun processCheckOverlayChanging(isChecked: Boolean) {
        settingDataHolder.validOverlayClock = isChecked
    }

}