package com.grid.gridlicense.model

import androidx.compose.ui.graphics.Color
import com.grid.pos.data.User.User

object SettingsModel {
    var buttonColor: Color = Color.Blue
    var buttonTextColor: Color = Color.White
    var topBarColor: Color = Color.White
    var backgroundColor: Color = Color.White
    var textColor: Color = Color.Black

    var currentUser: User? = null

    var sqlServerPath: String? = null
    var sqlServerDbUser: String? = null
    var sqlServerDbPassword: String? = null

    fun getSqlServerDbPath(): String {
        return "jdbc:jtds:sqlserver://${sqlServerPath};encrypt=true"
    }
}
