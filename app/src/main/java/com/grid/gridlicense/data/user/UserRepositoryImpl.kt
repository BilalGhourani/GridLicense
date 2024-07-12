package com.grid.pos.data.user

import com.grid.gridlicense.data.SQLServerWrapper

class UserRepositoryImpl() : UserRepository {
    override suspend fun insert(
            user: User
    ): User {
        SQLServerWrapper.insert(
            "users",
            listOf(
                "userid",
                "username",
                "password",
                "email",
                "deviseid"
            ),
            listOf(
                user.userId,
                user.userName,
                user.password,
                user.email,
                user.deviceID
            )
        )
        return user
    }

    override suspend fun delete(
            user: User
    ) {
        SQLServerWrapper.delete(
            "users",
            "userid = '${user.userId}'"
        )
    }

    override suspend fun update(
            user: User
    ) {
        SQLServerWrapper.update(
            "users",
            listOf(
                "username",
                "password",
                "email",
                "deviseid"
            ),
            listOf(
                user.userName,
                user.password,
                user.email,
                user.deviceID
            ),
            "userid = '${user.userId}'"
        )
    }

    override suspend fun getUserByCredentials(
            loginUsername: String,
            loginPassword: String
    ): User? {
        val where = "username = $loginUsername AND password=hashBytes ('SHA2_512', CONVERT(nvarchar(4000),'$loginPassword'+cast(usr_salt as nvarchar(36))))"
        val dbResult = SQLServerWrapper.getListOf(
            "users",
            "",
            mutableListOf("*"),
            where
        )
        val users: MutableList<User> = mutableListOf()
        dbResult.forEach { obj ->
            users.add(User().apply {
                userId = obj.optString("userid")
                userName = obj.optString("username")
                password = obj.optString("password")
                email = obj.optString("email")
                deviceID = obj.optString("deviseid")
            })
        }
        if (users.isNotEmpty()) {
            return users[0]
        }
        return null
    }

    override suspend fun getAllUsers(): MutableList<User> {
        val dbResult = SQLServerWrapper.getListOf(
            "users",
            "",
            mutableListOf("*"),
            ""
        )
        val users: MutableList<User> = mutableListOf()
        dbResult.forEach { obj ->
            users.add(User().apply {
                userId = obj.optString("userid")
                userName = obj.optString("username")
                password = obj.optString("password")
                email = obj.optString("email")
                deviceID = obj.optString("deviseid")
            })
        }
        return users

    }
}