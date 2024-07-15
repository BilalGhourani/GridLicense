package com.grid.gridlicense.data.license

import com.grid.gridlicense.model.LicenseModel

interface LicenseRepository {

    // suspend is a coroutine keyword,
    // instead of having a callback we can just wait till insert is done
    suspend fun insert(license: License): License

    // Delete an License
    suspend fun delete(license: License)

    // Update an License
    suspend fun update(license: License)


    // Get all Licenses as stream.
    suspend fun getAllLicenses(): MutableList<License>

    // Get all License Models with as stream.
    suspend fun getAllLicenseModels(): MutableList<LicenseModel>
}
