package com.grid.gridlicense.data.license

import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.model.LicenseModel
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.data.client.Client
import java.util.Date

class LicenseRepositoryImpl() : LicenseRepository {
    override suspend fun insert(
        license: License
    ): License {
        SQLServerWrapper.insert(
            "licenses",
            listOf(
                "licenseid",
                "cltid",
                "company",
                "deviseid",
                "module",
                "expirydatemessage",
                "expirydate",
                "createduser",
                "createddate",
                "userstamp",
                "timestamp"
            ),
            listOf(
                license.licenseid,
                license.cltid,
                license.company,
                license.deviseid,
                license.module,
                license.expirydatemessage,
                DateHelper.getDateInFormat(
                    license.expirydate!!,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                ),
                license.createduser,
                DateHelper.getDateInFormat(
                    license.createddate!!,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                ),
                license.userstamp,
                DateHelper.getDateInFormat(
                    license.timestamp!!,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                )
            )
        )
        return license
    }

    override suspend fun delete(
        license: License
    ) {
        SQLServerWrapper.delete(
            "licenses",
            "licenseid = '${license.licenseid}'"
        )
    }

    override suspend fun update(
        license: License
    ) {
        SQLServerWrapper.update(
            "licenses",
            listOf(
                "cltid",
                "company",
                "deviseid",
                "module",
                "expirydatemessage",
                "expirydate"
            ),
            listOf(
                license.cltid,
                license.company,
                license.deviseid,
                license.module,
                license.expirydatemessage,
                DateHelper.getDateInFormat(
                    license.expirydate!!,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                )
            ),
            "licenseid = '${license.licenseid}'"
        )
    }

    override suspend fun getAllLicenses(): MutableList<License> {
        val licenses: MutableList<License> = mutableListOf()
        try {
            val dbResult = SQLServerWrapper.getListOf(
                "licenses",
                "",
                mutableListOf("*"),
                ""
            )
            dbResult?.let {
                while (it.next()) {
                    licenses.add(License().apply {
                        licenseid = it.getString("licenseid")
                        cltid = it.getString("cltid")
                        company = it.getString("company")
                        deviseid = it.getString("deviseid")
                        module = it.getString("module")
                        expirydatemessage = it.getBoolean("expirydatemessage")
                        val expiry = it.getObject("expirydate")
                        expirydate = if (expiry is Date) expiry else DateHelper.getDateFromString(
                            expiry as String,
                            "yyyy-MM-dd hh:mm:ss.SSS"
                        )
                        createduser = it.getString("createduser")
                        val created = it.getObject("createddate")
                        createddate =
                            if (created is Date) created else DateHelper.getDateFromString(
                                created as String,
                                "yyyy-MM-dd hh:mm:ss.SSS"
                            )
                        userstamp = it.getString("userstamp")
                        val timeSt = it.getObject("timestamp")
                        timestamp = if (timeSt is Date) timeSt else DateHelper.getDateFromString(
                            timeSt as String,
                            "yyyy-MM-dd hh:mm:ss.SSS"
                        )
                    })
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return licenses

    }

    override suspend fun getAllLicenseModels(): MutableList<LicenseModel> {
        val licenseModels: MutableList<LicenseModel> = mutableListOf()
        try {
            val dbResult = SQLServerWrapper.getListOf(
                "licenses",
                "",
                mutableListOf("*"),
                "",
                "INNER JOIN clients on cltid = clientid"
            )
            dbResult?.let {
                while (it.next()) {
                    val license = License()
                    license.licenseid = it.getString("licenseid")
                    license.cltid = it.getString("cltid")
                    license.company = it.getString("company")
                    license.deviseid = it.getString("deviseid")
                    license.module = it.getString("module")
                    license.expirydatemessage = it.getBoolean("expirydatemessage")
                    val expiry = it.getObject("expirydate")
                    license.expirydate = when (expiry) {
                        null -> null
                        is Date -> expiry
                        else -> DateHelper.getDateFromString(
                            expiry as String,
                            "yyyy-MM-dd hh:mm:ss.SSS"
                        )
                    }
                    license.createduser = it.getString("createduser")
                    val created = it.getObject("createddate")
                    license.createddate = when (created) {
                        null -> null
                        is Date -> created
                        else -> DateHelper.getDateFromString(
                            created as String,
                            "yyyy-MM-dd hh:mm:ss.SSS"
                        )
                    }
                    license.userstamp = it.getString("userstamp")
                    val timeSt = it.getObject("timestamp")
                    license.timestamp = when (timeSt) {
                        null -> null
                        is Date -> timeSt
                        else -> DateHelper.getDateFromString(
                            timeSt as String,
                            "yyyy-MM-dd hh:mm:ss.SSS"
                        )
                    }
                    val client = Client()
                    client.clientid = it.getString("clientid")
                    client.clientName = it.getString("name")
                    client.clientEmail = it.getString("email")
                    client.clientPhone = it.getString("phone")
                    client.clientCountry = it.getString("country")
                    licenseModels.add(
                        LicenseModel(
                            license,
                            client
                        )
                    )
                }
                SQLServerWrapper.closeResultSet(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return licenseModels
    }
}