package com.grid.gridlicense

import android.app.Application
import android.util.Log
import com.grid.gridlicense.utils.FileUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

@HiltAndroidApp
class App : Application() {

    private var configs: JSONObject? = null
    private var isFirebaseInitialized: Boolean = false

    companion object {
        private lateinit var instance: App
        fun getInstance(): App {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineScope(Dispatchers.IO).launch {
            initAppConfig()
        }
    }

    private fun initAppConfig() {
        val configString: String = FileUtils.readFileFromAssets(
            "config.json",
            this
        )
        if (configString.isNotEmpty()) {
            try {
                configs = JSONObject(configString)
            } catch (e: JSONException) {
                Log.e(
                    "App",
                    e.message.toString()
                )
            }
        }
    }

    fun getConfigValue(
            key: String,
            fallback: String = ""
    ): String {
        return configs?.optString(
            key,
            fallback
        ) ?: fallback
    }
}