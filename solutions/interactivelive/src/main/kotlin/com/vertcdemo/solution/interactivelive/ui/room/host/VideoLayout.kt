package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.core.annotation.RoomStatus
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo

const val MY_VIDEO_FULL_SCREEN_LAYOUT_ID = "my-video-full-screen"
const val MY_VIDEO_1vN_LAYOUT_ID = "my-video-1vN"
const val AUDIENCE_LINK_1v1_LAYOUT_ID = "audience-video-1v1"
const val AUDIENCE_LINK_1vN_LAYOUT_ID = "audience-video-1vN"
const val MY_VIDEO_PK_LAYOUT_ID = "my-video-pk"
const val ANCHOR_VIDEO_PK_LAYOUT_ID = "anchor-video-pk"
const val ANCHOR_VIDEO_PK_DURATION_LAYOUT_ID = "anchor-video-pk-duration"

@Composable
fun VideoLayout(
    viewModel: HostViewModel,
    engine: IRTCEngine
) {
    val roomStatus by viewModel.roomStatus.collectAsState()
    val host by viewModel.hostUserInfo.collectAsState()

    when (roomStatus) {
        RoomStatus.LIVE -> {
            MyVideoView(
                host,
                engine = engine,
                modifier = Modifier
                    .layoutId(MY_VIDEO_FULL_SCREEN_LAYOUT_ID),
            )
        }

        RoomStatus.AUDIENCE_LINK -> {
            val audienceLinked by viewModel.audienceLinkUsers.collectAsState()
            if (audienceLinked.size == 1) {
                AudienceLink1v1Layout(
                    viewModel,
                    engine,
                    host = host,
                    audience = audienceLinked.first(),
                )
            } else if (audienceLinked.size > 1) {
                AudienceLink1vNLayout(
                    viewModel,
                    engine,
                    host = host,
                    audiences = audienceLinked,
                )
            } else {
                MyVideoView(
                    host,
                    engine = engine,
                    modifier = Modifier
                        .layoutId(MY_VIDEO_FULL_SCREEN_LAYOUT_ID),
                )
            }
        }

        RoomStatus.PK -> {
            val anchorLinkedUsers by viewModel.anchorLinkedUsers.collectAsState()
            AnchorLinkLayout(viewModel, engine, host, anchorLinkedUsers)
        }
    }
}

@Composable
private fun AudienceLink1v1Layout(
    viewModel: HostViewModel,
    engine: IRTCEngine,
    host: LiveUserInfo,
    audience: LiveUserInfo,
) {
    MyVideoView(
        host,
        engine = engine,
        modifier = Modifier
            .layoutId(MY_VIDEO_FULL_SCREEN_LAYOUT_ID),
    )

    AudienceVideoView1(
        userInfo = audience,
        engine = engine,
        modifier = Modifier
            .layoutId(AUDIENCE_LINK_1v1_LAYOUT_ID),
    )
}

@Composable
private fun AudienceLink1vNLayout(
    viewModel: HostViewModel,
    engine: IRTCEngine,
    host: LiveUserInfo,
    audiences: List<LiveUserInfo>,
) {
    MyVideoView(
        host,
        engine = engine,
        modifier = Modifier
            .layoutId(MY_VIDEO_1vN_LAYOUT_ID),
    )

    for (i in 0..5) {
        if (i in audiences.indices) {
            val info = audiences[i]
            key(info.userId) {
                AudienceVideoViewN(
                    userInfo = info,
                    engine = engine,
                    modifier = Modifier
                        .layoutId("$AUDIENCE_LINK_1vN_LAYOUT_ID-$i"),
                )
            }
        } else {
            key("empty-position-$i") {
                AudienceVideoViewNEmpty(
                    modifier = Modifier
                        .layoutId("$AUDIENCE_LINK_1vN_LAYOUT_ID-$i"),
                )
            }
        }
    }
}

@Composable
private fun AnchorLinkLayout(
    viewModel: HostViewModel,
    engine: IRTCEngine,
    host: LiveUserInfo,
    anchors: List<LiveUserInfo>,
) {
    MyVideoView(
        host,
        engine = engine,
        modifier = Modifier
            .layoutId(MY_VIDEO_PK_LAYOUT_ID),
    )

    if (anchors.isNotEmpty()) {
        val anchor = anchors.first()

        AnchorVideoView(
            anchor,
            engine = engine,
            modifier = Modifier
                .layoutId(ANCHOR_VIDEO_PK_LAYOUT_ID),
        )
    }

    PKDurationLabel(
        modifier = Modifier
            .layoutId(ANCHOR_VIDEO_PK_DURATION_LAYOUT_ID),
        startTime = viewModel.anchorLinkStartTime
    )
}