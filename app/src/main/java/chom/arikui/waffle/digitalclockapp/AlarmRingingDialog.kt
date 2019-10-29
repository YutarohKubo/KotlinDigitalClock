package chom.arikui.waffle.digitalclockapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class AlarmRingingDialog : DialogFragment() {

    companion object {
        fun newInstance(): AlarmRingingDialog {
            val dialog = AlarmRingingDialog()
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val act = activity
        val builder = AlertDialog.Builder(act)
        isCancelable = false
        return builder.setTitle("Ringing Alarm!!").setPositiveButton("StopAlarm") { dialog, which ->
            if (act is MainActivity) {
                act.stopAlarm()
                act.switchAlarm?.isChecked = false
                act.alarmCheckState = false
                act.saveAlarmCheckState()
                act.switchAlarmResource()
            }
        }.create()
    }
}