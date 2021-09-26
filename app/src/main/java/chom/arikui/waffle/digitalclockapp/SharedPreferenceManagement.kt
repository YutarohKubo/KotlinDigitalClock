package chom.arikui.waffle.digitalclockapp

class SharedPreferenceManagement {

    companion object {
        const val NAME_SHOP_ITEM = "shop_item"
    }

    enum class KeyData(val keyName: String) {
        KEY_PREMIUM_BOOLEAN("item_premium")
    }

}