package com.grid.gridlicense

import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.grid.gridlicense.ui.navigation.AuthNavGraph
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.ui.theme.White
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val activityViewModel: ActivityScopedViewModel by viewModels()

    private var connectivityManager: ConnectivityManager? = null
    private val networkHandler = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
        }

        override fun onLost(network: Network) {
            Toast.makeText(
                this@MainActivity,
                "The application no longer has the ability to access the internet.",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
        ) {
        }

        override fun onLinkPropertiesChanged(
                network: Network,
                linkProperties: LinkProperties
        ) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        connectivityManager = getSystemService(ConnectivityManager::class.java)
        window.setBackgroundDrawableResource(R.drawable.white_background)
        setContent {
            val navController = rememberNavController()
            GridLicenseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AuthNavGraph(
                        modifier = Modifier
                            .background(color = White)
                            .padding(0.dp),
                        navController = navController,
                        activityViewModel = activityViewModel,
                        startDestination = "LoginView"
                    )
                }
            }
        }
        registerActivityScopedEvent()
    }

    override fun onResume() {
        super.onResume()
        connectivityManager?.registerDefaultNetworkCallback(networkHandler)
    }

    override fun onPause() {
        super.onPause()
        connectivityManager?.unregisterNetworkCallback(networkHandler)
    }

    private fun registerActivityScopedEvent() {
        activityViewModel.mainActivityEvent.onEach { sharedEvent ->
            when (sharedEvent) {
                is ActivityScopedUIEvent.Finish -> {
                    this@MainActivity.finish()
                }

                is ActivityScopedUIEvent.OpenAppSettings -> {
                    openAppStorageSettings()
                }

                is ActivityScopedUIEvent.StartChooserActivity -> {
                    startActivity(sharedEvent.intent)
                }

            }
        }.launchIn(CoroutineScope(Dispatchers.Main))
    }

    private fun openAppStorageSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${packageName}")
        }
        startActivity(intent)
    }
}