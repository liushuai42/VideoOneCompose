package com.vertcdemo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vertcdemo.base.LocalCredential
import com.vertcdemo.app.ui.page.PageHome
import com.vertcdemo.app.ui.page.PageNotices
import com.vertcdemo.app.ui.page.PageProfile
import com.vertcdemo.app.ui.theme.VideoOneComposeTheme
import com.vertcdemo.base.CredentialViewModel
import com.vertcdemo.base.RouteHome
import com.vertcdemo.base.RouteLogin
import com.vertcdemo.base.utils.LocalNavController
import com.vertcdemo.login.ui.PageLogin
import com.vertcdemo.solution.interactivelive.interactiveLiveGraph
import kotlinx.serialization.Serializable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoOneComposeTheme {
                VideoOne()
            }
        }
    }
}

@Serializable
object RouteProfile

@Serializable
object RouteNotices

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun VideoOne() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val navController = rememberNavController()
        val credential = viewModel<CredentialViewModel>()
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalCredential provides credential
        ) {
            SharedTransitionLayout {

                NavHost(
                    navController = navController,
                    startDestination = RouteHome
                ) {
                    composable<RouteHome> {
                        PageHome(
                            modifier = Modifier.padding(innerPadding),
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable,
                        )
                    }

                    composable<RouteProfile> {
                        PageProfile(
                            modifier = Modifier.padding(innerPadding),
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable,
                        )
                    }

                    composable<RouteNotices> {
                        PageNotices(
                            modifier = Modifier.padding(innerPadding),
                        )
                    }

                    composable<RouteLogin> {
                        PageLogin(modifier = Modifier.padding(innerPadding))
                    }

                    interactiveLiveGraph(
                        navController = navController,
                        paddingValues = innerPadding,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoOnePreview() {
    VideoOneComposeTheme {
        VideoOne()
    }
}