package com.vertcdemo.solution.interactivelive.ui.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGesturesPadding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.utils.BString
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RTCSettingsDialog(
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest() },
        containerColor = Color.Black,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = {},
    ) {
        val viewModel: RTCSettingsViewModel = viewModel()

        val fps = viewModel.fps.collectAsState()
        val resolution = viewModel.resolution.collectAsState()
        val bitrate = viewModel.bitrate.collectAsState()

        Column {
            Text(
                text = stringResource(BString.settings),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(51.dp)
                    .wrapContentHeight(),
            )
            HorizontalDivider(thickness = .5.dp, color = Color(0x26FFFFFF))
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.video_fps),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1.0F)
                        .wrapContentHeight()
                )

                SwitchButtonX(
                    *FPS_VALUES,
                    index = if (fps.value == 15) 0 else 1,
                    onClick = { index ->
                        viewModel.setFPS(if (index == 0) 15 else 20)
                    }
                )
            }
            Row(
                modifier = Modifier
                    .height(52.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.video_quality),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1.0F)
                        .wrapContentHeight()
                )

                SwitchButtonX(
                    *RESOLUTION_VALUES,
                    index = when (resolution.value) {
                        Resolution.W540 -> 0
                        Resolution.W720 -> 1
                        else -> 2
                    },
                    onClick = { index ->
                        viewModel.setResolution(
                            when (index) {
                                0 -> Resolution.W540
                                1 -> Resolution.W720
                                else -> Resolution.W1080
                            }
                        )
                    }
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.video_bitrate),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .weight(1.0F)
                        .wrapContentHeight()
                )

                Text(
                    text = "${bitrate.value}",
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier
                        .wrapContentHeight()
                )

                Text(
                    text = stringResource(R.string.bitrate_kbps),
                    style = TextStyle(
                        color = Color(0xFF80838A),
                        fontSize = 14.sp,
                    ),
                    modifier = Modifier.padding(start = 3.dp)
                )
            }

            val colors = SliderDefaults.colors(
                thumbColor = Color(0xFFFF1764),
                activeTrackColor = Color(0xFFFF1764),
            )

            Slider(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .safeGesturesPadding()
                    .padding(horizontal = 16.dp),
                value = bitrate.value.toFloat(),
                onValueChange = {
                    viewModel.setBitrate(it.roundToInt())
                },
                onValueChangeFinished = {
                    viewModel.commitBitrate()
                },
                colors = colors,
                valueRange = resolution.value.bitrateRangeFloat(),
                track = { sliderState ->
                    SliderDefaults.Track(
                        colors = colors,
                        sliderState = sliderState,
                        drawStopIndicator = {},
                        drawTick = { _, _ -> },
                    )
                }
            )

            Spacer(
                modifier = Modifier
                    .height(48.dp)
            )
        }
    }
}


@Composable
private fun SwitchButtonX(
    vararg items: String,
    index: Int,
    onClick: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFF41464F),
                shape = CircleShape,
            )
            .height(38.dp)
            .padding(all = 3.dp)
    ) {
        val styleSelected = TextStyle(
            color = Color(0xFFFF1759),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        val styleNormal = TextStyle(
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        items.forEachIndexed { i, text ->
            Text(
                text = text,
                style = if (i == index) styleSelected else styleNormal,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .sizeIn(minWidth = 48.dp)
                    .height(32.dp)
                    .background(
                        color = if (i == index) Color.White else Color.Transparent,
                        shape = RoundedCornerShape(32.dp)
                    )
                    .wrapContentHeight()
                    .clickable {
                        onClick(i)
                    },
            )
        }
    }
}

enum class Resolution(
    val bitrateMin: Int,
    val bitrateMax: Int,
) {
    W1080(
        bitrateMin = 1000,
        bitrateMax = 3800
    ),
    W720(
        bitrateMin = 800,
        bitrateMax = 1900
    ),
    W540(
        bitrateMin = 500,
        bitrateMax = 1520,
    );

    fun bitrateRangeFloat(): ClosedFloatingPointRange<Float> {
        return bitrateMin.toFloat()..bitrateMax.toFloat()
    }
}

val FPS_VALUES = arrayOf("15", "20")

val RESOLUTION_VALUES = arrayOf(
    "540p",
    "720p",
    "1080p",
)

class RTCSettingsViewModel : ViewModel() {
    private val _fps = MutableStateFlow(15)
    val fps = _fps.asStateFlow()

    fun setFPS(fps: Int) {
        _fps.value = fps
    }

    private val _resolution = MutableStateFlow(Resolution.W720)
    val resolution = _resolution.asStateFlow()

    fun setResolution(resolution: Resolution) {
        _resolution.value = resolution

        if (_bitrate.value > resolution.bitrateMax) {
            _bitrate.value = resolution.bitrateMax
        } else if (_bitrate.value < resolution.bitrateMin) {
            _bitrate.value = resolution.bitrateMin
        }
    }

    private val _bitrate = MutableStateFlow(1600)
    val bitrate = _bitrate.asStateFlow()

    fun setBitrate(bitrate: Int) {
        _bitrate.value = bitrate
    }

    fun commitBitrate() {

    }
}

@Preview
@Composable
fun RTCSettingsDialogPreview() {
    var state by remember { mutableStateOf(true) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        if (state) {
            RTCSettingsDialog(
                onDismissRequest = {
                    state = false
                }
            )
        }
    }
}