package com.grid.gridlicense.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.data.user.User
import com.grid.gridlicense.data.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
        private val userRepository: UserRepository
) : ViewModel() {

    private val _UsersState = MutableStateFlow(UsersState())
    val usersState: MutableStateFlow<UsersState> = _UsersState

    init {
        //fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val listOfUsers = userRepository.getAllUsers()
            withContext(Dispatchers.Main) {
                usersState.value = usersState.value.copy(
                    users = listOfUsers
                )
            }
        }
    }

    fun searchInUsers(key: String) {
        usersState.value = usersState.value.copy(
            warning = null,
            isLoading = true
        )
        viewModelScope.launch(Dispatchers.IO) {
            val listOfUsers = userRepository.getAllUsersWithKey(key)
            withContext(Dispatchers.Main) {
                usersState.value = usersState.value.copy(
                    users = listOfUsers,
                    isLoading = false
                )
            }
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
                //user.prepareForInsert()
                SQLServerWrapper.openConnection()
                for (i in 28..1000) {
                    val usr = user.copy(userName = "user${i + 1}")
                    usr.prepareForInsert()
                    val addedModel = userRepository.insert(usr)
                    val users = usersState.value.users
                    users.add(addedModel)
                    if(i==1000) {
                        withContext(Dispatchers.Main) {
                            usersState.value = usersState.value.copy(
                                users = users,
                                selectedUser = addedModel,
                                isLoading = false,
                                clear = true
                            )
                        }
                    }
                }
                SQLServerWrapper.closeConnection()
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