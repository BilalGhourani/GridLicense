package com.grid.gridlicense.ui.license.manage

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.grid.gridlicense.ActivityScopedViewModel
import com.grid.gridlicense.BuildConfig
import com.grid.gridlicense.data.client.Client
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.common.UISwitch
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.ui.theme.LightBlue
import com.grid.gridlicense.utils.DateHelper
import com.grid.gridlicense.utils.Utils
import com.grid.gridlicense.ui.common.LoadingIndicator
import com.grid.gridlicense.ui.common.SearchableDropdownMenu
import com.grid.gridlicense.ui.common.UIAlertDialog
import com.grid.gridlicense.ui.common.UIButton
import com.grid.gridlicense.ui.common.UITextField
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseView(
        modifier: Modifier = Modifier,
        navController: NavController? = null,
        activityViewModel: ActivityScopedViewModel,
        viewModel: LicenseViewModel = hiltViewModel()
) {
    val licenseState: LicenseState by viewModel.state.collectAsState(
        LicenseState()
    )

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val deviceIdFocusRequester = remember { FocusRequester() }
    val moduleFocusRequester = remember { FocusRequester() }

    fun getDateFromState(time: Long): Date {
        return Calendar.getInstance().apply {
            timeInMillis = time
        }.time
    }

    fun shareExcelSheet(action: String) {
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

    val initialDate = Date()

    var clientIdState by remember { mutableStateOf("") }
    var companyState by remember { mutableStateOf("") }
    var deviceIdState by remember { mutableStateOf("") }
    var moduleState by remember { mutableStateOf("") }
    val expiryDatePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate.time)
    var expiryDateState by remember {
        mutableStateOf(
            DateHelper.getDateInFormat(
                getDateFromState(expiryDatePickerState.selectedDateMillis!!),
                "yyyy-MM-dd"
            )
        )
    }
    var expiryDateMessageState by remember { mutableStateOf(false) }
    var isRtaState by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var rtaDaysState by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(
        licenseState.warning,
        licenseState.isDone
    ) {
        licenseState.warning?.value?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short,
                )
            }
        }
    }

    fun handleBack() {
        if (licenseState.isLoading) {
            isPopupVisible = true
        } else {
            navController?.navigateUp()
        }
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
                                text = "Manage License",
                                color = SettingsModel.textColor,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        })
                }
            }) {
            Column(
                modifier = modifier.padding(it).verticalScroll(rememberScrollState())
            ) {

                SearchableDropdownMenu(
                    items = licenseState.clients.toMutableList(),
                    modifier = Modifier.padding(10.dp),
                    label = "Select Client",
                    selectedId = clientIdState
                ) { selectedClient ->
                    selectedClient as Client
                    clientIdState = selectedClient.clientid
                    licenseState.selectedLicense.cltid = selectedClient.clientid
                }

                UITextField(modifier = Modifier.padding(10.dp),
                    defaultValue = companyState,
                    label = "Company",
                    keyboardType = KeyboardType.Text,
                    placeHolder = "Company",
                    onAction = {
                        deviceIdFocusRequester.requestFocus()
                    }) { comp ->
                    companyState = comp.trim()
                    licenseState.selectedLicense.company = companyState
                }

                UITextField(modifier = Modifier.padding(10.dp),
                    defaultValue = deviceIdState,
                    label = "Device ID",
                    maxLines = 2,
                    keyboardType = KeyboardType.Text,
                    placeHolder = "device id",
                    focusRequester = deviceIdFocusRequester,
                    onAction = {
                        moduleFocusRequester.requestFocus()
                    }) { devId ->
                    deviceIdState = devId.trim()
                    licenseState.selectedLicense.deviseid = deviceIdState
                }

                UITextField(modifier = Modifier.padding(10.dp),
                    defaultValue = moduleState,
                    label = "Module",
                    keyboardType = KeyboardType.Text,
                    placeHolder = "Module",
                    focusRequester = moduleFocusRequester,
                    onAction = {
                        keyboardController?.hide()
                    }) { module ->
                    moduleState = module.trim()
                    licenseState.selectedLicense.module = moduleState
                }

                UISwitch(
                    modifier = Modifier.padding(10.dp),
                    checked = expiryDateMessageState,
                    text = "Expiry date message",
                ) { exdm ->
                    expiryDateMessageState = exdm
                    licenseState.selectedLicense.expirydatemessage = exdm
                }

                UITextField(modifier = Modifier.padding(10.dp),
                    defaultValue = expiryDateState,
                    label = "Expiry Date",
                    maxLines = 1,
                    readOnly = true,
                    keyboardType = KeyboardType.Text,
                    placeHolder = DateHelper.getDateInFormat(
                        initialDate,
                        "yyyy-MM-dd"
                    ),
                    trailingIcon = {
                        IconButton(onClick = {
                            showDatePicker = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Expiry Date",
                                tint = SettingsModel.buttonColor
                            )
                        }
                    }) { date ->
                    expiryDateState = date
                    val expDate = DateHelper.getDateFromString(
                        date,
                        "yyyy-MM-dd"
                    )
                    val expiryDate = DateHelper.editDate(
                        expDate,
                        0,
                        0,
                        0
                    )
                    licenseState.selectedLicense.expirydate = expiryDate
                }

                UISwitch(
                    modifier = Modifier.padding(10.dp),
                    checked = isRtaState,
                    text = "is RTA",
                ) { isRta ->
                    isRtaState = isRta
                    licenseState.selectedLicense.isRta = isRta
                }

                if (isRtaState) {
                    UITextField(modifier = Modifier.padding(10.dp),
                        defaultValue = rtaDaysState,
                        label = "Number Of Days",
                        keyboardType = KeyboardType.Number,
                        placeHolder = "Number Of Days",
                        onAction = {
                            keyboardController?.hide()
                        }) { days ->
                        rtaDaysState = Utils.getIntValue(
                            days,
                            rtaDaysState
                        )
                        licenseState.selectedLicense.rtaDays = rtaDaysState
                    }
                }

                UIButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(10.dp),
                    text = if (licenseState.isDone) "Share" else "Save"
                ) {
                    if (licenseState.isDone) {
                        shareExcelSheet(Intent.ACTION_SEND)
                    } else {
                        val date = getDateFromState(expiryDatePickerState.selectedDateMillis!!)
                        val expiryDate = DateHelper.editDate(
                            date,
                            0,
                            0,
                            0
                        )
                        licenseState.selectedLicense.expirydate = expiryDate
                        viewModel.generate(
                            context,
                            licenseState.selectedLicense
                        )
                    }
                }
            }
        }

        // date picker component
        if (showDatePicker) {
            DatePickerDialog(colors = DatePickerDefaults.colors(
                containerColor = SettingsModel.backgroundColor
            ),
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val expiryDate = getDateFromState(expiryDatePickerState.selectedDateMillis!!)
                            expiryDateState = DateHelper.getDateInFormat(
                                expiryDate,
                                "yyyy-MM-dd"
                            )
                            showDatePicker = false
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            "Submit",
                            color = SettingsModel.textColor,
                            style = TextStyle(
                                textDecoration = TextDecoration.None,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(
                            "Cancel",
                            color = SettingsModel.textColor,
                            style = TextStyle(
                                textDecoration = TextDecoration.None,
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        )
                    }
                }) {
                DatePicker(
                    state = expiryDatePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = SettingsModel.backgroundColor,
                        dayContentColor = SettingsModel.textColor,
                        currentYearContentColor = SettingsModel.textColor,
                        //navigationContentColor = SettingsModel.textColor,
                        yearContentColor = SettingsModel.textColor,
                        weekdayContentColor = SettingsModel.textColor,
                        titleContentColor = SettingsModel.textColor,
                        headlineContentColor = SettingsModel.textColor,
                        subheadContentColor = SettingsModel.textColor,
                        // dayInSelectionRangeContentColor = SettingsModel.textColor,
                        selectedDayContainerColor = LightBlue,
                        selectedDayContentColor = Color.White,
                        selectedYearContainerColor = LightBlue,
                        selectedYearContentColor = Color.White,
                        todayContentColor = SettingsModel.textColor,
                        todayDateBorderColor = LightBlue
                    )
                )
            }
        }

        AnimatedVisibility(
            visible = isPopupVisible,
            enter = fadeIn(
                initialAlpha = 0.4f
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 250)
            )
        ) {
            UIAlertDialog(
                onDismissRequest = {
                    isPopupVisible = false
                },
                onConfirmation = {
                    licenseState.isLoading = false
                    isPopupVisible = false
                    handleBack()
                },
                dialogTitle = "Alert.",
                dialogText = "Are you sure you want to cancel the license file ?",
                positiveBtnText = "Cancel",
                negativeBtnText = "Close",
                icon = Icons.Default.Info,
                height = 230.dp
            )
        }
        LoadingIndicator(
            show = licenseState.isLoading
        )

        if (licenseState.clear) {
            licenseState.clear = false
        }
    }
}