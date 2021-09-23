package chom.arikui.waffle.digitalclockapp

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.DialogFragment

class AttentionDialog : DialogFragment() {

    companion object {

        private const val KEY_MSG = "msg";
        private const val KEY_TEXT_POS_BUTTON = "text_pos_button";
        private const val KEY_TEXT_NEG_BUTTON = "text_neg_button";

        fun newInstance(sentence: String, textPosiButton: String, textNegButton: String = ""): AttentionDialog {
            val dialog = AttentionDialog()
            val args = Bundle()
            args.putString(KEY_MSG, sentence)
            args.putString(KEY_TEXT_POS_BUTTON, textPosiButton)
            args.putString(KEY_TEXT_NEG_BUTTON, textNegButton)
            dialog.arguments = args
            return dialog
        }
    }

    var okListener: (() -> Unit)? = null
    var negListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val bundle = arguments
        val messageStr = bundle?.getString(KEY_MSG) ?: ""
        val textPOSButton = bundle?.getString(KEY_TEXT_POS_BUTTON) ?: ""
        val textNEGButton = bundle?.getString(KEY_TEXT_NEG_BUTTON) ?: ""

        // 画面外タッチで、ダイアログを非表示にできないようにする
        isCancelable = false
        val builder = AlertDialog.Builder(activity, R.style.MyAlertDialogStyle)

        builder.setIcon(R.mipmap.ico_attention1).setTitle(getString(R.string.attention))
                .setMessage(messageStr)
        if (!TextUtils.isEmpty(textPOSButton)) {
            builder.setPositiveButton(textPOSButton){ dialog, which ->
                okListener?.let { it() }
            }
        }
        if (!TextUtils.isEmpty(textNEGButton)) {
            builder.setNegativeButton(textNEGButton){ dialog, which ->
                negListener?.let { it() }
            }
        }

        return builder.create()
    }
}