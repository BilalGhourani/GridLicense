package com.grid.gridlicense.ui.license

import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
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
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.SwipeToDeleteContainer
import com.grid.gridlicense.ui.common.UIButton
import com.grid.gridlicense.ui.common.UITextField
import com.grid.gridlicense.ui.license.components.LicenseListCell
import com.grid.gridlicense.ui.theme.GridLicenseTheme
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

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var searchState by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        .padding(top = 10.dp),
                    text = "Create new license"
                ) {
                    navController?.navigate("LicenseView")
                }

                UITextField(modifier = Modifier.padding(
                    vertical = 10.dp,
                    horizontal = 5.dp,
                ),
                    defaultValue = searchState,
                    label = "Search",
                    height = 60.dp,
                    placeHolder = "Search",
                    imeAction = ImeAction.Done,
                    onAction = { keyboardController?.hide() }) {
                    searchState = it
                    viewModel.search(it.lowercase().trim())
                }

                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    state.licenseModels.forEach { licenseModel ->
                        item {
                            SwipeToDeleteContainer(item = licenseModel,
                                onDelete = { licModel ->
                                    viewModel.deleteLicense(licModel)
                                }) { licModel ->
                                LicenseListCell(modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(5.dp),
                                    licenseModel = licModel,
                                    onEdit = {
                                        viewModel.generate(
                                            context,
                                            licModel.license.deviseid!!,
                                            licModel.license.expirydate!!,
                                            false,
                                            "0"
                                        )
                                    })
                            }
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