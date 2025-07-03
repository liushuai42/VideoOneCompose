package com.vertcdemo.solution.interactivelive.network

import com.vertcdemo.base.network.sendEvent
import com.vertcdemo.base.utils.SolutionDataManager
import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.core.annotation.LivePermitType
import com.vertcdemo.solution.interactivelive.core.annotation.MediaStatus
import com.vertcdemo.solution.interactivelive.core.events.MessageBody
import com.vertcdemo.solution.interactivelive.network.data.CreateRoomResponse
import com.vertcdemo.solution.interactivelive.network.data.FinishRoomResponse
import com.vertcdemo.solution.interactivelive.network.data.GetAnchorListResponse
import com.vertcdemo.solution.interactivelive.network.data.GetAudienceListResponse
import com.vertcdemo.solution.interactivelive.network.data.GetRoomListResponse
import com.vertcdemo.solution.interactivelive.network.data.JoinRoomResponse
import com.vertcdemo.solution.interactivelive.network.data.LinkResponse
import com.vertcdemo.solution.interactivelive.network.data.LinkResponseUsers
import com.vertcdemo.solution.interactivelive.network.data.PermitAudienceLinkResponse
import com.vertcdemo.solution.interactivelive.network.data.ReconnectResponse
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

object LiveService {
    var appId: String = ""

    suspend fun clearUser() {
        return sendEvent(CMD_CLEAR_USER, commonParams())
    }

    suspend fun getRoomList(): GetRoomListResponse {
        // Clear timeout users
        clearUser()

        return sendEvent(CMD_GET_ROOM_LIST, commonParams())
    }

    private fun commonParams(): Map<String, JsonElement> = mapOf(
        "app_id" to JsonPrimitive(requireNotNull(appId)),
        "user_id" to JsonPrimitive(SolutionDataManager.userId),
        "device_id" to JsonPrimitive(SolutionDataManager.deviceId)
    )

    private operator fun Map<String, JsonElement>.plus(pair: Pair<String, String>): Map<String, JsonElement> =
        LinkedHashMap(this).apply {
            put(pair.first, JsonPrimitive(pair.second))
        }

    private operator fun Map<String, JsonElement>.plus(map: Map<String, String>): Map<String, JsonElement> =
        LinkedHashMap(this).apply {
            map.forEach { (key, value) -> put(key, JsonPrimitive(value)) }
        }

    suspend fun createRoom(): CreateRoomResponse {
        val params = commonParams() + ("user_name" to SolutionDataManager.userName)

        return sendEvent(CMD_CREATE_LIVE, params)
    }

    suspend fun startLive(roomId: String) {
        val params = commonParams() + ("room_id" to roomId)
        return sendEvent(CMD_START_LIVE, params)
    }

    suspend fun finishLive(roomId: String): FinishRoomResponse {
        val params = commonParams() + ("room_id" to roomId)

        return sendEvent(CMD_FINISH_LIVE, params)
    }


    /**
     * Audience joins a live room.
     *
     * @param roomId   The room id to be joined
     */
    @PendingConfirmed
    suspend fun joinRoom(roomId: String): JoinRoomResponse {
        val params = commonParams() + mapOf(
            "room_id" to roomId,
            "user_name" to SolutionDataManager.userName
        )
        return sendEvent(CMD_JOIN_ROOM, params)
    }

    /**
     * Audience leaves a live room.
     *
     * @param roomId The room id to be leave
     */
    @PendingConfirmed
    suspend fun leaveRoom(roomId: String) {
        val params = commonParams() +
                ("room_id" to roomId)
        return sendEvent(CMD_LEAVE_ROOM, params)
    }

    @PendingConfirmed
    suspend fun reconnect(roomId: String): ReconnectResponse {
        val params = commonParams() +
                ("room_id" to roomId)
        return sendEvent(CMD_LIVE_RECONNECT, params)
    }

    @PendingConfirmed
    suspend fun sendMessage(roomId: String, body: MessageBody) {
        val params = commonParams() + mapOf(
            "room_id" to roomId,
            "message" to json.encodeToString(body)
        )

        return sendEvent(CMD_SEND_MESSAGE, params)
    }

    suspend fun getAnchorList(): GetAnchorListResponse {
        return sendEvent(CMD_GET_ANCHOR_LIST, commonParams())
    }

    @PendingConfirmed
    suspend fun getAudienceList(roomId: String): GetAudienceListResponse {
        val params = commonParams() +
                ("room_id" to roomId)
        return sendEvent(CMD_GET_AUDIENCE_LIST, params)
    }

