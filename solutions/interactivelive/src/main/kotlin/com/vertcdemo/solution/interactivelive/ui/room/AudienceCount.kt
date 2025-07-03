package com.vertcdemo.solution.interactivelive.ui.room

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.solution.interactivelive.R

@Composable
fun AudienceCount(count: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .height(24.dp)
            .background(
                color = Color(0x33000000),
                shape = RoundedCornerShape(48.dp),
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    )
    {
        Image(
            painter = painterResource(id = R.drawable.ic_audience_num),
            contentDescription = null,
        )
        Text(
            text = "$count",
            style = TextStyle(
                color = Color.White,
                fontSize = 12.sp,
            ),
            modifier = Modifier.padding(start = 2.dp)
        )
    }
}

@Preview
@Composable
private fun PreviewHostAvatar() {
    AudienceCount(count = 42)
}

