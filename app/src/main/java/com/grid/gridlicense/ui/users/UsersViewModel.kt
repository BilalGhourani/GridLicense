package com.grid.gridlicense.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.data.user.User
import com.grid.gridlicense.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
        private val userRepository: UserRepository
) : ViewModel() {

    private val _UsersState = MutableStateFlow(UsersState())
    val usersState: MutableStateFlow<UsersState> = _UsersState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchUsers()
        }
    }

    private suspend fun fetchUsers() {
        val listOfUsers =  userRepository.getAllUsers()
        viewModelScope.launch(Dispatchers.Main) {
            usersState.value = usersState.value.copy(
                users = listOfUsers
            )
        }
    }

    fun saveUser(user: User) {
        if (user.userName.isNullOrEmpty() || user.password.isNullOrEmpty()) {
            usersState.value = usersState.value.copy(
                warning = Event("Please fill all inputs"),
                isLoading = false
            )
            return
        }
        usersState.value = usersState.value.copy(
            isLoading = true
        )
        val isInserting = user.isNew()
        CoroutineScope(Dispatchers.IO).launch {
            if (isInserting) {
                user.prepareForInsert()
                val addedModel = userRepository.insert(user)
                val users = usersState.value.users
                users.add(addedModel)
                viewModelScope.launch(Dispatchers.Main) {
                    usersState.value = usersState.value.copy(
                        users = users,
                        selectedUser = addedModel,
                        isLoading = false,
                        clear = true
                    )
                }
            } else {
                userRepository.update(user)
                viewModelScope.launch(Dispatchers.Main) {
                    usersState.value = usersState.value.copy(
                        selectedUser = user,
                        isLoading = false,
                        clear = true
                    )
                }
            }
        }
    }

    fun deleteSelectedUser(user: User) {
        if (user.userId.isEmpty()) {
            usersState.value = usersState.value.copy(
                warning = Event("Please select an user to delete"),
                isLoading = false
            )
            return
        }
        usersState.value = usersState.value.copy(
            warning = null,
            isLoading = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            userRepository.delete(user)
            val users = usersState.value.users
            users.remove(user)
            viewModelScope.launch(Dispatchers.Main) {
                usersState.value = usersState.value.copy(
                    users = users,
                    selectedUser = User(),
                    isLoading = false,
                    clear = true
                )
            }
        }
    }

}