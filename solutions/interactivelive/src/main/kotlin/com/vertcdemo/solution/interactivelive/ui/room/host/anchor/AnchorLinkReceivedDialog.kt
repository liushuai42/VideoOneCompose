package com.vertcdemo.solution.interactivelive.ui.room.host.anchor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkInviteEvent
import com.vertcdemo.solution.interactivelive.core.events.FakeAnchorLinkInviteEvent
import com.vertcdemo.solution.interactivelive.ui.room.FakeRoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.utils.BString

private const val DIALOG_TIMEOUT_MS = 10000

@Composable
fun AnchorLinkReceivedDialog(
    roomArgs: RoomArgs,
    event: AnchorLinkInviteEvent,
    viewModel: AnchorLinkViewModel = viewModel(),
    onDismissRequest: () -> Unit = {},
) {
    val userId = event.userInfo.userId
    val userName = event.userInfo.userName
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .paint(
                    painter = painterResource(id = R.drawable.bg_anchor_invite_dialog),
                    contentScale = ContentScale.Fit,
                )
                .size(width = 320.dp, height = 400.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Avatars[userId]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.anchor_link_from, userName),
                    style = LocalTextStyle.current.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    )
                )
            }

            Text(
                text = stringResource(R.string.receive_a_new_pk_invitation),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 40.dp),
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 44.dp)
                    .height(height = 44.dp),
            ) {
                Text(
                    text = stringResource(BString.decline),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1F)
                        .padding(start = 24.dp)
                        .fillMaxHeight()
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.replyAnchorLinkInvite(
                                args = roomArgs,
                                event,
                                replyType = LivePermitType.REJECT
                            )
                            onDismissRequest()
                        }
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(R.string.accept),
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 24.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF1764),
                                    Color(0xFFED3596)
                                )
                            ),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            viewModel.replyAnchorLinkInvite(
                                args = roomArgs,
                                event,
                                replyType = LivePermitType.ACCEPT
                            )
                            onDismissRequest()
                        }
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                )
            }

            val progress = remember { Animatable(0F) }

            LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier.fillMaxWidth(),
                drawStopIndicator = {},
                color = Color.Transparent,
                trackColor = Color(0x66FFFFFF),
            )

            LaunchedEffect("auto-dismiss") {
                progress.animateTo(
                    targetValue = 1F,
                    animationSpec = tween(
                        durationMillis = DIALOG_TIMEOUT_MS,
                        easing = LinearEasing
                    )
                )
                onDismissRequest()
            }
        }
    }
}

@Preview
@Composable
fun PreviewAnchorLinkReceivedDialog() {
    AnchorLinkReceivedDialog(
        roomArgs = FakeRoomArgs,
        event = FakeAnchorLinkInviteEvent
    )
}
