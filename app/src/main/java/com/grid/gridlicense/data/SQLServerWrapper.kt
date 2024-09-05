package com.grid.gridlicense.data

import com.grid.gridlicense.model.SettingsModel
import org.json.JSONObject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

object SQLServerWrapper {

    private var mConnection: Connection? = null
    private fun getDatabaseConnection(): Connection {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return DriverManager.getConnection(
            SettingsModel.getSqlServerDbPath(),
            SettingsModel.sqlServerDbUser,
            SettingsModel.sqlServerDbPassword
        )
    }

    fun openConnection() {
        try {
            mConnection = getDatabaseConnection()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeConnection() {
        try {
            mConnection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun closeResultSet(resultSet: ResultSet) {
        try {
            val statement = resultSet.statement
            if (mConnection == null && !statement.connection.isClosed) {
                statement.connection.close()
            }
            if (!statement.isClosed) {
                statement.close()
            }
            if (!resultSet.isClosed) {
                resultSet.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getListOf(
            tableName: String,
            colPrefix: String = "",
            columns: MutableList<String>,
            where: String,
            joinSubQuery: String = "",
    ): ResultSet? {
        try {
            val connection = getConnection()
            val cols = columns.joinToString(", ")
            val whereQuery = if (where.isNotEmpty()) "WHERE $where" else ""
            val query = "SELECT $colPrefix $cols FROM $tableName $joinSubQuery $whereQuery"
            val statement = connection.prepareStatement(query)
            return statement.executeQuery()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun executeProcedure(
            procedureName: String,
            params: List<Any>,
    ): List<JSONObject> {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var resultSet: ResultSet? = null
        val result = mutableListOf<JSONObject>()
        try {
            connection = getConnection()

            val parameters = params.joinToString(", ")
            // Prepare the stored procedure call
            val query = "select dbo.$procedureName($parameters) as $procedureName" // Modify with your procedure and parameters
            statement = connection.prepareStatement(query)
            resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val obj = JSONObject()
                val value = resultSet.getString(procedureName) // Replace with actual column names
                obj.put(
                    procedureName,
                    value
                )
                result.add(obj)
            }


        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            resultSet?.close()
            statement?.close()
            if (mConnection == null) {
                connection?.close()
            }
        }
        return result
    }

    fun insert(
            tableName: String,
            columns: List<String>,
            values: List<Any?>
    ) {
        if (columns.size != values.size) {
            return
        }
        val cols = columns.joinToString(", ")
        val vals = values.joinToString(", ") {
            if (it is String) {
                if (it.startsWith("HashBytes")) {
                    it
                } else {
                    "'$it'"
                }
            } else {
                "'$it'"
            }
        }
        val sqlQuery = "INSERT INTO $tableName ($cols) VALUES ($vals)"
        runDbQuery(
            sqlQuery,
            mutableListOf()
        )
    }

    fun update(
            tableName: String,
            columns: List<String>,
            values: List<Any?>,
            where: String
    ) {
        if (columns.size != values.size) {
            return
        }
        //val setStatement = columns.joinToString(", ") { "$it = ?" }
        // Combine the lists into the desired format
        val setStatement = columns.zip(values) { param, value ->
            if (value is String && value.startsWith("HashBytes")) {
                "$param=$value"
            } else {
                "$param='$value'"
            }
        }.joinToString(", ")
        val whereQuery = if (where.isNotEmpty()) "WHERE $where " else ""
        val sqlQuery = "UPDATE $tableName SET $setStatement $whereQuery"
        runDbQuery(
            sqlQuery,
            mutableListOf()
        )
    }

    fun delete(
            tableName: String,
            where: String,
            innerJoin: String = ""
    ) {
        val whereQuery = if (where.isNotEmpty()) "WHERE $where " else ""
        val sqlQuery = "DELETE FROM $tableName $innerJoin $whereQuery"
        runDbQuery(
            sqlQuery,
            listOf()
        )
    }

    private fun runDbQuery(
            query: String,
            params: List<Any?>
    ): Boolean {
        var connection: Connection? = null
        var statement: PreparedStatement? = null
        var isSuccess = false
        try {
            connection = getConnection()
            statement = connection.prepareStatement(query)

            params.forEachIndexed { index, param ->
                statement.setObject(
                    index + 1,
                    param
                )
            }
            val executeVal = statement.executeUpdate()
            isSuccess = executeVal > 0
        } catch (e: Exception) {
            e.printStackTrace()
            isSuccess = false
        } finally {
            statement?.close()
            if (mConnection == null) {
                connection?.close()
            }
        }
        return isSuccess
    }

    private fun getConnection(): Connection {
        if (mConnection != null && !mConnection!!.isClosed) {
            return mConnection!!
        }
        return getDatabaseConnection()
    }
}