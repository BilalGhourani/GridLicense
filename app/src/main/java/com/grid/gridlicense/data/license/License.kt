package com.grid.gridlicense.data.license

import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.utils.Utils
import java.util.Date

data class License(
        var licenseid: String,
        var cltid: String? = null,
        var company: String? = null,
        var deviseid: String? = null,
        var module: String? = null,
        var expirydatemessage: Boolean = false,
        var expirydate: Date? = null,
        var isRta: Boolean = false,
        var rtaDays: String? = null,
        var createduser: String? = null,
        var createddate: Date? = null,
        var userstamp: String? = null,
        var timestamp: Date? = null,
        ) : DataModel() {
    constructor() : this("")

    override fun getId(): String {
        return licenseid
    }

    override fun getName(): String {
        return company ?: ""
    }

    override fun isNew(): Boolean {
        return licenseid.isEmpty()
    }

    override fun prepareForInsert() {
        if (licenseid.isEmpty()) {
            licenseid = Utils.generateRandomUuidString()
        }
    }
}
