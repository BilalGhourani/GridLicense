package com.grid.gridlicense.data.client

interface ClientRepository {

    // suspend is a coroutine keyword,
    // instead of having a callback we can just wait till insert is done
    suspend fun insert(client: Client): Client

    // Delete an Client
    suspend fun delete(client: Client)

    // Update an Client
    suspend fun update(client: Client)


    // Get all Clients as stream.
    suspend fun getAllClients(): MutableList<Client>
}
