package com.grid.gridlicense.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.grid.gridlicense.model.HomeSectionModel
import java.time.Year
import java.util.Calendar
import java.util.UUID

object Utils {

    val homeViewList = mutableListOf(
        HomeSectionModel(
            "Users",
            "UsersView"
        ),
        HomeSectionModel(
            "Clients",
            "ClientsView"
        ),
        HomeSectionModel(
            "Licenses",
            "LicensesListView"
        )
    )

    fun generateRandomUuidString(): String {
        return UUID.randomUUID().toString()
    }

    fun isTablet(configuration: Configuration): Boolean {
        return configuration.screenWidthDp > 840
    }

    fun getListHeight(
            listSize: Int = 0,
            cellHeight: Int,
            min: Int = 1,
            max: Int = 8
    ): Dp {
        var size = listSize
        if (size < min) size = min
        else if (size > max) size = max
        return (size * cellHeight).dp + 50.dp
    }

    fun getCurrentYear(): String {
        val calendar: Calendar = Calendar.getInstance()
        val currentYear = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Year.now().value
        } else {
            calendar[Calendar.YEAR]
        }
        return currentYear.toString()
    }

    fun getIntValue(
            new: String,
            old: String
    ): String {
        return if (new.isEmpty()) {
            new
        } else {
            when (new.toIntOrNull()) {
                null -> old //old value
                else -> new  //new value
            }
        }
    }

    fun floatToColor(
            hue: Float,
            saturation: Float = 1f,
            brightness: Float = 1f
    ): Color {
        // Convert HSV to RGB
        val hsv = floatArrayOf(
            hue,
            saturation,
            brightness
        )
        return Color(android.graphics.Color.HSVToColor(hsv))
    }

    fun getDeviceID(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }

    fun getFirstInstallationTime(context: Context): Long {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
            packageInfo.firstInstallTime
        } catch (e: Exception) {
            Log.e(
                "exception",
                e.message.toString()
            )
            0
        }
    }

}