package com.grid.gridlicense.ui.license

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grid.gridlicense.ActivityScopedViewModel
import com.grid.gridlicense.BuildConfig
import com.grid.gridlicense.R
import com.grid.gridlicense.model.LicenseModel
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.license.components.LicenseListCell
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.UIButton
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun LicensesListView(
        modifier: Modifier = Modifier,
        navController: NavController? = null,
        activityViewModel: ActivityScopedViewModel,
        viewModel: LicensesListViewModel = hiltViewModel()
) {
    val state: LicensesListState by viewModel.state.collectAsState(
        LicensesListState()
    )

    val context = LocalContext.current

    fun shareLicense(action: String) {
        viewModel.licenseFile?.let { file ->
            val shareIntent = Intent()
            shareIntent.setAction(action)
            val attachment = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID,
                file
            )
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                attachment
            )
            shareIntent.setType("application/octet-stream")

            activityViewModel.startChooserActivity(
                Intent.createChooser(
                    shareIntent,
                    "send license file"
                )
            )
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var orientation by remember { mutableIntStateOf(Configuration.ORIENTATION_PORTRAIT) }
    val configuration = LocalConfiguration.current
    val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(configuration) {
        snapshotFlow { configuration.orientation }.collect {
            orientation = it
        }
    }


    LaunchedEffect(
        state.warning,
        state.isDone
    ) {
        state.warning?.value?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }

    }

    fun handleBack() {
        navController?.navigateUp()
    }
    BackHandler {
        handleBack()
    }

    GridLicenseTheme {
        Scaffold(containerColor = SettingsModel.backgroundColor,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                Surface(
                    shadowElevation = 3.dp,
                    color = SettingsModel.backgroundColor
                ) {
                    TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = SettingsModel.topBarColor),
                        navigationIcon = {
                            IconButton(onClick = { handleBack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = SettingsModel.buttonColor
                                )
                            }
                        },
                        title = {
                            Text(
                                text = "Licenses List",
                                color = SettingsModel.textColor,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        },
                        actions = {
                            IconButton(onClick = { navController?.navigate("SettingsView") }) {
                                Icon(
                                    painterResource(R.drawable.ic_settings),
                                    contentDescription = "Back",
                                    tint = SettingsModel.buttonColor
                                )
                            }
                        })
                }
            }) {
            Column(
                modifier = modifier.padding(it)
            ) {
                UIButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(10.dp),
                    text = "Create new license"
                ) {
                    navController?.navigate("LicenseView")
                }

                LazyColumn(
                    modifier = if (isLandscape) {
                        modifier
                            .fillMaxWidth()
                            .weight(1f)
                    } else {
                        modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .weight(1f)
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    stickyHeader {
                        LicenseListCell(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(color = Color.LightGray),
                            licenseModel = LicenseModel(),
                            isHeader = true,
                            isLandscape = isLandscape,
                            index = 0
                        )
                    }
                    state.licenseModels.forEachIndexed { index, licenseModel ->
                        item {
                            val color = if (index % 2 == 0) Color.White else Color.LightGray
                            LicenseListCell(modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(color = color),
                                licenseModel = licenseModel,
                                isLandscape = isLandscape,
                                index = index,
                                onEdit = {
                                    viewModel.generate(
                                        context,
                                        licenseModel.license.deviseid!!,
                                        licenseModel.license.expirydate!!,
                                        false,
                                        "0"
                                    )
                                },
                                onRemove = { viewModel.deleteLicense(licenseModel) })
                        }
                    }
                }
            }
        }

        LoadingIndicator(
            show = state.isLoading
        )

        if (state.clear) {
            state.clear = false
        }
    }
}