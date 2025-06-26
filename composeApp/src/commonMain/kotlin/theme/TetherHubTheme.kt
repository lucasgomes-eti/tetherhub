package theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun TetherHubTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = DeepSkyBlue,
        onPrimary = Black,
        primaryContainer = CelestialBlue,
        onPrimaryContainer = Black,
        surface = AliceBlue,
        error = MexicanPink,
        tertiaryContainer = SpringGreen
    )
    MaterialTheme(colorScheme = colorScheme, content = content)
}