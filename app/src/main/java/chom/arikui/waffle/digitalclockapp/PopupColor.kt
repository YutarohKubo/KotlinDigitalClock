package chom.arikui.waffle.digitalclockapp

import android.graphics.Color
import android.graphics.Point
import android.view.Gravity
import android.view.View
import android.widget.*

class PopupColor(private val activity: MainActivity) {

    companion object {
        private const val TAG = "POPUP_COLOR"
    }

    fun showPopup(v: View) {
        val popupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_set_color_popup, null)
        val settingDataHolder = activity.settingDataHolder
        val fileIOWrapper = activity.fileIOWrapper

        val textRValue = popupView.findViewById<TextView>(R.id.text_r_value)
        val textGValue = popupView.findViewById<TextView>(R.id.text_g_value)
        val textBValue = popupView.findViewById<TextView>(R.id.text_b_value)
        val seekBarRed = popupView.findViewById<SeekBar>(R.id.seekbar_r)
        val seekBarGreen = popupView.findViewById<SeekBar>(R.id.seekbar_g)
        val seekBarBlue = popupView.findViewById<SeekBar>(R.id.seekbar_b)
        val sampleBackground = popupView.findViewById<TextView>(R.id.text_sample_background)
        var sampleText = popupView.findViewById<TextView>(R.id.text_sample)
        val sampleTextTitle = popupView.findViewById<TextView>(R.id.text_sample_title)
        val buttonDefaultColor = popupView.findViewById<ImageButton>(R.id.button_default_color)
        val buttonColorOk = popupView.findViewById<ImageButton>(R.id.button_color_ok)
        val checkBoxUnifyColor = popupView.findViewById<CheckBox>(R.id.checkbox_unify_colors)

        val textDay = activity.findViewById<TextView>(R.id.text_now_day)
        val textMonth = activity.findViewById<TextView>(R.id.text_now_month)
        val textYear = activity.findViewById<TextView>(R.id.text_now_year)
        val textWeek = activity.findViewById<TextView>(R.id.text_now_week)
        val textHour = activity.findViewById<TextView>(R.id.text_now_hour)
        val textDivideTime = activity.findViewById<TextView>(R.id.text_divide_hour_and_minute)
        val textMinute = activity.findViewById<TextView>(R.id.text_now_minute)
        val textSecond = activity.findViewById<TextView>(R.id.text_now_second)
        val textTopAlarmTime = activity.findViewById<TextView>(R.id.text_top_alarm_time)

