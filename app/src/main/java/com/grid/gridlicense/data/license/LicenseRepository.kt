package com.grid.pos.data.license

interface LicenseRepository {

    // suspend is a coroutine keyword,
    // instead of having a callback we can just wait till insert is done
    suspend fun insert(license: License): License

    // Delete an User
    suspend fun delete(license: License)

    // Update an User
    suspend fun update(license: License)


    // Get all Users as stream.
    suspend fun getAllLicenses(): MutableList<License>
}
