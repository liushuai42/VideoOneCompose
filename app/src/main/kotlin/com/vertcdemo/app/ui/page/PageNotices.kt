package com.vertcdemo.app.ui.page

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.vertcdemo.app.R
import com.vertcdemo.app.ui.theme.Background
import com.vertcdemo.app.ui.theme.VideoOneComposeTheme
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.utils.LocalNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun PageNotices(modifier: Modifier = Modifier) {
    StatusBarColor(darkIcons = true)

    val viewModel: NoticesViewModel = viewModel()
    val notices = viewModel.notices.collectAsState()
    Column(
        modifier = Modifier
            .background(Background)
            .paint(
                painter = painterResource(R.drawable.bg_profile),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopStart,
            )
            .fillMaxSize()
            .safeContentPadding()
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(state = rememberScrollState())
    ) {
        Text(
            text = notices.value,
            style = LocalTextStyle.current.copy(
                color = Color.Black,
                fontSize = 14.sp,
            )
        )
    }
}

class NoticesViewModel(
    private val application: Application
) : AndroidViewModel(application = application) {
    val notices = MutableStateFlow("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            BufferedReader(
                InputStreamReader(application.assets.open("notices.txt"))
            ).use {
                notices.value = it.readText()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoticesPreview() {
    VideoOneComposeTheme {
        CompositionLocalProvider(LocalNavController provides rememberNavController()) {
            PageNotices()
        }
    }
}