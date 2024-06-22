package com.grid.gridlicense.ui.license

import com.grid.gridlicense.model.Event

data class LicenseState(
        var isDone: Boolean = false,
        var isLoading: Boolean = false,
        var clear: Boolean = false,
        var warning: Event<String>? = null,
)