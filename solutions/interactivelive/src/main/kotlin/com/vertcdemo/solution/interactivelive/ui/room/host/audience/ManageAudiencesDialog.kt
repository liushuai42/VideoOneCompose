package com.vertcdemo.solution.interactivelive.ui.room.host.audience

import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.annotation.MediaStatus
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import com.vertcdemo.solution.interactivelive.ui.CreativeButton
import com.vertcdemo.solution.interactivelive.ui.room.host.HostViewModel
import com.vertcdemo.solution.interactivelive.ui.room.host.HostViewModel.Companion.MAX_LINK_COUNT
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageAudiencesDialog(
    viewModel: HostViewModel,
) {
    ModalBottomSheet(
        onDismissRequest = { viewModel.manageAudienceDialog.dismiss() },
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = {},
        modifier = Modifier,
    ) {
        DialogContent(viewModel)
    }
}

@Composable
private fun DialogContent(viewModel: HostViewModel) {
    val tabIndex by viewModel.manageAudiencesTabIndex.collectAsState()
    val newApplicationApplyDot by viewModel.newApplicationApplyDot.collectAsState()

    LaunchedEffect("init-tab-index") {
        if (newApplicationApplyDot) {
            viewModel.setManageAudiencesTab(TAB_AUDIENCE_LINK_APPLICATIONS)
        }
    }
    DisposableEffect("reset-tab-index") {
        onDispose {
            viewModel.setManageAudiencesTab(TAB_AUDIENCE_LINK_LINKED)
        }
    }

    Column(
        modifier = Modifier
            .height(375.dp)
            .fillMaxWidth()
            .background(
                color = Color(0xFF161823),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_pk_panel_p1),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
    ) {
        ScrollableTabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = tabIndex,
            containerColor = Color.Transparent,
            divider = { HorizontalDivider(color = Color(0x26FFFFFF)) },
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                    color = Color.White
                )
            }
        ) {
            val selectedStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )

            val normalStyle = LocalTextStyle.current.copy(
                color = Color(0xFF80838A),
                fontSize = 16.sp,
            )

            Tab(
                selected = tabIndex == TAB_AUDIENCE_LINK_LINKED,
                onClick = { viewModel.setManageAudiencesTab(TAB_AUDIENCE_LINK_LINKED) },
                text = {
                    Text(
                        text = stringResource(id = R.string.co_host),
                        style = if (tabIndex == TAB_AUDIENCE_LINK_LINKED) {
                            selectedStyle
                        } else {
                            normalStyle
                        }
                    )
                }
            )

            Tab(
                selected = tabIndex == TAB_AUDIENCE_LINK_APPLICATIONS,
                onClick = {
                    viewModel.setManageAudiencesTab(TAB_AUDIENCE_LINK_APPLICATIONS)
                },
                text = {
                    Row {
                        Text(
                            text = stringResource(id = R.string.co_host_application),
                            style = if (tabIndex == TAB_AUDIENCE_LINK_APPLICATIONS) {
                                selectedStyle
                            } else {
                                normalStyle
                            }
                        )
                        if (newApplicationApplyDot) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = Color(0xFFF5B433),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clip(CircleShape)
                            )
                        }
                    }
                }
            )
        }

        Box {
            if (tabIndex == TAB_AUDIENCE_LINK_LINKED) {
                LinkedAudiences(viewModel)
            } else {
                AudienceLinkApplications(viewModel)
            }
        }
    }
}

// region Audience-Link Audiences
@Composable
private fun LinkedAudiences(viewModel: HostViewModel) {
    val audienceLinkUsers by viewModel.audienceLinkUsers.collectAsState()
    var durationInSeconds by remember {
        mutableLongStateOf(0L)
    }

    var showEndAllLinkDialog by remember {
        mutableStateOf(false)
    }

    var showEndOneLinkDialog by remember {
        mutableStateOf<LiveUserInfo?>(null)
    }

    if (audienceLinkUsers.isEmpty()) {
        LinkedAudienceEmpty(viewModel)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatAudienceLinkDuration(durationInSeconds),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
            LazyColumn(modifier = Modifier.weight(1F)) {
                itemsIndexed(audienceLinkUsers) { index, user ->
                    LinkedAudienceItem(
                        index = index, user = user,
                        onClickMic = {
                            // Note only support close
                            viewModel.manageAudienceMedia(user, mic = MediaStatus.OFF)
                        },
                        onClickCamera = {
                            // Note only support close
                            viewModel.manageAudienceMedia(user, camera = MediaStatus.OFF)
                        },
                        onHangup = {
                            showEndOneLinkDialog = user
                        }
                    )
                }
            }
            TextButton(
                onClick = {
                    showEndAllLinkDialog = true
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 11.dp, bottom = 16.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF53F3F),
                        shape = RoundedCornerShape(2.dp)
                    )
            ) {
                Text(
                    text = stringResource(R.string.end_all_co_host_session),
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }
        }
        LaunchedEffect("duration-counter") {
            while ((viewModel.audienceLinkStartTime > 0L) && isActive) {
                durationInSeconds =
                    (SystemClock.uptimeMillis() - viewModel.audienceLinkStartTime) / 1000L
                delay(1000)
            }
        }
    }

    if (showEndAllLinkDialog) {
        AudienceLinkFinishAll(
            onConfirm = { confirm ->
                showEndAllLinkDialog = false
                if (confirm) {
                    viewModel.finishAudienceLink()
                }
            }
        )
    }

    if (showEndOneLinkDialog != null) {
        val userInfo = showEndOneLinkDialog!!
        AudienceLinkFinishOne(
            userInfo = userInfo,
            onConfirm = { confirm ->
                showEndOneLinkDialog = null
                if (confirm) {
                    viewModel.finishAudienceLink(userInfo)
                }
            }
        )
    }
}

