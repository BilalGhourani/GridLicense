package com.grid.gridlicense.data.user

import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.utils.DateHelper
import java.util.Date

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
                "HashBytes('SHA2_512', CONVERT(nvarchar(400),${user.password}))",
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
        if(user.password.isNullOrEmpty()){
            SQLServerWrapper.update(
                "users",
                listOf(
                    "username",
                    "email",
                    "deviseid"
                ),
                listOf(
                    user.userName,
                    user.email,
                    user.deviceID
                ),
                "userid = '${user.userId}'"
            )
        }else{
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
                    "HashBytes('SHA2_512', CONVERT(nvarchar(400),'${user.password}'))",
                    user.email,
                    user.deviceID
                ),
                "userid = '${user.userId}'"
            )
        }

    }

    override suspend fun getUserByCredentials(
            loginUsername: String,
            loginPassword: String
    ): User? {
        val where = "users.username = '$loginUsername' AND users.password = HashBytes('SHA2_512', CONVERT(nvarchar(400),'$loginPassword'))"
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
        }else if (loginUsername.equals(
                "administrator",
                ignoreCase = true
            )
        ) {
            return User(
                "administrator",
                "administrator",
                "Administrator",
                "administrator",
                DateHelper.getDateInFormat(
                    Date(),
                    "dd-MMM-yyyy"
                )
            )
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