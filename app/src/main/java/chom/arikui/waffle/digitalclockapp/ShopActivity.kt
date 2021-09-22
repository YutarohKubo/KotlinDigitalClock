package chom.arikui.waffle.digitalclockapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import chom.arikui.waffle.digitalclockapp.databinding.ShopActivityBinding

class ShopActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ShopActivityBinding>(
            this, R.layout.shop_activity
        )
    }
}