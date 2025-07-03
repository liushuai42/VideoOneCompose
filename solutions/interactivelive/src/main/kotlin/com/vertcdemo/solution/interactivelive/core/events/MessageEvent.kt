package com.vertcdemo.solution.interactivelive.core.events

import com.vertcdemo.base.utils.json
import com.vertcdemo.solution.interactivelive.core.annotation.GiftType
import com.vertcdemo.solution.interactivelive.core.annotation.MessageType
import com.vertcdemo.solution.interactivelive.network.data.LiveUserInfo
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageEvent(
    @SerialName("user") val user: LiveUserInfo,
    @SerialName("message") val message: String,
) {
    val body: MessageBody by lazy {
        json.decodeFromString(message)
    }
}

@Serializable
data class MessageBody(
    @MessageType
    @SerialName("type") val type: Int,
    @EncodeDefault(mode = EncodeDefault.Mode.NEVER)
    @SerialName("content") val content: String? = null,
    @GiftType
    @EncodeDefault(mode = EncodeDefault.Mode.NEVER)
    @SerialName("giftType") val giftType: Int = 0,
    @EncodeDefault(mode = EncodeDefault.Mode.NEVER)
    @SerialName("count") val count: Int = 0,
) {
    companion object {
        val Like = MessageBody(type = MessageType.LIKE)

        val GiftLike = MessageBody(type = MessageType.GIFT, giftType = GiftType.LIKE)

        val GiftSugar = MessageBody(type = MessageType.GIFT, giftType = GiftType.SUGAR)

        val GiftDiamond = MessageBody(type = MessageType.GIFT, giftType = GiftType.DIAMOND)

        val GiftFireworks = MessageBody(type = MessageType.GIFT, giftType = GiftType.FIREWORKS)

        fun normal(content: String) = MessageBody(type = MessageType.MSG, content = content)
    }
}