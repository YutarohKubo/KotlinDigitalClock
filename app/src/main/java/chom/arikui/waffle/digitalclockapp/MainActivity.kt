package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var date = Date()

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS")
        private const val RELOAD_TIME_HANDLE_ID = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timerHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        when(msg.what) {
                            RELOAD_TIME_HANDLE_ID -> {
                                date = Date()
                                text_now_date.text = dateFormat.format(date)
                                text_now_time.text = timeFormat.format(date)
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

    override fun  attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
