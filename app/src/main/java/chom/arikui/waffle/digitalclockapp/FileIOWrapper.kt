package chom.arikui.waffle.digitalclockapp

import android.content.Context
import android.graphics.Color
import java.io.*

class FileIOWrapper(private val mActivity: MainActivity) {

    private val mSettingDataHolder = mActivity.settingDataHolder

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
    }

    fun loadAlarmTime() {
        try {
            BufferedReader(InputStreamReader(mActivity.openFileInput(ALARM_TIME_FILE_NAME))).use {
                val line = it.readLine()
                if (line != null) {
                    mSettingDataHolder.alarmTime = line
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
                builder.append(mSettingDataHolder.alarmTime)
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
                            mSettingDataHolder.nowAlarmSound = data
                            break
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (mSettingDataHolder.nowAlarmSound == null) {
                if (mActivity.listAlarmData.size > 0) {
                    //Uriが見当たらなかった場合、listAlarmDataの一番上をアラーム音に設定する
                    mSettingDataHolder.nowAlarmSound = mActivity.listAlarmData[0]
                }
            }
        }
    }

    fun saveNowAlarmSound() {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(NOW_ALARM_SOUND_FINE_NAME, Context.MODE_PRIVATE))).use {
                val builder = StringBuilder()
                builder.append(mSettingDataHolder.nowAlarmSound?.uri)
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
                    mSettingDataHolder.alarmCheckState = line.toBoolean()
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
                builder.append(mSettingDataHolder.alarmCheckState)
                builder.append(System.getProperty("line.separator"))
                it.write(builder.toString())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadTextColor(fileName: String) {
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
                    NOW_DAY_COLOR_FILE_NAME -> mSettingDataHolder.colorDay = Color.rgb(redValue, greenValue, blueValue)
                    NOW_MONTH_COLOR_FILE_NAME -> mSettingDataHolder.colorMonth = Color.rgb(redValue, greenValue, blueValue)
                    NOW_YEAR_COLOR_FILE_NAME -> mSettingDataHolder.colorYear = Color.rgb(redValue, greenValue, blueValue)
                    NOW_WEEK_COLOR_FILE_NAME -> mSettingDataHolder.colorWeek = Color.rgb(redValue, greenValue, blueValue)
                    NOW_HOUR_COLOR_FILE_NAME -> mSettingDataHolder.colorHour = Color.rgb(redValue, greenValue, blueValue)
                    DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME -> mSettingDataHolder.colorDivideTime = Color.rgb(redValue, greenValue, blueValue)
                    NOW_MINUTE_COLOR_FILE_NAME -> mSettingDataHolder.colorMinute = Color.rgb(redValue, greenValue, blueValue)
                    NOW_SECOND_COLOR_FILE_NAME -> mSettingDataHolder.colorSecond = Color.rgb(redValue, greenValue, blueValue)
                    TOP_ALARM_TIME_COLOR_FILE_NAME -> mSettingDataHolder.colorTopAlarmTime = Color.rgb(redValue, greenValue, blueValue)
                    else -> throw IllegalArgumentException()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveTextColor(fileName: String) {
        try {
            BufferedWriter(OutputStreamWriter(mActivity.openFileOutput(fileName, Context.MODE_PRIVATE))).use {
                val redValue: Int
                val greenValue: Int
                val blueValue: Int
                when (fileName) {
                    NOW_DAY_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorDay)
                        greenValue = Color.green(mSettingDataHolder.colorDay)
                        blueValue = Color.blue(mSettingDataHolder.colorDay)
                    }
                    NOW_MONTH_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorMonth)
                        greenValue = Color.green(mSettingDataHolder.colorMonth)
                        blueValue = Color.blue(mSettingDataHolder.colorMonth)
                    }
                    NOW_YEAR_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorYear)
                        greenValue = Color.green(mSettingDataHolder.colorYear)
                        blueValue = Color.blue(mSettingDataHolder.colorYear)
                    }
                    NOW_WEEK_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorWeek)
                        greenValue = Color.green(mSettingDataHolder.colorWeek)
                        blueValue = Color.blue(mSettingDataHolder.colorWeek)
                    }
                    NOW_HOUR_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorHour)
                        greenValue = Color.green(mSettingDataHolder.colorHour)
                        blueValue = Color.blue(mSettingDataHolder.colorHour)
                    }
                    DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorDivideTime)
                        greenValue = Color.green(mSettingDataHolder.colorDivideTime)
                        blueValue = Color.blue(mSettingDataHolder.colorDivideTime)
                    }
                    NOW_MINUTE_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorMinute)
                        greenValue = Color.green(mSettingDataHolder.colorMinute)
                        blueValue = Color.blue(mSettingDataHolder.colorMinute)
                    }
                    NOW_SECOND_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorSecond)
                        greenValue = Color.green(mSettingDataHolder.colorSecond)
                        blueValue = Color.blue(mSettingDataHolder.colorSecond)
                    }
                    TOP_ALARM_TIME_COLOR_FILE_NAME -> {
                        redValue = Color.red(mSettingDataHolder.colorTopAlarmTime)
                        greenValue = Color.green(mSettingDataHolder.colorTopAlarmTime)
                        blueValue = Color.blue(mSettingDataHolder.colorTopAlarmTime)
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