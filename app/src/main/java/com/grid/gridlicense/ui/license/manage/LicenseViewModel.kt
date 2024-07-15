package com.grid.gridlicense.ui.license.manage

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.App
import com.grid.gridlicense.data.client.ClientRepository
import com.grid.gridlicense.data.license.License
import com.grid.gridlicense.data.license.LicenseRepository
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.utils.CryptoUtils
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.utils.FileUtils
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
class LicenseViewModel @Inject constructor(
        private val licenseRepository: LicenseRepository,
        private val clientRepository: ClientRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LicenseState())
    val state: MutableStateFlow<LicenseState> = _state

    var licenseFile: File? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchClients()
        }
    }

    private suspend fun fetchClients() {
        val listOfClients = clientRepository.getAllClients()
        viewModelScope.launch(Dispatchers.Main) {
            state.value = state.value.copy(
                clients = listOfClients
            )
        }
    }

    fun showError(message: String) {
        state.value = state.value.copy(
            warning = Event(message),
            isLoading = false
        )
    }

    fun saveClient(license: License) {
        if (license.cltid.isNullOrEmpty()) {
            showError("select a client!")
            return
        }
        state.value = state.value.copy(
            isLoading = true,
            warning = null
        )
        val isInserting = license.isNew()
        CoroutineScope(Dispatchers.IO).launch {
            if (isInserting) {
                license.prepareForInsert()
                val addedModel = licenseRepository.insert(license)
                viewModelScope.launch(Dispatchers.Main) {
                    state.value = state.value.copy(
                        selectedLicense = addedModel,
                        isLoading = false,
                        clear = true
                    )
                }
            } else {
                licenseRepository.update(license)
                viewModelScope.launch(Dispatchers.Main) {
                    state.value = state.value.copy(
                        selectedLicense = license,
                        isLoading = false,
                        clear = true
                    )
                }
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