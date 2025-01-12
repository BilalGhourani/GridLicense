package com.grid.gridlicense

import android.content.Intent

sealed class ActivityScopedUIEvent {
    data object Finish : ActivityScopedUIEvent()
    data object OpenAppSettings : ActivityScopedUIEvent()

    class StartChooserActivity(
        var intent: Intent
    ) : ActivityScopedUIEvent()

    class RequestStoragePermission(
       var delegate: (Boolean) -> Unit
    ) : ActivityScopedUIEvent()

}