package io.lostImagin4tion.vkVoiceNotes.ui.screens.main

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import io.lostImagin4tion.vkVoiceNotes.ui.theme.VkVoiceNotesRippleTheme
import io.lostImagin4tion.vkVoiceNotes.ui.screens.navigation.Navigation
import io.lostImagin4tion.vkVoiceNotes.ui.theme.VkVoiceNotesTheme

/**
 * [MainActivity] - the initial activity that starts navigation across the app
 *
 * @author Egor Danilov
 */
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            val navController = rememberNavController()
            val snackbarHostState = remember { SnackbarHostState() }

            VkVoiceNotesTheme {
                CompositionLocalProvider(
                    LocalRippleTheme provides VkVoiceNotesRippleTheme
                ) {
                    SetupStatusBarColor()

                    Scaffold(
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                modifier = Modifier.navigationBarsPadding()
                            ) {
                                Snackbar(
                                    snackbarData = it,
                                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                                    shape = MaterialTheme.shapes.small
                                )
                            }
                        },
                        content = {
                            Navigation(
                                snackbarHostState = snackbarHostState,
                                paddingValues = it,
                                navController = navController
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SetupStatusBarColor() {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val currentWindow = (view.context as? Activity)?.window
            ?: error("Not in an activity - unable to get Window reference")

        val color = MaterialTheme.colorScheme.background.toArgb()
        val isLightStatusBar = !isSystemInDarkTheme()

        SideEffect {
            currentWindow.statusBarColor = color
            WindowCompat.getInsetsController(currentWindow, view)
                .isAppearanceLightStatusBars = isLightStatusBar
        }
    }
}