package com.vertcdemo.solution.interactivelive.ui.rooms

import android.util.Log
import android.view.SurfaceView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.base.LocalCredential
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.ui.ProgressDialog
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.EmptyEngine
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.ui.CreativeButton
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.liveBackground
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.host.DialogState
import com.vertcdemo.solution.interactivelive.ui.room.host.IDialogState
import com.vertcdemo.solution.interactivelive.utils.BString
import com.vertcdemo.solution.interactivelive.utils.UNDER_CONSTRUCTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PageCreateLiveRoom"
private const val EXPERIENCE_MINUTES = 20

@Composable
fun PageCreateLiveRoom(
    paddingValues: PaddingValues = ZeroPadding,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
    viewModel: CreateLiveRoomViewModel = viewModel(),
    onBackPressed: () -> Unit = {},
    onEnterRoom: (RoomArgs) -> Unit = {},
) {
    StatusBarColor(darkIcons = false)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .liveBackground(),
        constraintSet = ConstraintSet {
            val surface = createRefFor("surface")
            constrain(surface) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)

                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }

            val close = createRefFor("close")
            constrain(close) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }

            val (titleRow, timeTipsRow) = createRefsFor("title_row", "time_tips_row")
            constrain(titleRow) {
                top.linkTo(close.bottom)
                start.linkTo(parent.start, margin = 40.dp)
                end.linkTo(parent.end, margin = 40.dp)

                width = Dimension.fillToConstraints
            }

            constrain(timeTipsRow) {
                top.linkTo(titleRow.bottom, margin = 6.dp)
                start.linkTo(titleRow.start)
                end.linkTo(titleRow.end)

                width = Dimension.fillToConstraints
            }

            val (startButton, actions) = createRefsFor("start", "actions")
            constrain(startButton) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 46.dp)
            }

            constrain(actions) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(startButton.top, margin = 32.dp)
            }
        }
    ) {
        AndroidView(
            modifier = Modifier
                .layoutId("surface"),
            factory = ::SurfaceView,
            update = engine::setLocalVideoView
        )

        val (userId, userName) = if (LocalInspectionMode.current) {
            mutableStateOf("42") to mutableStateOf("John Doe")
        } else {
            val credential = LocalCredential.current

            credential.userId.collectAsState() to credential.userName.collectAsState()
        }

        Image(
            painter = painterResource(id = R.drawable.ic_live_close),
            contentDescription = null,
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .layoutId("close")
                .size(44.dp)
                .clickable(onClick = onBackPressed),
            contentScale = ContentScale.Inside,
        )

        Row(
            modifier = Modifier
                .layoutId("title_row")
                .height(68.dp)
                .background(color = Color(0x33000000), shape = RoundedCornerShape(8.dp))
        ) {
            Image(
                painter = painterResource(id = Avatars[userId.value]),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 6.dp)
                    .size(56.dp)
                    .clip(shape = RoundedCornerShape(8.dp)),
            )

            Text(
                text = stringResource(R.string.live_show_suffix, userName.value),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 12.dp, top = 10.dp)
            )
        }

        Row(
            modifier = Modifier
                .background(color = Color(0x33000000), shape = RoundedCornerShape(8.dp))
                .layoutId("time_tips_row")
                .padding(all = 5.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_live_alert16),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(
                    R.string.application_experiencing_xxx_title,
                    EXPERIENCE_MINUTES
                ),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 6.dp),
            )
        }

        Row(
            modifier = Modifier
                .layoutId("actions"),
            horizontalArrangement = Arrangement.Center
        ) {
            ActionItem(
                icon = R.drawable.ic_live_flip32_h,
                text = R.string.camera_flip,
                onClick = {
                    engine.switchCamera()
                }
            )
            ActionItem(
                icon = R.drawable.ic_live_beauty32,
                text = R.string.effects,
                onClick = { UNDER_CONSTRUCTION() }
            )
            ActionItem(
                icon = R.drawable.ic_live_settings32,
                text = BString.settings,
                onClick = {
                    viewModel.settings.show()
                }
            )
        }

        CreativeButton(
            onClick = { viewModel.startLive() },
            modifier = Modifier
                .padding(bottom = paddingValues.calculateBottomPadding())
                .layoutId("start")
                .size(width = 300.dp, height = 52.dp),
            text = stringResource(R.string.start_live).uppercase()
        )

        val showLoading by viewModel.loading.collectAsState()
        if (showLoading) {
            ProgressDialog(
                onDismissRequest = { viewModel.loading.dismiss() },
                properties = DialogProperties(
                    dismissOnClickOutside = false
                )
            )
        }

        val countDown by viewModel.countDown.collectAsState()
        if (countDown > 0) {
            CountDownTips(countDown)
        }

        val settings by viewModel.settings.collectAsState()
        if (settings) {
            RTCSettingsDialog(onDismissRequest = {
                viewModel.settings.dismiss()
            })
        }

        val uiState = viewModel.uiState.collectAsState().value
        if (uiState is UiState.Next) {
            onEnterRoom(uiState.args)
        }

        LaunchedEffect("boot") {
            engine.startVideoCapture()
        }
    }
}

@Composable
private fun ActionItem(
    @DrawableRes icon: Int,
    @StringRes text: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .size(width = 80.dp, height = 60.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
        Text(
            text = stringResource(id = text),
            style = TextStyle(
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            ),
        )
    }
}

@Composable
private fun CountDownTips(number: Int = 3) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false,
        )
    ) {
        (LocalView.current.parent as DialogWindowProvider).window.setDimAmount(0F)

        Box(
            modifier = Modifier
                .background(
                    color = Color(0x80000000), shape = CircleShape
                )
                .size(100.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$number",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
            )
        }
    }
}

class CreateLiveRoomViewModel : ViewModel() {
    val settings = DialogState()
    val loading: IDialogState = DialogState()

    private val _countDown = MutableStateFlow(-1)
    val countDown = _countDown.asStateFlow()

    fun startLive() {
        viewModelScope.launch(Dispatchers.Default) {
            loading.show()

            try {
                val response = LiveService.createRoom()
                val args = response.toRoomArgs()

                loading.dismiss()

                for (i in 3 downTo 1) {
                    _countDown.value = i
                    if (i != 1) {
                        delay(1000L)
                    }
                }
                LiveService.startLive(args.rtsRoomId)
                _countDown.value = 0
                _uiState.value = UiState.Next(args)
            } catch (e: Exception) {
                Log.e(TAG, "startLive: ", e)
            } finally {
                loading.dismiss()
            }
        }
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Normal)
    val uiState = _uiState.asStateFlow()
}

sealed interface UiState {
    object Normal : UiState
    class Next(val args: RoomArgs) : UiState
}
