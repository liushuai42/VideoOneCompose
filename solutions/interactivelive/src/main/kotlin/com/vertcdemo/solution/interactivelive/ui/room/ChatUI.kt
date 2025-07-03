package com.vertcdemo.solution.interactivelive.ui.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.ui.room.host.CONTENT_ID_HOST
import com.vertcdemo.solution.interactivelive.ui.room.host.IChatViewModel

@Composable
fun ChatLayout(chatViewModel: IChatViewModel, modifier: Modifier = Modifier) {
    val chatList by chatViewModel.chatList.collectAsState()
    LazyColumn(
        modifier = modifier
            .fadingEdge(
                Brush.verticalGradient(
                    0F to Color.Transparent,
                    0.15F to Color.Black,
                )
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Bottom),
    ) {
        items(chatList) {
            ChatItem(it)
        }
    }
}

@Composable
fun ChatItem(text: AnnotatedString) {
    val inlineContent = remember {
        mapOf(
            CONTENT_ID_HOST to InlineTextContent(
                placeholder = Placeholder(
                    width = 26.sp,
                    height = 14.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                ),
                children = {
                    Image(
                        painter = painterResource(R.drawable.ic_message_host),
                        contentDescription = null,
                    )
                })
        )
    }
    Text(
        modifier = Modifier
            .background(
                color = Color(0x3D000000),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            color = Color.White
        ),
        inlineContent = inlineContent
    )
}

fun Modifier.fadingEdge(brush: Brush) = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        drawRect(brush, blendMode = BlendMode.DstIn)
    }