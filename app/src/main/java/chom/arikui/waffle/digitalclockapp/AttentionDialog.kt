package chom.arikui.waffle.digitalclockapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AttentionDialog : DialogFragment() {

    companion object {

        lateinit var callbackOk: () -> Unit?

        fun newInstance(sentence: String, buttonOkCallback: () -> Unit?): AttentionDialog {
            val dialog = AttentionDialog()
            val args = Bundle()
            args.putString("text", sentence)
            dialog.arguments = args
            callbackOk = buttonOkCallback
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bundle = arguments

        val builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)

        return builder.setIcon(R.mipmap.ico_attention1).setTitle("Attention")
                .setMessage(bundle?.getString("text")).setPositiveButton("Yes"){ dialog, which ->
                    callbackOk()
                }
                .setNegativeButton("No"){ dialog, which ->

                }.create()
    }
}