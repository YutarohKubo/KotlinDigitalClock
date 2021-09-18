package chom.arikui.waffle.digitalclockapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

class PopupColor(private val activity: MainActivity) {

    companion object {
        private const val TAG = "POPUP_COLOR"
    }

    private var mPopupWindow: PopupWindow? = null
    private lateinit var imagePicSetting: ImageView
    private var imageBmp: Bitmap? = null
    private lateinit var buttonImageRotate: ImageButton

    fun showPopup(v: View) {
        mPopupWindow = PopupWindow(activity)
        val popupView = activity.layoutInflater.inflate(R.layout.layout_set_color_popup, null)
        val fileIOWrapper = activity.fileIOWrapper

        val textRValue = popupView.findViewById<TextView>(R.id.text_r_value)
        val textGValue = popupView.findViewById<TextView>(R.id.text_g_value)
        val textBValue = popupView.findViewById<TextView>(R.id.text_b_value)
        val seekBarRed = popupView.findViewById<SeekBar>(R.id.seekbar_r)
        val seekBarGreen = popupView.findViewById<SeekBar>(R.id.seekbar_g)
        val seekBarBlue = popupView.findViewById<SeekBar>(R.id.seekbar_b)
        val frameSample = popupView.findViewById<FrameLayout>(R.id.frame_sample)
        val sampleTextBackground = popupView.findViewById<TextView>(R.id.text_sample_background)
        var sampleText = popupView.findViewById<TextView>(R.id.text_sample)
        val sampleBackgroundFrame = popupView.findViewById<FrameLayout>(R.id.frame_background_setting)
        imagePicSetting = popupView.findViewById(R.id.image_pic_setting)
        val sampleTextTitle = popupView.findViewById<TextView>(R.id.text_sample_title)
        val buttonDefaultColor = popupView.findViewById<ImageButton>(R.id.button_default_color)
        val buttonColorOk = popupView.findViewById<ImageButton>(R.id.button_color_ok)
        val checkBoxUnifyArea = popupView.findViewById<LinearLayout>(R.id.checkbox_unify_area)
        val checkBoxUnifyColor = popupView.findViewById<CheckBox>(R.id.checkbox_unify_colors)
        val radioBackgroundMode = popupView.findViewById<RadioGroup>(R.id.radio_background_mode)
        val radioColor = popupView.findViewById<RadioButton>(R.id.radio_mode_color)
        val buttonSetPicture = popupView.findViewById<TextView>(R.id.button_set_picture)
        buttonImageRotate = popupView.findViewById(R.id.button_image_rotate)

        val textDay = activity.findViewById<TextView>(R.id.text_now_day)
        val textMonth = activity.findViewById<TextView>(R.id.text_now_month)
        val textYear = activity.findViewById<TextView>(R.id.text_now_year)
        val textWeek = activity.findViewById<TextView>(R.id.text_now_week)
        val textHour = activity.findViewById<TextView>(R.id.text_now_hour)
        val textDivideTime = activity.findViewById<TextView>(R.id.text_divide_hour_and_minute)
        val textMinute = activity.findViewById<TextView>(R.id.text_now_minute)
        val textSecond = activity.findViewById<TextView>(R.id.text_now_second)
        val textTopAlarmTime = activity.findViewById<TextView>(R.id.text_top_alarm_time)
        val backgroundFrame = activity.findViewById<FrameLayout>(R.id.activity_root)
        val backgroundImage = activity.findViewById<ImageView>(R.id.image_pic)

        val displaySize = activity.displaySize()

        when (v.id) {
            R.id.frame_now_day -> {
                sampleText.text = textDay.text
                sampleTextTitle.text = "DAY"
                sampleText.setTextColor(ClockSettingDataHolder.colorDay)
            }
            R.id.frame_now_month -> {
                sampleText.text = textMonth.text
                sampleTextTitle.text = "MONTH"
                sampleText.setTextColor(ClockSettingDataHolder.colorMonth)
            }
            R.id.frame_now_year -> {
                sampleTextBackground.text = "8888"
                sampleText.text = textYear.text
                sampleTextTitle.text = "YEAR"
                sampleText.setTextColor(ClockSettingDataHolder.colorYear)
            }
            R.id.text_now_week -> {
                frameSample.visibility = View.GONE
                sampleText = popupView.findViewById(R.id.text_now_week_sample)
                sampleText.visibility = View.VISIBLE
                sampleText.text = textWeek.text
                sampleTextTitle.text = "WEEK of day"
                sampleText.setTextColor(ClockSettingDataHolder.colorWeek)
            }
            R.id.frame_now_hour -> {
                sampleText.text = textHour.text
                sampleTextTitle.text = "HOUR"
                sampleText.setTextColor(ClockSettingDataHolder.colorHour)
            }
            R.id.text_divide_hour_and_minute -> {
                frameSample.visibility = View.GONE
                sampleTextTitle.visibility = View.GONE
                sampleText = popupView.findViewById(R.id.text_divide_hour_and_minute_sample)
                sampleText.visibility = View.VISIBLE
                sampleText.text = textDivideTime.text
                sampleText.setTextColor(ClockSettingDataHolder.colorDivideTime)
            }
            R.id.frame_now_minute -> {
                sampleText.text = textMinute.text
                sampleTextTitle.text = "MINUTE"
                sampleText.setTextColor(ClockSettingDataHolder.colorMinute)
            }
            R.id.frame_now_second -> {
                sampleText.text = textSecond.text
                sampleTextTitle.text = "SECOND"
                sampleText.setTextColor(ClockSettingDataHolder.colorSecond)
            }
            R.id.frame_top_alarm_time -> {
                sampleTextBackground.text = "88:88"
                sampleText.text = textTopAlarmTime.text
                sampleTextTitle.text = "ALARM TIME"
                sampleText.setTextColor(ClockSettingDataHolder.colorTopAlarmTime)
            }
            R.id.activity_root -> {
                frameSample.visibility = View.GONE
                radioBackgroundMode.visibility = View.VISIBLE
                val imageSettingArea = popupView.findViewById<LinearLayout>(R.id.image_setting_area)
                buttonImageRotate.setOnClickListener {
                    // 画像回転ボタン押下で、時計回りに90度回転
                    imageSampleRotate90()
                }
                val spaceOkBelow = popupView.findViewById<Space>(R.id.space_button_ok_below)
                radioBackgroundMode.setOnCheckedChangeListener { group, checkedId ->
                    when(checkedId) {
                        R.id.radio_mode_color -> {
                            imagePicSetting.visibility = View.GONE
                            imageSettingArea.visibility = View.GONE
                            buttonDefaultColor.visibility = View.VISIBLE
                            spaceOkBelow.visibility = View.GONE
                            sampleTextTitle.visibility = View.VISIBLE
                        }
                        R.id.radio_mode_pic -> {
                            imagePicSetting.visibility = View.VISIBLE
                            imageSettingArea.visibility = View.VISIBLE
                            buttonDefaultColor.visibility = View.GONE
                            spaceOkBelow.visibility = View.VISIBLE
                            sampleTextTitle.visibility = View.GONE
                        }
                    }
                }
                // タイトルは"BACKGROUND"
                sampleTextTitle.text = "BACKGROUND"
                // 写真の設定ボタン
                buttonSetPicture.setOnClickListener {
                    // ユーザー指定のファイル管理アプリに飛ばして、画像を選択させる
                    openImageSelecting()
                }
                // 表示色の統一チェックボックスは非表示とする
                checkBoxUnifyArea.visibility = View.GONE
                val frameHeight = CalculateUtil.convertDp2Px(96, activity)
                val frameWidth = frameHeight * (displaySize.x.toFloat() / displaySize.y)
                val lParam = RelativeLayout.LayoutParams(frameWidth.toInt(), frameHeight.toInt())
                lParam.setMargins(0, 0, 0, CalculateUtil.convertDp2Px(5, activity).toInt())
                sampleBackgroundFrame.layoutParams = lParam
                sampleBackgroundFrame.visibility = View.VISIBLE
                sampleBackgroundFrame.setBackgroundColor(ClockSettingDataHolder.colorBackground)
                // 画像回転ボタンの有効無効状態を更新する
                updateStateImageRotate()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }

        var redValue = Color.red(sampleText.currentTextColor)
        var greenValue = Color.green(sampleText.currentTextColor)
        var blueValue = Color.blue(sampleText.currentTextColor)
        // カラーシークバーに関する設定
        if (v.id == R.id.activity_root) {
            // 背景の設定である場合
            val frameBackgroundDrawable = sampleBackgroundFrame.background as ColorDrawable
            redValue = Color.red(frameBackgroundDrawable.color)
            greenValue = Color.green(frameBackgroundDrawable.color)
            blueValue = Color.blue(frameBackgroundDrawable.color)
        }
        textRValue.text = redValue.toString()
        textGValue.text = greenValue.toString()
        textBValue.text = blueValue.toString()
        seekBarRed.progress = redValue
        seekBarGreen.progress = greenValue
        seekBarBlue.progress = blueValue

        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textRValue.text = progress.toString()
                if (v.id == R.id.activity_root) {
                    // 背景の設定である場合
                    sampleBackgroundFrame.setBackgroundColor(
                        Color.rgb(
                            progress,
                            seekBarGreen.progress,
                            seekBarBlue.progress
                        )
                    )
                } else {
                    sampleText.setTextColor(
                        Color.rgb(
                            progress,
                            seekBarGreen.progress,
                            seekBarBlue.progress
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textGValue.text = progress.toString()
                if (v.id == R.id.activity_root) {
                    // 背景の設定である場合
                    sampleBackgroundFrame.setBackgroundColor(
                        Color.rgb(
                            seekBarRed.progress,
                            progress,
                            seekBarBlue.progress
                        )
                    )
                } else {
                    sampleText.setTextColor(
                        Color.rgb(
                            seekBarRed.progress,
                            progress,
                            seekBarBlue.progress
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textBValue.text = progress.toString()
                if (v.id == R.id.activity_root) {
                    // 背景の設定である場合
                    sampleBackgroundFrame.setBackgroundColor(
                        Color.rgb(
                            seekBarRed.progress,
                            seekBarGreen.progress,
                            progress
                        )
                    )
                } else {
                    sampleText.setTextColor(
                        Color.rgb(
                            seekBarRed.progress,
                            seekBarGreen.progress,
                            progress
                        )
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        buttonDefaultColor.setOnClickListener { _ ->
            val defaultRedValue: Int
            val defaultGreenValue: Int
            val defaultBlueValue: Int
            // サンプル色をデフォルトに変更
            if (v.id == R.id.activity_root) {
                defaultRedValue = ClockSettingDataHolder.DEFAULT_BACKGROUND_RED_VALUE
                defaultGreenValue = ClockSettingDataHolder.DEFAULT_BACKGROUND_GREEN_VALUE
                defaultBlueValue = ClockSettingDataHolder.DEFAULT_BACKGROUND_BLUE_VALUE
                sampleBackgroundFrame.setBackgroundColor(
                    Color.rgb(
                        defaultRedValue,
                        defaultGreenValue,
                        defaultBlueValue
                    )
                )
            } else {
                defaultRedValue = ClockSettingDataHolder.DEFAULT_COLOR_RED_VALUE
                defaultGreenValue = ClockSettingDataHolder.DEFAULT_COLOR_GREEN_VALUE
                defaultBlueValue = ClockSettingDataHolder.DEFAULT_COLOR_BLUE_VALUE
                sampleText.setTextColor(
                    Color.rgb(
                        defaultRedValue,
                        defaultGreenValue,
                        defaultBlueValue
                    )
                )
            }

            // シークバーのアップデート
            textRValue.text = defaultRedValue.toString()
            textGValue.text = defaultGreenValue.toString()
            textBValue.text = defaultBlueValue.toString()
            seekBarRed.progress = defaultRedValue
            seekBarGreen.progress = defaultGreenValue
            seekBarBlue.progress = defaultBlueValue
        }

        //色統一チェックボックスチェック時において、OKボタン押下時の出現アラートダイアログのOKボタンのコールバック
        val dialogOkCallback = {
            ClockSettingDataHolder.colorDay = sampleText.currentTextColor
            ClockSettingDataHolder.colorMonth = sampleText.currentTextColor
            ClockSettingDataHolder.colorYear = sampleText.currentTextColor
            ClockSettingDataHolder.colorWeek = sampleText.currentTextColor
            ClockSettingDataHolder.colorHour = sampleText.currentTextColor
            ClockSettingDataHolder.colorDivideTime = sampleText.currentTextColor
            ClockSettingDataHolder.colorMinute = sampleText.currentTextColor
            ClockSettingDataHolder.colorSecond = sampleText.currentTextColor
            ClockSettingDataHolder.colorTopAlarmTime = sampleText.currentTextColor
            activity.updateClockColor()
            fileIOWrapper.saveColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
            fileIOWrapper.saveColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)
            if (mPopupWindow != null) {
                mPopupWindow!!.dismiss()
            }
        }

        buttonColorOk.setOnClickListener { _ ->
            if (checkBoxUnifyColor.isChecked) {
                val dialog =
                    AttentionDialog.newInstance(activity.resources.getString(R.string.unify_time_colors_dialog_message))
                dialog.okListener = dialogOkCallback
                dialog.show(activity.supportFragmentManager, TAG)
            } else {
                when (v.id) {
                    R.id.frame_now_day -> {
                        ClockSettingDataHolder.colorDay = sampleText.currentTextColor
                        textDay.setTextColor(ClockSettingDataHolder.colorDay)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_DAY_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_month -> {
                        ClockSettingDataHolder.colorMonth = sampleText.currentTextColor
                        textMonth.setTextColor(ClockSettingDataHolder.colorMonth)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_MONTH_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_year -> {
                        ClockSettingDataHolder.colorYear = sampleText.currentTextColor
                        textYear.setTextColor(ClockSettingDataHolder.colorYear)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_YEAR_COLOR_FILE_NAME)
                    }
                    R.id.text_now_week -> {
                        ClockSettingDataHolder.colorWeek = sampleText.currentTextColor
                        textWeek.setTextColor(ClockSettingDataHolder.colorWeek)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_WEEK_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_hour -> {
                        ClockSettingDataHolder.colorHour = sampleText.currentTextColor
                        textHour.setTextColor(ClockSettingDataHolder.colorHour)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_HOUR_COLOR_FILE_NAME)
                    }
                    R.id.text_divide_hour_and_minute -> {
                        ClockSettingDataHolder.colorDivideTime = sampleText.currentTextColor
                        textDivideTime.setTextColor(ClockSettingDataHolder.colorDivideTime)
                        fileIOWrapper.saveColor(FileIOWrapper.DIVIDE_HOUR_AND_MINUTE_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_minute -> {
                        ClockSettingDataHolder.colorMinute = sampleText.currentTextColor
                        textMinute.setTextColor(ClockSettingDataHolder.colorMinute)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_MINUTE_COLOR_FILE_NAME)
                    }
                    R.id.frame_now_second -> {
                        ClockSettingDataHolder.colorSecond = sampleText.currentTextColor
                        textSecond.setTextColor(ClockSettingDataHolder.colorSecond)
                        fileIOWrapper.saveColor(FileIOWrapper.NOW_SECOND_COLOR_FILE_NAME)
                    }
                    R.id.frame_top_alarm_time -> {
                        ClockSettingDataHolder.colorTopAlarmTime = sampleText.currentTextColor
                        textTopAlarmTime.setTextColor(ClockSettingDataHolder.colorTopAlarmTime)
                        fileIOWrapper.saveColor(FileIOWrapper.TOP_ALARM_TIME_COLOR_FILE_NAME)
                    }
                    R.id.activity_root -> {
                        val frameBackgroundDrawable = sampleBackgroundFrame.background as ColorDrawable
                        ClockSettingDataHolder.colorBackground = frameBackgroundDrawable.color
                        backgroundFrame.setBackgroundColor(ClockSettingDataHolder.colorBackground)
                        fileIOWrapper.saveColor(FileIOWrapper.CLOCK_BACKGROUND_COLOR)
                        ClockSettingDataHolder.backgroundBmp = imageBmp
                        backgroundImage.setImageBitmap(ClockSettingDataHolder.backgroundBmp)
                        fileIOWrapper.saveBackgroundPic()
                        if (radioColor.isChecked) {
                            backgroundImage.visibility = View.GONE
                        } else {
                            backgroundImage.visibility = View.VISIBLE
                        }
                    }
                    else -> {
                        throw IllegalArgumentException()
                    }
                }
                mPopupWindow?.dismiss()
            }
        }

        mPopupWindow?.contentView = popupView
        mPopupWindow?.isOutsideTouchable = true
        mPopupWindow?.isFocusable = true

        mPopupWindow?.width = displaySize.x - 200
        mPopupWindow?.height = displaySize.y - 120

        // 画面中央に表示
        mPopupWindow?.showAtLocation(popupView, Gravity.CENTER, 0, 0)
    }

    /**
     * Bitmapを背景画像サンプル領域に、当てはめる
     */
    fun setImageSampleFromBitmap(bitmap: Bitmap) {
        if (mPopupWindow != null) {
            imageBmp = bitmap
            imagePicSetting.setImageBitmap(bitmap)
            updateStateImageRotate()
        }
    }

    /**
     * サンプル画像をnoImageへリセットする
     */
    private fun resetImageSample() {
        imageBmp = null
        imagePicSetting.setImageResource(R.drawable.noimage)
        updateStateImageRotate()
    }

    /**
     * サンプル画像を90度時計回りに回転する
     */
    private fun imageSampleRotate90() {
        val bitmap = (imagePicSetting.drawable as BitmapDrawable).bitmap
        val matrix = Matrix()
        val width = bitmap.width
        val height = bitmap.height
        matrix.setRotate(90F, width.toFloat() / 2, height.toFloat() / 2)
        val rotatedBitMap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        imageBmp = rotatedBitMap
        imagePicSetting.setImageBitmap(rotatedBitMap)
    }

    /**
     * 画像回転ボタンの有効無効状態を更新する
     */
    private fun updateStateImageRotate() {
        buttonImageRotate.isEnabled = imageBmp != null
    }

    fun isShowing() = (mPopupWindow !=null && mPopupWindow!!.isShowing)

    /**
     * 画像選択アプリへ移行する (Todo VERSION_CODES.KITKAT以降)
     */
    private fun openImageSelecting() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        activity.startActivityForResult(intent, MainActivity.READ_PIC_REQ_CODE)
    }

}