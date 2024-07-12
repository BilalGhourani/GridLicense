package com.grid.pos.data.client

import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.pos.data.User.Client

class ClientRepositoryImpl() : ClientRepository {
    override suspend fun insert(
            client: Client
    ): Client {
        SQLServerWrapper.insert(
            "clients",
            listOf(
                "clientid",
                "name",
                "email",
                "phone",
                "country"
            ),
            listOf(
                client.clientid,
                client.name,
                client.email,
                client.phone,
                client.country
            )
        )
        return client
    }

    override suspend fun delete(
            client: Client
    ) {
        SQLServerWrapper.delete(
            "clients",
            "clientid = '${client.clientid}'"
        )
    }

    override suspend fun update(
            client: Client
    ) {
        SQLServerWrapper.update(
            "clients",
            listOf(
                "name",
                "email",
                "phone",
                "country"
            ),
            listOf(
                client.name,
                client.email,
                client.phone,
                client.country
            ),
            "userid = '${client.clientid}'"
        )
    }

    override suspend fun getAllClients(): MutableList<Client> {
        val dbResult = SQLServerWrapper.getListOf(
            "clients",
            "",
            mutableListOf("*"),
            ""
        )
        val clients: MutableList<Client> = mutableListOf()
        dbResult.forEach { obj ->
            clients.add(Client().apply {
                clientid = obj.optString("clientid")
                name = obj.optString("name")
                email = obj.optString("email")
                phone = obj.optString("phone")
                country = obj.optString("country")
            })
        }
        return clients

    }
}