package com.grid.gridlicense.ui.clients

import com.grid.gridlicense.model.Event
import com.grid.gridlicense.data.client.Client

data class ClientsState(
        val clients: MutableList<Client> = mutableListOf(),
        var selectedClient: Client = Client(),
        val isLoading: Boolean = false,
        var clear: Boolean = false,
        val warning: Event<String>? = null,
)