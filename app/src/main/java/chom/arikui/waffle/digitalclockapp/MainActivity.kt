package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.content.res.Configuration
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS")
        private val dayFormat = SimpleDateFormat("dd")
        private val monthFormat = SimpleDateFormat("MM")
        private val yearFormat = SimpleDateFormat("yyyy")
        private val hourFormat = SimpleDateFormat("HH")
        private val minuteFormat = SimpleDateFormat("mm")
        private val secondFormat = SimpleDateFormat("ss")
        private const val RELOAD_TIME_HANDLE_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        hideSystemUI()

        val timerHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        when(msg.what) {
                            RELOAD_TIME_HANDLE_ID -> {
                                val cal = Calendar.getInstance()
                                val date = cal.time
                                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                    if (text_now_date != null) {
                                        text_now_date.text = dateFormat.format(date)
                                        text_now_time.text = timeFormat.format(date)
                                    }
                                } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                    if (text_now_day != null) {
                                        text_now_day.text = dayFormat.format(date)
                                        text_now_month.text = monthFormat.format(date)
                                        text_now_year.text = yearFormat.format(date)
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
                                        text_now_hour.text = hourFormat.format(date)
                                        text_now_minute.text = minuteFormat.format(date)
                                        text_now_second.text = secondFormat.format(date)
                                    }
                                } else {

                                }
                            }
                        }
                    }
                }

        val reloadTimeThread = Thread{
            while (true) {
                timerHandler.sendEmptyMessage(RELOAD_TIME_HANDLE_ID)
                Thread.sleep(1)
            }
        }

        reloadTimeThread.start()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun  attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
