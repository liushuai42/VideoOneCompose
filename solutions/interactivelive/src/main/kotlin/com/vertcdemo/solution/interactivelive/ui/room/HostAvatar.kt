package com.vertcdemo.solution.interactivelive.ui.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.avatars.Avatars

@Composable
fun HostAvatar(
    modifier: Modifier = Modifier,
    userId: String = "42",
    userName: String = "John Doe"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                color = Color(0x33000000),
                shape = RoundedCornerShape(48.dp),
            )
            .padding(2.dp),
    ) {
        Image(
            painter = painterResource(Avatars[userId]),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
        )

        Text(
            text = userName,
            style = TextStyle(
                color = Color.White,
                fontSize = 14.sp,
            ),
            maxLines = 1,
            modifier = Modifier
                .padding(start = 4.dp, end = 10.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewHostAvatar() {
    HostAvatar()
}