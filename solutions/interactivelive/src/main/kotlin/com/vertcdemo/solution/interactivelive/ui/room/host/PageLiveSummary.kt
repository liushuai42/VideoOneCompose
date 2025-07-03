package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.base.utils.SolutionDataManager
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.EmptyEngine
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.network.data.LiveSummary
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.liveBackground
import java.text.DecimalFormat

@Composable
fun PageLiveSummary(
    paddingValues: PaddingValues = ZeroPadding,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
    summary: LiveSummary = LiveSummary(),
    onBackPressed: () -> Unit = {}
) {
    val userId = SolutionDataManager.userId
    val userName = SolutionDataManager.userName

    val duration = summary.duration
    val viewers = summary.viewers
    val likes = summary.likes
    val gifts = summary.gifts

    Column(
        modifier = Modifier
            .liveBackground()
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.ic_live_arrow_left24),
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .size(44.dp)
                .clickable(onClick = onBackPressed),
        )

        Image(
            painter = painterResource(Avatars[userId]),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.CenterHorizontally)
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp)),
        )

        Text(
            text = stringResource(R.string.live_show_suffix, userName),
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            ),
            modifier = Modifier
                .padding(top = 14.dp)
                .align(Alignment.CenterHorizontally),
        )
        Text(
            text = stringResource(R.string.live_show_duration_mins, (duration / 1000 / 60)),
            style = TextStyle(
                fontSize = 14.sp,
                color = Color(0xE6FFFFFF),
            ),
            modifier = Modifier
                .padding(top = 2.dp)
                .align(Alignment.CenterHorizontally),
        )

        Column(
            modifier = Modifier
                .padding(top = 32.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0x1FFFFFFF),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(vertical = 16.dp, horizontal = 12.dp),
        ) {
            Text(
                text = stringResource(R.string.summary_overview),
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xE6FFFFFF),
                    fontWeight = FontWeight.Bold,
                )
            )

            val labelStyle = TextStyle(
                fontSize = 12.sp,
                color = Color(0x80FFFFFF)
            )
            val valueStyle = TextStyle(
                fontSize = 20.sp,
                color = Color(0xE6FFFFFF),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.label_viewers),
                style = labelStyle,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = viewers.formatViewers(),
                style = valueStyle
            )
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Column(modifier = Modifier.width(140.dp)) {
                    Text(
                        text = stringResource(R.string.label_likes),
                        style = labelStyle
                    )
                    Text(
                        text = likes.formatKiloSeparated(),
                        style = valueStyle
                    )
                }
                Column(modifier = Modifier.width(140.dp)) {
                    Text(
                        text = stringResource(R.string.label_gifts),
                        style = labelStyle
                    )
                    Text(
                        text = gifts.formatKiloSeparated(),
                        style = valueStyle
                    )
                }
            }
        }

        OutlinedButton(
            onClick = onBackPressed,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .padding(top = 32.dp)
                .sizeIn(minWidth = 174.dp)
                .align(Alignment.CenterHorizontally),
            border = BorderStroke(1.dp, Color.White),
        ) {
            Text(
                text = stringResource(R.string.back_to_homepage),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }
    }

    LaunchedEffect("boot") {
        engine.stopCapture()
    }
}

@Preview
@Composable
fun PageLiveSummaryPreview() {
    PageLiveSummary()
}

private fun Int.formatViewers(): String = if (this < 1000) {
    DecimalFormat("#").format(this.toLong())
} else {
    val value: Double = this.toDouble() / 1000F
    DecimalFormat(",###.#K").format(value)
}

private fun Int.formatKiloSeparated(): String {
    return DecimalFormat(",###").format(this)
}
