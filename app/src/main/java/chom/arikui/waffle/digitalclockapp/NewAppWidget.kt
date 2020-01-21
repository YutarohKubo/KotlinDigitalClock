package chom.arikui.waffle.digitalclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class NewAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.i(TAG, "onUpdate")
        launchUpdateWeekDisplayTimer(context)

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        Log.i(TAG, "onEnabled")
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        Log.i(TAG, "onDisabled")
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        Log.i(TAG, "onReceive")
        if (intent == null) {
            return
        }
        if (intent.action == "update_week") {
            Log.i(TAG, "update_week")
            val cal = Calendar.getInstance()
            remoteViews.setTextViewText(R.id.text_now_week, when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "SUN"
                Calendar.MONDAY -> "MON"
                Calendar.TUESDAY -> "TUE"
                Calendar.WEDNESDAY -> "WED"
                Calendar.THURSDAY -> "THU"
                Calendar.FRIDAY -> "FRI"
                Calendar.SATURDAY -> "SAT"
                else -> "ERR"
            })

            launchUpdateWeekDisplayTimer(context)
        }
    }

    companion object {

        private const val RELOAD_TIME_HANDLE_ID = 1
        private const val TAG = "AppWidget"
        private val dayFormat = SimpleDateFormat("dd")
        private val monthFormat = SimpleDateFormat("MM")
        private val yearFormat = SimpleDateFormat("yyyy")
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
        private val secondFormat = SimpleDateFormat("ss")

        private lateinit var remoteViews: RemoteViews
        private var nowTime = Date()

        private lateinit var reloadTimeThread: Thread
        private val timerHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    RELOAD_TIME_HANDLE_ID -> {
                        val cal = Calendar.getInstance()
                        nowTime = cal.time
                        remoteViews.setTextViewText(R.id.text_now_day, dayFormat.format(nowTime))
                        remoteViews.setTextViewText(R.id.text_now_month, monthFormat.format(nowTime))
                        remoteViews.setTextViewText(R.id.text_now_year, yearFormat.format(nowTime))
                        remoteViews.setTextViewText(R.id.text_now_week, when (cal.get(Calendar.DAY_OF_WEEK)) {
                            Calendar.SUNDAY -> "SUN"
                            Calendar.MONDAY -> "MON"
                            Calendar.TUESDAY -> "TUE"
                            Calendar.WEDNESDAY -> "WED"
                            Calendar.THURSDAY -> "THU"
                            Calendar.FRIDAY -> "FRI"
                            Calendar.SATURDAY -> "SAT"
                            else -> "ERR"
                        })
                        remoteViews.setTextViewText(R.id.text_now_hour, hourFormat.format(nowTime))
                        remoteViews.setTextViewText(R.id.text_now_minute, minuteFormat.format(nowTime))
                        remoteViews.setTextViewText(R.id.text_now_second, secondFormat.format(nowTime))
                    }
                }
            }
        }

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                     appWidgetId: Int) {

            Log.i(TAG, "updateAppWidget")
            // Construct the RemoteViews object
            remoteViews = RemoteViews(context.packageName, R.layout.new_app_widget)
            remoteViews.setOnClickPendingIntent(R.id.widget_area, clickAction(context))
            val cal = Calendar.getInstance()
            remoteViews.setTextViewText(R.id.text_now_week, when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SUNDAY -> "SUN"
                Calendar.MONDAY -> "MON"
                Calendar.TUESDAY -> "TUE"
                Calendar.WEDNESDAY -> "WED"
                Calendar.THURSDAY -> "THU"
                Calendar.FRIDAY -> "FRI"
                Calendar.SATURDAY -> "SAT"
                else -> "ERR"
            })
            //views.setImageViewBitmap(R.id.text_now_day_background, buildUpdate(context, "88"))

            // Instruct the widget manager to update the widget

            /*reloadTimeThread = Thread {
                while (true) {
                    timerHandler.sendEmptyMessage(RELOAD_TIME_HANDLE_ID)
                    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
                    Thread.sleep(500)
                }
            }
            reloadTimeThread.start()*/

            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }

        private fun clickAction (context: Context): PendingIntent {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            return PendingIntent.getActivity(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        fun updateTextNowDayColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_day, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowMonthColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_month, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowYearColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_year, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowWeekColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_week, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowHourColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_hour, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextDivideHourAndMinuteColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_divide_hour_and_minute, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowMinuteColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_minute, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        fun updateTextNowSecondColor (context: Context, color: Int) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(ComponentName(context, NewAppWidget::class.java))
            remoteViews.setTextColor(R.id.text_now_second, color)
            for (appWidgetId in ids) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }

        private fun launchUpdateWeekDisplayTimer (context: Context?) {
            val intent = Intent(context, NewAppWidget::class.java)
            intent.action = "update_week"
            val pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.add(Calendar.DATE, 1)
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
                else -> alarmManager.set(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
            }
        }
    }
}

