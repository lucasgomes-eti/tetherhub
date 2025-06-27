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
    val typography = MaterialTheme.typography.copy(
        displaySmall = DisplaySmall(),
        bodySmall = BodySmall(),
        bodyMedium = BodyMedium(),
        bodyLarge = BodyLarge(),
        titleMedium = TitleMedium(),
        labelSmall = LabelSmall(),
        labelMedium = LabelMedium(),
        labelLarge = LabelLarge()
    )
    MaterialTheme(colorScheme = colorScheme, content = content, typography = typography)
}