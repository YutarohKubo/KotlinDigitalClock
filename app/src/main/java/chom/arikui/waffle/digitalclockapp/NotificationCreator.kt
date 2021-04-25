package chom.arikui.waffle.digitalclockapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

class NotificationCreator(private val mContext: Context) {

    companion object {
        private const val CHANNEL_ID = "clock_service_notification"
        private const val PENDING_CODE_SWITCH_DISPLAY = 1000
        private const val PENDING_CODE_HOME = 1001
        private const val PENDING_CODE_EXIT = 1002
        const val ACTION_SWITCH_DISPLAY = "chom.arikui.waffle.digitalclockapp.action_switch_display"
        const val ACTION_EXIT = "chom.arikui.waffle.digitalclockapp.action_exit"
    }

    var clockDisplayListener: ((visibility: Boolean) -> Unit)? = null
    private var mNotification: Notification? = null
    private var mCustomView: RemoteViews? = null
    private var mClockVisible = true

    fun build(): Notification {
        val manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = mContext.getString(R.string.app_full_name)
        val notifyDescription = mContext.getString(R.string.description_notification)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(channel)
        }
        mCustomView = RemoteViews(mContext.packageName, R.layout.layout_overlay_notification)
        // 表示切替ボタンの初期化
        val switchIntent = Intent(ACTION_SWITCH_DISPLAY)
        val pendingSwitchIntent = PendingIntent.getBroadcast(mContext, PENDING_CODE_SWITCH_DISPLAY,
                switchIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mCustomView?.setOnClickPendingIntent(R.id.button_switch_clock_visibility, pendingSwitchIntent)
        updateSwitchVisibilityView()
        // ホームボタンの初期化
        val homeIntent = Intent(mContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingHomeIntent = PendingIntent.getActivity(mContext, PENDING_CODE_HOME, homeIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mCustomView?.setOnClickPendingIntent(R.id.button_home, pendingHomeIntent)
        // 終了ボタンの初期化
        val exitIntent = Intent(ACTION_EXIT)
        val pendingExitIntent = PendingIntent.getBroadcast(mContext, PENDING_CODE_EXIT, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mCustomView?.setOnClickPendingIntent(R.id.button_exit, pendingExitIntent)
        mNotification = NotificationCompat.Builder(mContext, CHANNEL_ID).apply {
            setSmallIcon(R.drawable.icon_alarm_normal)
            setCustomContentView(mCustomView)
            setStyle(NotificationCompat.DecoratedCustomViewStyle())
            // setNotificationSilent()
        }.build()
        return mNotification!!
    }

    /**
     * 時計表示/非表示のViewの状態を更新する
     */
    private fun updateSwitchVisibilityView() {
        mCustomView?.setImageViewResource(
                R.id.image_switch_clock_visibility,
                if (mClockVisible)
                    R.drawable.icon_hide_clock
                else
                    R.drawable.icon_show_clock
        )
        mCustomView?.setTextViewText(
                R.id.text_clock_visibility,
                mContext.getText(
                        if (mClockVisible)
                            R.string.clock_hide
                        else
                            R.string.clock_visible
                )
        )
        val manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotification?.let {
            manager.notify(DigitalClockService.CLOCK_NOTIFICATION_ID, mNotification)
        }
    }

    inner class SwitchDisplayReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            mClockVisible = !mClockVisible
            updateSwitchVisibilityView()
            clockDisplayListener?.let { it(mClockVisible) }
        }
    }

    inner class ExitReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            context?.stopService(Intent(context, DigitalClockService::class.java))
        }
    }

}