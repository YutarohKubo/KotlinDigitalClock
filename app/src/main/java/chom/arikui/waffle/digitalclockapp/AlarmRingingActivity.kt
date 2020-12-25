package chom.arikui.waffle.digitalclockapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_alarm_ringing.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class AlarmRingingActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private lateinit var intentAlarm: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_ringing)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        intentAlarm = intent

        mediaPlayer = MediaPlayer.create(this, Uri.parse(intentAlarm.getStringExtra("alarm_uri")))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer?.setAudioAttributes(
                    AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
        } else {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_ALARM)
        }
        mediaPlayer?.isLooping = true

        text_alarm_time_now_ringing.text = intentAlarm.getStringExtra("alarm_time")
        button_alarm_stop.setOnClickListener { _ ->
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.stop()
            }
            val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
            pendingIntent.cancel()
            alarmManager.cancel(pendingIntent)
            finish()
        }

        mediaPlayer?.start()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer?.isPlaying!!) {
            mediaPlayer?.stop()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}