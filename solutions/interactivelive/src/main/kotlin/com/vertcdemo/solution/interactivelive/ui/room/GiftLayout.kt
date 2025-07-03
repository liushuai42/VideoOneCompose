package com.vertcdemo.solution.interactivelive.ui.room

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.annotation.GiftType
import com.vertcdemo.solution.interactivelive.network.data.FakeGiftEvent
import com.vertcdemo.solution.interactivelive.network.data.IGiftEvent
import com.vertcdemo.solution.interactivelive.ui.room.host.HostViewModel
import com.vertcdemo.solution.interactivelive.utils.BString
import kotlinx.coroutines.delay

@Composable
private fun GiftItem(
    gift: IGiftEvent,
    modifier: Modifier = Modifier
) {
    val (giftIcon, giftName) = when (gift.giftType) {
        GiftType.LIKE -> R.drawable.ic_gift_like to BString.gift_like
        GiftType.SUGAR -> R.drawable.ic_gift_suger to BString.gift_sugar
        GiftType.DIAMOND -> R.drawable.ic_gift_diamond to BString.gift_diamond
        GiftType.FIREWORKS -> R.drawable.ic_gift_fireworks to BString.gift_fireworks
        else -> R.drawable.ic_gift_like to BString.gift_like
    }

    Row(
        modifier = modifier
            .size(width = 194.dp, height = 44.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF1634C6),
                        Color(0x991634C6),
                        Color(0x1A1634C6),
                    ),
                ),
                shape = CircleShape
            )
    ) {
        Image(
            painter = painterResource(Avatars[gift.userId]),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(36.dp)
                .clip(CircleShape)
                .align(Alignment.CenterVertically)
        )

        Column(
            modifier = Modifier
                .padding(start = 4.dp)
                .fillMaxSize()
                .weight(1F),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = gift.username,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
            )

            Text(
                text = stringResource(BString.send_gift, stringResource(giftName)),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xBFFFFFFF),
                ),
                maxLines = 1,
            )
        }

        Image(
            painter = painterResource(giftIcon),
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(44.dp)
        )
    }
}

@Composable
private fun AnimatedGiftItem(key: String, viewModel: HostViewModel, modifier: Modifier = Modifier) {
    var item by remember { mutableStateOf<IGiftEvent?>(null) }

    AnimatedVisibility(
        visible = item != null,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally(),
        modifier = modifier
    ) {
        item?.let { GiftItem(gift = it) }
    }

    LaunchedEffect("animation-fetcher-$key") {
        viewModel.gifts.collect {
            item = it
            delay(3000)
            item = null
            delay(300)
        }
    }
}

@Composable
fun GiftLayout(
    viewModel: HostViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.height(96.dp)) {
        AnimatedGiftItem(
            key = "slot1",
            viewModel,
            modifier = Modifier.align(Alignment.BottomStart)
        )

        AnimatedGiftItem(
            key = "slot2",
            viewModel,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@Preview
@Composable
fun GiftLayoutPreview() {
    GiftItem(gift = FakeGiftEvent)
}
