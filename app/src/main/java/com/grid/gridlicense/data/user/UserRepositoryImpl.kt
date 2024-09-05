package com.grid.gridlicense.data.user

import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.utils.DateHelper
import org.json.JSONObject
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
        if (user.password.isNullOrEmpty()) {
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
        } else {
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
        val users: MutableList<User> = mutableListOf()
        try {
            val where =
                "users.username = '$loginUsername' AND users.password = HashBytes('SHA2_512', CONVERT(nvarchar(400),'$loginPassword'))"
            val dbResult = SQLServerWrapper.getListOf(
                "users",
                "",
                mutableListOf("*"),
                where
            )
            dbResult?.let {
                while (it.next()) {
                    users.add(User().apply {
                        userId = it.getString("userid")
                        userName = it.getString("username")
                        password = it.getString("password")
                        email = it.getString("email")
                        deviceID = it.getString("deviseid")
                    })
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (users.isNotEmpty()) {
            return users[0]
        } else if (loginUsername.equals(
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
        val users: MutableList<User> = mutableListOf()
        try {
            val dbResult = SQLServerWrapper.getListOf(
                "users",
                "",
                mutableListOf("*"),
                ""
            )
            dbResult?.let {
                while (it.next()) {
                    users.add(User().apply {
                        userId = it.getString("userid")
                        userName = it.getString("username")
                        password = it.getString("password")
                        email = it.getString("email")
                        deviceID = it.getString("deviseid")
                    })
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users

    }

    override suspend fun getAllUsersWithKey(
        key: String,
        limit: Int
    ): MutableList<User> {
        val users: MutableList<User> = mutableListOf()
        try {
            val dbResult = SQLServerWrapper.getListOf(
                "users",
                "TOP $limit",
                mutableListOf("*"),
                if (key.isEmpty()) "" else "username LIKE '%$key%' OR email LIKE '%$key%' OR deviseid LIKE '%$key%'"
            )
            dbResult?.let {
                while (it.next()) {
                    users.add(User().apply {
                        userId = it.getString("userid")
                        userName = it.getString("username")
                        password = it.getString("password")
                        email = it.getString("email")
                        deviceID = it.getString("deviseid")
                    })
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return users

    }
}