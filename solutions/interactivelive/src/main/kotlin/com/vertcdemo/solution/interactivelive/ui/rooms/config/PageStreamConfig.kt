package com.vertcdemo.solution.interactivelive.ui.rooms.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.theme.Background
import com.vertcdemo.solution.interactivelive.utils.BString
import com.vertcdemo.solution.interactivelive.utils.LiveCoreConfig
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun PageStreamConfig(
    paddingValues: PaddingValues = ZeroPadding,
    onBackPressed: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxWidth()
    ) {
        Box {
            Image(
                painter = painterResource(R.drawable.ic_live_arrow_left24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.Black),
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onBackPressed() },
                contentScale = ContentScale.Inside
            )

            Text(
                text = stringResource(BString.settings),
                style = TextStyle(
                    color = Color(0xFF0C0D0E),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .wrapContentWidth()
                    .wrapContentHeight(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 16.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            val viewModel: IStreamConfig = if (LocalInspectionMode.current) {
                EmptyStreamConfig
            } else {
                viewModel<StreamConfigViewModel>()
            }

            val rtmPullStreaming = viewModel.rtmPullStreaming.collectAsState()
            val abr = viewModel.abr.collectAsState()
            StreamSwitch(
                title = stringResource(R.string.rtm_pull_streaming),
                description = stringResource(R.string.rtm_pull_streaming_description),
                state = rtmPullStreaming,
                onCheckedChanged = {
                    viewModel.setRTMPullStreaming(it)
                }
            )
            StreamSwitch(
                title = stringResource(R.string.abr),
                description = stringResource(R.string.abr_description),
                state = abr,
                onCheckedChanged = {
                    viewModel.setABR(it)
                }
            )
        }
    }
}

@Composable
fun StreamSwitch(
    title: String,
    description: String,
    state: State<Boolean>,
    onCheckedChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color(0xFF0C0D0E),
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                text = description,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color(0xFF737A87)
                )
            )
        }

        Switch(
            checked = state.value,
            onCheckedChange = { onCheckedChanged(it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1CB267),
                checkedBorderColor = Color.Transparent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFCCCED0),
                uncheckedBorderColor = Color.Transparent,
            ),
            modifier = Modifier
                .align(Alignment.CenterVertically)
        )
    }
}

internal interface IStreamConfig {
    val rtmPullStreaming: MutableStateFlow<Boolean>
    val abr: MutableStateFlow<Boolean>
    fun setRTMPullStreaming(value: Boolean)
    fun setABR(value: Boolean)
}

internal object EmptyStreamConfig : IStreamConfig {
    override val rtmPullStreaming = MutableStateFlow(false)
    override val abr = MutableStateFlow(false)

    override fun setRTMPullStreaming(value: Boolean) {
    }

    override fun setABR(value: Boolean) {
    }
}

internal class StreamConfigViewModel : ViewModel(), IStreamConfig {
    override val rtmPullStreaming = MutableStateFlow(LiveCoreConfig.rtmPullStreaming)
    override val abr = MutableStateFlow(LiveCoreConfig.abr)

    override fun setRTMPullStreaming(value: Boolean) {
        rtmPullStreaming.value = value
        LiveCoreConfig.rtmPullStreaming = value

        if (value) {
            // ABR & RTM Pull Streaming Can't be enabled at the same time
            setABR(false)
        }
    }

    override fun setABR(value: Boolean) {
        abr.value = value
        LiveCoreConfig.abr = value
        if (value) {
            // ABR & RTM Pull Streaming Can't be enabled at the same time
            setRTMPullStreaming(false)
        }
    }
}

@Preview
@Composable
fun PageStreamConfigPreview() {
    CompositionLocalProvider(LocalNavController provides rememberNavController()) {
        PageStreamConfig()
    }
}