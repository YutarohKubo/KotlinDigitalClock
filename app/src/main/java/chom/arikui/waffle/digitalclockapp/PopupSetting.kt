package chom.arikui.waffle.digitalclockapp

import android.graphics.Point
import android.view.Gravity
import android.widget.CheckBox
import android.widget.PopupWindow

class PopupSetting(private val activity: MainActivity) {

    companion object {
        private const val TAG = "POPUP_SETTING"
    }

    // Todo 実装これから
    fun showPopup() {
        val popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_popup_setting, null)
        val checkOverlayClock = popupView.findViewById<CheckBox>(R.id.check_permit_overlay_clock)
        checkOverlayClock.setOnCheckedChangeListener { view, isChecked ->

        }

        popupWindow.contentView = popupView
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        val d = activity.windowManager.defaultDisplay
        val p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow.width = p2.x - 200

        // 画面中央に表示
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

}