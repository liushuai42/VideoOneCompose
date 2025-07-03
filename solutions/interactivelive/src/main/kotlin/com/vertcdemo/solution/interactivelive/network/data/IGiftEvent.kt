package com.vertcdemo.solution.interactivelive.network.data

import com.vertcdemo.solution.interactivelive.core.annotation.GiftType

interface IGiftEvent {
    @get:GiftType
    val giftType: Int
    val userId: String
    val username: String
}

object FakeGiftEvent : IGiftEvent {
    override val giftType: Int = GiftType.FIREWORKS
    override val userId: String = "42"
    override val username: String = "John Doe"
}

data class GiftEvent(
    override val giftType: Int,
    override val userId: String,
    override val username: String,
) : IGiftEvent