package chom.arikui.waffle.digitalclockapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShopVM : ViewModel() {
    val upgradeEnable = MutableLiveData(false)
    val upgradeVisible = MutableLiveData(false)
    val premiumPriceLV = MutableLiveData("")
}