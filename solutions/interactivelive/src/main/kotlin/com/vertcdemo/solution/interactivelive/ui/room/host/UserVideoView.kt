package com.vertcdemo.solution.interactivelive.ui.room.host

import android.os.SystemClock
import android.view.SurfaceView
import android.view.TextureView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.EmptyEngine
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun AudienceVideoView1(
    userInfo: LiveUserInfo,
    modifier: Modifier = Modifier,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
    hasMore: Boolean = false,
    onMoreClicked: () -> Unit = {},
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
    ) {
        if (userInfo.isCameraOn) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = {
                    // NOTE SurfaceView is not CLIP supported
                    TextureView(it)
                },
                update = { view ->
                    engine.setRemoteVideoView(userInfo.userId, view)
                },
            )
        } else {
            Image(
                painter = painterResource(Avatars[userInfo.userId]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xB3000000))
                    )
                )
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (!userInfo.isMicOn) {
                Image(
                    painter = painterResource(id = R.drawable.ic_live_link_mute),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = Color(0x33000000), shape = CircleShape)
                )
            }
            Text(
                text = userInfo.userName,
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 14.sp,
                ),
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 2.dp),
                maxLines = 1,
            )
        }

        if (hasMore) {
            Image(
                painter = painterResource(id = R.drawable.ic_live_more),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 5.dp)
                    .size(24.dp)
                    .clickable { onMoreClicked() })
        }
    }
}

@Composable
fun AudienceVideoViewN(
    userInfo: LiveUserInfo,
    modifier: Modifier = Modifier,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable {

            }) {
        if (userInfo.isCameraOn) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = ::SurfaceView,
                update = { view ->
                    engine.setRemoteVideoView(userInfo.userId, view)
                })
        } else {
            Image(
                painter = painterResource(Avatars[userInfo.userId]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .height(25.17.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xB3000000))
                    )
                )
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            if (!userInfo.isMicOn) {
                Image(
                    painter = painterResource(id = R.drawable.ic_live_link_mute),
                    contentDescription = null,
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = Color(0x33000000), shape = CircleShape)
                )
            }
            Text(
                text = userInfo.userName,
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.58.sp,
                ),
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 2.dp),
                maxLines = 1,
            )
        }
    }
}

@Composable
fun AudienceVideoViewNEmpty(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0x66333399)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_live_plus),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0x33FFFFFF))
        )

        Text(
            text = stringResource(R.string.add_guest),
            style = LocalTextStyle.current.copy(
                color = Color(0x33FFFFFF),
                fontSize = 14.sp,
            ),
            modifier = Modifier.padding(top = 11.dp),
        )
    }
}

@Preview
@Composable
fun AudienceVideoView1Preview() {
    AudienceVideoView1(
        userInfo = FakeLiveUserInfo, modifier = Modifier.size(120.dp)
    )
}

@Preview
@Composable
fun AudienceVideoViewNPreview() {
    AudienceVideoViewN(
        userInfo = FakeLiveUserInfo, modifier = Modifier.size(96.dp)
    )
}

@Preview
@Composable
fun UserVideoViewNEmptyPreview() {
    AudienceVideoViewNEmpty(modifier = Modifier.size(96.dp))
}

@Stable
@Composable
fun MyVideoView(
    userInfo: LiveUserInfo,
    modifier: Modifier = Modifier,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = ::SurfaceView,
        update = { view ->
            engine.setLocalVideoView(view)
        },
    )
    if (!userInfo.isCameraOn) {
        Image(
            painter = painterResource(Avatars[userInfo.userId]),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier.fillMaxSize()
        )
    }
}

@Composable
fun AnchorVideoView(
    userInfo: LiveUserInfo,
    modifier: Modifier = Modifier,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (userInfo.isCameraOn) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = ::SurfaceView,
                update = { view ->
                    engine.setRemoteVideoView(userInfo.userId, view)
                },
            )
        } else {
            Image(
                painter = painterResource(Avatars[userInfo.userId]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Row(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 12.dp)
                .height(36.dp)
                .background(color = Color(0x33030303), shape = CircleShape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(Avatars[userInfo.userId]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(28.dp)
                    .clip(CircleShape),
            )

            Text(
                text = userInfo.userName,
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(start = 6.dp, end = 8.dp),
                softWrap = true,
            )

            if (!userInfo.isMicOn) {
                Image(
                    painter = painterResource(R.drawable.ic_live_link_mute),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0x33000000), CircleShape)
                )
            }
        }
    }
}

@Composable
fun PKDurationLabel(
    modifier: Modifier = Modifier,
    startTime: Long = SystemClock.uptimeMillis(),
) {
    Row(
        modifier = modifier
            .paint(
                painter = painterResource(R.drawable.bg_pk_timer),
                contentScale = ContentScale.FillBounds
            )
            .padding(horizontal = 16.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        var duration by remember { mutableLongStateOf(0) }
        Image(
            painter = painterResource(R.drawable.ic_live_pk16),
            contentDescription = null,
        )

        Text(
            text = String.format(
                Locale.ENGLISH,
                "%1$02d:%2$02d",
                duration / 60,
                duration % 60
            ),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.padding(start = 5.dp),
        )
        LaunchedEffect("pk-duration-counter", startTime) {
            while (startTime > 0L && isActive) {
                duration = (SystemClock.uptimeMillis() - startTime) / 1000L
                delay(1000L)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PKViewPreview() {
    ConstraintLayout(
        modifier = Modifier.size(width = 1080.dp, height = 1920.dp),
        constraintSet = ConstraintSet {
            val (host, anchor) = createRefsFor("host", "anchor")
            constrain(host) {
                top.linkTo(parent.top, 60.dp)

                start.linkTo(parent.start)
                end.linkTo(anchor.start)

                width = Dimension.fillToConstraints
                height = Dimension.ratio("9:16")
            }

            constrain(anchor) {
                top.linkTo(host.top)
                bottom.linkTo(host.bottom)
                start.linkTo(host.end)
                end.linkTo(parent.end)

                width = Dimension.fillToConstraints
                height = Dimension.ratio("9:16")
            }

            val pkDurationLabel = createRefFor("pk-duration-label")
            constrain(pkDurationLabel) {
                top.linkTo(host.top)
                start.linkTo(host.start)
                end.linkTo(anchor.end)
            }
        }
    ) {
        MyVideoView(
            userInfo = FakeLiveUserInfo,
            modifier = Modifier.layoutId("host"),
        )

        AnchorVideoView(
            userInfo = FakeLiveUserInfo,
            modifier = Modifier.layoutId("anchor"),
        )

        PKDurationLabel(
            modifier = Modifier
                .layoutId("pk-duration-label")
        )
    }
}
