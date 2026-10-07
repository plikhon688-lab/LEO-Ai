package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.CyberLoadingDialog
import com.example.ui.screens.CreationScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VoidBlack

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                LeoAnimeCharacterApp()
            }
        }
    }
}

@Composable
fun LeoAnimeCharacterApp(
    viewModel: MainViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val snackBarMessage by viewModel.snackBarMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackBarMessage) {
        snackBarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearSnackBar()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { err ->
            snackbarHostState.showSnackbar("Notice: $err", duration = SnackbarDuration.Long)
            viewModel.clearError()
        }
    }

    // Hardware/Gesture Back Navigation Handling
    when (currentScreen) {
        AppScreen.PROFILE -> {
            BackHandler {
                viewModel.navigateTo(AppScreen.CREATION)
            }
        }
        AppScreen.HISTORY -> {
            BackHandler {
                viewModel.navigateTo(AppScreen.CREATION)
            }
        }
        AppScreen.CREATION -> {
            // Default system back behavior
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .safeDrawingPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = VoidBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    AppScreen.CREATION -> CreationScreen(viewModel = viewModel)
                    AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                    AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                }
            }

            if (isGenerating) {
                CyberLoadingDialog()
            }
        }
    }
}
