package com.grid.pos.data.license

import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.utils.DateHelper
import java.util.Date

class LicenseRepositoryImpl() : LicenseRepository {
    override suspend fun insert(
            license: License
    ): License {
        SQLServerWrapper.insert(
            "licenses",
            listOf(
                "licenseid"
                ,"cltid"
                ,"company"
                ,"deviseid"
                ,"module"
                ,"expirydatemessage"
                ,"expirydate"
                ,"createduser"
                ,"createddate"
                ,"userstamp"
                ,"timestamp"
            ),
            listOf(
                license.licenseid,
                license.cltid,
                license.company,
                license.deviseid,
                license.module,
                license.expirydatemessage,
                license.expirydate,
                license.createduser,
                license.createddate,
                license.userstamp,
                license.timestamp
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
                "cltid"
                ,"company"
                ,"deviseid"
                ,"module"
                ,"expirydatemessage"
                ,"expirydate"
                ,"createduser"
                ,"createddate"
                ,"userstamp"
                ,"timestamp"
            ),
            listOf(
                license.cltid,
                license.company,
                license.deviseid,
                license.module,
                license.expirydatemessage,
                license.expirydate,
                license.createduser,
                license.createddate,
                license.userstamp,
                license.timestamp
            ),
            "licenseid = '${license.licenseid}'"
        )
    }

    override suspend fun getAllLicenses(): MutableList<License> {
        val dbResult = SQLServerWrapper.getListOf(
            "licenses",
            "",
            mutableListOf("*"),
            ""
        )
        val licenses: MutableList<License> = mutableListOf()
        dbResult.forEach { obj ->
            licenses.add(License().apply {
                licenseid = obj.optString("licenseid")
                cltid = obj.optString("cltid")
                company = obj.optString("company")
                deviseid = obj.optString("deviseid")
                module = obj.optString("module")
                expirydatemessage = obj.optBoolean("expirydatemessage")
                val expiry = obj.opt("expirydate")
                expirydate = if (expiry is Date) expiry else DateHelper.getDateFromString(
                    expiry as String,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                )
                createduser = obj.optString("createduser")
                val created = obj.opt("createddate")
                createddate = if (created is Date) created else DateHelper.getDateFromString(
                    created as String,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                )
                userstamp = obj.optString("userstamp")
                val timeSt = obj.opt("timestamp")
                timestamp = if (timeSt is Date) timeSt else DateHelper.getDateFromString(
                    timeSt as String,
                    "yyyy-MM-dd hh:mm:ss.SSS"
                )
            })
        }
        return licenses

    }
}