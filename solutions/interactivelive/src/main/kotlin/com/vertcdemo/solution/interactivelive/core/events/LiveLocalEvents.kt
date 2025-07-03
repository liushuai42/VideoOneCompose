package com.vertcdemo.solution.interactivelive.core.events

import com.ss.bytertc.engine.data.ForwardStreamInfo
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo

/**
 * 本地事件，用于通知接受了主播的连麦邀请
 */
data class AnchorLinkAcceptedEvent(
    val linkerId: String,
    val forwardInfo: ForwardInfo,
    val rtcUserList: List<LiveUserInfo>,
)


typealias ForwardInfo = ForwardStreamInfo
