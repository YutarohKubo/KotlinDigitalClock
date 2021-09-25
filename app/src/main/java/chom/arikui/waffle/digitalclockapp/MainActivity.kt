package chom.arikui.waffle.digitalclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
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
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import chom.arikui.waffle.digitalclockapp.CalculateUtil.SHOW_BACKGROUND_RGB_LIMIT
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCommonActivity(), CoroutineScope {

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
        const val READ_PIC_REQ_CODE = 2001
        private const val isTest = true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    lateinit var fileIOWrapper: FileIOWrapper

    lateinit var textNowDay: TextView
    lateinit var textNowMonth: TextView
    lateinit var textNowYear: TextView
    lateinit var textNowWeek: TextView
    lateinit var textNowHour: TextView
    lateinit var textDivideHourAndMinute: TextView
    lateinit var textNowMinute: TextView
    lateinit var textNowSecond: TextView
    lateinit var backgroundFrame: FrameLayout
    lateinit var imagePic: ImageView
    lateinit var textTopAlarmTime: TextView

    val listAlarmData = arrayListOf<RingtoneData>()
    private var mp: MediaPlayer = MediaPlayer()
    private var imageAlarm: ImageView? = null
    var nowTime = Date()

    private var mPopupAlarm: PopupAlarm? = null
    private var mPopupSetting: PopupSetting? = null
    private var mPopupColor: PopupColor? = null

    private var mLaunchOverlaySetting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 端末データ読み込み
        loadTerminalData()
        // 広告ロード
        loadAds()
        //Screenがスリープ状態になるのを拒否
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        fileIOWrapper = FileIOWrapper(this)
        fileIOWrapper.loadNowAlarmSound()
        fileIOWrapper.loadAlarmTime()
        fileIOWrapper.loadAlarmCheckState()
        fileIOWrapper.loadValidOverlayClock()
        fileIOWrapper.loadBackgroundMode()
        fileIOWrapper.loadBackgroundPic()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageAlarm = image_alarm
            switchAlarmResource()
            button_alarm.setOnClickListener { _ ->
                showInterstitial()
                if (listAlarmData.size == 0) {
                    Toast.makeText(this, getString(R.string.alert_no_alarm_data), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                mPopupAlarm = PopupAlarm(this)
                mPopupAlarm?.showPopup()
            }

            frame_now_day.setOnLongClickListener(mLongClickListener)
            textNowDay = text_now_day
            fileIOWrapper.loadColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
            frame_now_month.setOnLongClickListener(mLongClickListener)
            textNowMonth = text_now_month
            fileIOWrapper.loadColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
            frame_now_year.setOnLongClickListener(mLongClickListener)
            textNowYear = text_now_year
            fileIOWrapper.loadColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
            text_now_week.setOnLongClickListener(mLongClickListener)
            textNowWeek = text_now_week
            fileIOWrapper.loadColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
            frame_now_hour.setOnLongClickListener(mLongClickListener)
            textNowHour = text_now_hour
            fileIOWrapper.loadColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
            text_divide_hour_and_minute.setOnLongClickListener(mLongClickListener)
            textDivideHourAndMinute = text_divide_hour_and_minute
            fileIOWrapper.loadColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            frame_now_minute.setOnLongClickListener(mLongClickListener)
            textNowMinute = text_now_minute
            fileIOWrapper.loadColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
            frame_now_second.setOnLongClickListener(mLongClickListener)
            textNowSecond = text_now_second
            fileIOWrapper.loadColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
            activity_root.setOnLongClickListener(mLongClickListener)
            backgroundFrame = activity_root
            fileIOWrapper.loadColor(FileIOWrapper.CLOCK_BACKGROUND_COLOR)
            frame_top_alarm_time.setOnLongClickListener(mLongClickListener)
            textTopAlarmTime = text_top_alarm_time
            textTopAlarmTime.text = ClockSettingDataHolder.alarmTime
            imagePic = findViewById(R.id.image_pic)
            fileIOWrapper.loadColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)

            // Android8.0以上の端末で、ホーム画面などで時計を表示可能にするため、
            // ギアメニューキーを非表示にする
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                image_setting.visibility = View.VISIBLE
                image_setting.setOnClickListener { _ ->
                    showInterstitial()
                    mPopupSetting = PopupSetting(this)
                    mPopupSetting?.showPopup()
                }
            } else {
                image_setting.visibility = View.GONE
            }
            updateClockColor()
            updateClockBackgroundPic()
            // 時計の文字の背景の88を表示状態を更新する
            updateNumBackgroundState()

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
        if (ClockSettingDataHolder.validOverlayClock) {
            startService(Intent(this, DigitalClockService::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mp.isPlaying) {
            mp.stop()
        }
    }

    override fun onBackPressed() {
        val dialog = AttentionDialog.newInstance(resources.getString(R.string.confirming_app_finish_dialog_message), getString(R.string.yes), getString(R.string.no))
        dialog.okListener = { finish() }
        dialog.show(supportFragmentManager, TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == READ_PIC_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                val uri = data.data ?: return
                try {
                    val bmp = getBitmapFromUri(uri)
                    if (mPopupColor != null && mPopupColor!!.isShowing()) {
                        mPopupColor!!.setImageSampleFromBitmap(getBitmapFromUri(uri))
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 画像ファイルに関して、uriからBitmapに変換する
     */
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return bitmap
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

    private val mLongClickListener = object : View.OnLongClickListener {
        override fun onLongClick(v: View?): Boolean {
            if (v == null) {
                return false
            }
            showInterstitial()
            showPopupColor(v)
            return true
        }
    }

    fun showPopupColor(v: View) {
        mPopupColor = PopupColor(this@MainActivity)
        mPopupColor!!.showPopup(v)
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
            if (ClockSettingDataHolder.alarmCheckState) {
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
            if (ClockSettingDataHolder.alarmCheckState) {
                ClockSettingDataHolder.alarmCheckState = false
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
            if (ClockSettingDataHolder.validOverlayClock) {
                ClockSettingDataHolder.validOverlayClock = false
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
        intent.putExtra("check_alarm", ClockSettingDataHolder.alarmCheckState)
        intent.putExtra("alarm_uri", ClockSettingDataHolder.nowAlarmSound?.uri)
        intent.putExtra("alarm_time", ClockSettingDataHolder.alarmTime)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (ClockSettingDataHolder.alarmCheckState) {
            val timeArray = ClockSettingDataHolder.alarmTime.split(":").map { it.trim() }
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
        if (ClockSettingDataHolder.nowAlarmSound?.uri == null) {
            // アラームが存在せず、uriがnullである場合は、returnで抜ける
            return
        }
        mp = MediaPlayer.create(this, Uri.parse(ClockSettingDataHolder.nowAlarmSound?.uri))
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
        textNowDay.setTextColor(ClockSettingDataHolder.colorDay)
        textNowMonth.setTextColor(ClockSettingDataHolder.colorMonth)
        textNowYear.setTextColor(ClockSettingDataHolder.colorYear)
        textNowWeek.setTextColor(ClockSettingDataHolder.colorWeek)
        textNowHour.setTextColor(ClockSettingDataHolder.colorHour)
        textDivideHourAndMinute.setTextColor(ClockSettingDataHolder.colorDivideTime)
        textNowMinute.setTextColor(ClockSettingDataHolder.colorMinute)
        textNowSecond.setTextColor(ClockSettingDataHolder.colorSecond)
        textTopAlarmTime.setTextColor(ClockSettingDataHolder.colorTopAlarmTime)
        backgroundFrame.setBackgroundColor(ClockSettingDataHolder.colorBackground)
    }

    /**
     * 時計の文字の背景の表示非表示を切り替える
     */
    fun updateNumBackgroundState() {
        val textNowDayBackground = findViewById<TextView>(R.id.text_now_day_background)
        val textNowMonthBackground = findViewById<TextView>(R.id.text_now_month_background)
        val textNowYearBackground = findViewById<TextView>(R.id.text_now_year_background)
        val textNowHourBackground = findViewById<TextView>(R.id.text_now_hour_background)
        val textNowMinuteBackground = findViewById<TextView>(R.id.text_now_minute_background)
        val textNowSecondBackground = findViewById<TextView>(R.id.text_now_second_background)
        val textTopAlarmTimeBackground = findViewById<TextView>(R.id.text_top_alarm_time_background)
        if (ClockSettingDataHolder.backgroundMode == BackgroundMode.COLOR) {
            if (isBackgroundAllRGB50OrLess()) {
                textNowDayBackground.visibility = View.VISIBLE
                textNowMonthBackground.visibility = View.VISIBLE
                textNowYearBackground.visibility = View.VISIBLE
                textNowHourBackground.visibility = View.VISIBLE
                textNowMinuteBackground.visibility = View.VISIBLE
                textNowSecondBackground.visibility = View.VISIBLE
                textTopAlarmTimeBackground.visibility = View.VISIBLE
            } else {
                textNowDayBackground.visibility = View.GONE
                textNowMonthBackground.visibility = View.GONE
                textNowYearBackground.visibility = View.GONE
                textNowHourBackground.visibility = View.GONE
                textNowMinuteBackground.visibility = View.GONE
                textNowSecondBackground.visibility = View.GONE
                textTopAlarmTimeBackground.visibility = View.GONE
            }
        } else {
            textNowDayBackground.visibility = View.GONE
            textNowMonthBackground.visibility = View.GONE
            textNowYearBackground.visibility = View.GONE
            textNowHourBackground.visibility = View.GONE
            textNowMinuteBackground.visibility = View.GONE
            textNowSecondBackground.visibility = View.GONE
            textTopAlarmTimeBackground.visibility = View.GONE
        }
    }

    /**
     * 背景色のRGB全てが、50以下であるかどうかを返却する
     */
    private fun isBackgroundAllRGB50OrLess(): Boolean {
        val color = ClockSettingDataHolder.colorBackground
        return Color.red(color) <= SHOW_BACKGROUND_RGB_LIMIT
                && Color.green(color) <= SHOW_BACKGROUND_RGB_LIMIT
                && Color.blue(color) <= SHOW_BACKGROUND_RGB_LIMIT
    }

    /**
     * 背景画像を更新する
     */
    private fun updateClockBackgroundPic() {
        val mode = ClockSettingDataHolder.backgroundMode
        val bmp = ClockSettingDataHolder.backgroundBmp
        if (mode == BackgroundMode.COLOR) {
            // モードがカラーである時は、背景画像を非表示にして背景色を表示する
            imagePic.visibility = View.GONE
        } else {
            if (bmp == null) {
                imagePic.visibility = View.GONE
            } else {
                imagePic.visibility = View.VISIBLE
                imagePic.setImageBitmap(ClockSettingDataHolder.backgroundBmp)
            }
        }
    }

    /**
     * ナビゲージョンバーを除く画面サイズを取得する
     */
    fun displaySize(): Point {
        val d = windowManager.defaultDisplay
        val p = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p)

        return p
    }

    /**
     * viewの輝度/半輝度設定
     *
     * @param view     view
     * @param isEnable 輝度(true)/半輝度(false)
     */
    fun setViewEnabled(view: View?, isEnable: Boolean) {
        if (view == null) {
            return
        }
        view.isEnabled = isEnable
        if (isEnable) {
            view.alpha = 1.0f
        } else {
            view.alpha = 0.3f
        }
    }

    /**
     * インタースティシャル広告のロード
     */
    private fun loadAds() {
        if (isUpgradedPremium()) {
            Log.d(TAG, "premium member so do not load ad.")
            return
        }
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()

        mInterAd0 = InterstitialAd(this)

        mInterAd0!!.adListener = object : AdListener() {
            override fun onAdClosed() {
                super.onAdClosed()
                // 広告リロード
                mInterAd0?.loadAd(adRequest)
            }
        }

        if (isTest) {
            mInterAd0!!.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        } else {
            mInterAd0!!.adUnitId = "ca-app-pub-6669415411907480/8088997953"
        }

        mInterAd0!!.loadAd(adRequest)
    }

    /**
     * インタースティシャル広告表示
     */
    private fun showInterstitial() {
        if (isUpgradedPremium()) {
            Log.d(TAG, "premium member so do not show ad.")
            return
        }
        if (mInterAd0 == null || !mInterAd0!!.isLoaded) {
            Log.d(TAG, "Ad is finished loading so is not show.")
            return
        }
        if (countClickAdKey++ % 5 != 0) {
            return
        }
        mInterAd0!!.show()
    }
}
