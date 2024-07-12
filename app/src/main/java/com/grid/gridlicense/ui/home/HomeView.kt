package com.grid.gridlicense.ui.home

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.grid.gridlicense.ActivityScopedViewModel
import com.grid.gridlicense.ActivityState
import com.grid.gridlicense.R
import com.grid.gridlicense.model.Event
import com.grid.gridlicense.model.SettingsModel
import com.grid.gridlicense.ui.theme.GridLicenseTheme
import com.grid.gridlicense.utils.Utils
import com.grid.pos.ui.common.UIAlertDialog
import com.grid.pos.ui.common.UIButton

@OptIn(
    ExperimentalMaterial3Api::class
)
@Composable
fun HomeView(
        modifier: Modifier = Modifier,
        navController: NavController? = null,
        activityViewModel: ActivityScopedViewModel,
) {
    val activityState: ActivityState by activityViewModel.activityState.collectAsState(
        ActivityState()
    )
    var isLogoutPopupShown by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(
        true,
        activityState.isLoggedIn,
        activityState.warning
    ) {
        keyboardController?.hide()
        activityState.warning?.value?.let {
            if (it.isNotEmpty()) {
                isLogoutPopupShown = true
            }
        }
    }
    BackHandler {
        if (!isLogoutPopupShown) {
            activityState.warning = Event("Are you sure you want to logout?")
            activityState.forceLogout = false
            isLogoutPopupShown = true
        }
    }
    GridLicenseTheme {
        Scaffold(containerColor = SettingsModel.backgroundColor,
            topBar = {
                Surface(
                    shadowElevation = 3.dp,
                    color = SettingsModel.backgroundColor
                ) {
                    TopAppBar(colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = SettingsModel.topBarColor),
                        title = {
                            Text(
                                text = "Home",
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
            LazyVerticalGrid(
                modifier = modifier
                    .padding(it)
                    .padding(vertical = 30.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                columns = GridCells.Fixed(2)
            ) {
                Utils.homeViewList.forEachIndexed { index, item ->
                    item {
                        UIButton(
                            modifier = Modifier
                                .width(120.dp)
                                .height(80.dp)
                                .padding(
                                    horizontal = 3.dp,
                                    vertical = 5.dp
                                ),
                            text = item.title,
                            shape = RoundedCornerShape(15.dp)
                        ) {
                            navController?.navigate(item.composable)
                        }
                    }
                }
            }
        }
        fun logout() {
            isLogoutPopupShown = false
            activityViewModel.logout()
            navController?.clearBackStack("LoginView")
            navController?.navigate("LoginView")
        }
        AnimatedVisibility(
            visible = isLogoutPopupShown,
            enter = fadeIn(
                initialAlpha = 0.4f
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 250)
            )
        ) {
            UIAlertDialog(
                onDismissRequest = {
                    isLogoutPopupShown = false
                    activityState.warning = null
                    if (activityState.forceLogout) logout()
                },
                onConfirmation = {
                    isLogoutPopupShown = false
                    activityState.warning = null
                    logout()
                },
                dialogTitle = "Alert.",
                dialogText = activityState.warning?.value ?: "An error has occurred!",
                icon = Icons.Default.Info
            )
        }
    }
}