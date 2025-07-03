package com.vertcdemo.base.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.vertcdemo.base.R

@Composable
fun HomeItem(
    modifier: Modifier = Modifier,
    title: String = "Interactive Live",
    description: String = "PK, chat, gifts, likes, beauty effects, 1080P live experience",
    @DrawableRes backgroundRes: Int = R.drawable.interactive_live_bg_entry,
    onClick: () -> Unit = {},
) {
    ConstraintLayout(
        modifier = modifier
            .paint(
                painter = painterResource(backgroundRes),
                contentScale = ContentScale.Fit
            )
            .fillMaxWidth()
            .aspectRatio(ratio = 1634F / 1215F)
            .clickable(onClick = onClick),
        constraintSet = ConstraintSet {
            val (title, description, next)
                    = createRefsFor("title", "description", "next")

            constrain(title) {
                start.linkTo(description.start)
                end.linkTo(description.end)
                bottom.linkTo(description.top, margin = 2.dp)

                width = Dimension.fillToConstraints
            }

            constrain(description) {
                start.linkTo(parent.start, margin = 32.dp)
                end.linkTo(next.start, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 32.dp)

                width = Dimension.fillToConstraints
            }

            constrain(next) {
                end.linkTo(parent.end, margin = 32.dp)
                bottom.linkTo(parent.bottom, margin = 48.dp)

                width = Dimension.value(32.dp)
                height = Dimension.value(32.dp)
            }
        }
    ) {
        Text(
            modifier = Modifier.layoutId("title"),
            text = title,
            style = TextStyle(
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        )
        Text(
            modifier = Modifier.layoutId("description"),
            text = description,
            style = TextStyle(
                color = Color.White,
                fontSize = 12.sp,
            )
        )
        Image(
            modifier = Modifier.layoutId("next"),
            painter = painterResource(R.drawable.ic_action_next),
            contentDescription = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeItemPreview() {
    HomeItem()
}