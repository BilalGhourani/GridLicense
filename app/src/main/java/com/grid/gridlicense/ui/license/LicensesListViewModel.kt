package com.grid.gridlicense.ui.license

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.App
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.model.LicenseModel
import com.grid.gridlicense.utils.CryptoUtils
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.utils.FileUtils
import com.grid.gridlicense.data.license.LicenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LicensesListViewModel @Inject constructor(
        private val licenseRepository: LicenseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LicensesListState())
    val state: MutableStateFlow<LicensesListState> = _state

    var licenseFile: File? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchLicenses()
        }
    }

    fun showError(message: String) {
        state.value = state.value.copy(
            warning = Event(message),
            isLoading = false
        )
    }

    private suspend fun fetchLicenses() {
        val listOfLicenseModels = licenseRepository.getAllLicenseModels()
        viewModelScope.launch(Dispatchers.Main) {
            state.value = state.value.copy(
                licenseModels = listOfLicenseModels
            )
        }
    }

    fun deleteLicense(licenseModel: LicenseModel) {
        if (licenseModel.license.licenseid.isEmpty()) {
            showError("Please select a License to delete")
            return
        }
        state.value = state.value.copy(
            warning = null,
            isLoading = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            licenseRepository.delete(licenseModel.license)
            val licenseModels = state.value.licenseModels
            licenseModels.remove(licenseModel)
            viewModelScope.launch(Dispatchers.Main) {
                state.value = state.value.copy(
                    licenseModels = licenseModels,
                    isLoading = false,
                    clear = true
                )
            }
        }
    }

    fun generate(
            context: Context,
            deviceID: String,
            expiryDate: Date,
            isRta: Boolean,
            rtaDays: String,
    ) {
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