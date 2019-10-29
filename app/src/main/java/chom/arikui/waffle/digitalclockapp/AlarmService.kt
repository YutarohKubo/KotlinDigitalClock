package chom.arikui.waffle.digitalclockapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
class AlarmService : Service() {

    companion object {
        private const val TAG = "ALMSVC"
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
        public const val SERVICE_TYPE = "service_type"
        public const val ALARM_TIME = "alarm_time"
        public const val NOW_ALARM_SOUND_URI = "now_alarm_sound_uri"
        public const val ALARM_CHECK_STATE = "alarm_check_state"
    }

    var isAlarmRinging = false
    var thread: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val type = intent?.getSerializableExtra(SERVICE_TYPE)
        when(type) {
            ServiceType.ACTIVITY_RUNNING -> {
                val thread = Thread{
                    while (true) {
                        sendBroadcast(Intent(ServiceAction.MONITORING_ALARM.toString()))
                        Log.i(TAG, type.toString())
                        Thread.sleep(100)
                    }
                }
                thread.start()
            }
            ServiceType.ACTIVITY_DESTROYED -> {
                val alarmTime = intent.getStringExtra(ALARM_TIME)
                val nowAlarmSoundUri = intent.getStringExtra(NOW_ALARM_SOUND_URI)
                val alarmCheckState = intent.getBooleanExtra(ALARM_CHECK_STATE, false)
                val alarmTimeArray = alarmTime.split(":")
                thread = Thread {
                    while (true) {
                        val cal = Calendar.getInstance()
                        Log.i(TAG, type.toString())
                        if (Integer.parseInt(alarmTimeArray[0]) == Integer.parseInt(hourFormat.format(cal.time))
                                && Integer.parseInt(alarmTimeArray[1]) == Integer.parseInt(minuteFormat.format(cal.time))
                                && alarmCheckState) {
                            val intentStartAct = Intent(this, MainActivity::class.java)
                            intentStartAct.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            application.startActivity(intentStartAct)
                            break
                        }
                        Thread.sleep(100)
                    }
                }
                thread?.start()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        thread?.join()
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}