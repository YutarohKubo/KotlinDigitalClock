package chom.arikui.waffle.digitalclockapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "ALMRCVER"
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.i(TAG, "Launch Receiver")
            val startActivityIntent = Intent(p0, AlarmRingingActivity::class.java)
            startActivityIntent.putExtra("alarm_uri", p1?.getStringExtra("alarm_uri"))
            startActivityIntent.putExtra("alarm_time", p1?.getStringExtra("alarm_time"))
            startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            p0?.startActivity(startActivityIntent)
    }
}