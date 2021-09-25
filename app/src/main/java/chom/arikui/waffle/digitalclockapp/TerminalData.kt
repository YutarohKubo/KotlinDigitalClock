package chom.arikui.waffle.digitalclockapp

import android.content.Context

class TerminalData(context: Context) {

    var mPremiumState: Boolean

    init {
        val preference = context.getSharedPreferences(
            SharedPreferenceManagement.NAME_SHOP_ITEM,
            Context.MODE_PRIVATE
        )
        mPremiumState = preference.getBoolean(
            SharedPreferenceManagement.KeyData.KEY_PREMIUM_BOOLEAN.keyName,
            false
        )
    }

}