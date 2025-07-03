package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.EmptyEngine
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.core.RTCEngineViewModel.Companion.ENGINE_KEY
import com.vertcdemo.solution.interactivelive.core.events.AnchorLinkInviteEvent
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.liveBackground
import com.vertcdemo.solution.interactivelive.ui.room.AudienceCount
import com.vertcdemo.solution.interactivelive.ui.room.ChatLayout
import com.vertcdemo.solution.interactivelive.ui.room.FakeRoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.GiftLayout
import com.vertcdemo.solution.interactivelive.ui.room.HostAvatar
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.host.HostViewModel.Companion.ROOM_ARGS_KEY
import com.vertcdemo.solution.interactivelive.ui.room.host.anchor.AnchorLinkReceivedDialog

@Composable
fun PageRoomHost(
    args: RoomArgs,
    paddingValues: PaddingValues = ZeroPadding,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
    onEndLive: (LiveFinishReason) -> Unit = {}
) {
    val viewModel: HostViewModel = viewModel(
        factory = HostViewModel.Companion.Factory,
        extras = MutableCreationExtras().apply {
            set(ROOM_ARGS_KEY, args)
            set(ENGINE_KEY, engine)
        }
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .liveBackground(),

        constraintSet = ConstraintSet {
            val guidelineTop = createGuidelineFromTop(paddingValues.calculateTopPadding())
            val guidelineBottom = createGuidelineFromBottom(paddingValues.calculateBottomPadding())

            val (hostAvatar, audienceCount, close) = createRefsFor(
                "host_avatar",
                "audience_count",
                "close"
            )
            constrain(hostAvatar) {
                start.linkTo(parent.start, margin = 12.dp)
                top.linkTo(guidelineTop, margin = 20.dp)
            }
            constrain(audienceCount) {
                top.linkTo(hostAvatar.top)
                bottom.linkTo(hostAvatar.bottom)

                end.linkTo(close.start, margin = 8.dp)
            }
            constrain(close) {
                top.linkTo(hostAvatar.top)
                bottom.linkTo(hostAvatar.bottom)
                end.linkTo(parent.end, margin = 8.dp)
            }

            val durationAndNetState = createRefFor("duration_and_net_state")
            constrain(durationAndNetState) {
                start.linkTo(hostAvatar.start)
                top.linkTo(hostAvatar.bottom, margin = 6.dp)
            }

            val bottomBar = createRefFor("bottom_bar")
            constrain(bottomBar) {
                start.linkTo(parent.start, margin = 12.dp)
                end.linkTo(parent.end, margin = 12.dp)
                bottom.linkTo(guidelineBottom, margin = 21.dp)

                width = Dimension.fillToConstraints
            }

            val chatLayout = createRefFor("chat_layout")
            constrain(chatLayout) {
                start.linkTo(parent.start, margin = 12.dp)
                end.linkTo(parent.end, margin = 144.dp) // leave padding for Audience Window
                bottom.linkTo(bottomBar.top, margin = 14.dp)

                width = Dimension.fillToConstraints
                height = Dimension.value(160.dp)
            }

            val giftLayout = createRefFor("gift_layout")
            constrain(giftLayout) {
                start.linkTo(parent.start, margin = 12.dp)
                bottom.linkTo(chatLayout.top, margin = 4.dp)
            }

            val myVideoFullScreen = createRefFor(MY_VIDEO_FULL_SCREEN_LAYOUT_ID)
            constrain(myVideoFullScreen) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

            val audienceVideo1V1 = createRefFor(AUDIENCE_LINK_1v1_LAYOUT_ID)
            constrain(audienceVideo1V1) {
                end.linkTo(parent.end, margin = 12.dp)
                bottom.linkTo(bottomBar.top, margin = 32.dp)

                width = Dimension.value(120.dp)
                height = Dimension.value(120.dp)
            }

            val (myVideo1vN,
                audienceVideo0,
                audienceVideo1,
                audienceVideo2,
                audienceVideo3,
                audienceVideo4,
                audienceVideo5
            ) = createRefsFor(
                MY_VIDEO_1vN_LAYOUT_ID,
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-0",
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-1",
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-2",
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-3",
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-4",
                "$AUDIENCE_LINK_1vN_LAYOUT_ID-5",
            )
            constrain(myVideo1vN) {
                start.linkTo(parent.start)
                top.linkTo(durationAndNetState.bottom, margin = 14.dp)
                end.linkTo(audienceVideo0.start, margin = 2.dp)
                bottom.linkTo(bottomBar.top, margin = 14.dp)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints

                horizontalChainWeight = 1F
            }
            constrain(audienceVideo0) {
                start.linkTo(myVideo1vN.end)
                end.linkTo(parent.end)

                top.linkTo(myVideo1vN.top)
                bottom.linkTo(audienceVideo1.top, 2.dp)

                width = Dimension.ratio("1:1")
                height = Dimension.fillToConstraints
            }
            constrain(audienceVideo1) {
                start.linkTo(audienceVideo0.start)
                end.linkTo(audienceVideo0.end)

                top.linkTo(audienceVideo0.bottom)
                bottom.linkTo(audienceVideo2.top, margin = 2.dp)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(audienceVideo2) {
                start.linkTo(audienceVideo0.start)
                end.linkTo(audienceVideo0.end)

                top.linkTo(audienceVideo1.bottom)
                bottom.linkTo(audienceVideo3.top, margin = 2.dp)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(audienceVideo3) {
                start.linkTo(audienceVideo0.start)
                end.linkTo(audienceVideo0.end)

                top.linkTo(audienceVideo2.bottom)
                bottom.linkTo(audienceVideo4.top, margin = 2.dp)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(audienceVideo4) {
                start.linkTo(audienceVideo0.start)
                end.linkTo(audienceVideo0.end)

                top.linkTo(audienceVideo3.bottom)
                bottom.linkTo(audienceVideo5.top, margin = 2.dp)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
            constrain(audienceVideo5) {
                start.linkTo(audienceVideo0.start)
                end.linkTo(audienceVideo0.end)

                top.linkTo(audienceVideo4.bottom)
                bottom.linkTo(myVideo1vN.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

            val (myVideoPK, anchorVideoPK, anchorPKDuration) =
                createRefsFor(
                    MY_VIDEO_PK_LAYOUT_ID,
                    ANCHOR_VIDEO_PK_LAYOUT_ID,
                    ANCHOR_VIDEO_PK_DURATION_LAYOUT_ID
                )
            constrain(myVideoPK) {
                start.linkTo(parent.start)
                end.linkTo(anchorVideoPK.start)

                top.linkTo(durationAndNetState.bottom, margin = 14.dp)

                width = Dimension.fillToConstraints
                height = Dimension.ratio("9:16")
            }
            constrain(anchorVideoPK) {
                start.linkTo(myVideoPK.end)
                end.linkTo(parent.end)

                top.linkTo(myVideoPK.top)
                bottom.linkTo(myVideoPK.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

            constrain(anchorPKDuration) {
                start.linkTo(myVideoPK.start)
                end.linkTo(anchorVideoPK.end)

                top.linkTo(myVideoPK.top)
            }
        }
    ) {

        VideoLayout(viewModel, engine)

        // region Header 头部信息
        val audienceCount by viewModel.audienceCount.collectAsState()
        HostAvatar(
            modifier = Modifier
                .layoutId("host_avatar"),
            userId = args.roomInfo.hostUserId,
            userName = args.roomInfo.hostUsername,
        )

        AudienceCount(
            count = audienceCount,
            modifier = Modifier
                .layoutId("audience_count")
        )

        Image(
            painter = painterResource(id = R.drawable.ic_power),
            contentDescription = "Close",
            modifier = Modifier
                .layoutId("close")
                .clickable {
                    viewModel.confirmFinishDialog.show()
                }
        )

        DurationAndNetState(
            startTime = viewModel.startTime,
            networkState = engine.networkState.collectAsState(),
            modifier = Modifier
                .layoutId("duration_and_net_state")
        )
        // endregion

        ChatLayout(
            chatViewModel = viewModel,
            modifier = Modifier
                .layoutId("chat_layout")
        )

        GiftLayout(
            viewModel,
            modifier = Modifier
                .layoutId("gift_layout")
        )

        HostBottomBar(
            viewModel,
            modifier = Modifier
                .layoutId("bottom_bar")
        )
    }

    ConfirmFinishLiveDialogExt(viewModel)
    AnchorLinkConfirmInviteDialogExt(viewModel)

    BackHandler {
        viewModel.confirmFinishDialog.show()
    }

    LaunchedEffect("boot") {
        viewModel.startLive()
    }

    LaunchedEffect("finish-live-reason") {
        viewModel.liveFinish.collect { reason ->
            engine.stopLive()
            onEndLive(reason)
        }
    }
}

// region Dialogs
@Composable
private fun ConfirmFinishLiveDialogExt(viewModel: HostViewModel) {
    val confirmFinishLive by viewModel.confirmFinishDialog.collectAsState()
    if (confirmFinishLive) {
        ConfirmFinishLiveDialog(
            onDismiss = { confirmed ->
                viewModel.confirmFinishDialog.dismiss()
                if (confirmed) {
                    viewModel.requestFinishLive()
                }
            }
        )
    }
}

@Composable
private fun AnchorLinkConfirmInviteDialogExt(viewModel: HostViewModel) {
    val state by viewModel.anchorLinkReceivedDialog.collectAsState()
    if (state is DialogArgs.Show) {
        val event = (state as DialogArgs.Show).args as AnchorLinkInviteEvent
        AnchorLinkReceivedDialog(
            roomArgs = viewModel.args,
            event = event,
            onDismissRequest = {
                viewModel.anchorLinkReceivedDialog.dismiss()
            }
        )
    }
}
// endregion

@Preview
@Composable
fun PreviewPageRoomHost() {
    PageRoomHost(args = FakeRoomArgs)
}
