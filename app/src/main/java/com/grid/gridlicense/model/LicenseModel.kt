package com.grid.gridlicense.model

import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.license.License

data class LicenseModel(
        val license: License = License(),
        val client: Client = Client(),
) {

    fun getClientName(): String {
        return client.clientName ?: "N/A"
    }

    fun getCompany(): String {
        return license.company ?: "N/A"
    }

    fun getExpiryDate(): String {
        if (license.expirydate != null) {
            DateHelper.getDateInFormat(
                license.expirydate!!,
                "yyyy-MM-dd"
            )
        }
        return "N/A"
    }
}
