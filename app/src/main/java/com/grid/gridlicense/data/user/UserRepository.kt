package com.grid.pos.data.user

interface UserRepository {

    // suspend is a coroutine keyword,
    // instead of having a callback we can just wait till insert is done
    suspend fun insert(user: User): User

    // Delete an User
    suspend fun delete(user: User)

    // Update an User
    suspend fun update(user: User)

    // Get User by it's ID
    suspend fun getUserByCredentials(
            loginUsername: String,
            loginPassword: String
    ): User?

    // Get all Users as stream.
    suspend fun getAllUsers(): MutableList<User>
}
