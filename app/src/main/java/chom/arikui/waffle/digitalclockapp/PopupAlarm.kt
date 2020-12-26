package chom.arikui.waffle.digitalclockapp

import android.app.TimePickerDialog
import android.graphics.Point
import android.view.Gravity
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class PopupAlarm(private val activity: MainActivity) {

    private lateinit var textNowSound: TextView
    private val settingDataHolder = activity.settingDataHolder
    private var isTryPlayingAlarm = false

    companion object {
        private const val TAG = "POPUP_ALARM"
    }

    fun showPopup() {
        val popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_alerm, null)
        val fileIOWrapper = activity.fileIOWrapper

        val textAlarmTime = popupView.findViewById<TextView>(R.id.text_alarm_time)
        popupView.findViewById<ImageButton>(R.id.button_change_alarm_time).setOnClickListener { _ ->
            val timeArray = settingDataHolder.alarmTime.split(":").map { it.trim() }
            val dialog = TimePickerDialog(activity, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                settingDataHolder.alarmTime = String.format("%02d:%02d", hourOfDay, minute)
                textAlarmTime.text = settingDataHolder.alarmTime
                val textTopAlarmTime = activity.findViewById<TextView>(R.id.text_top_alarm_time)
                textTopAlarmTime.text = settingDataHolder.alarmTime
                fileIOWrapper.saveAlarmTime()
                activity.startAlarmManager()
            }, Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), true)

            dialog.show()
        }

        textNowSound = popupView.findViewById(R.id.text_now_sound)
        switchAlarmResource()
        val switchAlarm = popupView.findViewById<SwitchCompat>(R.id.switch_alarm)
        textNowSound.text = settingDataHolder.nowAlarmSound?.title
        if (settingDataHolder.alarmCheckState) {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        } else {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.pamplemousse))
        }
        textAlarmTime.text = settingDataHolder.alarmTime
        textAlarmTime.setTextColor(settingDataHolder.colorTopAlarmTime)
        switchAlarm?.isChecked = settingDataHolder.alarmCheckState
        switchAlarm?.setOnCheckedChangeListener { _, isChecked ->
            settingDataHolder.alarmCheckState = isChecked
            switchAlarmResource()
            fileIOWrapper.saveAlarmCheckState()
            activity.startAlarmManager()
            if (isChecked) {
                Toast.makeText(activity, "Alarm on", Toast.LENGTH_SHORT).show()
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
            settingDataHolder.nowAlarmSound = listView.getItemAtPosition(position) as RingtoneData
            textNowSound.text = settingDataHolder.nowAlarmSound?.title
            fileIOWrapper.saveNowAlarmSound()
            if (isTryPlayingAlarm) {
                activity.stopAlarm()
                activity.soundAlarm()
            }
        }

        popupWindow.contentView = popupView
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.setOnDismissListener {
            activity.stopAlarm()
            isTryPlayingAlarm = false
        }

        val d = activity.windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow.width = p2.x - 200
        popupWindow.height = p2.y - 180

        // 画面中央に表示
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun switchAlarmResource() {
        activity.switchAlarmResource()
        if (settingDataHolder.alarmCheckState) {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        } else {
            textNowSound.setTextColor(ContextCompat.getColor(activity, R.color.pamplemousse))
        }
    }

}