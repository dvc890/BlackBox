package top.niunaijun.blackbox.bean

import android.graphics.drawable.Drawable
import top.niunaijun.bcore.entity.location.BLocation

data class FakeLocationBean(
    val userID: Int,
    val name: String,
    val icon: Drawable,
    val packageName: String,
    var fakeLocationPattern: Int,
    var fakeLocation: BLocation?
)
