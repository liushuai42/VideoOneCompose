package com.vertcdemo.solution.interactivelive.ui.room.audience

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vertcdemo.solution.interactivelive.R
import com.vertcdemo.solution.interactivelive.core.EmptyEngine
import com.vertcdemo.solution.interactivelive.core.IRTCEngine
import com.vertcdemo.solution.interactivelive.ui.ZeroPadding
import com.vertcdemo.solution.interactivelive.ui.liveBackground

@Composable
fun PageRoomAudience(
    paddingValues: PaddingValues = ZeroPadding,
    engine: IRTCEngine = viewModel<EmptyEngine>(),
    onBackPressed: () -> Unit = {},
) {
    Box(
        Modifier
            .fillMaxSize()
            .liveBackground(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.under_construction),
            style = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}


@Composable
@Preview
fun PreviewPageRoomAudience() {
    PageRoomAudience()
}