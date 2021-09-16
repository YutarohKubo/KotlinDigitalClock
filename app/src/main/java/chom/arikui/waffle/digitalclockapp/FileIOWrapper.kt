package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.graphics.Color
import java.io.*

class FileIOWrapper(private val mActivity: MainActivity) {

    companion object {
        private const val TAG = "FILE_IO_WRAPPER"
        const val ALARM_TIME_FILE_NAME = "alarm_time.dc"
        const val NOW_ALARM_SOUND_FINE_NAME = "now_alarm_sound.dc"
        const val ALARM_SOUND_CHECK_STATE_FILE_NAME = "alarm_sound_check_state.dc"
        const val NOW_DAY_COLOR_FILE_NAME = "now_day_color.dc"
        const val NOW_MONTH_COLOR_FILE_NAME = "now_month_color.dc"
        const val NOW_YEAR_COLOR_FILE_NAME = "now_year_color.dc"
        const val NOW_WEEK_COLOR_FILE_NAME = "now_week_color.dc"
        const val NOW_HOUR_COLOR_FILE_NAME = "now_hour_color.dc"
        const val DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME = "divide_hour_and_minute_color.dc"
        const val NOW_MINUTE_COLOR_FILE_NAME = "now_minute_color.dc"
        const val NOW_SECOND_COLOR_FILE_NAME = "now_second_color.dc"
        const val TOP_ALARM_TIME_COLOR_FILE_NAME = "top_alarm_time_color.dc"
        const val CLOCK_BACKGROUND_COLOR = "clock_background_color.dc"
        const val VALID_OVERLAY_CLOCK_FILE_NAME = "valid_overlay_clock.dc"
    }

