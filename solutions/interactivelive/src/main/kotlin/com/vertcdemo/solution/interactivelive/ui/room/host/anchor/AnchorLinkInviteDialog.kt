package com.vertcdemo.solution.interactivelive.ui.room.host.anchor

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.annotation.LiveLinkMicStatus
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import com.vertcdemo.solution.interactivelive.ui.room.FakeRoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnchorLinkInviteDialog(
    roomArgs: RoomArgs = FakeRoomArgs,
    viewModel: AnchorLinkViewModel = viewModel(),
    onDismissRequest: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = {},
        modifier = Modifier,
    ) {
        DialogContent(roomArgs, viewModel, onDismissRequest)
    }

    LaunchedEffect("boot") {
        viewModel.requestAnchorList()
    }
}

@Composable
private fun DialogContent(
    roomArgs: RoomArgs = FakeRoomArgs,
    viewModel: AnchorLinkViewModel,
    onDismissRequest: () -> Unit = {}
) {
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
            .paint(
                painter = painterResource(id = R.drawable.bg_pk_panel_p0),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.invite_host_pk),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .height(51.dp)
                .wrapContentHeight()
        )
        HorizontalDivider(color = Color(0x26FFFFFF), thickness = 0.5.dp)

        val uiState = viewModel.uiState.collectAsState()
        if (uiState.value is UiState.Loaded) {
            val anchors = (uiState.value as UiState.Loaded).anchors
            if (anchors.isEmpty()) {
                Column(
                    modifier = Modifier
                        .padding(top = 97.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_guest_alert),
                        contentDescription = null,
                    )
                    Text(
                        text = stringResource(R.string.no_host_online),
                        style = LocalTextStyle.current.copy(
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }
            } else {
                LazyColumn {
                    itemsIndexed(anchors) { index, anchor ->
                        AnchorItem(index, anchor) {
                            viewModel.inviteAnchor(roomArgs, anchor)
                            onDismissRequest()
                        }
                    }
                }
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

@Composable
private fun AnchorItem(
    index: Int = 0,
    anchor: LiveUserInfo = FakeLiveUserInfo,
    onClicked: () -> Unit = {},
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
            painter = painterResource(Avatars[anchor.userId]),
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
            text = anchor.userName,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1F),
        )

        val actionText = when (anchor.linkMicStatus) {
            LiveLinkMicStatus.INVITING -> {
                R.string.Initiate_send
            }

            LiveLinkMicStatus.AUDIENCE_INTERACTING,
            LiveLinkMicStatus.HOST_INTERACTING -> {
                R.string.connecting
            }

            LiveLinkMicStatus.APPLYING,
            LiveLinkMicStatus.OTHER -> {
                R.string.invite
            }

            else -> {
                R.string.invite
            }
        }

        Text(
            text = stringResource(actionText),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF1764),
                            Color(0xFFED3596),
                        ),
                    ),
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable {
                    onClicked()
                },
        )
    }
}

sealed interface UiState {
    object Loading : UiState
    class Loaded(val anchors: List<LiveUserInfo> = emptyList()) : UiState
}

@Preview(showBackground = true)
@Composable
fun AnchorLinkInviteDialogPreview() {
    Box {
        DialogContent(viewModel = viewModel())
    }
}