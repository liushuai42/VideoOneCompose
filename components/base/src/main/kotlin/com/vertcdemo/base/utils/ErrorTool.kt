package com.vertcdemo.base.utils

import androidx.annotation.StringRes
import com.vertcdemo.base.R
import com.vertcdemo.base.network.HttpException

object ErrorTool {
    const val ERROR_CODE_TOKEN_EMPTY = 451
    const val ERROR_CODE_TOKEN_EXPIRED = 450

    fun getErrorMessage(e: HttpException) = getErrorMessage(e.code, e.message)

    fun getErrorMessage(code: Int, message: String?): String = when (code) {
        -1011 -> getString(R.string.network_message_1011)
        -101 -> getString(R.string.network_message_101)
        -1 -> getString(R.string.network_message_unknown)
        200 -> getString(R.string.network_message_200)
        400 -> getString(R.string.network_message_400)
        402 -> getString(R.string.network_message_402)
        404 -> getString(R.string.network_message_404)
        406 -> getString(R.string.network_message_406)
        416 -> getString(R.string.network_message_416)
        418 -> getString(R.string.network_message_418)
        419 -> getString(R.string.network_message_419)
        422 -> getString(R.string.network_message_422)
        430 -> getString(R.string.network_message_430)
        440 -> getString(R.string.network_message_440)
        441 -> getString(R.string.network_message_441)
        ERROR_CODE_TOKEN_EXPIRED,
        ERROR_CODE_TOKEN_EMPTY -> getString(R.string.network_message_450)

        472 -> getString(R.string.network_message_472)
        481 -> getString(R.string.network_message_481)
        500 -> getString(R.string.network_message_500)
        504 -> getString(R.string.network_message_504)
        506 -> getString(R.string.network_message_506)
        541 -> getString(R.string.network_message_541)
        560 -> getString(R.string.network_message_560)
        611 -> getString(R.string.network_message_611)
        622, 630 -> getString(R.string.network_message_622)
        632 -> getString(R.string.network_message_632)
        634 -> getString(R.string.network_message_634)
        643 -> getString(R.string.network_message_643)
        642, 644 -> getString(R.string.network_message_644)

        645 -> getString(R.string.network_message_645)
        702 -> getString(R.string.network_message_702)
        800, 801 -> getString(R.string.network_message_801)

        802 -> getString(R.string.network_message_802)
        804 -> getString(R.string.network_message_804)
        805 -> getString(R.string.network_message_805)
        806 -> getString(R.string.network_message_806)
        else -> message ?: "ERROR(null)"
    }

    private fun getString(@StringRes id: Int): String =
        ApplicationProvider.get().getString(id)
}