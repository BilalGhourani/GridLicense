package com.grid.gridlicense.data

abstract class DataModel {
    open fun getId(): String {
        return ""
    }

    open fun getName(): String {
        return ""
    }

    open fun isNew(): Boolean {
        return true
    }

    open fun prepareForInsert() {
    }
}