    @PendingConfirmed
    suspend fun manageGuestMedia(
        hostRoomId: String, hostUserId: String,
        guestRoomId: String, guestUserId: String,
        @MediaStatus camera: Int,
        @MediaStatus mic: Int
    ) {
        val params = commonParams() + mapOf(
            "host_room_id" to JsonPrimitive(hostRoomId),
            "host_user_id" to JsonPrimitive(hostUserId),
            "guest_room_id" to JsonPrimitive(guestRoomId),
            "guest_user_id" to JsonPrimitive(guestUserId),
            "mic" to JsonPrimitive(mic),
            "camera" to JsonPrimitive(camera),
        )

        return sendEvent(CMD_MANAGE_GUEST_MEDIA, params)
    }

    @PendingConfirmed
    suspend fun updateMediaStatus(
        roomId: String,
        @MediaStatus micStatus: Int,
        @MediaStatus cameraStatus: Int,
    ) {
        val params = commonParams() + mapOf(
            "room_id" to JsonPrimitive(roomId),
            "mic" to JsonPrimitive(micStatus),
            "camera" to JsonPrimitive(cameraStatus),
        )
        return sendEvent(CMD_UPDATE_MEDIA_STATUS, params)
    }

    @PendingConfirmed
    suspend fun updateResolution(roomId: String?, width: Int, height: Int) {
        val params = commonParams() + mapOf(
            "room_id" to JsonPrimitive(roomId),
            "width" to JsonPrimitive(width),
            "height" to JsonPrimitive(height),
        )
        return sendEvent(CMD_UPDATE_RESOLUTION, params)
    }

    @PendingConfirmed
    suspend fun inviteAudienceLink(
        hostRoomId: String, hostUserId: String,
        audienceRoomId: String, audienceUserId: String,
        extra: String,
    ): LinkResponse {
        val params = commonParams() + mapOf(
            "host_room_id" to hostRoomId,
            "host_user_id" to hostUserId,
            "audience_room_id" to audienceRoomId,
            "audience_user_id" to audienceUserId,
            "extra" to extra,
        )

        return sendEvent(CMD_AUDIENCE_LINK_MIC_INVITE, params)
    }

    /**
     * 主播同意或拒绝观众的连麦请求
     */
    suspend fun permitAudienceLink(
        linkerId: String,
        hostRoomId: String, hostUserId: String,
        audienceRoomId: String, audienceUserId: String,
        permitType: Int,
    ): PermitAudienceLinkResponse {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "host_room_id" to JsonPrimitive(hostRoomId),
            "host_user_id" to JsonPrimitive(hostUserId),
            "audience_room_id" to JsonPrimitive(audienceRoomId),
            "audience_user_id" to JsonPrimitive(audienceUserId),
            "permit_type" to JsonPrimitive(permitType),
        )

