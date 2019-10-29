package chom.arikui.waffle.digitalclockapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.SimpleDateFormat

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ALMRCVER"
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p0 is MainActivity) {
            val alarmTimeArray = p0.alarmTime.split(":")
            if (Integer.parseInt(alarmTimeArray[0]) == Integer.parseInt(hourFormat.format(p0.nowTime))
                    && Integer.parseInt(alarmTimeArray[1]) == Integer.parseInt(minuteFormat.format(p0.nowTime))
                    && p0.alarmCheckState
                    && !p0.isAlarmRinging) {
                p0.showAlarmRingingDialog()
                p0.soundAlarm()
                Log.i(TAG, "ringing alarm")
            } /*else {
                Log.i(TAG, "stop alarm")
                p0.stopAlarm()
            }*/
        }
    }
}