package com.grid.gridlicense.ui.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.data.client.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
        private val clientRepository: ClientRepository
) : ViewModel() {

    private val _ClientsState = MutableStateFlow(ClientsState())
    val clientsState: MutableStateFlow<ClientsState> = _ClientsState

    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchClients()
        }
    }

    private suspend fun fetchClients() {
        val listOfClients =  clientRepository.getAllClients()
        viewModelScope.launch(Dispatchers.Main) {
            clientsState.value = clientsState.value.copy(
                clients = listOfClients
            )
        }
    }

    fun saveClient(client: Client) {
        if (client.clientName.isNullOrEmpty()) {
            clientsState.value = clientsState.value.copy(
                warning = Event("Please fill all inputs"),
                isLoading = false
            )
            return
        }
        clientsState.value = clientsState.value.copy(
            isLoading = true
        )
        val isInserting = client.isNew()
        CoroutineScope(Dispatchers.IO).launch {
            if (isInserting) {
                client.prepareForInsert()
                val addedModel = clientRepository.insert(client)
                val clients = clientsState.value.clients
                clients.add(addedModel)
                viewModelScope.launch(Dispatchers.Main) {
                    clientsState.value = clientsState.value.copy(
                        clients = clients,
                        selectedClient = addedModel,
                        isLoading = false,
                        clear = true
                    )
                }
            } else {
                clientRepository.update(client)
                viewModelScope.launch(Dispatchers.Main) {
                    clientsState.value = clientsState.value.copy(
                        selectedClient = client,
                        isLoading = false,
                        clear = true
                    )
                }
            }
        }
    }

    fun deleteSelectedClient(client: Client) {
        if (client.clientid.isEmpty()) {
            clientsState.value = clientsState.value.copy(
                warning = Event("Please select a client to delete"),
                isLoading = false
            )
            return
        }
        clientsState.value = clientsState.value.copy(
            warning = null,
            isLoading = true
        )

        CoroutineScope(Dispatchers.IO).launch {
            clientRepository.delete(client)
            val clients = clientsState.value.clients
            clients.remove(client)
            viewModelScope.launch(Dispatchers.Main) {
                clientsState.value = clientsState.value.copy(
                    clients = clients,
                    selectedClient = Client(),
                    isLoading = false,
                    clear = true
                )
            }
        }
    }

}