        return sendEvent(CMD_AUDIENCE_LINK_MIC_PERMIT, params)
    }

    @PendingConfirmed
    suspend fun kickAudienceLink(
        hostRoomId: String, hostUserId: String,
        audienceRoomId: String, audienceUserId: String
    ) {
        val params = commonParams() + mapOf(
            "host_room_id" to JsonPrimitive(hostRoomId),
            "host_user_id" to JsonPrimitive(hostUserId),
            "audience_room_id" to JsonPrimitive(audienceRoomId),
            "audience_user_id" to JsonPrimitive(audienceUserId),
        )


        return sendEvent(CMD_AUDIENCE_LINK_MIC_KICK, params)
    }

    @PendingConfirmed
    suspend fun finishAudienceLink(roomId: String) {
        val params = commonParams() +
                ("room_id" to roomId)

        return sendEvent(CMD_AUDIENCE_LINK_MIC_FINISH, params)
    }

    @PendingConfirmed
    suspend fun applyAudienceLink(roomId: String): LinkResponse {
        val params = commonParams() +
                ("room_id" to roomId)

        return sendEvent(CMD_AUDIENCE_LINK_MIC_APPLY, params)
    }

    /**
     * 观众回复主播的连麦邀请
     *
     * Note: 目前此功能未实现 UI 交互
     */
    @PendingConfirmed
    suspend fun replyAudienceLink(
        linkerId: String,
        roomId: String,
        replyType: Int,
    ): LinkResponse {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "room_id" to JsonPrimitive(roomId),
            "reply_type" to JsonPrimitive(replyType),
        )

        return sendEvent(CMD_AUDIENCE_LINK_MIC_REPLY, params)
    }

    @PendingConfirmed
    suspend fun leaveAudienceLink(linkerId: String, roomId: String) {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "room_id" to JsonPrimitive(roomId),
        )

        return sendEvent(CMD_AUDIENCE_LINK_MIC_LEAVE, params)
    }

    @PendingConfirmed
    suspend fun cancelAudienceLink(linkerId: String, roomId: String) {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "room_id" to JsonPrimitive(roomId),
        )

        return sendEvent(CMD_AUDIENCE_LINK_MIC_CANCEL, params)
    }

    suspend fun inviteAnchorLink(
        inviterRoomId: String, inviterUserId: String,
        inviteeRoomId: String, inviteeUserId: String,
        extra: String,
    ): LinkResponse {
        val params = commonParams() + mapOf(
            "inviter_room_id" to inviterRoomId,
            "inviter_user_id" to inviterUserId,
            "invitee_room_id" to inviteeRoomId,
            "invitee_user_id" to inviteeUserId,
            "extra" to extra,
        )

        return sendEvent(CMD_ANCHOR_LINK_MIC_INVITE, params)
    }

    suspend fun replyAnchorLinkInvite(
        linkerId: String,
        inviterRoomId: String, inviterUserId: String,
        inviteeRoomId: String, inviteeUserId: String,
        @LivePermitType replyType: Int
    ): LinkResponseUsers {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "inviter_room_id" to JsonPrimitive(inviterRoomId),
            "inviter_user_id" to JsonPrimitive(inviterUserId),
            "invitee_room_id" to JsonPrimitive(inviteeRoomId),
            "invitee_user_id" to JsonPrimitive(inviteeUserId),
            "reply_type" to JsonPrimitive(replyType),
        )

        return sendEvent(CMD_ANCHOR_LINK_MIC_REPLY, params)
    }

    suspend fun finishAnchorLink(linkerId: String, roomId: String) {
        val params = commonParams() + mapOf(
            "linker_id" to JsonPrimitive(linkerId),
            "room_id" to JsonPrimitive(roomId),
        )

        return sendEvent(CMD_ANCHOR_LINK_MIC_FINISH, params)
    }

    private const val CMD_CLEAR_USER = "liveClearUser"
    private const val CMD_GET_ROOM_LIST = "liveGetActiveLiveRoomList"

    private const val CMD_CREATE_LIVE = "liveCreateLive"
    private const val CMD_START_LIVE = "liveStartLive"
    private const val CMD_FINISH_LIVE = "liveFinishLive"

    private const val CMD_JOIN_ROOM = "liveJoinLiveRoom"
    private const val CMD_LEAVE_ROOM = "liveLeaveLiveRoom"

    private const val CMD_LIVE_RECONNECT = "liveReconnect"

    private const val CMD_SEND_MESSAGE = "liveSendMessage"

    private const val CMD_GET_ANCHOR_LIST = "liveGetActiveAnchorList"
    private const val CMD_GET_AUDIENCE_LIST = "liveGetAudienceList"

    private const val CMD_MANAGE_GUEST_MEDIA = "liveManageGuestMedia"
    private const val CMD_UPDATE_MEDIA_STATUS = "liveUpdateMediaStatus"
    private const val CMD_UPDATE_RESOLUTION = "liveUpdateResolution"

    private const val CMD_AUDIENCE_LINK_MIC_INVITE = "liveAudienceLinkmicInvite"
    private const val CMD_AUDIENCE_LINK_MIC_PERMIT = "liveAudienceLinkmicPermit"
    private const val CMD_AUDIENCE_LINK_MIC_KICK = "liveAudienceLinkmicKick"
    private const val CMD_AUDIENCE_LINK_MIC_FINISH = "liveAudienceLinkmicFinish"
    private const val CMD_AUDIENCE_LINK_MIC_APPLY = "liveAudienceLinkmicApply"
    private const val CMD_AUDIENCE_LINK_MIC_REPLY = "liveAudienceLinkmicReply"
    private const val CMD_AUDIENCE_LINK_MIC_LEAVE = "liveAudienceLinkmicLeave"
    private const val CMD_AUDIENCE_LINK_MIC_CANCEL = "liveAudienceLinkmicCancel"

    private const val CMD_ANCHOR_LINK_MIC_INVITE = "liveAnchorLinkmicInvite"
    private const val CMD_ANCHOR_LINK_MIC_REPLY = "liveAnchorLinkmicReply"
    private const val CMD_ANCHOR_LINK_MIC_FINISH = "liveAnchorLinkmicFinish"
}

/**
 * API not used yet, Need to be confirmed
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class PendingConfirmed(val message: String = "")