        when (v.id) {
            R.id.frame_now_day -> {
                sampleText.text = textDay.text
                sampleTextTitle.text = "DAY"
                sampleText.setTextColor(settingDataHolder.colorDay)
            }
            R.id.frame_now_month -> {
                sampleText.text = textMonth.text
                sampleTextTitle.text = "MONTH"
                sampleText.setTextColor(settingDataHolder.colorMonth)
            }
            R.id.frame_now_year -> {
                sampleBackground.text = "8888"
                sampleText.text = textYear.text
                sampleTextTitle.text = "YEAR"
                sampleText.setTextColor(settingDataHolder.colorYear)
            }
            R.id.text_now_week -> {
                popupView.findViewById<FrameLayout>(R.id.frame_sample).visibility = View.GONE
                sampleText = popupView.findViewById(R.id.text_now_week_sample)
                sampleText.visibility = View.VISIBLE
                sampleText.text = textWeek.text
                sampleTextTitle.text = "WEEK of day"
                sampleText.setTextColor(settingDataHolder.colorWeek)
            }
            R.id.frame_now_hour -> {
                sampleText.text = textHour.text
                sampleTextTitle.text = "HOUR"
                sampleText.setTextColor(settingDataHolder.colorHour)
            }
            R.id.text_divide_hour_and_minute -> {
                popupView.findViewById<FrameLayout>(R.id.frame_sample).visibility = View.GONE
                popupView.findViewById<TextView>(R.id.text_sample_title).visibility = View.GONE
                sampleText = popupView.findViewById(R.id.text_divide_hour_and_minute_sample)
                sampleText.visibility = View.VISIBLE
                sampleText.text = textDivideTime.text
                sampleText.setTextColor(settingDataHolder.colorDivideTime)
            }
            R.id.frame_now_minute -> {
                sampleText.text = textMinute.text
                sampleTextTitle.text = "MINUTE"
                sampleText.setTextColor(settingDataHolder.colorMinute)
            }
            R.id.frame_now_second -> {
                sampleText.text = textSecond.text
                sampleTextTitle.text = "SECOND"
                sampleText.setTextColor(settingDataHolder.colorSecond)
            }
            R.id.frame_top_alarm_time -> {
                sampleBackground.text = "88:88"
                sampleText.text = textTopAlarmTime.text
                sampleTextTitle.text = "ALARM TIME"
                sampleText.setTextColor(settingDataHolder.colorTopAlarmTime)
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        val redValue = Color.red(sampleText.currentTextColor)
        val greenValue = Color.green(sampleText.currentTextColor)
        val blueValue = Color.blue(sampleText.currentTextColor)
        textRValue.text = redValue.toString()
        textGValue.text = greenValue.toString()
        textBValue.text = blueValue.toString()
        seekBarRed.progress = redValue
        seekBarGreen.progress = greenValue
        seekBarBlue.progress = blueValue

        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textRValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(progress, seekBarGreen.progress, seekBarBlue.progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textGValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(seekBarRed.progress, progress, seekBarBlue.progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textBValue.text = progress.toString()
                sampleText.setTextColor(Color.rgb(seekBarRed.progress, seekBarGreen.progress, progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        buttonDefaultColor.setOnClickListener { _ ->
            textRValue.text = ClockSettingDataHolder.DEFAULT_COLOR_RED_VALUE.toString()
            textGValue.text = ClockSettingDataHolder.DEFAULT_COLOR_GREEN_VALUE.toString()
            textBValue.text = ClockSettingDataHolder.DEFAULT_COLOR_BLUE_VALUE.toString()
            seekBarRed.progress = ClockSettingDataHolder.DEFAULT_COLOR_RED_VALUE
            seekBarGreen.progress = ClockSettingDataHolder.DEFAULT_COLOR_GREEN_VALUE
            seekBarBlue.progress = ClockSettingDataHolder.DEFAULT_COLOR_BLUE_VALUE
            sampleText.setTextColor(Color.rgb(ClockSettingDataHolder.DEFAULT_COLOR_RED_VALUE, ClockSettingDataHolder.DEFAULT_COLOR_GREEN_VALUE, ClockSettingDataHolder.DEFAULT_COLOR_BLUE_VALUE))
        }

        //色統一チェックボックスチェック時において、OKボタン押下時の出現アラートダイアログのOKボタンのコールバック
        val dialogOkCallback = {
            settingDataHolder.colorDay = sampleText.currentTextColor
            settingDataHolder.colorMonth = sampleText.currentTextColor
            settingDataHolder.colorYear = sampleText.currentTextColor
            settingDataHolder.colorWeek = sampleText.currentTextColor
            settingDataHolder.colorHour = sampleText.currentTextColor
            settingDataHolder.colorDivideTime = sampleText.currentTextColor
            settingDataHolder.colorMinute = sampleText.currentTextColor
            settingDataHolder.colorSecond = sampleText.currentTextColor
            settingDataHolder.colorTopAlarmTime = sampleText.currentTextColor
            activity.updateClockView()
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
            fileIOWrapper.saveTextColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)
            popupWindow.dismiss()
        }

        buttonColorOk.setOnClickListener { _ ->
            if (checkBoxUnifyColor.isChecked) {
                val dialog = AttentionDialog.newInstance(activity.resources.getString(R.string.unify_time_colors_dialog_message))
                dialog.okListener = dialogOkCallback
                dialog.show(activity.supportFragmentManager, TAG)
            } else {
                when (v.id) {
                    R.id.frame_now_day -> {
                        settingDataHolder.colorDay = sampleText.currentTextColor
                        textDay.setTextColor(settingDataHolder.colorDay)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_month -> {
                        settingDataHolder.colorMonth = sampleText.currentTextColor
                        textMonth.setTextColor(settingDataHolder.colorMonth)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_year -> {
                        settingDataHolder.colorYear = sampleText.currentTextColor
                        textYear.setTextColor(settingDataHolder.colorYear)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
                    }
                    R.id.text_now_week -> {
                        settingDataHolder.colorWeek = sampleText.currentTextColor
                        textWeek.setTextColor(settingDataHolder.colorWeek)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_hour -> {
                        settingDataHolder.colorHour = sampleText.currentTextColor
                        textHour.setTextColor(settingDataHolder.colorHour)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
                    }
                    R.id.text_divide_hour_and_minute -> {
                        settingDataHolder.colorDivideTime = sampleText.currentTextColor
                        textDivideTime.setTextColor(settingDataHolder.colorDivideTime)
                        fileIOWrapper.saveTextColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_minute -> {
                        settingDataHolder.colorMinute = sampleText.currentTextColor
                        textMinute.setTextColor(settingDataHolder.colorMinute)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_second -> {
                        settingDataHolder.colorSecond = sampleText.currentTextColor
                        textSecond.setTextColor(settingDataHolder.colorSecond)
                        fileIOWrapper.saveTextColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
                    }
                    R.id.frame_top_alarm_time -> {
                        settingDataHolder.colorTopAlarmTime = sampleText.currentTextColor
                        textTopAlarmTime.setTextColor(settingDataHolder.colorTopAlarmTime)
                        fileIOWrapper.saveTextColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)
                    }
                    else -> {
                        throw IllegalArgumentException()
                    }
                }
                popupWindow.dismiss()
            }
        }

        popupWindow.contentView = popupView
        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true

        val d = activity.windowManager.defaultDisplay
        var p2 = Point()
        // ナビゲーションバーを除く画面サイズを取得
        d.getSize(p2)

        popupWindow.width = p2.x - 200
        popupWindow.height = p2.y - 120

        // 画面中央に表示
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

}