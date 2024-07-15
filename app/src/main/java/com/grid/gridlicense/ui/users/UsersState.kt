package com.grid.gridlicense.ui.users

import com.grid.gridlicense.model.Event
import com.grid.gridlicense.data.user.User

data class UsersState(
        val users: MutableList<User> = mutableListOf(),
        var selectedUser: User = User(),
        val isLoading: Boolean = false,
        var clear: Boolean = false,
        val warning: Event<String>? = null,
)