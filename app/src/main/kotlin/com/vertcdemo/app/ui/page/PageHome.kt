package com.vertcdemo.app.ui.page

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vertcdemo.app.R
import com.vertcdemo.app.RouteProfile
import com.vertcdemo.app.ui.theme.Background
import com.vertcdemo.avatars.Avatars
import com.vertcdemo.base.LocalCredential
import com.vertcdemo.base.RouteHome
import com.vertcdemo.base.RouteLogin
import com.vertcdemo.base.StatusBarColor
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.solution.interactivelive.InteractiveLiveEntry

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PageHome(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    StatusBarColor(darkIcons = false)

    val credential = LocalCredential.current
    val userId = credential.userId.collectAsState()

    Box(
        Modifier
            .background(Background)
            .paint(
                painter = painterResource(R.drawable.bg_home_page),
                contentScale = ContentScale.Fit,
                alignment = Alignment.TopStart,
            )
            .fillMaxSize()
    ) {
        val navController = LocalNavController.current
        with(sharedTransitionScope) {
            Image(
                modifier = modifier
                    .padding(top = 11.dp, end = 12.dp)
                    .align(Alignment.TopEnd)
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-user-avatar"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .size(44.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(RouteProfile)
                    },
                painter = painterResource(Avatars[userId.value]),
                contentDescription = null,
            )
        }

        Column(
            modifier = modifier
                .padding(top = 15.dp)
                .padding(horizontal = 16.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.app_logo_white),
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .padding(top = 8.dp),
                text = stringResource(R.string.videoone_center_title),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
            Text(
                modifier = Modifier
                    .padding(top = 16.dp),
                text = stringResource(R.string.videoone_center_welcome),
                style = TextStyle(
                    color = Color.White,
                    fontSize = 14.sp,
                )
            )

            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {
                InteractiveLiveEntry()
            }
        }

        if (userId.value.isEmpty()) {
            navController.navigate(RouteLogin) {
                popUpTo(RouteHome) {
                    inclusive = true
                }
            }
        }
    }
}