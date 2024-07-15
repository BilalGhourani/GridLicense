package com.grid.gridlicense.data.client

import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.utils.Utils

data class Client(
        var clientid: String,
        var clientName: String? = null,
        var clientEmail: String? = null,
        var clientPhone: String? = null,
        var clientCountry: String? = null,
        ) : DataModel() {
    constructor() : this("")

    override fun getId(): String {
        return clientid
    }

    override fun getName(): String {
        return clientName ?: ""
    }

    override fun isNew(): Boolean {
        return clientid.isEmpty()
    }

    override fun prepareForInsert() {
        if (clientid.isEmpty()) {
            clientid = Utils.generateRandomUuidString()
        }
    }
}
