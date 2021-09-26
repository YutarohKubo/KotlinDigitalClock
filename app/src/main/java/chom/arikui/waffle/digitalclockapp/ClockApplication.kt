package chom.arikui.waffle.digitalclockapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.multidex.MultiDexApplication

class ClockApplication : MultiDexApplication() {

    companion object {
        private const val TAG = "ClockApplication"
    }

    var currentActivityName = ""

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(p0: Activity, p1: Bundle?) {
                currentActivityName = p0::class.java.name
                Log.d(TAG, "onActivityCreated is launched ($currentActivityName)")
            }

            override fun onActivityStarted(p0: Activity) {

            }

            override fun onActivityResumed(p0: Activity) {

            }

            override fun onActivityPaused(p0: Activity) {

            }

            override fun onActivityStopped(p0: Activity) {

            }

            override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

            }

            override fun onActivityDestroyed(p0: Activity) {
                Log.d(TAG, "onActivityDestroyed is launched (${p0::class.java.name})")
            }

        })
    }
}