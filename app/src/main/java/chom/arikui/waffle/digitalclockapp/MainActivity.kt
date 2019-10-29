package chom.arikui.waffle.digitalclockapp

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Point
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainAct"
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS")
        private val dayFormat = SimpleDateFormat("dd")
        private val monthFormat = SimpleDateFormat("MM")
        private val yearFormat = SimpleDateFormat("yyyy")
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
        private val secondFormat = SimpleDateFormat("ss")
        private const val RELOAD_TIME_HANDLE_ID = 1
        private const val ALARM_MONITOR_HANDLE_ID = 2
        private const val ALARM_TIME_FILE_NAME = "alarm_time.dc"
        private const val NOW_ALARM_SOUND_FINE_NAME = "now_alarm_sound.dc"
        private const val ALARM_SOUND_CHECK_STATE_FILE_NAME = "alarm_sound_check_state.dc"
    }

    var popupWindow: PopupWindow? = null
    private val listAlarmData = arrayListOf<RingtoneData>()
    private var mp: MediaPlayer? = null
    private var imageAlarm: ImageView? = null
    var nowTime = Date()
    var alarmTime = "00:00"
    private var nowAlarmSound: RingtoneData? = null
    var alarmCheckState = false
    var isAlarmRinging = false
    var isTryPlayingAlarm = false
    var switchAlarm: Switch? = null
    var textTopAlarmTime: TextView? = null
    var textNowSound: TextView? = null
    private val alarmBroadcastReceiver = AlarmBroadcastReceiver()

    private val timerHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                RELOAD_TIME_HANDLE_ID -> {
                    val cal = Calendar.getInstance()
                    nowTime = cal.time
                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (text_now_date != null) {
                            text_now_date.text = dateFormat.format(nowTime)
                            text_now_time.text = timeFormat.format(nowTime)
                        }
                    } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        if (text_now_day != null) {
                            text_now_day.text = dayFormat.format(nowTime)
                            text_now_month.text = monthFormat.format(nowTime)
                            text_now_year.text = yearFormat.format(nowTime)
                            text_now_week.text = when (cal.get(Calendar.DAY_OF_WEEK)) {
                                Calendar.SUNDAY -> "SUN"
                                Calendar.MONDAY -> "MON"
                                Calendar.TUESDAY -> "TUE"
                                Calendar.WEDNESDAY -> "WED"
                                Calendar.THURSDAY -> "THU"
                                Calendar.FRIDAY -> "FRI"
                                Calendar.SATURDAY -> "SAT"
                                else -> "ERR"
                            }
                            text_now_hour.text = hourFormat.format(nowTime)
                            text_now_minute.text = minuteFormat.format(nowTime)
                            text_now_second.text = secondFormat.format(nowTime)
                        }
                    } else {

                    }
                }
                ALARM_MONITOR_HANDLE_ID -> {
                    val alarmTimeArray = alarmTime.split(":")
                    if (Integer.parseInt(alarmTimeArray[0]) == Integer.parseInt(hourFormat.format(nowTime))
                            && Integer.parseInt(alarmTimeArray[1]) == Integer.parseInt(minuteFormat.format(nowTime))
                            && alarmCheckState
                            && !isAlarmRinging) {
                        showAlarmRingingDialog()
                        soundAlarm()
                        Log.i(TAG, "ringing alarm")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Screenがスリープ状態になるのを拒否
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        loadNowAlarmSound()
        loadAlarmTime()
        loadAlarmCheckState()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageAlarm = image_alarm
            switchAlarmResource()
            top_alarm_area.setOnClickListener { _ ->
                showPopup()
            }

            textTopAlarmTime = text_top_alarm_time
            textTopAlarmTime?.text = alarmTime
        }

        val reloadTimeThread = Thread{
            while (true) {
                timerHandler.sendEmptyMessage(RELOAD_TIME_HANDLE_ID)
                Thread.sleep(1)
            }
        }
        val alarmMonitorThread = Thread{
            while (true) {
                timerHandler.sendEmptyMessage(ALARM_MONITOR_HANDLE_ID)
                Thread.sleep(100)
            }
        }

        reloadTimeThread.start()
        alarmMonitorThread.start()

        /*val filter = IntentFilter()
        filter.addAction(ServiceAction.MONITORING_ALARM.toString())
        registerReceiver(alarmBroadcastReceiver, filter)*/
    }

    override fun onResume() {
        super.onResume()
        //stopAlarmService()

    }

    override fun onPause() {
        super.onPause()
        //startAlarmService(ServiceType.ACTIVITY_DESTROYED)
    }

    private fun loadAlarmData () {
        val manager = RingtoneManager(this)
        manager.setType(RingtoneManager.TYPE_ALL)
        val cursor = manager.cursor
        while (cursor.moveToNext()) {
            listAlarmData.add(RingtoneData(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    cursor.getString(RingtoneManager.ID_COLUMN_INDEX),
                    cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)))
        }
    }

    fun switchAlarmResource () =
            if (alarmCheckState) {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_setting)
                textNowSound?.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        } else {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_normal)
                textNowSound?.setTextColor(ContextCompat.getColor(this, R.color.pamplemousse))
        }

    private fun startAlarmService (serviceType: ServiceType) {
        val intent = Intent()
        intent.setClassName(packageName, "$packageName.AlarmService")
        intent.putExtra(AlarmService.SERVICE_TYPE, serviceType)
        intent.putExtra(AlarmService.ALARM_TIME, alarmTime)
        intent.putExtra(AlarmService.NOW_ALARM_SOUND_URI, nowAlarmSound?.uri)
        intent.putExtra(AlarmService.ALARM_CHECK_STATE, alarmCheckState)

        startService(intent)
    }

    private fun stopAlarmService() {
        intent = Intent()
        intent.setClassName(packageName, "$packageName.AlarmService")
        stopService(intent)
    }

    fun showAlarmRingingDialog () {
        val dialog = AlarmRingingDialog.newInstance()
        try {
            dialog.show(supportFragmentManager, TAG)
        } catch (e: IllegalStateException) {

        }
    }

    fun soundAlarm () {
        isAlarmRinging = true
        mp = MediaPlayer.create(this, Uri.parse(nowAlarmSound?.uri))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mp?.setAudioAttributes(
                    AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
        } else {
            mp?.setAudioStreamType(AudioManager.STREAM_ALARM)
        }
        mp?.isLooping = true
        mp?.start()
    }

    fun stopAlarm () {
        isAlarmRinging = false
        val tmpMp = mp ?: return
        if (tmpMp!!.isPlaying) {
            tmpMp.stop()
            tmpMp.prepare()
        }
    }

    private fun showPopup(){
        popupWindow = PopupWindow(this)
        val popupView = layoutInflater.inflate(R.layout.layout_alerm, null)
        (popupView.findViewById(R.id.button_change_alarm_time) as Button).setOnClickListener { _ ->
                val timeArray = alarmTime.split(":").map { it.trim() }
                val dialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    alarmTime = String.format("%02d:%02d", hourOfDay, minute)
                    (popupView.findViewById(R.id.text_alarm_time) as TextView).text = alarmTime
                    textTopAlarmTime?.text = alarmTime
                    saveAlarmTime()
                }, Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), true)
                dialog.show()
        }


        textNowSound = popupView.findViewById(R.id.text_now_sound) as TextView
        switchAlarmResource()
        val textAlarmTime = popupView.findViewById(R.id.text_alarm_time) as TextView
        switchAlarm = popupView.findViewById(R.id.switch_alarm) as Switch
        textNowSound?.text = nowAlarmSound?.title
        textAlarmTime.text = alarmTime
        switchAlarm?.isChecked = alarmCheckState
        switchAlarm?.setOnCheckedChangeListener { view, isChecked ->
            alarmCheckState = isChecked
            switchAlarmResource()
            saveAlarmCheckState()
        }
        val imageListenMusic = popupView.findViewById(R.id.image_listen_music) as ImageView
        imageListenMusic.setOnClickListener { view ->
            if (isTryPlayingAlarm) {
                stopAlarm()
                (view as ImageView).setImageResource(R.drawable.play_music)
                isTryPlayingAlarm = false
            } else {
                soundAlarm()
                (view as ImageView).setImageResource(R.drawable.stop_music)
                isTryPlayingAlarm = true
            }
        }
        val listAlarm = popupView.findViewById(R.id.list_sounds) as ListView
        val adapterAlarm = AdapterListAlarm(this, R.layout.layout_alerm_item, listAlarmData)
        listAlarm.adapter = adapterAlarm
        listAlarm.setOnItemClickListener { parent, view, position, id ->
            val listView = parent as ListView
            nowAlarmSound = listView.getItemAtPosition(position) as RingtoneData
            textNowSound?.text = nowAlarmSound?.title
            saveNowAlarmSound()
            if (isTryPlayingAlarm) {
                stopAlarm()
                soundAlarm()
            }
        }


        popupWindow?.contentView = popupView
        popupWindow?.isOutsideTouchable = true
        popupWindow?.isFocusable = true

        val d = windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow?.width = p2.x - 200
        popupWindow?.height = p2.y - 180

        // 画面中央に表示
        popupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun  attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun loadAlarmTime () {

        try {
            BufferedReader(InputStreamReader(this.openFileInput(ALARM_TIME_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    alarmTime = line
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveAlarmTime () {
        try {
            BufferedWriter(OutputStreamWriter(this.openFileOutput(ALARM_TIME_FILE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(alarmTime)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadNowAlarmSound () {

        loadAlarmData()

        try {
            BufferedReader(InputStreamReader(this.openFileInput(NOW_ALARM_SOUND_FINE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    for (data in listAlarmData) {
                        if (line == data.uri) {
                            nowAlarmSound = data
                            break
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (nowAlarmSound == null) {
                //Uriが見当たらなかった場合、listAlarmDataの一番上をアラーム音に設定する
                nowAlarmSound = listAlarmData[0]
            }
        }
    }

    private fun saveNowAlarmSound () {
        try {
            BufferedWriter(OutputStreamWriter(this.openFileOutput(NOW_ALARM_SOUND_FINE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(nowAlarmSound?.uri)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadAlarmCheckState () {

        try {
            BufferedReader(InputStreamReader(this.openFileInput(ALARM_SOUND_CHECK_STATE_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    alarmCheckState = line.toBoolean()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveAlarmCheckState () {
        try {
            BufferedWriter(OutputStreamWriter(this.openFileOutput(ALARM_SOUND_CHECK_STATE_FILE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(alarmCheckState)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    class AdapterListAlarm(val context: Context, val resource: Int, val dataList: List<RingtoneData>) : BaseAdapter() {

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            val item = getItem(p0) as RingtoneData
            var convertView = p1

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resource, null)
            }

            val textAlarmTitle = convertView!!.findViewById(R.id.text_alarm_title) as TextView
            textAlarmTitle.text = item.title

            return convertView
        }

        override fun getItem(p0: Int): Any {
            return dataList[p0]
        }

        override fun getItemId(p0: Int): Long {
            return 0
        }

        override fun getCount(): Int {
            return dataList.size
        }

    }
}
