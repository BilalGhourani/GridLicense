package com.grid.gridlicense.data

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.grid.gridlicense.App
import com.grid.gridlicense.model.SettingsModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

object DataStoreManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "GRID_POS_DATA_STORE"
    )

    suspend fun initValues() {
        initSettingsModel()
    }

    suspend fun putString(
            key: String,
            value: String
    ) {
        val preferencesKey = stringPreferencesKey(key)
        App.getInstance().applicationContext.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun removeKey(
            key: String
    ) {
        val preferencesKey = stringPreferencesKey(key)
        App.getInstance().applicationContext.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }

    suspend fun putInt(
            key: String,
            value: Int
    ) {
        val preferencesKey = intPreferencesKey(key)
        App.getInstance().applicationContext.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun putLong(
            key: String,
            value: Long
    ) {
        val preferencesKey = longPreferencesKey(key)
        App.getInstance().applicationContext.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun putBoolean(
            key: String,
            value: Boolean
    ) {
        val preferencesKey = booleanPreferencesKey(key)
        App.getInstance().applicationContext.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    suspend fun getString(
            key: String,
            fallback: String = ""
    ): String {
        return try {
            val preferencesKey = stringPreferencesKey(key)
            val preferences = App.getInstance().applicationContext.dataStore.data.first()
            val value = preferences[preferencesKey]
            return value ?: fallback
        } catch (e: Exception) {
            Log.e(
                "exception",
                e.message.toString()
            )
            fallback
        }
    }

    suspend fun getInt(
            key: String,
            fallback: Int = -1
    ): Int {
        return try {
            val preferencesKey = intPreferencesKey(key)
            val preferences = App.getInstance().applicationContext.dataStore.data.first()
            val value = preferences[preferencesKey]
            return value ?: fallback
        } catch (e: Exception) {
            Log.e(
                "exception",
                e.message.toString()
            )
            fallback
        }
    }

    suspend fun getLong(
            key: String,
            fallback: Long = -1L
    ): Long {
        return try {
            val preferencesKey = longPreferencesKey(key)
            val preferences = App.getInstance().applicationContext.dataStore.data.first()
            return preferences[preferencesKey] ?: fallback
        } catch (e: Exception) {
            Log.e(
                "exception",
                e.message.toString()
            )
            fallback
        }
    }

    suspend fun getBoolean(
            key: String,
            fallback: Boolean = false
    ): Boolean {
        return try {
            val preferencesKey = booleanPreferencesKey(key)
            val preferences = App.getInstance().applicationContext.dataStore.data.first()
            val value = preferences[preferencesKey]
            return value ?: fallback
        } catch (e: Exception) {
            Log.e(
                "exception",
                e.message.toString()
            )
            fallback
        }
    }

    suspend fun getValueByKey(key: Preferences.Key<*>): Any? {
        val value = App.getInstance().applicationContext.dataStore.data.map {
            it[key]
        }
        return value.firstOrNull()
    }

    suspend fun deleteAll() {
        App.getInstance().applicationContext.dataStore.edit {
            it.clear()
        }
    }

    private suspend fun initSettingsModel() {
        val buttonColor = getString(DataStoreKeys.BUTTON_COLOR.key)
        val buttonTextColor = getString(DataStoreKeys.BUTTON_TEXT_COLOR.key)
        val topBarColor = getString(DataStoreKeys.TOP_BAR_COLOR.key)
        val backgroundColor = getString(DataStoreKeys.BACKGROUND_COLOR.key)
        val textColor = getString(DataStoreKeys.TEXT_COLOR.key)

        if (buttonColor.isNotEmpty()) {
            SettingsModel.buttonColor = Color(buttonColor.toColorInt())
        }

        if (buttonTextColor.isNotEmpty()) {
            SettingsModel.buttonTextColor = Color(buttonTextColor.toColorInt())
        }

        if (topBarColor.isNotEmpty()) {
            SettingsModel.topBarColor = Color(topBarColor.toColorInt())
        }

        if (backgroundColor.isNotEmpty()) {
            SettingsModel.backgroundColor = Color(backgroundColor.toColorInt())
        }

        if (textColor.isNotEmpty()) {
            SettingsModel.textColor = Color(textColor.toColorInt())
        }

        SettingsModel.sqlServerPath = getString(
            DataStoreKeys.SQL_SERVER_PATH.key,
            ""
        )
        SettingsModel.sqlServerDbUser = getString(
            DataStoreKeys.SQL_SERVER_DB_USER.key,
            ""
        )
        SettingsModel.sqlServerDbPassword = getString(
            DataStoreKeys.SQL_SERVER_DB_PASSWORD.key,
            ""
        )
    }

    enum class DataStoreKeys(val key: String) {
        SQL_SERVER_PATH("SQL_SERVER_PATH"), SQL_SERVER_DB_USER("SQL_SERVER_DB_USER"), SQL_SERVER_DB_PASSWORD("SQL_SERVER_DB_PASSWORD"),


        BUTTON_COLOR("BUTTON_COLOR"), BUTTON_TEXT_COLOR("BUTTON_TEXT_COLOR"), BACKGROUND_COLOR(
            "BACKGROUND_COLOR"
        ),
        TOP_BAR_COLOR("TOP_BAR_COLOR"), TEXT_COLOR("TEXT_COLOR"),
    }
}