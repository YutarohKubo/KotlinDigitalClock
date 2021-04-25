package chom.arikui.waffle.digitalclockapp

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class DigitalClockService : Service() {

    companion object {
        private const val TAG = "DigitalClockService"
        private const val RELOAD_TIME_HANDLE_ID = 1
        const val CLOCK_NOTIFICATION_ID = 1
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

    // ディスプレイのサイズを格納する
    val displaySize: Point by lazy {
        val display = windowManager?.defaultDisplay
        val size = Point()
        display?.getSize(size)
        size
    }

    private lateinit var timerHandler: Handler

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate() : instance to string : $this")
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
        Log.i(TAG, "onStartCommand() : instance to string : $this")
        val notificationCreator = NotificationCreator(this)
        val notification = notificationCreator.build()
        startForeground(CLOCK_NOTIFICATION_ID, notification)

        // Notification押下時のPendingIntent受信先のレシーバー登録
        val switchDisplayReceiver = notificationCreator.SwitchDisplayReceiver()
        val switchDisplayFilter = IntentFilter(NotificationCreator.ACTION_SWITCH_DISPLAY)
        registerReceiver(switchDisplayReceiver, switchDisplayFilter)
        notificationCreator.clockDisplayListener = { visibility ->
            clockView?.visibility = if (visibility) View.VISIBLE else View.GONE
        }
        val exitReceiver = notificationCreator.ExitReceiver()
        val exitFilter = IntentFilter(NotificationCreator.ACTION_EXIT)
        registerReceiver(exitReceiver, exitFilter)

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
                        //or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,  // 画面外への拡張を許可
                PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        // 左上から、およそステータスバーの高さ分だけ下にずらして表示する
        params.y += CalculateUtil.convertDp2Px(50, this).toInt()
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
        clockView?.setOnClickListener { _ ->
            startActivity(Intent(application, MainActivity::class.java).apply {
                setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
        clockView?.setOnTouchListener { v, event ->
            val newDx = event.rawX.toInt()
            val newDy = event.rawY.toInt()
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    val centerX = newDx - (v.width / 2)
                    val centerY = newDy - (v.height / 2)
                    params.x = centerX
                    params.y = centerY
                    windowManager?.updateViewLayout(v, params)
                }
            }
            true
        }
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