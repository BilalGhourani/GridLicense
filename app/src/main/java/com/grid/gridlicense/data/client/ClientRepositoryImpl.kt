package com.grid.gridlicense.data.client

import com.grid.gridlicense.data.SQLServerWrapper

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
                client.clientName,
                client.clientEmail,
                client.clientPhone,
                client.clientCountry
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
                client.clientName,
                client.clientEmail,
                client.clientPhone,
                client.clientCountry
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
                clientName = obj.optString("name")
                clientEmail = obj.optString("email")
                clientPhone = obj.optString("phone")
                clientCountry = obj.optString("country")
            })
        }
        return clients

    }
}