package com.vertcdemo.solution.interactivelive.ui.room.host.anchor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.network.data.FakeLiveUserInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnchorLinkFinishDialog(
    myInfo: LiveUserInfo,
    userInfo: LiveUserInfo,
    linkerId: String,
    rtsRoomId: String,
    viewModel: AnchorLinkViewModel = viewModel(),
    onDismissRequest: () -> Unit = {}
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = {},
        modifier = Modifier,
    ) {
        DialogContent(
            myInfo,
            userInfo,
            linkerId,
            rtsRoomId,
            viewModel, onDismissRequest
        )
    }
}

@Composable
private fun DialogContent(
    myInfo: LiveUserInfo,
    userInfo: LiveUserInfo,
    linkerId: String,
    rtsRoomId: String,
    viewModel: AnchorLinkViewModel,
    onDismissRequest: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF161823),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_pk_panel_p1),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
            .paint(
                painter = painterResource(id = R.drawable.bg_pk_panel_p0),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.during_pk),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .height(51.dp)
                .wrapContentHeight()
        )
        HorizontalDivider(color = Color(0x26FFFFFF), thickness = 0.5.dp)
        VSInfo(myInfo, userInfo)

        TextButton(
            onClick = {
                viewModel.finishAnchorLink(linkerId, rtsRoomId)
                onDismissRequest()
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 32.dp, bottom = 36.dp)
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF53F3F),
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Text(
                text = stringResource(R.string.end_of_pk),
                style = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}

@Composable
private fun VSInfo(
    myInfo: LiveUserInfo,
    userInfo: LiveUserInfo,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .paint(
                painter = painterResource(id = R.drawable.ic_anchor_link_vs)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(Avatars[myInfo.userId]),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
        )
        Text(
            text = myInfo.userName,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            modifier = Modifier
                .padding(start = 6.dp)
                .weight(1F),

            )
        Spacer(
            Modifier
                .width(8.dp)
        )
        Text(
            text = userInfo.userName,
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
            ),
            maxLines = 1,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(end = 6.dp)
                .weight(1F),
        )
        Image(
            painter = painterResource(Avatars[userInfo.userId]),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnchorLinkFinishDialogPreview() {
    Box {
        DialogContent(
            myInfo = FakeLiveUserInfo,
            userInfo = FakeLiveUserInfo,
            linkerId = "",
            rtsRoomId = "",
            viewModel = viewModel()
        )
    }
}