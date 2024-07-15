package com.grid.gridlicense.data.user

import com.grid.gridlicense.data.DataModel
import com.grid.gridlicense.utils.Utils

data class User(
        var userId: String,
        var userName: String? = null,
        var password: String? = null,
        var email: String? = null,
        var deviceID: String? = null,
        ) : DataModel() {
    constructor() : this("")

    override fun getId(): String {
        return userId
    }

    override fun getName(): String {
        return userName ?: ""
    }

    override fun isNew(): Boolean {
        return userId.isEmpty()
    }

    override fun prepareForInsert() {
        if (userId.isEmpty()) {
            userId = Utils.generateRandomUuidString()
        }
    }
}
