package com.grid.gridlicense.ui.license.manage

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.text.format.DateUtils
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.App
import com.grid.gridlicense.BuildConfig
import com.grid.gridlicense.R
import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.client.ClientRepository
import com.grid.gridlicense.data.license.License
import com.grid.gridlicense.data.license.LicenseRepository
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.model.LicenseModel
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

    var selectedClientId: String? = null
    var listOfLicenses: MutableList<LicenseModel> = mutableListOf()

    var licenseFile: File? = null
    var licenseFilePath: String? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            SQLServerWrapper.openConnection()
        }
    }

    fun fetchLicenses() {
        state.value = state.value.copy(
            isLoading = true,
            warning = null
        )
        viewModelScope.launch(Dispatchers.IO) {
            val listOfLicenseModels = licenseRepository.getAllLicenseModels()
            listOfLicenses = listOfLicenseModels
            filterLicenses()
        }
    }

    fun filterClientLicenses() {
        viewModelScope.launch(Dispatchers.IO) {
            filterLicenses()
        }
    }

    private suspend fun filterLicenses() {
        if (selectedClientId.isNullOrEmpty()) {
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    licenses = listOfLicenses,
                    isLoading = false
                )
            }
        } else {
            val listOfLicenseModels = listOfLicenses.filter {
                it.license.cltid == selectedClientId
            }.toMutableList()
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    licenses = listOfLicenseModels,
                    isLoading = false
                )
            }
        }
    }

    fun fetchClients() {
        state.value = state.value.copy(
            isLoading = true,
            warning = null
        )
        viewModelScope.launch(Dispatchers.IO) {
            val listOfClients = clientRepository.getAllClients()
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    clients = listOfClients,
                    isLoading = false
                )
            }
        }
    }

    fun showError(message: String) {
        state.value = state.value.copy(
            warning = Event(message),
            isLoading = false
        )
    }

    fun saveLicense(
        context: Context,
    ) {
        val license = state.value.selectedLicense
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
                licenseRepository.insert(license)
                val client = state.value.selectedClient
                val licenses = state.value.licenses
                if (licenses.size > 0) {
                    licenses.add(
                        LicenseModel(
                            license,
                            client
                        )
                    )
                }
                viewModelScope.launch(Dispatchers.Main) {
                    generate(
                        context,
                        license,
                        licenses
                    )
                }
            } else {
                licenseRepository.update(license)
                viewModelScope.launch(Dispatchers.Main) {
                    generate(
                        context,
                        license,
                        state.value.licenses
                    )
                }
            }
        }
    }

    fun generate(
        context: Context,
        license: License,
        licenses: MutableList<LicenseModel>
    ) {
        state.value = state.value.copy(
            isLoading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val sep = "\\$@$\\"
            val deviceID = license.deviseid
            val dateString = DateHelper.getDateInFormat(
                license.expirydate!!,
                "yyyyMMdd"
            )
            val rtaStr = if (license.isRta) "1" else "0"
            val rtaDays = license.rtaDays
            val licenseString = if (rtaDays.isNullOrEmpty()) {
                "$deviceID$sep$dateString$sep$rtaStr"
            } else {

                "$deviceID$sep$dateString$sep$rtaStr$sep$rtaDays"
            }

            val encryptedOutput = CryptoUtils.encrypt(
                licenseString,
                App.getInstance().getConfigValue("key_for_license")
            )

            licenseFile = FileUtils.saveLicenseFile(
                context,
                encryptedOutput
            )
            val clientName =
                state.value.clients.firstOrNull { it.clientid == license.cltid }?.clientName ?: ""
            val date = DateHelper.getDateInFormat(license.createddate ?: Date(), "yyyyMMdd")
            licenseFilePath = FileUtils.saveToExternalStorage(
                context,
                "licenses",
                licenseFile!!.toUri(),
                "license-$clientName-$date",
                "license"
            )
            withContext(Dispatchers.Main) {
                state.value = state.value.copy(
                    selectedLicense = license,
                    licenses = licenses,
                    isDone = true,
                    isLoading = false
                )
            }
        }
    }

    fun deleteSelectedLicense() {
        val license = state.value.selectedLicense
        if (license.licenseid.isEmpty()) {
            showError("select a License!")
            return
        }
        state.value = state.value.copy(
            warning = null,
            isLoading = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            licenseRepository.delete(license)
            val licenses = state.value.licenses
            for (model in licenses) {
                if (model.license == license) {
                    licenses.remove(model)
                    break
                }
            }
            viewModelScope.launch(Dispatchers.Main) {
                state.value = state.value.copy(
                    selectedLicense = License(),
                    licenses = licenses,
                    isDone = false,
                    clear = true,
                    isLoading = false
                )
            }
        }
    }

    fun shareLicense(context: Context, callback: (Intent) -> Unit) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        val attachment: Uri = if (licenseFilePath != null) {
            Uri.parse(licenseFilePath)
        } else if (licenseFile != null) {
            FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID,
                licenseFile!!
            )
        } else {
            showError("No File found!")
            return
        }
        shareIntent.putExtra(
            Intent.EXTRA_STREAM,
            attachment
        )
        shareIntent.setType("application/octet-stream")
        callback.invoke(shareIntent)
    }

    fun openLicenseLocation(context: Context) {
        try {
            // Get the path to the "Download" folder
            val downloadUri = Uri.parse("content://com.android.externalstorage.documents/tree/primary:Download")

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                // Set the initial URI to point to the "Download" folder
                putExtra("android.provider.extra.INITIAL_URI", downloadUri)
            }

            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}