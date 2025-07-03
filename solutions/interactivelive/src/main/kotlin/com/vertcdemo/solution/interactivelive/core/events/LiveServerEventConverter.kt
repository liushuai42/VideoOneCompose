package com.vertcdemo.solution.interactivelive.core.events

import android.util.Log
import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.bus.LiveEventBus
import com.vertcdemo.solution.interactivelive.rtc.MESSAGE_TYPE_INFORM
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import org.json.JSONException
import org.json.JSONObject

const val ON_AUDIENCE_JOIN_ROOM = "liveOnAudienceJoinRoom"
const val ON_AUDIENCE_LEAVE_ROOM = "liveOnAudienceLeaveRoom"
const val ON_FINISH_LIVE = "liveOnFinishLive"
const val ON_LINK_MIC_STATUS = "liveOnLinkmicStatus"
const val ON_AUDIENCE_LINK_MIC_JOIN = "liveOnAudienceLinkmicJoin"
const val ON_AUDIENCE_LINK_MIC_LEAVE = "liveOnAudienceLinkmicLeave"
const val ON_AUDIENCE_LINK_MIC_FINISH = "liveOnAudienceLinkmicFinish"
const val ON_MEDIA_CHANGE = "liveOnMediaChange"
const val ON_AUDIENCE_LINK_MIC_INVITE = "liveOnAudienceLinkmicInvite"
const val ON_AUDIENCE_LINK_MIC_APPLY = "liveOnAudienceLinkmicApply"
const val ON_AUDIENCE_LINK_MIC_CANCEL = "liveOnAudienceLinkmicCancel"
const val ON_AUDIENCE_LINK_MIC_REPLY = "liveOnAudienceLinkmicReply"
const val ON_AUDIENCE_LINK_MIC_PERMIT = "liveOnAudienceLinkmicPermit"
const val ON_AUDIENCE_LINK_MIC_KICK = "liveOnAudienceLinkmicKick"
const val ON_ANCHOR_LINK_MIC_INVITE = "liveOnAnchorLinkmicInvite"
const val ON_ANCHOR_LINK_MIC_REPLY = "liveOnAnchorLinkmicReply"
const val ON_ANCHOR_LINK_MIC_FINISH = "liveOnAnchorLinkmicFinish"
const val ON_MANAGER_GUEST_MEDIA = "liveOnManageGuestMedia"
const val ON_MESSAGE_SEND = "liveOnMessageSend"

private const val TAG = "LiveRTSEventConverter"


class LiveServerEventConverter {
    private fun mapEvent(eventKey: String, eventData: String) {
        when (eventKey) {
            ON_AUDIENCE_JOIN_ROOM -> {
                broadcast(json.decodeFromString<LiveRTSUserEvent.Join>(eventData))
            }

            ON_AUDIENCE_LEAVE_ROOM -> {
                broadcast(json.decodeFromString<LiveRTSUserEvent.Leave>(eventData))
            }

            ON_FINISH_LIVE -> {
                broadcast(json.decodeFromString<LiveFinishEvent>(eventData))
            }

            ON_LINK_MIC_STATUS -> {
                broadcast(json.decodeFromString<RoomStatusEvent>(eventData))
            }

            ON_AUDIENCE_LINK_MIC_JOIN -> {
                broadcast(
                    json.decodeFromString<AudienceLinkStatusEvent.Join>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_LEAVE -> {
                broadcast(
                    json.decodeFromString<AudienceLinkStatusEvent.Leave>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_FINISH -> {
                broadcast(
                    json.decodeFromString<AudienceLinkFinishEvent>(
                        eventData
                    )
                )
            }

            ON_MEDIA_CHANGE -> {
                broadcast(json.decodeFromString<UserMediaChangedEvent>(eventData))
            }

            ON_AUDIENCE_LINK_MIC_INVITE -> {
                broadcast(
                    json.decodeFromString<AudienceLinkInviteEvent>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_APPLY -> {
                broadcast(
                    json.decodeFromString<AudienceLinkApplyEvent>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_CANCEL -> {
                broadcast(
                    json.decodeFromString<AudienceLinkCancelEvent>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_REPLY -> {
                broadcast(
                    json.decodeFromString<AudienceLinkReplyEvent>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_PERMIT -> {
                broadcast(
                    json.decodeFromString<AudienceLinkPermitEvent>(
                        eventData
                    )
                )
            }

            ON_AUDIENCE_LINK_MIC_KICK -> {
                broadcast(json.decodeFromString<AudienceLinkKickEvent>(eventData))
            }

            ON_ANCHOR_LINK_MIC_INVITE -> {
                broadcast(json.decodeFromString<AnchorLinkInviteEvent>(eventData))
            }

            ON_ANCHOR_LINK_MIC_REPLY -> {
                broadcast(json.decodeFromString<AnchorLinkReplyEvent>(eventData))
            }

            ON_ANCHOR_LINK_MIC_FINISH -> {
                broadcast(json.decodeFromString<AnchorLinkFinishEvent>(eventData))
            }

            ON_MANAGER_GUEST_MEDIA -> {
                broadcast(json.decodeFromString<UserMediaControlEvent>(eventData))
            }

            ON_MESSAGE_SEND -> {
                broadcast(json.decodeFromString<MessageEvent>(eventData))
            }

            else -> {
                Log.w(TAG, "Skip unknown eventKey: $eventKey")
            }
        }
    }

    private fun broadcast(event: Any) {
        LiveEventBus.post(event)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun onServerEvent(message: String) {
        Log.d(TAG, "onMessageReceived: $message")
        try {
            val body = JSONObject(message)
            val messageType = body.optString("message_type")
            if (MESSAGE_TYPE_INFORM == messageType) {
                val eventKey = body.optString("event")
                val eventData = body.optString("data")
                if (eventKey.isNullOrEmpty() || eventData.isNullOrEmpty()) {
                    Log.e(TAG, "onMessageReceived: Discard invalid inform message!")
                    return
                }

                mapEvent(eventKey, eventData)
            } else {
                Log.e(TAG, "onMessageReceived: Discard non-inform message!")
            }
        } catch (e: JSONException) {
            Log.e(TAG, "onMessageReceived: failed to parse!", e)
        } catch (e: SerializationException) {
            Log.e(TAG, "onMessageReceived: failed to parse!", e)
        }
    }
}
