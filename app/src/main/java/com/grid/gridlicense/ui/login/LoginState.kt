package com.grid.gridlicense.ui.login

import com.grid.gridlicense.model.Event
import com.grid.pos.data.User.User

data class LoginState(
        var selectedUser: User = User(),
        var needLicense: Boolean = false,
        val isLoggedIn: Boolean = false,
        var isLoading: Boolean = false,
        var warning: Event<String>? = null,
        val warningAction: String? = null,
)