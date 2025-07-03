package com.vertcdemo.solution.interactivelive.ui.room.host

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.utils.BString
import com.vertcdemo.solution.interactivelive.utils.UNDER_CONSTRUCTION

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreActionsDialog(hostViewModel: HostViewModel, onDismissRequest: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = {},
        modifier = Modifier,
    ) {
        DialogContent(hostViewModel)
    }
}

@Composable
private fun DialogContent(hostViewModel: HostViewModel) {
    Column(
        modifier =
            Modifier
                .background(color = Color(0xBF000000))
                .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.more),
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

        val hostUserInfo by hostViewModel.hostUserInfo.collectAsState()
        val isCameraOn = hostUserInfo.isCameraOn
        val isMicOn = hostUserInfo.isMicOn

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable(enabled = isCameraOn) { hostViewModel.switchCamera() }
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ItemIcon(
                    R.drawable.ic_live_flip32_light_h,
                    colorFilter = if (isCameraOn) {
                        null
                    } else {
                        ColorFilter.tint(Color(0x33FFFFFF))
                    }
                )
                ItemText(
                    BString.flip,
                    fontColor = if (isCameraOn) Color(0xE6FFFFFF) else Color(0x33FFFFFF)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable { hostViewModel.toggleMicrophone() }
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ItemIcon(
                    if (isMicOn) {
                        R.drawable.ic_live_microphone32_on
                    } else {
                        R.drawable.ic_live_microphone32_off
                    }
                )
                ItemText(if (isMicOn) BString.microphone_on else BString.microphone_off)
            }

            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable { hostViewModel.toggleCamera() }
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ItemIcon(
                    id = if (isCameraOn) {
                        R.drawable.ic_live_camera32_on
                    } else {
                        R.drawable.ic_live_camera32_off
                    }
                )
                ItemText(if (isCameraOn) BString.camera_on else BString.camera_off)
            }

            Column(
                modifier = Modifier
                    .weight(1F)
                    .clickable {
                        UNDER_CONSTRUCTION()
                    }
                    .padding(vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ItemIcon(R.drawable.ic_live_info2)
                ItemText(BString.information)
            }
        }
    }
}

@Composable
private fun ItemIcon(
    @DrawableRes id: Int,
    colorFilter: ColorFilter? = null
) {
    Image(
        painter = painterResource(id),
        contentDescription = null,
        colorFilter = colorFilter,
    )
}

@Composable
private fun ItemText(
    @StringRes id: Int,
    fontColor: Color = Color(0xE6FFFFFF),
) {
    Text(
        text = stringResource(id),
        style = LocalTextStyle.current.copy(
            color = fontColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        ),
        modifier = Modifier
            .padding(top = 8.dp)
    )
}