    fun loadAlarmTime() {
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(ALARM_TIME_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    ClockSettingDataHolder.alarmTime = line
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveAlarmTime() {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(ALARM_TIME_FILE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(ClockSettingDataHolder.alarmTime)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadNowAlarmSound() {
        mActivity.loadAlarmData()
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(NOW_ALARM_SOUND_FINE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    for (data in mActivity.listAlarmData) {
                        if (line == data.uri) {
                            ClockSettingDataHolder.nowAlarmSound = data
                            break
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (ClockSettingDataHolder.nowAlarmSound == null) {
                if (mActivity.listAlarmData.size > 0) {
                    //Uriが見当たらなかった場合、listAlarmDataの一番上をアラーム音に設定する
                    ClockSettingDataHolder.nowAlarmSound = mActivity.listAlarmData[0]
                }
            }
        }
    }

    fun saveNowAlarmSound() {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(NOW_ALARM_SOUND_FINE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(ClockSettingDataHolder.nowAlarmSound?.uri)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadAlarmCheckState() {
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(ALARM_SOUND_CHECK_STATE_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    ClockSettingDataHolder.alarmCheckState = line.toBoolean()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveAlarmCheckState() {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(ALARM_SOUND_CHECK_STATE_FILE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(ClockSettingDataHolder.alarmCheckState)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadValidOverlayClock() {
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(VALID_OVERLAY_CLOCK_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    ClockSettingDataHolder.validOverlayClock = line.toBoolean()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveValidOverlayClock() {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(VALID_OVERLAY_CLOCK_FILE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(ClockSettingDataHolder.validOverlayClock)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadColor(fileName: String) {
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(fileName))).use {
                var redValue: Int
                var greenValue: Int
                var blueValue: Int
                val line1 = it.readLine() ?: return
                redValue = Integer.parseInt(line1)
                val line2 = it.readLine() ?: return
                greenValue = Integer.parseInt(line2)
                val line3 = it.readLine() ?: return
                blueValue = Integer.parseInt(line3)
                when (fileName) {
                    NOW_DAY_COLOR_FILE_NAME -> ClockSettingDataHolder.colorDay = Color.rgb(redValue, greenValue, blueValue)
                    NOW_MONTH_COLOR_FILE_NAME -> ClockSettingDataHolder.colorMonth = Color.rgb(redValue, greenValue, blueValue)
                    NOW_YEAR_COLOR_FILE_NAME -> ClockSettingDataHolder.colorYear = Color.rgb(redValue, greenValue, blueValue)
                    NOW_WEEK_COLOR_FILE_NAME -> ClockSettingDataHolder.colorWeek = Color.rgb(redValue, greenValue, blueValue)
                    NOW_HOUR_COLOR_FILE_NAME -> ClockSettingDataHolder.colorHour = Color.rgb(redValue, greenValue, blueValue)
                    DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME -> ClockSettingDataHolder.colorDivideTime = Color.rgb(redValue, greenValue, blueValue)
                    NOW_MINUTE_COLOR_FILE_NAME -> ClockSettingDataHolder.colorMinute = Color.rgb(redValue, greenValue, blueValue)
                    NOW_SECOND_COLOR_FILE_NAME -> ClockSettingDataHolder.colorSecond = Color.rgb(redValue, greenValue, blueValue)
                    TOP_ALARM_TIME_COLOR_FILE_NAME -> ClockSettingDataHolder.colorTopAlarmTime = Color.rgb(redValue, greenValue, blueValue)
                    CLOCK_BACKGROUND_COLOR -> ClockSettingDataHolder.colorBackground = Color.rgb(redValue, greenValue, blueValue)
                    else -> throw IllegalArgumentException()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveColor(fileName: String) {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(fileName, Context.MODE_PRIVATE))).use {
                val redValue: Int
                val greenValue: Int
                val blueValue: Int
                when (fileName) {
                    NOW_DAY_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorDay)
                        greenValue = Color.green(ClockSettingDataHolder.colorDay)
                        blueValue = Color.blue(ClockSettingDataHolder.colorDay)
                    }
                    NOW_MONTH_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorMonth)
                        greenValue = Color.green(ClockSettingDataHolder.colorMonth)
                        blueValue = Color.blue(ClockSettingDataHolder.colorMonth)
                    }
                    NOW_YEAR_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorYear)
                        greenValue = Color.green(ClockSettingDataHolder.colorYear)
                        blueValue = Color.blue(ClockSettingDataHolder.colorYear)
                    }
                    NOW_WEEK_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorWeek)
                        greenValue = Color.green(ClockSettingDataHolder.colorWeek)
                        blueValue = Color.blue(ClockSettingDataHolder.colorWeek)
                    }
                    NOW_HOUR_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorHour)
                        greenValue = Color.green(ClockSettingDataHolder.colorHour)
                        blueValue = Color.blue(ClockSettingDataHolder.colorHour)
                    }
                    DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorDivideTime)
                        greenValue = Color.green(ClockSettingDataHolder.colorDivideTime)
                        blueValue = Color.blue(ClockSettingDataHolder.colorDivideTime)
                    }
                    NOW_MINUTE_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorMinute)
                        greenValue = Color.green(ClockSettingDataHolder.colorMinute)
                        blueValue = Color.blue(ClockSettingDataHolder.colorMinute)
                    }
                    NOW_SECOND_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorSecond)
                        greenValue = Color.green(ClockSettingDataHolder.colorSecond)
                        blueValue = Color.blue(ClockSettingDataHolder.colorSecond)
                    }
                    TOP_ALARM_TIME_COLOR_FILE_NAME -> {
                        redValue = Color.red(ClockSettingDataHolder.colorTopAlarmTime)
                        greenValue = Color.green(ClockSettingDataHolder.colorTopAlarmTime)
                        blueValue = Color.blue(ClockSettingDataHolder.colorTopAlarmTime)
                    }
                    CLOCK_BACKGROUND_COLOR -> {
                        redValue = Color.red(ClockSettingDataHolder.colorBackground)
                        greenValue = Color.green(ClockSettingDataHolder.colorBackground)
                        blueValue = Color.blue(ClockSettingDataHolder.colorBackground)
                    }
                    else -> throw IllegalArgumentException()
                }
                val builder = StringBuilder()
                builder.append(redValue.toString())
                builder.append(System.getProperty("line.separator"))
                builder.append(greenValue.toString())
                builder.append(System.getProperty("line.separator"))
                builder.append(blueValue.toString())
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}