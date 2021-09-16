package chom.arikui.waffle.digitalclockapp

import android.graphics.Color

object ClockSettingDataHolder {

    const val DEFAULT_COLOR_RED_VALUE = 127
    const val DEFAULT_COLOR_GREEN_VALUE = 176
    const val DEFAULT_COLOR_BLUE_VALUE = 0
    const val DEFAULT_BACKGROUND_RED_VALUE = 0
    const val DEFAULT_BACKGROUND_GREEN_VALUE = 0
    const val DEFAULT_BACKGROUND_BLUE_VALUE = 0
    private const val DEFAULT_ALARM_TIME = "00:00"
    private const val DEFAULT_ALARM_CHECK_STATE = false
    private const val DEFAULT_VALID_OVERLAY_CLOCK = false
    private val DEFAULT_COLOR = Color.rgb(DEFAULT_COLOR_RED_VALUE, DEFAULT_COLOR_GREEN_VALUE, DEFAULT_COLOR_BLUE_VALUE)
    private val DEFAULT_BACKGROUND_COLOR = Color.rgb(DEFAULT_BACKGROUND_RED_VALUE, DEFAULT_BACKGROUND_GREEN_VALUE, DEFAULT_BACKGROUND_BLUE_VALUE)

    var alarmTime = DEFAULT_ALARM_TIME
    var nowAlarmSound: RingtoneData? = null
    var alarmCheckState = DEFAULT_ALARM_CHECK_STATE
    var validOverlayClock = DEFAULT_VALID_OVERLAY_CLOCK

    var colorDay = DEFAULT_COLOR
    var colorMonth = DEFAULT_COLOR
    var colorYear = DEFAULT_COLOR
    var colorWeek = DEFAULT_COLOR
    var colorHour = DEFAULT_COLOR
    var colorDivideTime = DEFAULT_COLOR
    var colorMinute = DEFAULT_COLOR
    var colorSecond = DEFAULT_COLOR
    var colorTopAlarmTime = DEFAULT_COLOR
    var colorBackground = DEFAULT_BACKGROUND_COLOR
}