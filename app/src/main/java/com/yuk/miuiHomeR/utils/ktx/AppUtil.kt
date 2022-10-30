package com.yuk.miuiHomeR.utils.ktx

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import com.github.kyuubiran.ezxhelper.init.InitFields
import com.yuk.miuiHomeR.utils.PrefsUtils.getSharedPrefs
import moralnorm.internal.utils.DeviceHelper
import java.io.DataOutputStream
import java.util.*

fun dp2px(dpValue: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, InitFields.appContext.resources.displayMetrics).toInt()

fun px2dp(pxValue: Int): Int = (pxValue / InitFields.appContext.resources.displayMetrics.density + 0.5f).toInt()

fun getDensityDpi(): Int =
    (InitFields.appContext.resources.displayMetrics.widthPixels / InitFields.appContext.resources.displayMetrics.density).toInt()

fun isDarkMode(): Boolean =
    InitFields.appContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

@SuppressLint("PrivateApi")
@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun getProp(mKey: String): String =
    Class.forName("android.os.SystemProperties").getMethod("get", String::class.java).invoke(Class.forName("android.os.SystemProperties"), mKey)
        .toString()

@SuppressLint("PrivateApi")
fun getProp(mKey: String, defaultValue: Boolean): Boolean =
    Class.forName("android.os.SystemProperties").getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
        .invoke(Class.forName("android.os.SystemProperties"), mKey, defaultValue) as Boolean

fun checkVersionName(): String = InitFields.appContext.packageManager.getPackageInfo(InitFields.appContext.packageName, 0).versionName

fun isAlpha(): Boolean =
    InitFields.appContext.packageManager.getPackageInfo(InitFields.appContext.packageName, 0).versionName.contains("ALPHA", ignoreCase = true)

fun isPadDevice(): Boolean = DeviceHelper.isTablet() || DeviceHelper.isFoldDevice()
fun atLeastAndroidS(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun atLeastAndroidT(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

fun checkVersionCode(): Long = InitFields.appContext.packageManager.getPackageInfo(
    InitFields.appContext.packageName, 0
).longVersionCode

fun checkMiuiVersion(): String = when (getProp("ro.miui.ui.version.name")) {
    "V130" -> "13"
    "V125" -> "12.5"
    "V12" -> "12"
    "V11" -> "11"
    "V10" -> "10"
    else -> "?"
}

fun checkAndroidVersion(): String = getProp("ro.build.version.release")

/**
 * 执行 Shell 命令
 * @param command Shell 命令
 */
fun execShell(command: String) {
    try {
        val p = Runtime.getRuntime().exec("su")
        val outputStream = p.outputStream
        val dataOutputStream = DataOutputStream(outputStream)
        dataOutputStream.writeBytes(command)
        dataOutputStream.flush()
        dataOutputStream.close()
        outputStream.close()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
}

@SuppressLint("DiscouragedApi")
fun getCornerRadiusTop(): Int {
    val resourceId = InitFields.appContext.resources.getIdentifier("rounded_corner_radius_top", "dimen", "android")
    return if (resourceId > 0) {
        InitFields.appContext.resources.getDimensionPixelSize(resourceId)
    } else 100
}

fun setLocale(resources: Resources, locale: Locale) {
    var setLocal:Locale = locale
    if ("und" == locale.toLanguageTag() || "system" == locale.toLanguageTag()) {
        setLocal = Locale.getDefault()
    }
    Log.d("AppUtil", "setLocale: ${setLocal.toLanguageTag()}")
    val config = resources.configuration
    config.setLocale(setLocal)
    Locale.setDefault(setLocal)
    resources.updateConfiguration(config, resources.displayMetrics)
    if (atLeastAndroidT())
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(
                setLocal.toLanguageTag()
            )
        )
}

fun getLocale(tag: String, context: Context): Locale {
    return if (TextUtils.isEmpty(tag) || "SYSTEM" == tag) {
        val syslang = LocaleManagerCompat.getSystemLocales(context)
            .toLanguageTags().trim()
        Log.d("AppUtil", "syslang=$syslang")
        Locale(syslang)
    } else
        Locale.forLanguageTag(tag)
}

fun getLocale(context: Context): Locale {
    val pref = getSharedPrefs(context, true)
    val tag: String? = pref.getString("prefs_key_settings_language", null)
    Log.d("AppUtil", "getLocale: $tag")
    if (tag != null) {
        return getLocale(tag, context)
    }
    return getLocale("", context)
}

fun restart(context: Context?,activity: Activity) {
    try {
        activity.finish()
        activity.startActivity(Intent(context, activity.javaClass))
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        activity.recreate()
    } catch (e: Throwable) {
        Log.e("MIUIHomeR", "Failed to restart", e)
        activity.recreate()
    }
}
