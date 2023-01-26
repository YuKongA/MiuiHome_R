package com.yuk.miuiHomeR.hook

import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.putObject
import com.yuk.miuiHomeR.mPrefsMap

object DisableRecentViewWallpaperDarken : BaseHook() {
    override fun init() {

        if (!mPrefsMap.getBoolean("home_disable_darken")) return
        findMethod("com.miui.home.recents.DimLayer") {
            name == "dim" && parameterCount == 3
        }.hookBefore {
            it.args[0] = 0.0f
            it.thisObject.putObject("mCurrentAlpha", 0.0f)
        }

    }
}
