package com.grid.gridlicense.ui.license.manage

import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.license.License
import com.grid.gridlicense.model.Event

data class LicenseState(
        var clients: MutableList<Client> = mutableListOf(),
        var selectedLicense: License = License(),
        var isDone: Boolean = false,
        var isLoading: Boolean = false,
        var clear: Boolean = false,
        var warning: Event<String>? = null,
)