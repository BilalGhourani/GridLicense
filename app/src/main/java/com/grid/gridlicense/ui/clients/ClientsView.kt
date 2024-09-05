package com.grid.gridlicense.ui.clients

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RemoveCircleOutline
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.grid.gridlicense.R
import com.grid.gridlicense.data.SQLServerWrapper
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.SearchableDropdownMenuEx
import com.grid.gridlicense.ui.common.UIButton
import com.grid.gridlicense.ui.common.UITextField
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun ClientsView(
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    viewModel: ClientsViewModel = hiltViewModel()
) {
    val state by viewModel.clientsState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val countryFocusRequester = remember { FocusRequester() }

    var nameState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var phoneState by remember { mutableStateOf("") }
    var countryState by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.warning) {
        state.warning?.value?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    fun clear() {
        state.selectedClient = Client()
        nameState = ""
        phoneState = ""
        emailState = ""
        countryState = ""
    }
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    fun handleBack() {
        if(isImeVisible){
            keyboardController?.hide()
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            SQLServerWrapper.closeConnection()
        }
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
                    TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = SettingsModel.topBarColor
                    ),
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
                                text = "Clients",
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
            }) { it ->
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(color = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset(y = 90.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UITextField(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        defaultValue = nameState,
                        label = "Name",
                        placeHolder = "Enter Name",
                        onAction = { emailFocusRequester.requestFocus() }) {
                        nameState = it
                        state.selectedClient.clientName = it.trim()
                    }

                    UITextField(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        defaultValue = emailState,
                        label = "Email Address",
                        placeHolder = "Enter Email Address",
                        focusRequester = emailFocusRequester,
                        onAction = { phoneFocusRequester.requestFocus() }) {
                        emailState = it
                        state.selectedClient.clientEmail = it.trim()
                    }

                    UITextField(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        defaultValue = phoneState,
                        label = "Phone Number",
                        placeHolder = "Enter Phone Number",
                        keyboardType = KeyboardType.Number,
                        focusRequester = phoneFocusRequester,
                        onAction = { countryFocusRequester.requestFocus() }) {
                        phoneState = it
                        state.selectedClient.clientPhone = it.trim()
                    }

                    UITextField(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        defaultValue = countryState,
                        label = "Country",
                        placeHolder = "Enter Country",
                        focusRequester = countryFocusRequester,
                        imeAction = ImeAction.Done,
                        onAction = { keyboardController?.hide() }) {
                        countryState = it
                        state.selectedClient.clientCountry = it.trim()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        UIButton(
                            modifier = Modifier
                                .weight(.33f)
                                .padding(3.dp),
                            text = "Save"
                        ) {
                            viewModel.saveClient(state.selectedClient)
                        }

                        UIButton(
                            modifier = Modifier
                                .weight(.33f)
                                .padding(3.dp),
                            text = "Delete"
                        ) {
                            viewModel.deleteSelectedClient(state.selectedClient)
                        }

                        UIButton(
                            modifier = Modifier
                                .weight(.33f)
                                .padding(3.dp),
                            text = "Close"
                        ) {
                            handleBack()
                        }
                    }

                }

                SearchableDropdownMenuEx(items = state.clients.toMutableList(),
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .offset(y = 15.dp),
                    label = "Select Client",
                    selectedId = state.selectedClient.clientid,
                    onLoadItems = { viewModel.fetchClients() },
                    leadingIcon = { modifier ->
                        if (state.selectedClient.clientid.isNotEmpty()) {
                            Icon(
                                Icons.Default.RemoveCircleOutline,
                                contentDescription = "reset selection",
                                tint = Color.Black,
                                modifier = modifier
                            )
                        }
                    },
                    onLeadingIconClick = {
                        clear()
                    }) { selectedClient ->
                    selectedClient as Client
                    state.selectedClient = selectedClient
                    nameState = selectedClient.clientName ?: ""
                    emailState = selectedClient.clientEmail ?: ""
                    phoneState = selectedClient.clientPhone ?: ""
                    countryState = selectedClient.clientCountry ?: ""
                }

            }
        }
        LoadingIndicator(
            show = state.isLoading
        )

        if (state.clear) {
            clear()
            state.clear = false
        }
    }
}