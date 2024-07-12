package com.grid.pos.data.client

import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.utils.Utils

data class Client(
        var clientid: String,
        var name: String? = null,
        var email: String? = null,
        var phone: String? = null,
        var country: String? = null,
        ) : DataModel() {
    constructor() : this("")

    override fun getId(): String {
        return clientid
    }

    override fun getName(): String {
        return name ?: ""
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
