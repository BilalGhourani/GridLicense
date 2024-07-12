package com.grid.gridlicense

import com.grid.gridlicense.model.Event

data class ActivityState(
    var isLoggedIn: Boolean = false,
    var warning: Event<String>? = null,
    var forceLogout: Boolean = false
)
