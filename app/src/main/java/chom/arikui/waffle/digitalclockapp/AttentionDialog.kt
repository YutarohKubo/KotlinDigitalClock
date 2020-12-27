package chom.arikui.waffle.digitalclockapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class AttentionDialog : DialogFragment() {

    companion object {

        fun newInstance(sentence: String): AttentionDialog {
            val dialog = AttentionDialog()
            val args = Bundle()
            args.putString("text", sentence)
            dialog.arguments = args
            return dialog
        }
    }

    var okListener: (() -> Unit)? = null
    var negListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bundle = arguments

        val builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)

        return builder.setIcon(R.mipmap.ico_attention1).setTitle("Attention")
                .setMessage(bundle?.getString("text")).setPositiveButton("Yes"){ dialog, which ->
                    okListener?.let { it() }
                }
                .setNegativeButton("No"){ dialog, which ->
                    negListener?.let { it() }
                }.create()
    }
}