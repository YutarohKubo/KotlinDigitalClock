package chom.arikui.waffle.digitalclockapp

import android.app.TimePickerDialog
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class PopupAlarm(private val activity: MainActivity) {

    var popupWindow: PopupWindow? = null
    private lateinit var textNowSound: TextView
    lateinit var switchAlarm: SwitchCompat
    private val fileIOWrapper = activity.fileIOWrapper
    private var isTryPlayingAlarm = false

    companion object {
        private const val TAG = "POPUP_ALARM"
    }

    fun showPopup() {
        popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_alerm, null)

        val textAlarmTime = popupView.findViewById<TextView>(R.id.text_alarm_time)
        popupView.findViewById<View>(R.id.button_alarm_time_setting).setOnClickListener { _ ->
            val timeArray = ClockSettingDataHolder.alarmTime.split(":").map { it.trim() }
            val dialog = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                ClockSettingDataHolder.alarmTime = String.format("%02d:%02d", hourOfDay, minute)
                textAlarmTime.text = ClockSettingDataHolder.alarmTime
                val textTopAlarmTime = activity.findViewById<TextView>(R.id.text_top_alarm_time)
                textTopAlarmTime.text = ClockSettingDataHolder.alarmTime
                fileIOWrapper.saveAlarmTime()
                activity.startAlarmManager()
            }, Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), true)

            dialog.show()
        }

        textNowSound = popupView.findViewById(R.id.text_now_sound)
        switchAlarmResource()
        switchAlarm = popupView.findViewById(R.id.switch_alarm)
        textNowSound.text = ClockSettingDataHolder.nowAlarmSound?.title
        if (ClockSettingDataHolder.alarmCheckState) {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        } else {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.pamplemousse))
        }
        textAlarmTime.text = ClockSettingDataHolder.alarmTime
        textAlarmTime.setTextColor(ClockSettingDataHolder.colorTopAlarmTime)
        switchAlarm.isChecked = ClockSettingDataHolder.alarmCheckState
        switchAlarm.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Settings.canDrawOverlays(activity)) {
                    val dialog = AttentionDialog.newInstance(activity.resources.getString(R.string.need_to_allow_display_over_other_apps_to_enable_alarm), activity.getString(R.string.yes))
                    dialog.okListener = activity::gotoSettingOverlay
                    dialog.negListener = { view.isChecked = false }
                    dialog.show(activity.supportFragmentManager, TAG)
                } else {
                    processSwitchAlarmChanging(true)
                }
            } else {
                processSwitchAlarmChanging(false)
            }
        }
        val imageListenMusic = popupView.findViewById(R.id.image_listen_music) as ImageView
        imageListenMusic.setOnClickListener { view ->
            isTryPlayingAlarm = if (isTryPlayingAlarm) {
                activity.stopAlarm()
                (view as ImageView).setImageResource(R.drawable.play_music)
                false
            } else {
                activity.soundAlarm()
                (view as ImageView).setImageResource(R.drawable.stop_music)
                true
            }
        }
        val listAlarm = popupView.findViewById<ListView>(R.id.list_sounds)
        val adapterAlarm = AdapterListAlarm(activity, R.layout.layout_alerm_item, activity.listAlarmData)
        listAlarm.adapter = adapterAlarm
        listAlarm.setOnItemClickListener { parent, _, position, _ ->
            val listView = parent as ListView
            ClockSettingDataHolder.nowAlarmSound = listView.getItemAtPosition(position) as RingtoneData
            textNowSound.text = ClockSettingDataHolder.nowAlarmSound?.title
            fileIOWrapper.saveNowAlarmSound()
            activity.startAlarmManager()
            if (isTryPlayingAlarm) {
                activity.stopAlarm()
                activity.soundAlarm()
            }
        }

        popupWindow?.contentView = popupView
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true
        popupWindow?.setOnDismissListener {
            activity.stopAlarm()
            isTryPlayingAlarm = false
        }

        val d = activity.windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow?.width = p2.x - 200
        popupWindow?.height = p2.y - 180

        // 画面中央に表示
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun switchAlarmResource() {
        activity.switchAlarmResource()
        if (ClockSettingDataHolder.alarmCheckState) {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        } else {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.pamplemousse))
        }
    }

    fun processSwitchAlarmChanging(isChecked: Boolean) {
        ClockSettingDataHolder.alarmCheckState = isChecked
        switchAlarmResource()
        fileIOWrapper.saveAlarmCheckState()
        activity.startAlarmManager()
        if (isChecked) {
            Toast.makeText(activity, "Alarm on", Toast.LENGTH_SHORT).show()
        }
    }

}