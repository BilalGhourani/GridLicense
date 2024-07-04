package com.grid.gridlicense.ui.license

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.App
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.utils.CryptoUtils
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.utils.FileUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LicenseState())
    val state: MutableStateFlow<LicenseState> = _state

    var licenseFile: File? = null

    fun showError(message: String) {
        state.value = state.value.copy(
            warning = Event(message),
            isLoading = false
        )
    }

    fun generate(
            context: Context,
            deviceID: String,
            expiryDate: Date,
            isRta: Boolean,
            rtaDays: String,
    ) {
        if (deviceID.isEmpty()) {
            showError("please enter a device id!")
            return
        }
        state.value = state.value.copy(
            isLoading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val sep = "\\$@$\\"
            val dateString = DateHelper.getDateInFormat(
                expiryDate,
                "yyyyMMdd"
            )
            val rtaStr = if (isRta) "1" else "0"
            val licenseString = if (rtaDays.isNotEmpty()) {
                "$deviceID$sep$dateString$sep$rtaStr$sep$rtaDays"
            } else {
                "$deviceID$sep$dateString$sep$rtaStr"
            }

            val encryptedOutput = CryptoUtils.encrypt(
                licenseString,
                App.getInstance().getConfigValue("key_for_license")
            )

            licenseFile = FileUtils.saveLicenseFile(
                context,
                encryptedOutput
            )
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    isDone = true,
                    isLoading = false
                )
            }
        }
    }
}