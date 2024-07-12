package com.grid.pos.data.client

import com.grid.pos.data.User.Client

interface ClientRepository {

    // suspend is a coroutine keyword,
    // instead of having a callback we can just wait till insert is done
    suspend fun insert(client: Client): Client

    // Delete an User
    suspend fun delete(client: Client)

    // Update an User
    suspend fun update(client: Client)


    // Get all Users as stream.
    suspend fun getAllClients(): MutableList<Client>
}
