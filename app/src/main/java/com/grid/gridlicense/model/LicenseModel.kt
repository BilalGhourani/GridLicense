package com.grid.gridlicense.model

import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.license.License

data class LicenseModel(
        val license: License = License(),
        val client: Client = Client(),
) : DataModel() {
    private var expiryDate: String = ""
    override fun getName(): String {
        return "${client.clientName} - ${license.deviseid} - ${getExpiryDate()}"
    }

    fun getClientName(): String {
        return client.clientName ?: "N/A"
    }

    fun getCompany(): String {
        return license.company ?: "N/A"
    }

    fun getDeviceID(): String {
        return license.deviseid ?: "N/A"
    }

    fun getExpiryDate(): String {
        if (expiryDate.isNotEmpty()) {
            return expiryDate
        }
        if (license.expirydate != null) {
            expiryDate = DateHelper.getDateInFormat(
                license.expirydate!!,
                "yyyy-MM-dd"
            )
            return expiryDate
        }
        return "N/A"
    }

    fun getCreatedDate(): String {
        if (license.createddate != null) {
            return DateHelper.getDateInFormat(
                license.createddate!!,
                "yyyy-MM-dd"
            )
        }
        return "N/A"
    }

    fun isMatchingTheKey(key: String): Boolean {
        return client.clientName?.lowercase()?.contains(key) == true || license.company?.lowercase()
            ?.contains(key) == true || license.deviseid?.lowercase()
            ?.contains(key) == true || getExpiryDate().contains(key) || getCreatedDate().contains(key)
    }
}
