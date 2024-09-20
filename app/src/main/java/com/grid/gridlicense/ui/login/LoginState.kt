package com.grid.gridlicense.ui.login

import com.grid.gridlicense.data.user.User
import com.grid.gridlicense.model.Event

data class LoginState(
        var selectedUser: User = User(),
        val isLoggedIn: Boolean = false,
        var isLoading: Boolean = false,
        var warning: Event<String>? = null,
        val warningAction: String? = null,
)