package com.vertcdemo.solution.interactivelive.ui.room.host.audience

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudienceLinkFinishOne(
    userInfo: LiveUserInfo = FakeLiveUserInfo,
    onConfirm: (Boolean) -> Unit = {},
) {
    val userId = userInfo.userId
    val userName = userInfo.userName
    BasicAlertDialog(
        onDismissRequest = { onConfirm(false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .sizeIn(minWidth = 280.dp, maxWidth = 560.dp)
                .height(IntrinsicSize.Min)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.padding(top = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = Avatars[userId]),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                )
                Text(
                    text = userName,
                    style = LocalTextStyle.current.copy(
                        color = Color(0xFF020814),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Title(
                text = stringResource(R.string.end_an_audience_title),
                modifier = Modifier.padding(top = 12.dp),
            )

            Message(
                text = stringResource(R.string.end_an_audience_message),
            )
            HorizontalDivider(color = Color(0x1F161823), thickness = 0.5.dp)
            Buttons(
                onCancel = { onConfirm(false) },
                onConfirm = { onConfirm(true) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudienceLinkFinishAll(
    onConfirm: (Boolean) -> Unit = {},
) {
    BasicAlertDialog(
        onDismissRequest = { onConfirm(false) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        Column(
            modifier = Modifier
                .sizeIn(minWidth = 280.dp, maxWidth = 560.dp)
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title(
                text = stringResource(R.string.end_all_co_audiences_title),
                modifier = Modifier.padding(top = 24.dp),
            )
            Message(
                text = stringResource(R.string.end_all_co_audiences_message),
            )
            HorizontalDivider(color = Color(0x1F161823), thickness = 0.5.dp)
            Buttons(
                onCancel = { onConfirm(false) },
                onConfirm = { onConfirm(true) },
            )
        }
    }
}

@Composable
private fun Buttons(
    onCancel: () -> Unit = { },
    onConfirm: () -> Unit = { }
) {
    Row {
        TextButton(
            onClick = onCancel,
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = stringResource(R.string.cancel),
                style = LocalTextStyle.current.copy(
                    color = Color(0xBF161823),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        VerticalDivider(color = Color(0x1F161823), thickness = 0.5.dp)
        TextButton(
            onClick = onConfirm,
            modifier = Modifier.weight(1F)
        ) {
            Text(
                text = stringResource(R.string.confirm),
                style = LocalTextStyle.current.copy(
                    color = Color(0xFF161823),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
    }
}

@Composable
private fun Title(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = LocalTextStyle.current.copy(
            color = Color(0xFF161823),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = modifier
            .padding(horizontal = 16.dp)
    )
}

@Composable
private fun Message(text: String) {
    Text(
        text = text,
        style = LocalTextStyle.current.copy(
            color = Color(0xBF161823),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .padding(top = 12.dp)
            .padding(horizontal = 16.dp)
            .padding(bottom = 20.dp),
    )
}

@Composable
@Preview(showBackground = true)
fun AudienceLinkFinishOnePreview() {
    AudienceLinkFinishOne()
}

@Composable
@Preview(showBackground = true)
fun AudienceLinkFinishAllPreview() {
    AudienceLinkFinishAll()
}
