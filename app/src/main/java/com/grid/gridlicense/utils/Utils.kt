package com.grid.gridlicense.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.time.Year
import java.util.Calendar
import java.util.UUID

object Utils {

    fun generateRandomUuidString(): String {
        return UUID.randomUUID().toString()
    }

    fun isTablet(configuration: Configuration): Boolean {
        return configuration.screenWidthDp > 840
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