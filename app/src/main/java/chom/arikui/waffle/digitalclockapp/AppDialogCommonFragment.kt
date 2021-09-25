package chom.arikui.waffle.digitalclockapp

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.lang.IllegalStateException

open class AppDialogCommonFragment : DialogFragment() {

    /**
     * ダイアログ表示しようとしたときにアプリがバックグラウンドに行ってしまっているときの
     * エラーをもみ消すダイアログ表示
     */
    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    /**
     * ダイアログ消そうとしたときにアプリがバックグラウンドに行ってしまっているときの
     * エラーをもみ消すダイアログ表示
     */
    override fun dismissAllowingStateLoss() {
        try {
            super.dismissAllowingStateLoss()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

}