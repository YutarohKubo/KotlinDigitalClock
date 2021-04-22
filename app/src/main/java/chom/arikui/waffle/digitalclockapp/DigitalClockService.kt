package chom.arikui.waffle.digitalclockapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DigitalClockService : Service() {

    companion object {
        private const val RELOAD_TIME_HANDLE_ID = 1
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
        private val secondFormat = SimpleDateFormat("ss")
        private val defaultColor = Color.rgb(ClockSettingDataHolder.DEFAULT_COLOR_RED_VALUE, ClockSettingDataHolder.DEFAULT_COLOR_GREEN_VALUE, ClockSettingDataHolder.DEFAULT_COLOR_BLUE_VALUE)
    }

    private var clockView: View? = null
    private var textHour: TextView? = null
    private var textDivideTime: TextView? = null
    private var textMinute: TextView? = null
    private var textSecond: TextView? = null
    private var windowManager: WindowManager? = null
    private var nowTime = Date()

    private lateinit var timerHandler: Handler

    override fun onCreate() {
        super.onCreate()
        timerHandler = object : Handler(mainLooper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    RELOAD_TIME_HANDLE_ID -> {
                        val cal = Calendar.getInstance()
                        nowTime = cal.time
                        textHour?.text = hourFormat.format(nowTime)
                        textMinute?.text = minuteFormat.format(nowTime)
                        textSecond?.text = secondFormat.format(nowTime)
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCreator.build(this)
        startForeground(1, notification)
        val overlayType = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY
        }

        val inflater = LayoutInflater.from(this)
        windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                overlayType,  // Overlay レイヤに表示
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  // フォーカスを奪わない
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
                PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        clockView = inflater.inflate(R.layout.layout_clock_overlay, null)
        textHour = clockView?.findViewById(R.id.text_now_hour_overlay)
        textDivideTime = clockView?.findViewById(R.id.text_divide_hour_and_minute_overlay)
        textMinute = clockView?.findViewById(R.id.text_now_minute_overlay)
        textSecond = clockView?.findViewById(R.id.text_now_second_overlay)
        val colorHour = intent?.getIntExtra(EventIdUtil.COLOR_HOUR, defaultColor) ?: defaultColor
        val colorDivideTime = intent?.getIntExtra(EventIdUtil.COLOR_DIVIDE_TIME, defaultColor) ?: defaultColor
        val colorMinute = intent?.getIntExtra(EventIdUtil.COLOR_MINUTE, defaultColor) ?: defaultColor
        val colorSecond = intent?.getIntExtra(EventIdUtil.COLOR_SECOND, defaultColor) ?: defaultColor
        initClockColor(colorHour, colorDivideTime, colorMinute, colorSecond)
        windowManager?.addView(clockView, params)

        val reloadTimeThread = Thread {
            while (true) {
                timerHandler.sendEmptyMessage(RELOAD_TIME_HANDLE_ID)
                Thread.sleep(1)
            }
        }

        reloadTimeThread.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager?.removeView(clockView)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun initClockColor(colorHour: Int, colorDivideTime: Int, colorMinute: Int, colorSecond: Int) {
        textHour?.setTextColor(colorHour)
        textDivideTime?.setTextColor(colorDivideTime)
        textMinute?.setTextColor(colorMinute)
        textSecond?.setTextColor(colorSecond)
    }
}