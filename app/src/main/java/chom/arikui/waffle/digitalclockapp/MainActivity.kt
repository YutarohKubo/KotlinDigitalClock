package chom.arikui.waffle.digitalclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.text.SimpleDateFormat
import java.util.*
import java.io.*

class MainActivity() : AppCompatActivity(), View.OnLongClickListener {

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
        private const val DEFAULT_COLOR_RED_VALUE = 127
        private const val DEFAULT_COLOR_GREEN_VALUE = 176
        private const val DEFAULT_COLOR_BLUE_VALUE = 0
        private const val ALARM_TIME_FILE_NAME = "alarm_time.dc"
        private const val NOW_ALARM_SOUND_FINE_NAME = "now_alarm_sound.dc"
        private const val ALARM_SOUND_CHECK_STATE_FILE_NAME = "alarm_sound_check_state.dc"
        private const val NOW_DAY_COLOR_FILE_NAME = "now_day_color.dc"
        private const val NOW_MONTH_COLOR_FILE_NAME = "now_month_color.dc"
        private const val NOW_YEAR_COLOR_FILE_NAME = "now_year_color.dc"
        private const val NOW_WEEK_COLOR_FILE_NAME = "now_week_color.dc"
        private const val NOW_HOUR_COLOR_FILE_NAME = "now_hour_color.dc"
        private const val DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME = "divide_hour_and_minute_color.dc"
        private const val NOW_MINUTE_COLOR_FILE_NAME = "now_minute_color.dc"
        private const val NOW_SECOND_COLOR_FILE_NAME = "now_second_color.dc"
        private const val TOP_ALARM_TIME_COLOR_FILE_NAME = "top_alarm_time_color.dc"

