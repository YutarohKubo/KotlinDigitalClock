package chom.arikui.waffle.digitalclockapp

import android.graphics.Color

class ClockSettingDataHolder() {

    companion object {
        const val DEFAULT_COLOR_RED_VALUE = 127
        const val DEFAULT_COLOR_GREEN_VALUE = 176
        const val DEFAULT_COLOR_BLUE_VALUE = 0
    }

    var alarmTime = "00:00"
    var nowAlarmSound: RingtoneData? = null
    var alarmCheckState = false

    var colorDay = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorMonth = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorYear = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorWeek = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorHour = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorDivideTime = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorMinute = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorSecond = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    var colorTopAlarmTime = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
}