package com.grid.gridlicense.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.data.user.UserRepository
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.model.SettingsModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
        private val repository: UserRepository
) : ViewModel() {

    private val _usersState = MutableStateFlow(LoginState())
    val usersState: MutableStateFlow<LoginState> = _usersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            SQLServerWrapper.openConnection()
        }
    }

    fun login(
            username: String,
            password: String
    ) {
        if (username.isEmpty() || password.isEmpty()) {
            usersState.value = usersState.value.copy(
                warning = Event("Please fill all inputs"),
                isLoading = false,
                warningAction = ""
            )
            return
        }
        usersState.value = usersState.value.copy(
            isLoading = true,
            warningAction = ""
        )
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUserByCredentials(
                username,
                password
            )
            user?.let {
                SettingsModel.currentUser = it
                viewModelScope.launch(Dispatchers.Main) {
                    usersState.value = usersState.value.copy(
                        selectedUser = it,
                        isLoading = false,
                        isLoggedIn = true
                    )
                }
            } ?: run {
                viewModelScope.launch(Dispatchers.Main) {
                    usersState.value = usersState.value.copy(
                        isLoading = false,
                        warning = Event("Username or Password are incorrect!"),
                        warningAction = ""
                    )
                }
            }

        }
    }
}