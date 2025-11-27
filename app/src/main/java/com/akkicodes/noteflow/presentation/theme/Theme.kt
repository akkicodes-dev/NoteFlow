package com.akkicodes.noteflow.presentation.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.akkicodes.noteflow.presentation.screens.settings.model.SettingsViewModel

// ------------ UNIQUE CUSTOM COLORS (Premium Mint Theme) -------------

private val md_dark_primary = Color(0xFF00C8A6)
private val md_dark_onPrimary = Color(0xFF003B32)

private val md_dark_secondary = Color(0xFF4CE0C9)
private val md_dark_onSecondary = Color(0xFF003730)

private val md_dark_background = Color(0xFF0B1A18)
private val md_dark_onBackground = Color(0xFFE0F7F4)

private val md_dark_surface = Color(0xFF0F1E1C)
private val md_dark_onSurface = Color(0xFFCCF2EE)

// ------------ DARK THEME -------------
private val darkSchemes = darkColorScheme(
    primary = md_dark_primary,
    onPrimary = md_dark_onPrimary,

    secondary = md_dark_secondary,
    onSecondary = md_dark_onSecondary,

    background = md_dark_background,
    onBackground = md_dark_onBackground,

    surface = md_dark_surface,
    onSurface = md_dark_onSurface,
)

// ------------ LIGHT THEME (soft mint) -------------
private val lightSchemes = lightColorScheme(
    primary = Color(0xFF00AF92),
    onPrimary = Color.White,

    secondary = Color(0xFF54DCC7),
    onSecondary = Color.White,

    background = Color(0xFFF1FFFC),
    onBackground = Color(0xFF00332D),

    surface = Color.White,
    onSurface = Color(0xFF00453E),
)


// ------------ SELECT WHICH COLOR SCHEME TO USE -------------
private fun getColorScheme(
    context: Context,
    isDarkTheme: Boolean,
    isDynamicTheme: Boolean,
    isAmoledTheme: Boolean
): ColorScheme {

    if (isDynamicTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDarkTheme) {
            val scheme = dynamicDarkColorScheme(context)
            return if (isAmoledTheme) {
                scheme.copy(surface = Color.Black, surfaceContainerLow = Color.Black)
            } else scheme
        } else {
            return dynamicLightColorScheme(context)
        }
    }

    if (isDarkTheme) {
        return if (isAmoledTheme) {
            darkScheme.copy(surface = Color.Black, surfaceContainerLow = Color.Black)
        } else {
            darkScheme
        }
    }

    return lightScheme
}


// ------------ MAIN THEME FUNCTION -------------
@Composable
fun LeafNotesTheme(
    settingsModel: SettingsViewModel,
    content: @Composable () -> Unit
) {
    if (settingsModel.settings.value.automaticTheme) {
        settingsModel.update(settingsModel.settings.value.copy(dynamicTheme = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S))
        settingsModel.update(settingsModel.settings.value.copy(darkTheme = isSystemInDarkTheme()))
    }

    val context = LocalContext.current
    val activity = LocalView.current.context as Activity

    WindowCompat.getInsetsController(activity.window, activity.window.decorView).apply {
        isAppearanceLightStatusBars = !settingsModel.settings.value.darkTheme
    }

    if (settingsModel.settings.value.screenProtection) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    MaterialTheme(
        colorScheme = getColorScheme(
            context,
            settingsModel.settings.value.darkTheme,
            settingsModel.settings.value.dynamicTheme,
            settingsModel.settings.value.amoledTheme
        ),
        typography = Typography(),
        content = content
    )
}
