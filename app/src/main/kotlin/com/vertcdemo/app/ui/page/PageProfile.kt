package com.vertcdemo.app.ui.page

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.vertcdemo.app.BuildConfig
import com.vertcdemo.app.BuildConfig.GITHUB_REPO
import com.vertcdemo.app.R
import com.vertcdemo.app.RouteNotices
import com.vertcdemo.app.ui.theme.Background
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.base.LocalCredential
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.login.BuildConfig.PRIVACY_POLICY_URL
import com.vertcdemo.login.BuildConfig.TERMS_OF_SERVICE_URL

typealias BaseString = com.vertcdemo.base.R.string

private const val TAG = "PageProfile"

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PageProfile(
    modifier: Modifier = Modifier,

    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    StatusBarColor(darkIcons = true)

    val credential = LocalCredential.current

    Box(
        Modifier
            .background(Background)
            .paint(
                painter = painterResource(R.drawable.bg_profile),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopStart,
            )
            .fillMaxSize(),
    ) {
        val navController = LocalNavController.current

        val userId = credential.userId.collectAsState()
        val userName = credential.userName.collectAsState()

        Image(
            modifier = modifier
                .size(44.dp)
                .align(Alignment.TopStart)
                .clickable { navController.popBackStack() },
            painter = painterResource(R.drawable.ic_profile_back),
            contentDescription = "Back",
            contentScale = ContentScale.Inside,
        )
        with(sharedTransitionScope) {
            Column(modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .align(Alignment.CenterHorizontally)
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "image-user-avatar"),
                            animatedVisibilityScope = animatedContentScope
                        )
                        .size(80.dp)
                        .clip(CircleShape),
                    painter = painterResource(Avatars[userId.value]),
                    contentDescription = null,
                )

                Text(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.CenterHorizontally),
                    text = userName.value,
                    style = TextStyle(
                        color = Color(0xFF1D2129),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )

                Column(
                    Modifier
                        .padding(start = 16.dp, top = 20.dp, end = 16.dp)
                        .verticalScroll(state = rememberScrollState())
                        .weight(1F, fill = false)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(corner = CornerSize(8.dp))
                        )
                ) {
                    val context = LocalContext.current
                    SettingItemText(
                        key = stringResource(R.string.language),
                        value = stringResource(R.string.language_value)
                    )
                    SettingItemAction(
                        key = stringResource(R.string.privacy_policy),
                        onClick = { context.openBrowser(PRIVACY_POLICY_URL) })
                    SettingItemAction(
                        key = stringResource(R.string.terms_of_service),
                        onClick = { context.openBrowser(TERMS_OF_SERVICE_URL) })
                    SettingItemAction(
                        key = stringResource(R.string.notices),
                        onClick = {
                            navController.navigate(RouteNotices)
                        })

                    val deleteAccountDialogState = remember {
                        mutableStateOf(false)
                    }

                    SettingItemAction(
                        key = stringResource(R.string.cancel_account),
                        onClick = { deleteAccountDialogState.value = true })
                    SettingItemText(
                        key = stringResource(R.string.app_version),
                        value = "v${BuildConfig.VERSION_NAME}"
                    )
                    SettingItemAction(
                        key = stringResource(R.string.github),
                        onClick = { context.openBrowser(GITHUB_REPO) })

                    if (deleteAccountDialogState.value) {
                        ProfileConfirmDialog(
                            dialogState = deleteAccountDialogState,
                            title = stringResource(R.string.cancel_account),
                            text = stringResource(R.string.cancel_account_alert_message),
                            onConfirm = {
                                credential.deleteAccount()
                                navController.popBackStack()
                            }
                        )
                    }
                }

                val logOutDialogState = remember {
                    mutableStateOf(false)
                }

                OutlinedButton(
                    onClick = {
                        logOutDialogState.value = true
                    },
                    modifier = Modifier
                        .padding(all = 16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(corner = CornerSize(4.dp))
                ) {
                    Text(
                        text = stringResource(R.string.log_out), style = TextStyle(
                            color = Color(0xFF74767B),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }

                if (logOutDialogState.value) {
                    ProfileConfirmDialog(
                        dialogState = logOutDialogState,
                        title = stringResource(R.string.log_out),
                        text = stringResource(R.string.log_out_alert_message),
                        onConfirm = {
                            credential.logout()
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingItemText(
    key: String = "Key",
    value: String = "Value",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = key, style = TextStyle(
                color = Color(0xFF020814),
                fontSize = 16.sp,
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
        Text(
            text = value,
            modifier = Modifier.align(alignment = Alignment.CenterVertically),
            style = TextStyle(
                color = Color(0xFF74767B),
                fontSize = 14.sp,
            )
        )
    }
}

@Composable
fun SettingItemAction(key: String = "Key", onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)) {
        Text(
            text = key, style = TextStyle(
                color = Color(0xFF020814),
                fontSize = 16.sp,
            )
        )
        Spacer(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
        )
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0xFF86909C)),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(12.dp)
                .align(alignment = Alignment.CenterVertically)
                .rotate(180F),
        )
    }
}

@Composable
private fun ProfileConfirmDialog(
    dialogState: MutableState<Boolean>,
    title: String,
    text: String,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { dialogState.value = false },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = {
                dialogState.value = false
                onConfirm()
            }) {
                Text(
                    text = stringResource(BaseString.confirm),
                    style = TextStyle(color = Color(0xFFD7312A))
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                dialogState.value = false
            }) {
                Text(text = stringResource(BaseString.cancel))
            }
        })
}

private fun Context.openBrowser(url: String) {
    try {
        startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    } catch (e: ActivityNotFoundException) {
        Log.d(TAG, "openBrowser: failed: $url")
    }
}