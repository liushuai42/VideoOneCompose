package com.vertcdemo.solution.interactivelive

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vertcdemo.base.network.data.RTCAppInfoResponse
import com.vertcdemo.base.network.sendEvent
import com.vertcdemo.base.ui.HomeItem
import com.vertcdemo.base.ui.ProgressDialog
import com.vertcdemo.base.utils.CoroutineViewModel
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.solution.interactivelive.core.RTCEngineViewModel
import com.vertcdemo.solution.interactivelive.network.LiveService
import com.vertcdemo.solution.interactivelive.network.data.InteractiveLiveAppInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveSummary
import com.vertcdemo.solution.interactivelive.network.data.LiveSummaryNavType
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgs
import com.vertcdemo.solution.interactivelive.ui.room.RoomArgsNavType
import com.vertcdemo.solution.interactivelive.ui.room.audience.PageRoomAudience
import com.vertcdemo.solution.interactivelive.ui.room.host.LiveFinishReason
import com.vertcdemo.solution.interactivelive.ui.room.host.PageLiveSummary
import com.vertcdemo.solution.interactivelive.ui.room.host.PageRoomHost
import com.vertcdemo.solution.interactivelive.ui.rooms.PageCreateLiveRoom
import com.vertcdemo.solution.interactivelive.ui.rooms.PageRoomList
import com.vertcdemo.solution.interactivelive.ui.rooms.config.PageStreamConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.reflect.typeOf

private const val TAG = "InteractiveLiveEntry"

