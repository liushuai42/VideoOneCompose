package com.vertcdemo.solution.interactivelive.rtc

import android.util.Log
import androidx.annotation.CallSuper
import com.ss.bytertc.engine.handler.IRTCRoomEventHandler
import com.ss.bytertc.engine.handler.IRTCVideoEventHandler
import com.vertcdemo.solution.interactivelive.core.events.LiveServerEventConverter
import org.json.JSONException
import org.json.JSONObject


const val UID_SERVER = "server"
const val MESSAGE_TYPE_INFORM = "inform"

private const val TAG = "RTSEventHandlerRTS"

open class RTCVideoEventHandlerRTS() :
    IRTCVideoEventHandler() {
    private val eventConsumer = LiveServerEventConverter()

    @CallSuper
    override fun onUserMessageReceivedOutsideRoom(uid: String, message: String) {
        if (UID_SERVER == uid) {
            eventConsumer.onServerEvent(message)
        }
    }
}

open class RTCRoomEventHandlerRTS(
    private val notifyReconnect: Boolean = false
) : IRTCRoomEventHandler() {

    private val eventConsumer = LiveServerEventConverter()

    protected fun isFirstJoinRoomSuccess(state: Int, extraInfo: String): Boolean {
        return joinRoomType(extraInfo) == 0 && state == 0
    }

    protected fun isReconnectSuccess(state: Int, extraInfo: String): Boolean {
        return joinRoomType(extraInfo) == 1 && state == 0
    }

    protected fun joinRoomType(extraInfo: String): Int =
        try {
            val body = JSONObject(extraInfo)
            body.optInt("join_type", -1)
        } catch (e: JSONException) {
            Log.e(TAG, "joinRoomType: extraInfo parse error", e)
            -1
        }

    @CallSuper
    override fun onRoomMessageReceived(uid: String, message: String) {
        if (UID_SERVER == uid) {
            eventConsumer.onServerEvent(message)
        }
    }

    @CallSuper
    override fun onUserMessageReceived(uid: String, message: String) {
        if (UID_SERVER == uid) {
            eventConsumer.onServerEvent(message)
        }
    }

    @CallSuper
    override fun onRoomStateChanged(roomId: String, uid: String, state: Int, extraInfo: String) {
//        if (state == ErrorCode.ERROR_CODE_DUPLICATE_LOGIN) {
//            SolutionEventBus.post(RTSLogoutEvent())
//        }
//        if (mNotifyReconnect && isReconnectSuccess(state, extraInfo)) {
//            SolutionEventBus.post(RTCReconnectToRoomEvent(roomId, uid, state, extraInfo))
//        }
    }
}
