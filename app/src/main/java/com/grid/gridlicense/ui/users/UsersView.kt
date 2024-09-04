package com.grid.gridlicense.ui.users

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grid.gridlicense.R
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.common.SearchableDropdownMenu
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.data.user.User
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.UIButton
import com.grid.gridlicense.ui.common.UITextField
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun UsersView(
        navController: NavController? = null,
        modifier: Modifier = Modifier,
        viewModel: UsersViewModel = hiltViewModel()
) {
    val usersState: UsersState by viewModel.usersState.collectAsState(
        UsersState()
    )
    val keyboardController = LocalSoftwareKeyboardController.current
    val passwordFocusRequester = remember { FocusRequester() }
    val emailFocusRequester = remember { FocusRequester() }
    val deviceIdFocusRequester = remember { FocusRequester() }

    var nameState by remember { mutableStateOf("") }
    var usernameState by remember { mutableStateOf("") }
    var passwordState by remember { mutableStateOf("") }
    var emailState by remember { mutableStateOf("") }
    var deviceIdState by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(usersState.warning) {
        usersState.warning?.value?.let { message ->
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
                                text = "Users",
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
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SearchableDropdownMenu(
                            items = usersState.users.toMutableList(),
                            modifier = Modifier.padding(10.dp),
                            label =  "Select User" ,
                            selectedId = usersState.selectedUser.userId,
                            onSearch = {viewModel.searchInUsers(it)}
                        ) { selectedUser ->
                            selectedUser as User
                            usersState.selectedUser = selectedUser
                            nameState = selectedUser.userName ?: ""
                            usernameState = selectedUser.userName ?: ""
                            passwordState = ""//selectedUser.password ?: ""
                            emailState = selectedUser.email ?: ""
                            deviceIdState = selectedUser.deviceID ?: ""
                        }

                        UITextField(modifier = Modifier.padding(10.dp),
                            defaultValue = usernameState,
                            label = "Username",
                            placeHolder = "Enter Username",
                            onAction = { passwordFocusRequester.requestFocus() }) {
                            usernameState = it
                            usersState.selectedUser.userName = it.trim()
                        }

                        UITextField(modifier = Modifier.padding(10.dp),
                            defaultValue = passwordState,
                            label = "Password",
                            placeHolder = "Enter Password",
                            focusRequester = passwordFocusRequester,
                            keyboardType = KeyboardType.Password,
                            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            onAction = { emailFocusRequester.requestFocus() },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                    Icon(
                                        imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                                        tint = SettingsModel.buttonColor
                                    )
                                }
                            }) {
                            passwordState = it
                            usersState.selectedUser.password = it.trim()
                        }

                        UITextField(modifier = Modifier.padding(10.dp),
                            defaultValue = emailState,
                            label = "Email",
                            placeHolder = "Enter Email",
                            focusRequester = emailFocusRequester,
                            onAction = { deviceIdFocusRequester.requestFocus() }) {
                            emailState = it
                            usersState.selectedUser.email = it.trim()
                        }

                        UITextField(modifier = Modifier.padding(10.dp),
                            defaultValue = deviceIdState,
                            label = "Device ID",
                            placeHolder = "Enter Device ID",
                            focusRequester = deviceIdFocusRequester,
                            imeAction = ImeAction.Done,
                            onAction = { keyboardController?.hide() }) {
                            deviceIdState = it
                            usersState.selectedUser.deviceID = it.trim()
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            UIButton(
                                modifier = Modifier
                                    .weight(.33f)
                                    .padding(3.dp),
                                text = "Save"
                            ) {
                                viewModel.saveUser(usersState.selectedUser)
                            }

                            UIButton(
                                modifier = Modifier
                                    .weight(.33f)
                                    .padding(3.dp),
                                text = "Delete"
                            ) {
                                viewModel.deleteSelectedUser(usersState.selectedUser)
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
                }
            }
        }
        LoadingIndicator(
            show = usersState.isLoading
        )

        if (usersState.clear) {
            usersState.selectedUser = User()
            nameState = ""
            usernameState = ""
            passwordState = ""
            emailState = ""
            deviceIdState = ""
            usersState.clear = false
        }
    }
}