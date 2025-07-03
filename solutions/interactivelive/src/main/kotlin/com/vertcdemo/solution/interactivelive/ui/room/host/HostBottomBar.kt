package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import com.vertcdemo.solution.interactivelive.ui.room.host.anchor.AnchorLinkFinishDialog
import com.vertcdemo.solution.interactivelive.ui.room.host.anchor.AnchorLinkInviteDialog
import com.vertcdemo.solution.interactivelive.ui.room.host.audience.ManageAudiencesDialog

@Composable
fun HostBottomBar(
    viewModel: HostViewModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(36.dp)
    ) {
        val roomStatus by viewModel.roomStatus.collectAsState()
        val isAudienceLink = roomStatus == RoomStatus.AUDIENCE_LINK
        val isPk = roomStatus == RoomStatus.PK

        val newApplicationApplyDot by viewModel.newApplicationApplyDot.collectAsState()

        Box {
            Image(
                painter = painterResource(
                    id = if (isAudienceLink)
                        R.drawable.ic_live_users_gray
                    else
                        R.drawable.ic_live_users_colored
                ),
                contentDescription = "Host Avatar",
                modifier = Modifier.clickable {
                    viewModel.manageAudienceDialog.show()
                }
            )
            if (newApplicationApplyDot) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .background(
                            color = Color(0xFFF5B433),
                            shape = CircleShape
                        ),
                )
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_live_pk_colored),
            contentDescription = "Host Avatar",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(36.dp)
                .background(
                    color = Color(0x33000000),
                    shape = CircleShape
                )
                .clickable {
                    if (isPk) {
                        viewModel.anchorLinkFinishDialog.show()
                    } else {
                        viewModel.anchorLinkInviteDialog.show()
                    }
                },
            contentScale = ContentScale.Inside,
            colorFilter = if (isPk) ColorFilter.tint(Color(0xB3FFFFFF)) else null,
        )
        Text(
            text = stringResource(R.string.add_a_comment),
            style = TextStyle(
                color = Color(0x4DFFFFFF),
                fontSize = 14.sp
            ),
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1F)
                .height(36.dp)
                .background(
                    color = Color(0x33000000),
                    shape = CircleShape
                )
                .align(Alignment.CenterVertically)
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_live_beauty24),
            contentDescription = "Host Avatar",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(36.dp)
                .background(
                    color = Color(0x33000000),
                    shape = CircleShape
                ),
            contentScale = ContentScale.Inside,
        )
        Image(
            painter = painterResource(id = R.drawable.ic_live_more),
            contentDescription = "Host Avatar",
            modifier = Modifier
                .padding(start = 8.dp)
                .size(36.dp)
                .background(
                    color = Color(0x33000000),
                    shape = CircleShape
                )
                .clickable {
                    viewModel.moreActionsDialog.show()
                },
            contentScale = ContentScale.Inside,
        )
    }

    ManageAudienceDialogExt(viewModel)
    AnchorLinkInviteDialogExt(viewModel)
    AnchorLinkFinishDialogExt(viewModel)
    MoreActionDialogExt(viewModel)
}

@Composable
private fun ManageAudienceDialogExt(viewModel: HostViewModel) {
    val showManageAudienceDialog by viewModel.manageAudienceDialog.collectAsState()
    if (showManageAudienceDialog) {
        ManageAudiencesDialog(viewModel)
    }
}

@Composable
private fun AnchorLinkInviteDialogExt(viewModel: HostViewModel) {
    val anchorInviteDialog by viewModel.anchorLinkInviteDialog.collectAsState()
    if (anchorInviteDialog) {
        AnchorLinkInviteDialog(
            roomArgs = viewModel.args,
            onDismissRequest = {
                viewModel.anchorLinkInviteDialog.dismiss()
            })
    }
}

@Composable
private fun AnchorLinkFinishDialogExt(viewModel: HostViewModel) {
    val state by viewModel.anchorLinkFinishDialog.collectAsState()
    if (state) {
        val linkerId = requireNotNull(viewModel.anchorLinkId) { "anchorLinkerId is Not set!" }
        val anchorLinkedUsers by viewModel.anchorLinkedUsers.collectAsState()
        require(anchorLinkedUsers.isNotEmpty()) { "anchorLinkedUsers is Empty!" }
        val userInfo = anchorLinkedUsers.first()

        AnchorLinkFinishDialog(
            myInfo = viewModel.args.userInfo,
            userInfo,
            linkerId = linkerId,
            rtsRoomId = viewModel.args.rtsRoomId,
            onDismissRequest = {
                viewModel.anchorLinkFinishDialog.dismiss()
            })
    }
}

@Composable
private fun MoreActionDialogExt(viewModel: HostViewModel) {
    val state by viewModel.moreActionsDialog.collectAsState()
    if (state) {
        MoreActionsDialog(viewModel, onDismissRequest = {
            viewModel.moreActionsDialog.dismiss()
        })
    }
}