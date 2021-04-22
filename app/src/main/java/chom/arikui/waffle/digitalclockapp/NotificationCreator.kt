package chom.arikui.waffle.digitalclockapp

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

object NotificationCreator {

    private const val CHANNEL_ID = "clock_service_notification"

    fun build(context: Context): Notification {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "通知のタイトル的情報を設定"
        val notifyDescription = "この通知の詳細情報を設定します"
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    CHANNEL_ID, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = notifyDescription
            }
            manager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, CHANNEL_ID).apply {
            //setContentTitle("通知のタイトル1")
            //setContentText("通知の内容1")
            setSmallIcon(R.mipmap.ic_launcher)
        }.build()
    }

}