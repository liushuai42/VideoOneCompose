package com.vertcdemo.solution.interactivelive.ui.rooms

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.network.data.LiveRoomInfo
import com.vertcdemo.solution.interactivelive.ui.CreativeButton
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.theme.Background
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "PageRoomList"

/**
 * 单主播时，是否使用 LiveCore 推流
 *
 */
private const val LIVE_CORE_MODE = false

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PageRoomList(
    paddingValues: PaddingValues = ZeroPadding,
    onStreamSettings: () -> Unit = {},
    onBackPressed: () -> Unit = {},
    onGoLive: () -> Unit = {},
    onEnterRoom: (LiveRoomInfo) -> Unit = {}
) {
    StatusBarColor(darkIcons = true)

    Box(
        Modifier
            .background(color = Background)
            .paint(
                painter = painterResource(R.drawable.bg_room_list_bottom),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.BottomStart
            )
            .paint(
                painter = painterResource(R.drawable.bg_room_list_header),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopStart
            )
            .fillMaxSize()
    ) {
        if (LIVE_CORE_MODE) {
            Image(
                modifier = Modifier
                    .padding(top = paddingValues.calculateTopPadding())
                    .align(Alignment.TopEnd)
                    .size(44.dp)
                    .clickable { onStreamSettings() },
                painter = painterResource(R.drawable.ic_live_core_settings),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
        }

        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxSize()
        ) {
            Image(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onBackPressed() },
                painter = painterResource(R.drawable.ic_live_arrow_left24),
                colorFilter = ColorFilter.tint(color = Color.Black),
                contentDescription = null,
                contentScale = ContentScale.Inside
            )
            Text(
                modifier = Modifier.padding(start = 40.dp, top = 24.dp),
                text = stringResource(R.string.interact_live_tag),
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Tags()

            RoomList(onEnterRoom)
        }

        val permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
        )

        val context = LocalContext.current

        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { granted ->
            if (granted.values.all { it }) {
                onGoLive()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.go_live_need_permission),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val permissionState = rememberMultiplePermissionsState(permissions)

        CreativeButton(
            onClick = {
                if (permissionState.allPermissionsGranted) {
                    onGoLive()
                } else if (permissionState.shouldShowRationale) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.go_live_need_permission),
                        Toast.LENGTH_LONG
                    ).show()

                    // TODO show a dialog to explain why we need these permissions
                } else {
                    permissionLauncher.launch(permissions.toTypedArray())
                }
            },
            modifier = Modifier
                .padding(bottom = 46.dp)
                .size(width = 152.dp, height = 52.dp)
                .align(Alignment.BottomCenter),
            text = stringResource(R.string.go_live).uppercase(),
        )
    }
}

@Composable
fun Tags() {
    Row(Modifier.padding(start = 40.dp, top = 4.dp)) {
        val tagStyle = TextStyle(
            fontSize = 12.sp,
            color = Color(0xFF49454F),
            fontWeight = FontWeight.Bold
        )
        val tagModifier = Modifier
            .background(
                color = Color(0x0D161823),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
        Text(
            text = stringResource(R.string.host_pk_tag),
            modifier = tagModifier,
            style = tagStyle
        )
        Text(
            text = stringResource(R.string.co_host_tag),
            modifier = Modifier.padding(start = 6.dp) then tagModifier,
            style = tagStyle
        )
        Text(
            text = stringResource(R.string.gift_tag),
            modifier = Modifier.padding(start = 6.dp) then tagModifier,
            style = tagStyle
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomList(onEnterRoom: (LiveRoomInfo) -> Unit = {}) {
    val viewModel: RoomListViewModel = viewModel()
    val isLoading = viewModel.isLoading.collectAsState()
    val rooms = viewModel.rooms.collectAsState()

    PullToRefreshBox(
        isRefreshing = isLoading.value,
        onRefresh = { viewModel.requestRoomList() },
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxSize(),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (rooms.value.isEmpty()) {
                item(key = "no_room", contentType = "no_room") {
                    NoRoom()
                }
            } else {
                items(
                    items = rooms.value,
                    key = { it.roomId },
                    contentType = { "room_item" },
                ) { room ->
                    RoomItem(room, onEnterRoom)
                }


                item(key = "bottom_spacer", contentType = "bottom_spacer") {
                    Spacer(Modifier.height(98.dp))
                }
            }
        }
    }
}

@Composable
fun LazyItemScope.NoRoom() {
    Column(
        modifier = Modifier
            .fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .size(100.dp),
            painter = painterResource(R.drawable.ic_guest_alert),
            contentDescription = null,
            contentScale = ContentScale.Inside
        )
        Text(
            text = stringResource(R.string.no_host_live),
            style = TextStyle(
                fontSize = 16.sp,
                color = Color(0xFF161823),
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun RoomItem(room: LiveRoomInfo, onEnterRoom: (LiveRoomInfo) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(98.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable {
                onEnterRoom(room)
            }
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Image(
            modifier = Modifier
                .padding(start = 2.dp)
                .size(58.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(Avatars[room.hostUserId]),
            contentDescription = null,
            contentScale = ContentScale.Inside
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.live_show_suffix, room.hostUsername),
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )

            Image(
                painter = painterResource(R.drawable.ic_live_status),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 6.dp),
            )
        }
    }
}

@Preview
@Composable
fun PageRoomListPreview() {
    CompositionLocalProvider(
        LocalNavController provides rememberNavController()
    ) {
        PageRoomList()
    }
}

class RoomListViewModel : ViewModel() {
    private val _rooms = MutableStateFlow(emptyList<LiveRoomInfo>())
    val rooms = _rooms.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        requestRoomList()
    }

    fun requestRoomList() {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val result = LiveService.getRoomList()
                _rooms.value = result.rooms
            } catch (e: Exception) {
                Log.d(TAG, "requestRoomList failed", e)
            }
            _isLoading.value = false
        }
    }
}