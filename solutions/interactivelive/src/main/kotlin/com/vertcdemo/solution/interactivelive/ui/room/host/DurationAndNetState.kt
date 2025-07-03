package com.vertcdemo.solution.interactivelive.ui.room.host

import android.os.SystemClock
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.utils.NetworkState
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale

@Composable
fun DurationAndNetState(
    startTime: Long,
    networkState: State<NetworkState>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .height(24.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFFF1764),
                            Color(0xFFED3596)
                        ),
                    ),
                    shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                )
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painterResource(R.drawable.ic_live),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(3.dp))

            Text(
                text = stringResource(R.string.label_live),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        var durationText by remember {
            mutableStateOf("00:00")
        }

        Text(
            text = durationText,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .height(24.dp)
                .background(
                    color = Color(0x33000000),
                    shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                )
                .wrapContentHeight()
                .padding(horizontal = 4.dp),
        )

        when (networkState.value) {
            NetworkState.NONE -> {}
            NetworkState.GOOD -> NetworkStatusView(
                COLORS_GOOD,
                R.string.net_quality_good
            )

            NetworkState.BAD -> NetworkStatusView(
                COLORS_BAD,
                R.string.net_quality_poor
            )

            NetworkState.DISCONNECTED -> NetworkStatusView(
                COLORS_DISCONNECTED,
                R.string.net_quality_disconnect
            )
        }

        LaunchedEffect(startTime) {
            while (isActive) {
                val currentTime = SystemClock.uptimeMillis()
                val duration = (currentTime - startTime) / 1000L
                val minutes = duration / 60L
                val seconds = duration % 60L
                durationText = "%1$02d:%2$02d".format(Locale.ENGLISH, minutes, seconds)
                delay(1000L)
            }
        }
    }
}

@Composable
private fun RowScope.NetworkStatusView(colors: Array<Color>, text: Int) {
    Spacer(modifier = Modifier.width(6.dp))

    Box(
        modifier = Modifier
            .size(10.dp)
            .drawWithCache {
                val (ring, solid) = colors
                onDrawBehind {
                    drawCircle(
                        color = ring,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                    drawCircle(
                        color = solid,
                        radius = 3.dp.toPx()
                    )
                }
            }
    )

    Spacer(modifier = Modifier.width(4.dp))

    Text(
        text = stringResource(text),
        style = LocalTextStyle.current.copy(
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}

private val COLORS_DISCONNECTED = arrayOf(Color(0x4DDB373F), Color(0xFFDB373F))
private val COLORS_BAD = arrayOf(Color(0x4DF2A200), Color(0xFFF2A200))
private val COLORS_GOOD = arrayOf(Color(0x4D0BE09B), Color(0xFF0BE09B))

@Preview
@Composable
fun TimeLabelPreview() {
    DurationAndNetState(
        startTime = SystemClock.uptimeMillis(),
        networkState = remember { mutableStateOf(NetworkState.NONE) },
    )
}