@Composable
private fun LinkedAudienceEmpty(viewModel: IAudienceLinkViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .padding(top = 45.dp),
            painter = painterResource(id = R.drawable.ic_guest_alert),
            contentDescription = null
        )

        Text(
            text = stringResource(R.string.no_co_host_user),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        CreativeButton(
            modifier = Modifier
                .padding(top = 12.dp),
            onClick = {
                viewModel.setManageAudiencesTab(TAB_AUDIENCE_LINK_APPLICATIONS)
            },
            text = stringResource(R.string.view_application),
        )
    }
}

@Composable
private fun LinkedAudienceItem(
    index: Int = 0,
    user: LiveUserInfo,
    onClickMic: () -> Unit,
    onClickCamera: () -> Unit,
    onHangup: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${index + 1}",
            style = LocalTextStyle.current.copy(
                color = Color(0xFF80838A),
                fontSize = 12.sp,
            )
        )

        Image(
            painter = painterResource(Avatars[user.userId]),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape,
                ),
        )

        Text(
            text = user.userName,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1F),
        )

        Image(
            painter = painterResource(
                id = if (user.isMicOn) {
                    R.drawable.ic_live_microphone_on
                } else {
                    R.drawable.ic_live_microphone_off
                }
            ),
            colorFilter = if (user.isMicOn) {
                null
            } else {
                ColorFilter.tint(Color(0xFF41464F))
            },
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(36.dp)
                .clickable(enabled = user.isMicOn, onClick = onClickMic)
                .padding(all = 6.dp),
        )

        Image(
            painter = painterResource(
                id = if (user.isCameraOn) {
                    R.drawable.ic_live_camera_on
                } else {
                    R.drawable.ic_live_camera_off
                }
            ),
            colorFilter = if (user.isCameraOn) {
                null
            } else {
                ColorFilter.tint(Color(0xFF41464F))
            },
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(36.dp)
                .clickable(enabled = user.isCameraOn, onClick = onClickCamera)
                .padding(all = 6.dp),
        )

        Image(
            painter = painterResource(id = R.drawable.ic_live_hangup52),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(36.dp)
                .clickable(onClick = onHangup)
                .padding(all = 6.dp),
        )
    }
}
// endregion

// region Audience-Link Applications
@Composable
private fun AudienceLinkApplications(viewModel: HostViewModel) {
    val applications by viewModel.audienceLinkApplications.collectAsState()


    if (applications.isEmpty()) {
        AudienceLinkApplicationsEmpty()
    } else {
        val acceptable = applications.size < MAX_LINK_COUNT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = AnnotatedString.fromHtml(
                    stringResource(
                        R.string.audience_link_request_count,
                        applications.size
                    )
                ),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
            LazyColumn(modifier = Modifier.weight(1F)) {
                itemsIndexed(applications) { index, item ->
                    AudienceLinkApplicationItem(
                        index = index,
                        user = item.applicant,
                        acceptable = acceptable
                    ) { accept ->
                        viewModel.permitAudienceLink(
                            request = item,
                            permitType = if (accept) LivePermitType.ACCEPT else LivePermitType.REJECT
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudienceLinkApplicationsEmpty() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier
                .padding(top = 45.dp),
            painter = painterResource(id = R.drawable.ic_guest_alert),
            contentDescription = null
        )

        Text(
            text = stringResource(R.string.no_guest_requests_yet),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        Text(
            text = stringResource(R.string.you_can_t_invite_guests_in_a_subscriber_live),
            style = LocalTextStyle.current.copy(
                color = Color(0xFF80838A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}


@Composable
private fun formatAudienceLinkDuration(durationInSeconds: Long): AnnotatedString {
    val minutes = durationInSeconds / 60L
    val seconds = durationInSeconds % 60L
    return AnnotatedString.fromHtml(stringResource(R.string.audience_link_time, minutes, seconds))
}

@Composable
private fun AudienceLinkApplicationItem(
    index: Int = 0,
    user: LiveUserInfo,
    acceptable: Boolean = false,
    onClicked: (Boolean) -> Unit = {},
) {
    Row(
        modifier = Modifier
            .height(60.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${index + 1}",
            style = LocalTextStyle.current.copy(
                color = Color(0xFF80838A),
                fontSize = 12.sp,
            )
        )

        Image(
            painter = painterResource(Avatars[user.userId]),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape,
                ),
        )

        Text(
            text = user.userName,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1F),
        )

        Text(
            text = stringResource(R.string.reject_audience_link),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable {
                    onClicked(false)
                },
        )

        Text(
            text = stringResource(R.string.agree_audience_link),
            style = LocalTextStyle.current.copy(
                color = if (acceptable) Color.White else Color(0xFFCCCED0),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .buttonBackground(enabled = acceptable)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable(enabled = acceptable) {
                    onClicked(true)
                },
        )
    }
}

private fun Modifier.buttonBackground(enabled: Boolean = false) =
    if (enabled)
        this.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFF1764),
                    Color(0xFFED3596),
                ),
            ),
            shape = RoundedCornerShape(4.dp),
        )
    else {
        this.background(
            color = Color(0xFF80838A),
            shape = RoundedCornerShape(4.dp),
        )
    }
// endregion