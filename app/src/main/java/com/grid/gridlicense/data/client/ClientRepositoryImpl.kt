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
            "clientid = '${client.clientid}'"
        )
    }

    override suspend fun getAllClients(): MutableList<Client> {
        val clients: MutableList<Client> = mutableListOf()
        try {
            val dbResult = SQLServerWrapper.getListOf(
                "clients",
                "",
                mutableListOf("*"),
                "",
                "ORDER BY name ASC"
            )
            dbResult?.let {
                while (it.next()) {
                    clients.add(Client().apply {
                        clientid = it.getString("clientid")
                        clientName = it.getString("name")
                        clientEmail = it.getString("email")
                        clientPhone = it.getString("phone")
                        clientCountry = it.getString("country")
                    })
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return clients

    }
}