@Composable
fun InteractiveLiveEntry(modifier: Modifier = Modifier) {
    val viewModel: CoroutineViewModel = viewModel()

    val navController = LocalNavController.current

    HomeItem(
        modifier = modifier,
        title = stringResource(R.string.interactive_live_title),
        description = stringResource(R.string.interactive_live_description),
        backgroundRes = R.drawable.interactive_live_bg_entry,
        onClick = {
            viewModel.launch {
                try {
                    val appInfo = withContext(Dispatchers.Default) {
                        sendEvent<InteractiveLiveAppInfo, RTCAppInfoResponse>(
                            eventName = "getAppInfo",
                            body = InteractiveLiveAppInfo()
                        )
                    }

                    LiveService.appId = appInfo.appId
                    navController.navigate(RouteInteractiveLive(appInfo))
                } catch (e: Exception) {
                    Log.d(TAG, "getAppInfo: error", e)
                }
                viewModel.isLoading = false
            }
        }
    )

    if (viewModel.isLoading) {
        ProgressDialog(
            onDismissRequest = {
                viewModel.cancel()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

interface IRTCAppInfo {
    val appId: String
    val bid: String
}

@Immutable
@Serializable
data class RouteInteractiveLive(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String
) : IRTCAppInfo {
    constructor(info: RTCAppInfoResponse) : this(info.appId, info.bid)
}

@Immutable
@Serializable
data class RouteRoomList(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String
) : IRTCAppInfo

@Serializable
object RouteStreamConfig

@Serializable
data class RouteCreateLiveRoom(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String
) : IRTCAppInfo

@Immutable
@Serializable
data class RouteHost(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String
) : IRTCAppInfo

@Immutable
@Serializable
data class RouteRoomHost(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String,
    @SerialName("args")
    val args: RoomArgs,
) : IRTCAppInfo

@Immutable
@Serializable
data class RouteRoomAudience(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String,
    @SerialName("room_id")
    val roomId: String,
) : IRTCAppInfo

@Serializable
class RouteLiveSummary(
    @SerialName("app_id")
    override val appId: String,
    @SerialName("bid")
    override val bid: String,
    @SerialName("args")
    val args: RoomArgs,
    @SerialName("summary")
    val summary: LiveSummary,
) : IRTCAppInfo {

    constructor(parent: RouteRoomHost, summary: LiveSummary) :
            this(parent.appId, parent.bid, parent.args, summary)
}

fun NavGraphBuilder.interactiveLiveGraph(
    navController: androidx.navigation.NavHostController,
    paddingValues: PaddingValues = PaddingValues(all = 0.dp)
) {
    navigation<RouteInteractiveLive>(
        startDestination = RouteRoomList::class,
    ) {
        composable<RouteRoomList> { backStackEntry ->
            val route = backStackEntry.toRoute<RouteRoomList>()
            PageRoomList(
                paddingValues = paddingValues,
                onBackPressed = { navController.popBackStack() },
                onStreamSettings = { navController.navigate(RouteStreamConfig) },
                onGoLive = { navController.navigate(RouteHost(route.appId, route.bid)) },
                onEnterRoom = { room ->
                    navController.navigate(
                        RouteRoomAudience(
                            route.appId,
                            route.bid,
                            room.roomId,
                        )
                    )
                }
            )
        }
        composable<RouteStreamConfig> {
            PageStreamConfig(
                paddingValues = paddingValues,
                onBackPressed = { navController.popBackStack() },
            )
        }

        navigation<RouteHost>(startDestination = RouteCreateLiveRoom::class) {
            composable<RouteCreateLiveRoom> { backStackEntry ->
                val route = backStackEntry.toRoute<RouteCreateLiveRoom>()

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(RouteHost::class)
                }

                val engine = engineViewModel(parentEntry, route)
                PageCreateLiveRoom(
                    engine = engine,
                    paddingValues = paddingValues,
                    onBackPressed = { navController.popBackStack() },
                    onEnterRoom = { args ->
                        navController.navigate(
                            RouteRoomHost(route.appId, route.bid, args)
                        ) {
                            popUpTo(RouteCreateLiveRoom::class) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable<RouteRoomHost>(
                typeMap = mapOf(typeOf<RoomArgs>() to RoomArgsNavType)
            ) { backStackEntry ->
                val route = backStackEntry.toRoute<RouteRoomHost>()

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(RouteHost::class)
                }

                val engine = engineViewModel(parentEntry, route)

                PageRoomHost(
                    route.args,
                    paddingValues = paddingValues,
                    engine = engine,
                    onEndLive = { reason ->
                        if (reason is LiveFinishReason.End) {
                            navController.navigate(
                                RouteLiveSummary(route, reason.summary)
                            ) {
                                popUpTo(RouteRoomHost::class) {
                                    inclusive = true
                                }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable<RouteLiveSummary>(
                typeMap = mapOf(
                    typeOf<RoomArgs>() to RoomArgsNavType,
                    typeOf<LiveSummary>() to LiveSummaryNavType,
                )
            ) { backStackEntry ->
                val route = backStackEntry.toRoute<RouteLiveSummary>()

                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(RouteHost::class)
                }

                val engine = engineViewModel(parentEntry, route)

                PageLiveSummary(
                    paddingValues = paddingValues,
                    engine = engine,
                    summary = route.summary,
                    onBackPressed = { navController.popBackStack() },
                )
            }
        }

        composable<RouteRoomAudience> { backStackEntry ->
            val route = backStackEntry.toRoute<RouteRoomAudience>()

            val engine = engineViewModel(backStackEntry, route)
            PageRoomAudience(
                paddingValues = paddingValues,
                engine = engine,
                onBackPressed = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun engineViewModel(
    viewModelStoreOwner: ViewModelStoreOwner,
    info: IRTCAppInfo
): RTCEngineViewModel = viewModel<RTCEngineViewModel>(
    viewModelStoreOwner = viewModelStoreOwner,
    factory = RTCEngineViewModel.Factory,
    extras = MutableCreationExtras().apply {
        this[APPLICATION_KEY] = LocalContext.current.applicationContext as Application
        this[RTCEngineViewModel.APP_ID_KEY] = info.appId
        this[RTCEngineViewModel.BID_KEY] = info.bid
    }
)
