package com.grid.gridlicense.ui.license

import com.grid.gridlicense.model.Event
import com.grid.gridlicense.model.LicenseModel

data class LicensesListState(
        val licenseModels: MutableList<LicenseModel> = mutableListOf(),
        var isLoading: Boolean = false,
        var isDone: Boolean = false,
        var clear: Boolean = false,
        var warning: Event<String>? = null,
)