        private const val isTest = false
    }

    lateinit var textNowDay: TextView
    lateinit var textNowMonth: TextView
    lateinit var textNowYear: TextView
    lateinit var textNowWeek: TextView
    lateinit var textNowHour: TextView
    lateinit var textDivideHourAndMinute: TextView
    lateinit var textNowMinute: TextView
    lateinit var textNowSecond: TextView
    lateinit var textTopAlarmTime: TextView

    private var alarmPopup: PopupWindow? = null
    private var colorPopup: PopupWindow? = null
    private val listAlarmData = arrayListOf<RingtoneData>()
    private var mp: MediaPlayer = MediaPlayer()
    private var imageAlarm: ImageView? = null
    var nowTime = Date()
    var alarmTime = "00:00"
    private var nowAlarmSound: RingtoneData? = null
    var alarmCheckState = false
    var isTryPlayingAlarm = false
    var switchAlarm: Switch? = null
    var textNowSound: TextView? = null

    private lateinit var mInterAdCloseApp: InterstitialAd

    private val timerHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        mInterAdCloseApp = InterstitialAd(this)

        mInterAdCloseApp.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                mInterAdCloseApp.loadAd(adRequest)
            }
        }

        if (isTest) {
            mInterAdCloseApp.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        } else {
            mInterAdCloseApp.adUnitId = "ca-app-pub-6669415411907480/8088997953"
        }

        mInterAdCloseApp.loadAd(adRequest)

        //Screenがスリープ状態になるのを拒否
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        loadNowAlarmSound()
        loadAlarmTime()
        loadAlarmCheckState()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageAlarm = image_alarm
            switchAlarmResource()
            top_alarm_area.setOnClickListener { _ ->
                showAlarmPopup()
            }

            textTopAlarmTime = text_top_alarm_time
            textTopAlarmTime.text = alarmTime

            frame_now_day.setOnLongClickListener(this)
            textNowDay = text_now_day
            loadTextColor(textNowDay, NOW_DAY_COLOR_FILE_NAME)
            frame_now_month.setOnLongClickListener(this)
            textNowMonth = text_now_month
            loadTextColor(textNowMonth, NOW_MONTH_COLOR_FILE_NAME)
            frame_now_year.setOnLongClickListener(this)
            textNowYear = text_now_year
            loadTextColor(textNowYear, NOW_YEAR_COLOR_FILE_NAME)
            text_now_week.setOnLongClickListener(this)
            textNowWeek = text_now_week
            loadTextColor(textNowWeek, NOW_WEEK_COLOR_FILE_NAME)
            frame_now_hour.setOnLongClickListener(this)
            textNowHour = text_now_hour
            loadTextColor(textNowHour, NOW_HOUR_COLOR_FILE_NAME)
            text_divide_hour_and_minute.setOnLongClickListener(this)
            textDivideHourAndMinute = text_divide_hour_and_minute
            loadTextColor(textDivideHourAndMinute, DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            frame_now_minute.setOnLongClickListener(this)
            textNowMinute = text_now_minute
            loadTextColor(textNowMinute, NOW_MINUTE_COLOR_FILE_NAME)
            frame_now_second.setOnLongClickListener(this)
            textNowSecond = text_now_second
            loadTextColor(textNowSecond, NOW_SECOND_COLOR_FILE_NAME)
            frame_top_alarm_time.setOnLongClickListener(this)
            textTopAlarmTime = text_top_alarm_time
            loadTextColor(textTopAlarmTime, TOP_ALARM_TIME_COLOR_FILE_NAME)

            if (resources.getBoolean(R.bool.is_tablet)) {
                val topAlarmArea = top_alarm_area
                val lp = topAlarmArea.layoutParams
                val mlp = lp as ViewGroup.MarginLayoutParams
                mlp.topMargin = 40
                topAlarmArea.layoutParams = mlp
                val timeArea = time_area
                val lpTimeArea = timeArea.layoutParams
                val mlpTimeArea = lpTimeArea as ViewGroup.MarginLayoutParams
                mlpTimeArea.topMargin = 80
                timeArea.layoutParams = mlpTimeArea
            }

        }

        val reloadTimeThread = Thread {
            while (true) {
                timerHandler.sendEmptyMessage(RELOAD_TIME_HANDLE_ID)
                Thread.sleep(1)
            }
        }

        reloadTimeThread.start()

    }

    override fun onResume() {
        super.onResume()
        startAlarmManager()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
        }
    }

    override fun onBackPressed() {
        showInterstitial()
        val dialog = AttentionDialog.newInstance(resources.getString(R.string.confirming_app_finish_dialog_message)) {
            finish()
        }
        dialog.show(supportFragmentManager, TAG)
    }

    private fun showInterstitial() {
        if (mInterAdCloseApp.isLoaded) {
            mInterAdCloseApp.show()
        }
    }

    private fun loadAlarmData() {
        val manager = RingtoneManager(this)
        manager.setType(RingtoneManager.TYPE_ALL)
        val cursor = manager.cursor
        while (cursor.moveToNext()) {
            listAlarmData.add(RingtoneData(cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    cursor.getString(RingtoneManager.ID_COLUMN_INDEX),
                    cursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/" + cursor.getString(RingtoneManager.ID_COLUMN_INDEX)))
        }
    }

    fun switchAlarmResource() =
            if (alarmCheckState) {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_setting)
                textNowSound?.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            } else {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_normal)
                textNowSound?.setTextColor(ContextCompat.getColor(this, R.color.pamplemousse))
            }

    private fun startAlarmManager() {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra("check_alarm", alarmCheckState)
        intent.putExtra("alarm_uri", nowAlarmSound?.uri)
        intent.putExtra("alarm_time", alarmTime)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (alarmCheckState) {
            val timeArray = alarmTime.split(":").map { it.trim() }
            val cal = Calendar.getInstance()
            val calForService = Calendar.getInstance()
            cal.time = nowTime
            calForService.time = nowTime
            calForService.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
            calForService.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
            calForService.set(Calendar.SECOND, 0)
            calForService.set(Calendar.MILLISECOND, 0)

            if (cal.after(calForService as Calendar)) {
                calForService.add(Calendar.DATE, 1)
            }

            Log.i(TAG, "alarmTime = " + dateFormat.format(calForService.time) + " " + timeFormat.format(calForService.time))

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> alarmManager.setAlarmClock(AlarmManager.AlarmClockInfo(calForService.timeInMillis, null), pendingIntent)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> alarmManager.setExact(AlarmManager.RTC_WAKEUP, calForService.timeInMillis, pendingIntent)
                else -> alarmManager.set(AlarmManager.RTC_WAKEUP, calForService.timeInMillis, pendingIntent)
            }
        } else {
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun soundAlarm() {
        mp = MediaPlayer.create(this, Uri.parse(nowAlarmSound?.uri))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mp.setAudioAttributes(
                    AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
        } else {
            mp.setAudioStreamType(AudioManager.STREAM_ALARM)
        }
        mp.isLooping = true
        mp.start()
    }

    private fun stopAlarm() {
        if (mp.isPlaying) {
            mp.stop()
        }
    }

    private fun showAlarmPopup() {
        alarmPopup = PopupWindow(this)
        val popupView = layoutInflater.inflate(R.layout.layout_alerm, null)
        val textAlarmTime = popupView.findViewById<TextView>(R.id.text_alarm_time)
        popupView.findViewById<ImageButton>(R.id.button_change_alarm_time).setOnClickListener { _ ->
            val timeArray = alarmTime.split(":").map { it.trim() }
            val dialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                alarmTime = String.format("%02d:%02d", hourOfDay, minute)
                textAlarmTime.text = alarmTime
                textTopAlarmTime.text = alarmTime
                saveAlarmTime()
                startAlarmManager()
            }, Integer.parseInt(timeArray[0]), Integer.parseInt(timeArray[1]), true)

            dialog.show()
        }


        textNowSound = popupView.findViewById(R.id.text_now_sound) as TextView
        switchAlarmResource()
        switchAlarm = popupView.findViewById(R.id.switch_alarm) as Switch
        textNowSound?.text = nowAlarmSound?.title
        textAlarmTime.text = alarmTime
        textAlarmTime.setTextColor(textTopAlarmTime.currentTextColor)
        switchAlarm?.isChecked = alarmCheckState
        switchAlarm?.setOnCheckedChangeListener { view, isChecked ->
            alarmCheckState = isChecked
            switchAlarmResource()
            saveAlarmCheckState()
            startAlarmManager()
            if (isChecked) {
                Toast.makeText(this, "Alarm on", Toast.LENGTH_SHORT).show()
            }
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
        val listAlarm = popupView.findViewById<ListView>(R.id.list_sounds)
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


        alarmPopup?.contentView = popupView
        alarmPopup?.isOutsideTouchable = true
        alarmPopup?.isFocusable = true
        alarmPopup?.setOnDismissListener {
            if (mp.isPlaying) {
                mp.stop()
            }
            isTryPlayingAlarm = false
        }

        val d = windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        alarmPopup?.width = p2.x - 200
        alarmPopup?.height = p2.y - 180

        // 画面中央に表示
        alarmPopup?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun showColorPopup (v: View) {
        colorPopup = PopupWindow(this)
        val popupView = layoutInflater.inflate(R.layout.layout_set_color_popup, null)
        val textRValue = popupView.findViewById<TextView>(R.id.text_r_value)
        val textGValue = popupView.findViewById<TextView>(R.id.text_g_value)
        val textBValue = popupView.findViewById<TextView>(R.id.text_b_value)
        val seekBarRed = popupView.findViewById<SeekBar>(R.id.seekbar_r)
        val seekBarGreen = popupView.findViewById<SeekBar>(R.id.seekbar_g)
        val seekBarBlue = popupView.findViewById<SeekBar>(R.id.seekbar_b)
        val sampleBackground = popupView.findViewById<TextView>(R.id.text_sample_background)
        var sampleText = popupView.findViewById<TextView>(R.id.text_sample)
        val sampleTextTitle = popupView.findViewById<TextView>(R.id.text_sample_title)
        val buttonDefaultColor = popupView.findViewById<ImageButton>(R.id.button_default_color)
        val buttonColorOk = popupView.findViewById<ImageButton>(R.id.button_color_ok)
        val checkBoxUnifyColor = popupView.findViewById<CheckBox>(R.id.checkbox_unify_colors)

        lateinit var targetText: TextView
        var colorFileName = ""

        when (v.id) {
            R.id.frame_now_day -> {
                targetText = textNowDay
                sampleText.text = textNowDay.text
                sampleTextTitle.text = "DAY"
                sampleText.setTextColor(textNowDay.currentTextColor)
                colorFileName = NOW_DAY_COLOR_FILE_NAME
            }
            R.id.frame_now_month -> {
                targetText = textNowMonth
                sampleText.text = textNowMonth.text
                sampleTextTitle.text = "MONTH"
                sampleText.setTextColor(textNowMonth.currentTextColor)
                colorFileName = NOW_MONTH_COLOR_FILE_NAME
            }
            R.id.frame_now_year -> {
                targetText = textNowYear
                sampleBackground.text = "8888"
                sampleText.text = textNowYear.text
                sampleTextTitle.text = "YEAR"
                sampleText.setTextColor(textNowYear.currentTextColor)
                colorFileName = NOW_YEAR_COLOR_FILE_NAME
            }
            R.id.text_now_week -> {
                popupView.findViewById<FrameLayout>(R.id.frame_sample).visibility = View.GONE
                sampleText = popupView.findViewById<TextView>(R.id.text_now_week_sample)
                sampleText.visibility = View.VISIBLE
                targetText = textNowWeek
                sampleText.text = textNowWeek.text
                sampleTextTitle.text = "WEEK of day"
                sampleText.setTextColor(textNowWeek.currentTextColor)
                colorFileName = NOW_WEEK_COLOR_FILE_NAME
            }
            R.id.frame_now_hour -> {
                targetText = textNowHour
                sampleText.text = textNowHour.text
                sampleTextTitle.text = "HOUR"
                sampleText.setTextColor(textNowHour.currentTextColor)
                colorFileName = NOW_HOUR_COLOR_FILE_NAME
            }
            R.id.text_divide_hour_and_minute -> {
                popupView.findViewById<FrameLayout>(R.id.frame_sample).visibility = View.GONE
                popupView.findViewById<TextView>(R.id.text_sample_title).visibility = View.GONE
                sampleText = popupView.findViewById<TextView>(R.id.text_divide_hour_and_minute_sample)
                sampleText.visibility = View.VISIBLE
                targetText = textDivideHourAndMinute
                sampleText.text = textDivideHourAndMinute.text
                sampleText.setTextColor(textDivideHourAndMinute.currentTextColor)
                colorFileName = DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME
            }
            R.id.frame_now_minute -> {
                targetText = textNowMinute
                sampleText.text = textNowMinute.text
                sampleTextTitle.text = "MINUTE"
                sampleText.setTextColor(textNowMinute.currentTextColor)
                colorFileName = NOW_MINUTE_COLOR_FILE_NAME
            }
            R.id.frame_now_second -> {
                targetText = textNowSecond
                sampleText.text = textNowSecond.text
                sampleTextTitle.text = "SECOND"
                sampleText.setTextColor(textNowSecond.currentTextColor)
                colorFileName = NOW_SECOND_COLOR_FILE_NAME
            }
            R.id.frame_top_alarm_time -> {
                targetText = textTopAlarmTime
                sampleBackground.text = "88:88"
                sampleText.text = textTopAlarmTime.text
                sampleTextTitle.text = "ALARM TIME"
                sampleText.setTextColor(textTopAlarmTime.currentTextColor)
                colorFileName = TOP_ALARM_TIME_COLOR_FILE_NAME
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        val redValue = Color.red(sampleText.currentTextColor)
        val greenValue = Color.green(sampleText.currentTextColor)
        val blueValue = Color.blue(sampleText.currentTextColor)
        textRValue.text = redValue.toString()
        textGValue.text = greenValue.toString()
        textBValue.text = blueValue.toString()
        seekBarRed.progress = redValue
        seekBarGreen.progress = greenValue
        seekBarBlue.progress = blueValue

        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textRValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(progress, seekBarGreen.progress, seekBarBlue.progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textGValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(seekBarRed.progress, progress, seekBarBlue.progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textBValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(seekBarRed.progress, seekBarGreen.progress, progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        buttonDefaultColor.setOnClickListener { _ ->
            textRValue.text = DEFAULT_COLOR_RED_VALUE.toString()
            textGValue.text = DEFAULT_COLOR_GREEN_VALUE.toString()
            textBValue.text = DEFAULT_COLOR_BLUE_VALUE.toString()
            seekBarRed.progress = DEFAULT_COLOR_RED_VALUE
            seekBarGreen.progress = DEFAULT_COLOR_GREEN_VALUE
            seekBarBlue.progress = DEFAULT_COLOR_BLUE_VALUE
            sampleText.setTextColor(Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE))
        }

        //色統一チェックボックスチェック時において、OKボタン押下時の出現アラートダイアログのOKボタンのコールバック
        val dialogOkCallback = {
            textNowDay.setTextColor(sampleText.currentTextColor)
            textNowMonth.setTextColor(sampleText.currentTextColor)
            textNowYear.setTextColor(sampleText.currentTextColor)
            textNowWeek.setTextColor(sampleText.currentTextColor)
            textNowHour.setTextColor(sampleText.currentTextColor)
            textDivideHourAndMinute.setTextColor(sampleText.currentTextColor)
            textNowMinute.setTextColor(sampleText.currentTextColor)
            textNowSecond.setTextColor(sampleText.currentTextColor)
            textTopAlarmTime.setTextColor(sampleText.currentTextColor)
            saveTextColor(textNowDay, NOW_DAY_COLOR_FILE_NAME)
            saveTextColor(textNowMonth, NOW_MONTH_COLOR_FILE_NAME)
            saveTextColor(textNowYear, NOW_YEAR_COLOR_FILE_NAME)
            saveTextColor(textNowWeek, NOW_WEEK_COLOR_FILE_NAME)
            saveTextColor(textNowHour, NOW_HOUR_COLOR_FILE_NAME)
            saveTextColor(textDivideHourAndMinute, DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            saveTextColor(textNowMinute, NOW_MINUTE_COLOR_FILE_NAME)
            saveTextColor(textNowSecond, NOW_SECOND_COLOR_FILE_NAME)
            saveTextColor(textTopAlarmTime, TOP_ALARM_TIME_COLOR_FILE_NAME)
            colorPopup?.dismiss()
        }

        buttonColorOk.setOnClickListener { _ ->
            if (checkBoxUnifyColor.isChecked) {
                val dialog = AttentionDialog.newInstance(resources.getString(R.string.unify_time_colors_dialog_message), dialogOkCallback)
                dialog.show(supportFragmentManager, TAG)
            } else {
                targetText.setTextColor(sampleText.currentTextColor)
                saveTextColor(targetText, colorFileName)
                colorPopup?.dismiss()
            }
        }

        colorPopup?.contentView = popupView
        colorPopup?.isOutsideTouchable = true
        colorPopup?.isFocusable = true

        val d = windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        colorPopup?.width = p2.x - 200
        colorPopup?.height = p2.y - 120

        // 画面中央に表示
        colorPopup?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun loadAlarmTime() {

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

    private fun saveAlarmTime() {
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

    private fun loadNowAlarmSound() {

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

    private fun saveNowAlarmSound() {
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

    private fun loadAlarmCheckState() {

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

    fun saveAlarmCheckState() {
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

    private fun loadTextColor(text: TextView, fileName: String) {

        try {
            BufferedReader(InputStreamReader(this.openFileInput(fileName))).use {
                var redValue = 0
                var greenValue = 0
                var blueValue = 0
                val line1 = it.readLine()
                if (line1 == null) {
                    text.setTextColor(Color.rgb(127, 176, 0))
                    return
                }
                redValue = Integer.parseInt(line1)
                val line2 = it.readLine()
                if (line2 == null) {
                    text.setTextColor(Color.rgb(127, 176, 0))
                    return
                }
                greenValue = Integer.parseInt(line2)
                val line3 = it.readLine()
                if (line3 == null) {
                    text.setTextColor(Color.rgb(127, 176, 0))
                    return
                }
                blueValue = Integer.parseInt(line3)
                text.setTextColor(Color.rgb(redValue, greenValue, blueValue))
            }
        } catch (e: IOException) {
            text.setTextColor(Color.rgb(127, 176, 0))
            e.printStackTrace()
        }
    }

    private fun saveTextColor(text: TextView, fileName: String) {
        try {
            BufferedWriter(OutputStreamWriter(this.openFileOutput(fileName, Context.MODE_PRIVATE))).use {
                val redValue = Color.red(text.currentTextColor)
                val greenValue = Color.green(text.currentTextColor)
                val blueValue = Color.blue(text.currentTextColor)
                val builder = StringBuilder()
                builder.append(redValue.toString())
                builder.append(System.getProperty("line.separator"))
                builder.append(greenValue.toString())
                builder.append(System.getProperty("line.separator"))
                builder.append(blueValue.toString())
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onLongClick(v: View?): Boolean {

        if (v == null) {
            return false
        }

        showColorPopup(v)

        return true
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
