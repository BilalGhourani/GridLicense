package com.grid.gridlicense.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.grid.gridlicense.ActivityScopedViewModel
import com.grid.gridlicense.data.DataStoreManager
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.common.ColorPickerPopup
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.ui.theme.LightGrey
import com.grid.gridlicense.utils.Extension.toHexCode
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.UIButton
import com.grid.gridlicense.ui.common.UITextField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
        modifier: Modifier = Modifier,
        navController: NavController? = null,
        activityScopedViewModel: ActivityScopedViewModel
) {

    var sqlServerPath by remember { mutableStateOf(SettingsModel.sqlServerPath ?: "") }
    var sqlServerName by remember { mutableStateOf(SettingsModel.sqlServerName ?: "") }
    var sqlServerDbName by remember { mutableStateOf(SettingsModel.sqlServerDbName ?: "") }
    var sqlServerDbUser by remember { mutableStateOf(SettingsModel.sqlServerDbUser ?: "") }
    var sqlServerDbPassword by remember { mutableStateOf(SettingsModel.sqlServerDbPassword ?: "") }
    var passwordVisibility by remember { mutableStateOf(false) }

    val sqlServerNameRequester = remember { FocusRequester() }
    val sqlServerDbNameRequester = remember { FocusRequester() }
    val sqlServerUserRequester = remember { FocusRequester() }
    val sqlServerPasswordRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var buttonColorState by remember { mutableStateOf(SettingsModel.buttonColor) }
    var buttonTextColorState by remember { mutableStateOf(SettingsModel.buttonTextColor) }
    var topBarColorState by remember { mutableStateOf(SettingsModel.topBarColor) }
    var backgroundColorState by remember { mutableStateOf(SettingsModel.backgroundColor) }
    var textColorState by remember { mutableStateOf(SettingsModel.textColor) }
    var colorPickerType by remember { mutableStateOf(ColorPickerType.BUTTON_COLOR) }
    var isColorPickerShown by remember { mutableStateOf(false) }

    var isFirebaseSectionExpanded by remember { mutableStateOf(false) }
    var isColorsSectionExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    val isLoggedId = activityScopedViewModel.isLoggedIn()

    fun handleBack() {
        navController?.navigateUp()
    }
    BackHandler {
        handleBack()
    }


    GridLicenseTheme {
        Scaffold(containerColor = SettingsModel.backgroundColor,
            topBar = {
                Surface(
                    shadowElevation = 3.dp,
                    color = backgroundColorState
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
                                text = "Settings",
                                color = textColorState,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        })
                }
            }) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
            ) {
                if (!isLoggedId) {
                    Card(
                        modifier = Modifier
                            .padding(10.dp)
                            .animateContentSize(),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LightGrey,
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .clickable {
                                    isFirebaseSectionExpanded = !isFirebaseSectionExpanded
                                },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Connectivity Settings",
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center,
                                style = TextStyle(
                                    textDecoration = TextDecoration.None,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp
                                ),
                                color = SettingsModel.textColor
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                null,
                                Modifier
                                    .padding(16.dp)
                                    .size(20.dp)
                                    .align(
                                        Alignment.CenterVertically
                                    )
                                    .rotate(
                                        if (isFirebaseSectionExpanded) 180f else 0f
                                    ),
                                tint = Color.Black
                            )
                        }

                        if (isFirebaseSectionExpanded) {
                            UITextField(modifier = Modifier.padding(10.dp),
                                defaultValue = sqlServerPath,
                                label = "SQL Server Path",
                                placeHolder = "host:port",
                                imeAction = ImeAction.Next,
                                onAction = { sqlServerNameRequester.requestFocus() }) { path ->
                                sqlServerPath = path
                            }

                            UITextField(modifier = Modifier.padding(10.dp),
                                defaultValue = sqlServerName,
                                label = "Sql Server Name",
                                placeHolder = "Sql Server Name",
                                focusRequester = sqlServerNameRequester,
                                imeAction = ImeAction.Next,
                                onAction = { sqlServerDbNameRequester.requestFocus() }) { name ->
                                sqlServerName = name
                            }
                            UITextField(modifier = Modifier.padding(10.dp),
                                defaultValue = sqlServerDbName,
                                label = "Database Name",
                                placeHolder = "Database Name",
                                focusRequester = sqlServerDbNameRequester,
                                imeAction = ImeAction.Next,
                                onAction = { sqlServerUserRequester.requestFocus() }) { dbName ->
                                sqlServerDbName = dbName
                            }

                            UITextField(modifier = Modifier.padding(10.dp),
                                defaultValue = sqlServerDbUser,
                                label = "Database User",
                                placeHolder = "user",
                                focusRequester = sqlServerUserRequester,
                                imeAction = ImeAction.Next,
                                onAction = { sqlServerPasswordRequester.requestFocus() }) { user ->
                                sqlServerDbUser = user
                            }

                            UITextField(modifier = Modifier.padding(10.dp),
                                defaultValue = sqlServerDbPassword,
                                label = "Database Password",
                                placeHolder = "password",
                                focusRequester = sqlServerUserRequester,
                                keyboardType = KeyboardType.Password,
                                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                                imeAction = ImeAction.Done,
                                onAction = { keyboardController?.hide() },
                                trailingIcon = {
                                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                                        Icon(
                                            imageVector = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                            contentDescription = if (passwordVisibility) "Hide password" else "Show password",
                                            tint = SettingsModel.buttonColor
                                        )
                                    }
                                }) { password ->
                                sqlServerDbPassword = password
                            }

                            UIButton(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .height(60.dp)
                                    .padding(
                                        10.dp
                                    )
                                    .align(
                                        Alignment.CenterHorizontally
                                    ),
                                text = "Save"
                            ) {
                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    SettingsModel.sqlServerPath = sqlServerPath
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.SQL_SERVER_PATH.key,
                                        sqlServerPath
                                    )
                                    SettingsModel.sqlServerName = sqlServerName
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.SQL_SERVER_NAME.key,
                                        sqlServerName
                                    )
                                    SettingsModel.sqlServerDbName = sqlServerDbName
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.SQL_SERVER_DB_NAME.key,
                                        sqlServerDbName
                                    )
                                    SettingsModel.sqlServerDbUser = sqlServerDbUser
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.SQL_SERVER_DB_USER.key,
                                        sqlServerDbUser
                                    )
                                    SettingsModel.sqlServerDbPassword = sqlServerDbPassword
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.SQL_SERVER_DB_PASSWORD.key,
                                        sqlServerDbPassword
                                    )
                                    delay(1000L)
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(10.dp)
                        .animateContentSize(),
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightGrey,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { isColorsSectionExpanded = !isColorsSectionExpanded },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Colors",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            style = TextStyle(
                                textDecoration = TextDecoration.None,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            ),
                            color = SettingsModel.textColor
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            Icons.Default.KeyboardArrowDown,
                            null,
                            Modifier
                                .padding(16.dp)
                                .size(20.dp)
                                .align(
                                    Alignment.CenterVertically
                                )
                                .rotate(
                                    if (isColorsSectionExpanded) 180f else 0f
                                ),
                            tint = Color.Black
                        )
                    }
                    if (isColorsSectionExpanded) {
                        UIButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(10.dp),
                            text = "Button Color",
                            buttonColor = buttonColorState,
                            textColor = buttonTextColorState
                        ) {
                            colorPickerType = ColorPickerType.BUTTON_COLOR
                            isColorPickerShown = true
                        }

                        UIButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(10.dp),
                            text = "Button Text Color",
                            buttonColor = buttonColorState,
                            textColor = buttonTextColorState
                        ) {
                            colorPickerType = ColorPickerType.BUTTON_TEXT_COLOR
                            isColorPickerShown = true
                        }

                        UIButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(10.dp),
                            text = "Top Bar Color",
                            buttonColor = buttonColorState,
                            textColor = buttonTextColorState
                        ) {
                            colorPickerType = ColorPickerType.TOP_BAR_COLOR
                            isColorPickerShown = true
                        }

                        UIButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(10.dp),
                            text = "Background Color",
                            buttonColor = buttonColorState,
                            textColor = buttonTextColorState
                        ) {
                            colorPickerType = ColorPickerType.BACKGROUND_COLOR
                            isColorPickerShown = true
                        }

                        UIButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .padding(10.dp),
                            text = "Text Color",
                            buttonColor = buttonColorState,
                            textColor = buttonTextColorState
                        ) {
                            colorPickerType = ColorPickerType.TEXT_COLOR
                            isColorPickerShown = true
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                if (isLoggedId) {
                    UIButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .padding(10.dp),
                        text = "Logout",
                        buttonColor = buttonColorState,
                        textColor = buttonTextColorState
                    ) {
                        activityScopedViewModel.logout()
                        navController?.clearBackStack("LoginView")
                        navController?.navigate("LoginView")
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = isColorPickerShown,
            enter = fadeIn(
                initialAlpha = 0.4f
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 250)
            )
        ) {
            Dialog(
                onDismissRequest = { isColorPickerShown = false },
            ) {
                ColorPickerPopup(defaultColor = when (colorPickerType) {
                    ColorPickerType.BUTTON_COLOR -> buttonColorState
                    ColorPickerType.BUTTON_TEXT_COLOR -> buttonTextColorState
                    ColorPickerType.BACKGROUND_COLOR -> backgroundColorState
                    ColorPickerType.TOP_BAR_COLOR -> topBarColorState
                    ColorPickerType.TEXT_COLOR -> textColorState
                },
                    onDismiss = { isColorPickerShown = false },
                    onSubmit = {
                        when (colorPickerType) {
                            ColorPickerType.BUTTON_COLOR -> {
                                buttonColorState = it
                                SettingsModel.buttonColor = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.BUTTON_COLOR.key,
                                        it.toHexCode()
                                    )
                                }
                            }

                            ColorPickerType.BUTTON_TEXT_COLOR -> {
                                buttonTextColorState = it
                                SettingsModel.buttonTextColor = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.BUTTON_TEXT_COLOR.key,
                                        it.toHexCode()
                                    )
                                }
                            }

                            ColorPickerType.BACKGROUND_COLOR -> {
                                backgroundColorState = it
                                SettingsModel.backgroundColor = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.BACKGROUND_COLOR.key,
                                        it.toHexCode()
                                    )
                                }
                            }

                            ColorPickerType.TOP_BAR_COLOR -> {
                                topBarColorState = it
                                SettingsModel.topBarColor = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.TOP_BAR_COLOR.key,
                                        it.toHexCode()
                                    )
                                }
                            }

                            ColorPickerType.TEXT_COLOR -> {
                                textColorState = it
                                SettingsModel.textColor = it
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.putString(
                                        DataStoreManager.DataStoreKeys.TEXT_COLOR.key,
                                        it.toHexCode()
                                    )
                                }
                            }
                        }
                        isColorPickerShown = false
                    })

            }
        }
        LoadingIndicator(
            show = isLoading
        )
    }
}

enum class ColorPickerType(val key: String) {
    BUTTON_COLOR("BUTTON_COLOR"), BUTTON_TEXT_COLOR("BUTTON_TEXT_COLOR"), TOP_BAR_COLOR(
        "TOP_BAR_COLOR"
    ),
    BACKGROUND_COLOR("BACKGROUND_COLOR"), TEXT_COLOR("TEXT_COLOR")
}