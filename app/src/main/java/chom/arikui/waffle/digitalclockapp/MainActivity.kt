package chom.arikui.waffle.digitalclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

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
        private const val OVERLAY_PERMISSION_REQ_CODE = 2000

        private const val isTest = true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    lateinit var settingDataHolder: ClockSettingDataHolder
    lateinit var fileIOWrapper: FileIOWrapper

    lateinit var textNowDay: TextView
    lateinit var textNowMonth: TextView
    lateinit var textNowYear: TextView
    lateinit var textNowWeek: TextView
    lateinit var textNowHour: TextView
    lateinit var textDivideHourAndMinute: TextView
    lateinit var textNowMinute: TextView
    lateinit var textNowSecond: TextView
    lateinit var textTopAlarmTime: TextView

    val listAlarmData = arrayListOf<RingtoneData>()
    private var mp: MediaPlayer = MediaPlayer()
    private var imageAlarm: ImageView? = null
    var nowTime = Date()

    private var mPopupAlarm: PopupAlarm? = null
    private var mPopupSetting: PopupSetting? = null

    private lateinit var mInterAdCloseApp: InterstitialAd

    private var mLaunchOverlaySetting = false

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

        settingDataHolder = ClockSettingDataHolder()
        fileIOWrapper = FileIOWrapper(this)
        fileIOWrapper.loadNowAlarmSound()
        fileIOWrapper.loadAlarmTime()
        fileIOWrapper.loadAlarmCheckState()
        fileIOWrapper.loadValidOverlayClock()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageAlarm = image_alarm
            switchAlarmResource()
            button_alarm.setOnClickListener { _ ->
                mPopupAlarm = PopupAlarm(this)
                mPopupAlarm?.showPopup()
            }

            frame_now_day.setOnLongClickListener(mLongClickListener)
            textNowDay = text_now_day
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
            frame_now_month.setOnLongClickListener(mLongClickListener)
            textNowMonth = text_now_month
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
            frame_now_year.setOnLongClickListener(mLongClickListener)
            textNowYear = text_now_year
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
            text_now_week.setOnLongClickListener(mLongClickListener)
            textNowWeek = text_now_week
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
            frame_now_hour.setOnLongClickListener(mLongClickListener)
            textNowHour = text_now_hour
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
            text_divide_hour_and_minute.setOnLongClickListener(mLongClickListener)
            textDivideHourAndMinute = text_divide_hour_and_minute
            fileIOWrapper.loadTextColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            frame_now_minute.setOnLongClickListener(mLongClickListener)
            textNowMinute = text_now_minute
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
            frame_now_second.setOnLongClickListener(mLongClickListener)
            textNowSecond = text_now_second
            fileIOWrapper.loadTextColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
            frame_top_alarm_time.setOnLongClickListener(mLongClickListener)
            textTopAlarmTime = text_top_alarm_time
            textTopAlarmTime.text = settingDataHolder.alarmTime
            fileIOWrapper.loadTextColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)

            // Android8.0以上の端末で、ホーム画面などで時計を表示可能にするため、
            // ギアメニューキーを非表示にする
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                image_setting.visibility = View.VISIBLE
                image_setting.setOnClickListener { _ ->
                    mPopupSetting = PopupSetting(this)
                    mPopupSetting?.showPopup()
                }
            } else {
                image_setting.visibility = View.GONE
            }
            updateClockColor()

            if (resources.getBoolean(R.bool.is_tablet)) {
                val bottomArea = bottom_area
                val lp = bottomArea.layoutParams
                val mlp = lp as ViewGroup.MarginLayoutParams
                mlp.topMargin = 40
                bottomArea.layoutParams = mlp
                val timeArea = time_area
                val lpTimeArea = timeArea.layoutParams
                val mlpTimeArea = lpTimeArea as ViewGroup.MarginLayoutParams
                mlpTimeArea.topMargin = 80
                timeArea.layoutParams = mlpTimeArea
            }
        }

        launch {
            while (true) {
                updateClockDisplay()
                delay(10)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        stopService(Intent(this, DigitalClockService::class.java))
    }

    override fun onResume() {
        super.onResume()
        resetAlarmState()
        resetOverlayClockState()
        mLaunchOverlaySetting = false
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        if (settingDataHolder.validOverlayClock) {
            val serviceIntent = Intent(this, DigitalClockService::class.java)
            serviceIntent.putExtra(EventIdUtil.COLOR_HOUR, settingDataHolder.colorHour)
            serviceIntent.putExtra(EventIdUtil.COLOR_DIVIDE_TIME, settingDataHolder.colorDivideTime)
            serviceIntent.putExtra(EventIdUtil.COLOR_MINUTE, settingDataHolder.colorMinute)
            serviceIntent.putExtra(EventIdUtil.COLOR_SECOND, settingDataHolder.colorSecond)
            startService(serviceIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
        }
    }

    override fun onBackPressed() {
        showInterstitial()
        val dialog = AttentionDialog.newInstance(resources.getString(R.string.confirming_app_finish_dialog_message))
        dialog.okListener = { finish() }
        dialog.show(supportFragmentManager, TAG)
    }

    /**
     * 時計の表示更新
     */
    private fun updateClockDisplay() {
        val cal = Calendar.getInstance()
        nowTime = cal.time
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (text_now_date != null) {
                text_now_date.text = dateFormat.format(nowTime)
                text_now_time.text = timeFormat.format(nowTime)
            }
        } else {
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
        }
    }

    /**
     * ポップアップウィンドウを許可設定画面へ遷移する
     */
    fun gotoSettingOverlay() {
        mLaunchOverlaySetting = true
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE)
    }

    private fun showInterstitial() {
        if (mInterAdCloseApp.isLoaded) {
            mInterAdCloseApp.show()
        }
    }

    private val mLongClickListener = object : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            if (v == null) {
                return false
            }
            val mPopupColor = PopupColor(this@MainActivity)
            mPopupColor.showPopup(v)
            return true
        }
    }

    fun loadAlarmData() {
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
            if (settingDataHolder.alarmCheckState) {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_on)
            } else {
                imageAlarm?.setImageResource(R.drawable.icon_alarm_off)
            }

    private fun resetAlarmState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Log.d(TAG, "Ringing Alarm overlay is invalid for api " + Build.VERSION.SDK_INT)
            startAlarmManager()
            return
        }
        // 他のアプリに重ねての表示が許可されていなければ、アラームをオフにする(UIにも反映する)
        if (!Settings.canDrawOverlays(this)) {
            if (settingDataHolder.alarmCheckState) {
                settingDataHolder.alarmCheckState = false
                fileIOWrapper.saveAlarmCheckState()
                switchAlarmResource()
            }
        }
        // ポップアップ許可設定画面から戻ってきた場合でなければ、アラームポップアップに対しては何も操作せずに抜ける
        if (!mLaunchOverlaySetting) {
            startAlarmManager()
            return
        }
        // アラームポップアップが出ていない場合は、
        // アラームポップアップに対しては何も操作せずに抜ける
        if (mPopupAlarm == null || mPopupAlarm!!.popupWindow == null || !(mPopupAlarm!!.popupWindow!!.isShowing)) {
            startAlarmManager()
            return
        }
        if (Settings.canDrawOverlays(this)) {
            mPopupAlarm?.processSwitchAlarmChanging(true)
        } else {
            mPopupAlarm?.switchAlarm?.isChecked = false
        }
    }

    private fun resetOverlayClockState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Log.d(TAG, "Overlay clock is invalid for api " + Build.VERSION.SDK_INT)
            return
        }
        // 他のアプリに重ねての表示が許可されていなければ、
        // ホーム画面などで時計を表示させないようにする
        if (!Settings.canDrawOverlays(this)) {
            if (settingDataHolder.validOverlayClock) {
                settingDataHolder.validOverlayClock = false
                fileIOWrapper.saveValidOverlayClock()
            }
        }
        // ポップアップ許可設定画面から戻ってきた場合でなければ、設定ポップアップに対しては何も操作せずに抜ける
        if (!mLaunchOverlaySetting) {
            return
        }
        // 設定ポップアップが出ていない場合は、
        // 設定ポップアップに対しては何も操作せずに抜ける
        if (mPopupSetting == null || mPopupSetting!!.popupWindow == null || !(mPopupSetting!!.popupWindow!!.isShowing)) {
            return
        }
        if (Settings.canDrawOverlays(this)) {
            mPopupSetting?.processCheckOverlayChanging(true)
        } else {
            mPopupSetting?.checkOverlayClock?.isChecked = false
        }
    }

    fun startAlarmManager() {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java)
        intent.putExtra("check_alarm", settingDataHolder.alarmCheckState)
        intent.putExtra("alarm_uri", settingDataHolder.nowAlarmSound?.uri)
        intent.putExtra("alarm_time", settingDataHolder.alarmTime)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (settingDataHolder.alarmCheckState) {
            val timeArray = settingDataHolder.alarmTime.split(":").map { it.trim() }
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

    fun soundAlarm() {
        mp = MediaPlayer.create(this, Uri.parse(settingDataHolder.nowAlarmSound?.uri))
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

    fun stopAlarm() {
        if (mp.isPlaying) {
            mp.stop()
        }
    }

    /**
     * 時計の色更新
     */
    fun updateClockColor() {
        textNowDay.setTextColor(settingDataHolder.colorDay)
        textNowMonth.setTextColor(settingDataHolder.colorMonth)
        textNowYear.setTextColor(settingDataHolder.colorYear)
        textNowWeek.setTextColor(settingDataHolder.colorWeek)
        textNowHour.setTextColor(settingDataHolder.colorHour)
        textDivideHourAndMinute.setTextColor(settingDataHolder.colorDivideTime)
        textNowMinute.setTextColor(settingDataHolder.colorMinute)
        textNowSecond.setTextColor(settingDataHolder.colorSecond)
        textTopAlarmTime.setTextColor(settingDataHolder.colorTopAlarmTime)
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}
